package com.example.lms.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.lms.model.*;
import com.example.lms.repository.*;
import com.example.lms.response.ResultResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.AmazonS3;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CSVServiceTests {

    @Mock
    private BookRepo bookRepo;
    @Mock
    private ReaderRepo readerRepo;
    @Mock
    private IssueRepo issueRepo;
    @Mock
    private AuthorRepo authorRepo;
    @Mock
    private CategoryRepo categoryRepo;
    @InjectMocks
    private CSVService csvService;

    private MultipartFile validFileBook;
    private MultipartFile validFileReader;
    private MultipartFile validFileIssues;
    private MultipartFile emptyFile;

    @BeforeEach
    public void setup()
    {
        String dataBook = "Id,Title,Description,Location,Price,AvailableQuantity,IssuedQuantity,AuthorNames,CategoryName\n" +
                "1,\"The Great Gatsby\",\"A novel set in the 1920s about the American Dream.\",\"Library A\",10.99,6,0,\"John Doe;Author\",\"Fiction\"\n" +
                "2,\"To Kill a Mockingbird\",\"A novel about racial injustice in the Deep South.\",\"Library B\",8.99,7,1,\"Harper Lee;Author\",\"Classic\"";

        String dataReader = "Name,Age,Email,Phone,Address\n" +
                "John Doe,25,johndoe@example.com,1234567890,123 Elm St\n" +
                "Jane Smith,30,janesmith@example.com,0987654321,456 Oak St";

        String dataIssue = "Id,BookId,ReaderId,IssueDate,DueDate,ReturnDate\n" +
                "1,1,1,2024-09-01,2024-09-15,2024-09-14\n" +
                "2,4,2,2024-09-12,2024-09-26,";

        validFileBook = new MockMultipartFile("file", "book.csv", "text/csv", dataBook.getBytes());
        validFileReader = new MockMultipartFile("file", "reader.csv", "text/csv", dataReader.getBytes());
        validFileIssues = new MockMultipartFile("file", "issue.csv", "text/csv", dataIssue.getBytes());
        emptyFile = new MockMultipartFile("file", "", "text/pdf", new byte[0]);

    }


    @DisplayName("test processCSV Book success")
    @Test
    public void testProcessCSV_BookSuccess() throws IOException, InterruptedException {
        String type = "book";

        when(categoryRepo.findByName("Fiction")).thenReturn(new Category("Fiction"));
        when(categoryRepo.findByName("Classic")).thenReturn(new Category("Classic"));
        when(authorRepo.findByName("John Doe")).thenReturn(new Author(1, "John Doe"));
        when(authorRepo.findByName("Harper Lee")).thenReturn(new Author(2, "Harper Lee"));
        when(authorRepo.findByName("Author")).thenReturn(new Author(2, "Author"));
        when(bookRepo.saveAll(any(List.class))).thenReturn(new ArrayList<>());

        csvService.processCSV(validFileBook, type);

        verify(bookRepo, times(1)).saveAll(any(List.class));

    }

    @DisplayName("test processCSV Reader success")
    @Test
    public void testProcessCSV_ReaderSuccess() throws IOException, InterruptedException {
        String type = "reader";
        when(readerRepo.saveAll(any(List.class))).thenReturn(new ArrayList<>());
        csvService.processCSV(validFileReader, type);

        verify(readerRepo, times(1)).saveAll(any(List.class));
    }

    @DisplayName("Test processCSV for Issues success")
    @Test
    public void testProcessCSV_IssueSuccess() throws IOException, InterruptedException {
        String type = "issue";

        when(bookRepo.findById(any(Integer.class))).thenReturn(Optional.of(new Books()));
        when(readerRepo.findById(any(Integer.class))).thenReturn(Optional.of(new Reader()));
        when(issueRepo.saveAll(any(List.class))).thenReturn(new ArrayList<>());

        csvService.processCSV(validFileIssues, type);

        verify(issueRepo, times(1)).saveAll(any(List.class));
    }

    @DisplayName("Test processCSV Fail")
    @Test
    public void testProcessCSV_Fail() throws IOException, InterruptedException {
        String type = "book";
        Exception exception = assertThrows(IllegalArgumentException.class , () -> {
            csvService.processCSV(emptyFile, type);
        });
        verify(bookRepo, never()).saveAll(any(List.class));
    }

    @DisplayName("Test downloadAndUpload success")
    @Test
    public void testDownloadAndUpload_Success() throws IOException, InterruptedException
    {
        AmazonS3 s3Client = mock(AmazonS3.class);
        AmazonS3ClientBuilder builder = mock(AmazonS3ClientBuilder.class);
        Mockito.mockStatic(AmazonS3ClientBuilder.class);
        when(AmazonS3ClientBuilder.standard()).thenReturn(builder);
        when(builder.withCredentials(any())).thenReturn(builder);
        when(builder.build()).thenReturn(s3Client);
        when(builder.withRegion(Regions.US_EAST_1)).thenReturn(builder);


        Books Record1 = new Books(1,"Atomic Habits","How to build healthy and better habits" ,"S1R4C6" , BigDecimal.valueOf(124) , 5 , 2 , new Category("TextBook"), Arrays.asList(new Author(1,"Author1") , new Author(2,"Author2")),  null );
        Books Record2 = new Books(2,"Thinking Fast and slow","How to create good mental health" ,"S5R2C4" ,BigDecimal.valueOf(250) , 2 , 0 , new Category("Health"), Arrays.asList(new Author(3,"Author3") , new Author(4,"Author4")),  null );
        Books Record3 = new Books(3,"It","t is a 1986 horror novel by American author" ,"S5R1C5" ,BigDecimal.valueOf(299) , 10, 5, new Category("Horror"), Arrays.asList(new Author(5,"Stephen King") , new Author(1,"Author1")),  null );
        List<Books> books = Arrays.asList(Record1,Record2,Record3);
        when(bookRepo.findAll()).thenReturn(books);

        Reader reader1 = new Reader(1,"Reader1",15,"reader1@gmail.com", "8791401775", "jkb place hyderabad",null);
        Reader reader2 = new Reader(2,"Reader2",25,"reader2@gmail.com", "8791443775", "durgam chevru hyderabad",null);
        Reader reader3 = new Reader(3,"Reader3",34,"reader3@gmail.com", "9371401775", "sanali sapazio hyderabad",null);
        List<Reader> readers = Arrays.asList(reader1,reader2,reader3);
        when(readerRepo.findAll()).thenReturn(readers);

        Issue issue1 = new Issue(1,reader1,new Date(2004-2-2), new Date(2004-2-12) , null ,Record1);
        Issue issue2 = new Issue(2,reader2,new Date(2004-2-2), new Date(2004-2-12) , null ,Record3);
        List<Issue> issues = Arrays.asList(issue1,issue2);
        when(issueRepo.findAll()).thenReturn(issues);

        when(s3Client.putObject(any(PutObjectRequest.class))).thenReturn(null);

        ResultResponse response = csvService.downloadAndUpload();

        assertEquals("200", response.getMetaDataResponse().getCode());
        assertEquals("File successfully uploaded to S3 Bucket", response.getMetaDataResponse().getMessage());
        assertNotNull(response.getResult());
    }


}
