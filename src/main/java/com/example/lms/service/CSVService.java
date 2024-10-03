package com.example.lms.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.lms.model.Reader;
import com.example.lms.dto.BookDto;
import com.example.lms.model.*;
import com.example.lms.dto.IssueDto;
import com.example.lms.dto.ReaderDto;
import com.example.lms.repository.*;
import com.example.lms.response.MetaDataResponse;
import com.example.lms.response.ResultResponse;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CSVService {

    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private ReaderRepo readerRepo;
    @Autowired
    private IssueRepo issueRepo;
    @Autowired
    private AuthorRepo authorRepo;
    @Autowired
    private CategoryRepo categoryRepo;


    @Transactional
    public void processCSV(MultipartFile file ,String type) throws IOException, InterruptedException {
        if(!hasCSVFormat(file)) throw new IllegalArgumentException("Invalid File Type (CSV Required)");
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        switch (type.toLowerCase())
        {
            case "book":
                List<Books> books = parseBookCSV(file,executorService);
                bookRepo.saveAll(books);
                break;
            case "reader":
                List<Reader> readers = parseReaderCSV(file,executorService);
                readerRepo.saveAll(readers);
                break;
            case "issue":
                List<Issue> issues = parseIssueCSV(file,executorService);
                issueRepo.saveAll(issues);
                break;
            default:
                throw new IllegalArgumentException("Invalid CSV file type");
        }
        executorService.shutdown();
    }

    public boolean hasCSVFormat(MultipartFile file) throws IOException {
        return Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv");
    }

    private List<Books> parseBookCSV(MultipartFile file , ExecutorService executorService) throws IOException {

        List<Books> listBook = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger BookCountRecord = new AtomicInteger(0);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            Thread.sleep(1000L);
            HeaderColumnNameMappingStrategy<BookDto> stratergy = new HeaderColumnNameMappingStrategy<>();
            stratergy.setType(BookDto.class);

            CsvToBean<BookDto> csvToBean = new CsvToBeanBuilder<BookDto>(br)
                    .withMappingStrategy(stratergy)
                    .withIgnoreEmptyLine(true)
                    .withIgnoreLeadingWhiteSpace(true).build();

            List<BookDto> bookDtoList = csvToBean.parse();
            Set<String> existingCategories = new HashSet<>();
            Set<String> existingAuthors = new HashSet<>();

            for (BookDto csvLine : bookDtoList)
            {
                executorService.execute(() -> {

                    String categoryName = csvLine.getCategoryName();
                    Category category = categoryRepo.findByName(categoryName);
                    if(category == null)
                    {
                        category = new Category(categoryName);
                        category = categoryRepo.save(category);
                    }
                    existingCategories.add(categoryName);

                    List<Author> authors = new ArrayList<>();
                    String csvAuthors  = csvLine.getAuthorNames();
                    for(String authorName : csvAuthors.split(";")) {
                        Author author = authorRepo.findByName(authorName.trim());
                        if (author == null) {
                            author = new Author();
                            author.setName(authorName.trim());
                            author = authorRepo.save(author);
                        }
                        authors.add(author);
                        existingAuthors.add(author.getName());
                    }
                     Books createdBook = Books.builder()
                             .id(csvLine.getId())
                             .title(csvLine.getTitle())
                            .price(csvLine.getPrice())
                            .description(csvLine.getDescription())
                            .location(csvLine.getLocation())
                            .available_quantity(csvLine.getAvailable_quantity())
                            .issue_quantity(csvLine.getIssue_quantity())
                            .category(category).authors(authors)
                            .build();
                    listBook.add(createdBook);
                    log.info("created book : {} ",createdBook);
                    BookCountRecord.incrementAndGet();
                });
            }
            executorService.shutdown();
            executorService.awaitTermination(1,TimeUnit.HOURS);
            System.out.println("Size of list book: " + listBook.size());
            System.out.println("book count record: " + BookCountRecord);

        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage());
        }
        return listBook;
    }

    private List<Issue> parseIssueCSV(MultipartFile file, ExecutorService executorService) throws IOException {
        List<Issue> listIssue = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger IssueCountRecord = new AtomicInteger(0);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            HeaderColumnNameMappingStrategy<IssueDto> strategy = new HeaderColumnNameMappingStrategy<IssueDto>();
            strategy.setType(IssueDto.class);

            CsvToBean<IssueDto> csvToBean = new CsvToBeanBuilder<IssueDto>(br)
                    .withMappingStrategy(strategy)
                    .withIgnoreEmptyLine(true)
                    .withIgnoreLeadingWhiteSpace(true).build();
            List<IssueDto> issueDtoList = csvToBean.parse();

            for (IssueDto csvLine : issueDtoList)
            {
                executorService.execute(() -> {

                    Books book = bookRepo.findById(csvLine.getBookId()).orElse(null);
                    if(book == null) {
                        throw new RuntimeException("Book not found with id " + csvLine.getBookId());
                    }

                    Reader reader = readerRepo.findById(csvLine.getReaderId()).orElse(null);
                    if(reader == null) {
                        throw new RuntimeException("Reader not found with id " + csvLine.getReaderId());
                    }
                    Issue.IssueBuilder issueBuilder = Issue.builder()
                            .issue_id(csvLine.getId())
                            .book(book)
                            .reader(reader)
                            .issue_date(csvLine.getIssueDate())
                            .due_date(csvLine.getDueDate());
                    Date returnDate = csvLine.getReturnDate();
                    if(returnDate != null) {
                        issueBuilder.return_date(returnDate);
                    }
                    Issue createdIssue = issueBuilder.build();
                    listIssue.add(createdIssue);
                    log.info("created issue : {}", createdIssue);
                    IssueCountRecord.incrementAndGet();
                });
            }
            executorService.shutdown();
            executorService.awaitTermination(1,TimeUnit.HOURS);
            System.out.println("Size of list Issues: " + listIssue.size());
            System.out.println("Issue count record: " + IssueCountRecord);

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return listIssue;
    }

    private List<Reader> parseReaderCSV(MultipartFile file, ExecutorService executorService) {
        List<Reader> listReader = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger ReaderCountRecord = new AtomicInteger(0);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            HeaderColumnNameMappingStrategy<ReaderDto> strategy = new HeaderColumnNameMappingStrategy<ReaderDto>();
            strategy.setType(ReaderDto.class);

            CsvToBean<ReaderDto> csvToBean = new CsvToBeanBuilder<ReaderDto>(br)
                    .withMappingStrategy(strategy)
                    .withIgnoreEmptyLine(true)
                    .withIgnoreLeadingWhiteSpace(true).build();
            List<ReaderDto> readerDtoList = csvToBean.parse();

            for (ReaderDto csvLine : readerDtoList)
            {
                executorService.execute(() -> {
                    Reader createdReader = Reader.builder()
                            .name(csvLine.getName())
                            .age(csvLine.getAge())
                            .email(csvLine.getEmail())
                            .pno(csvLine.getPno())
                            .address(csvLine.getAddress())
                            .build();
                    listReader.add(createdReader);
                    log.info("created issue : {}", createdReader);
                    ReaderCountRecord.incrementAndGet();
                });
            }
            executorService.shutdown();
            executorService.awaitTermination(1,TimeUnit.HOURS);
            System.out.println("Size of list Readers: " + listReader.size());
            System.out.println("Reader count record: " + ReaderCountRecord);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return listReader;
    }

    public ResultResponse downloadAndUpload() throws IOException {

        File bookCsv = generateBookCSV();
        File readerCsv = generateReaderCSV();
        File issueCsv = generateIssueCSV();

        MetaDataResponse metaDataResponse = new MetaDataResponse();
        ResultResponse resultResponse = new ResultResponse();

        try {

            String file1Name = System.currentTimeMillis()+"_"+ bookCsv.getName();
            String file2Name = System.currentTimeMillis()+"_"+readerCsv.getName();
            String file3Name = System.currentTimeMillis()+"_"+issueCsv.getName();

            BasicSessionCredentials awsCredentials = new BasicSessionCredentials("ASIA3BSAUZXSFQU4CMLF", "rMR3AhV0TOsoi4PwlZoc/Z5OusxYLfz3Ln23p78N",
                    "IQoJb3JpZ2luX2VjEHkaCXVzLWVhc3QtMSJHMEUCIQC3uIUB7mT0PoZG8wIWFkGlBtn2sq78w4IxTQwfCFCXjwIgL6IX5bVW26G9DD+1+R97jZ6lH5hE6GfAVbulutkP8ggqkwMIwv//////////ARAAGgw3NTkyNzEwNTA3MjQiDJ8LWwpZJEswsjXYPyrnAkM51gE9NA5rlCGIkBe7Cl7rh4+4QijjN7NC/m3RoEI5Q/b3Ipj6xrYrbtaBzUZ3I+XnA7r+ytqmB43/2hcDqxr++5uR/tFpeDFssNOBqvnl48LGkjdXfXiSS23/aR9fN/A9FuHe/aVtfl/mKzX8LM2N6Vfoeb3MB2FNMh/vdBho0+TipiJKIezrkkhDRmcuZ/xZNJIkfAs14GAjdHXXenggYv7WxeUptLiR3+27xojsnwQwaxzIxXhbvQJMqGpiJDuaXrXJ9HaH8EA0UPHaHXKepjKcsw1GOS+GeC29zVT2RVpVDgiEAKEQZFqqasfvgsbOLRmNFInimukGiARzYFbCYSqfeknXoSQHTlWoQZHHZIwJ9Bl04yPMOVwCm3ac1b5vWlEfr67d2omp53/6sFFLT9njgwHU0rUVgo/4g6BBjpWLiOGk1DDx625NkX4Ffh/X2HgLcFPsKhkpwCZ1h+jKljH3mhp8MJWU+7cGOqYBsylml+/SsH+pPO7yZtTgDQ0rMGAVZLNJiFGzy20n1TwVJHaE/qz7qRKh5qktQKHbbeINy5YBBvU9Z9qxTh7UxETEJl5qI0j9j8+aT1zH5WqWE4CKd+sfo1EhJaaCHkxRiHots5NbxE0p5OW0anh3TF25HYFc4LBCkA/YKs5wOfuYDTnGWNzZb1ZBxcVw1eVCqKfz7KaAEDTpNxX0WGhoI6EiZX/eXw=="
            );
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(Regions.US_EAST_1)
                    .build();
            s3Client.putObject(new PutObjectRequest("iv-downloads-test", file1Name, bookCsv));
            s3Client.putObject(new PutObjectRequest("iv-downloads-test", file2Name, readerCsv));
            s3Client.putObject(new PutObjectRequest("iv-downloads-test", file3Name, issueCsv));
            bookCsv.delete();
            readerCsv.delete();
            issueCsv.delete();
            metaDataResponse.setCode("200");
            metaDataResponse.setMessage("File successfully uploaded to S3 Bucket");
            metaDataResponse.setNoOfRecords("3");
            resultResponse.setMetaDataResponse(metaDataResponse);
            resultResponse.setResult(file1Name+" "+file2Name+" "+file3Name);
            return resultResponse;

        }
        catch (AmazonS3Exception e) {
            metaDataResponse.setCode("400");
            metaDataResponse.setMessage("Failed to upload to S3 Bucket: " + e.getMessage());
            metaDataResponse.setNoOfRecords("0");
            resultResponse.setMetaDataResponse(metaDataResponse);
            resultResponse.setResult(null);
            log.error("S3 Exception: {}", e.getMessage());
            return resultResponse;
        }
        catch (Exception e) {
            metaDataResponse.setCode("400");
            metaDataResponse.setMessage("Failed to uploaded to S3 Bucket");
            metaDataResponse.setNoOfRecords("0");
            resultResponse.setMetaDataResponse(metaDataResponse);
            resultResponse.setResult(null);
            e.printStackTrace();
            return resultResponse;
        }
    }

    private File generateIssueCSV() throws IOException {
        List<Issue> issues = issueRepo.findAll();
        File issueCsv = new File("issues.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(issueCsv))) {
            writer.write("Book ID,Reader ID,Issue Date,Due Date,Return Date\n");
            for (Issue issue : issues) {
                writer.write(String.format("%d,%d,%s,%s,%s\n",
                        issue.getBook().getId(),
                        issue.getReader().getReader_id(),
                        issue.getIssue_date(),
                        issue.getDue_date(),
                        issue.getReturn_date()));
            }
        }
        return issueCsv;
    }

    private File generateReaderCSV() throws IOException {
        File readerCsv = new File("Reader.csv");
        List<Reader> readers = readerRepo.findAll();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(readerCsv))) {
            writer.write("Id,Name,Age,Email,Phone Number,Address\n");
            for (Reader reader : readers) {
                writer.write(String.format("%d,%s,%d,%s,%s,%s\n",
                        reader.getReader_id(),
                        reader.getName(),
                        reader.getAge(),
                        reader.getEmail(),
                        reader.getPno(),
                        reader.getAddress()));
            }
        }
        return readerCsv;
    }

    private File generateBookCSV() throws IOException {

        File bookCsv = new File("books.csv");
        List<Books> books = bookRepo.findAll();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(bookCsv))) {
            writer.write("Id,Title,Price,Description,Location,Available Quantity,Issue Quantity,Category,Authors\n");
            for (Books book : books) {
                String authors = book.getAuthors().stream()
                        .map(Author::getName)
                        .collect(Collectors.joining("; "));
                writer.write(String.format("%d,%s,%s,%s,%s,%d,%d,%s,%s\n",
                        book.getId(),
                        book.getTitle(),
                        book.getPrice(),
                        book.getDescription(),
                        book.getLocation(),
                        book.getAvailable_quantity(),
                        book.getIssue_quantity(),
                        book.getCategory().getName(),
                        authors));
            }
        }
        return bookCsv;
    }


}
