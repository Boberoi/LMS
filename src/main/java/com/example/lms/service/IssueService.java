package com.example.lms.service;

import com.example.lms.model.Books;
import com.example.lms.model.Issue;
import com.example.lms.model.Reader;
import com.example.lms.repository.BookRepo;
import com.example.lms.repository.IssueRepo;
import com.example.lms.repository.ReaderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Date;
import java.util.List;

@Service
public class IssueService {
    private static final Logger logger = LogManager.getLogger(IssueService.class);

    @Autowired
    private IssueRepo repo;
    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private ReaderRepo readerRepo;

    public Issue createIssue(Issue issue) {
        logger.info("Creating issue for book ID: {} and reader ID: {}", issue.getBook().getId(), issue.getReader().getReader_id());

        Books existBook = bookRepo.findById(issue.getBook().getId()).orElseThrow(() -> {
            logger.error("Book not found: ID {}", issue.getBook().getId());
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        });

        Reader existReader = readerRepo.findById(issue.getReader().getReader_id()).orElseThrow(() -> {
                    logger.error("Reader not found: ID {}", issue.getReader().getReader_id());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Reader not found");
        });

        int available = existBook.getAvailable_quantity();
        if(available <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Available quantity is less than or equal to 0");
        existBook.setAvailable_quantity(available-1);
        existBook.setIssue_quantity(existBook.getIssue_quantity()+1);
        issue.setBook(existBook);
        issue.setReader(existReader);

        logger.info("Issue created successfully for book ID: {}", existBook.getId());
        return repo.save(issue);
    }

    public String returnBook(Issue issue){

        Issue existingIssue = repo.findById(issue.getIssue_id()).orElseThrow(() -> {
            logger.error("Issue not found: ID {}", issue.getIssue_id());
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found");
        });
        if(existingIssue.getReturn_date() != null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book Already Returned");

        Date firstDate = existingIssue.getIssue_date();
        Date lastDate = existingIssue.getDue_date();
        Date returnDate = issue.getReturn_date();

        long time = lastDate.getTime() - firstDate.getTime();
        long diff = (time/ (1000 * 60 * 60 * 24)) %365 ;

        long fine = 0 ;
        if(returnDate != null) {
            if(returnDate.getTime() < lastDate.getTime()) fine = 0;
            else fine = (( ( returnDate.getTime() - lastDate.getTime() )/ (1000 * 60 * 60 * 24) ) %365 )*10;
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Return date is null");

        long fee = diff * 5 + fine;

        if(issue.getReader() != null && issue.getReader().getReader_id()!= null && !issue.getReader().getReader_id().equals(existingIssue.getReader().getReader_id()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Issued by different Reader");

        if(issue.getBook() != null && issue.getBook().getId()!= null && !issue.getBook().getId().equals(existingIssue.getBook().getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Issued on Book");

        Books existBook = bookRepo.findById(existingIssue.getBook().getId()).orElseThrow(() -> {
            logger.error("Book not found: ID {}", issue.getBook().getId());
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        });

        Reader existReader = readerRepo.findById(existingIssue.getReader().getReader_id()).orElseThrow(() -> {
            logger.error("Reader not found: ID {}", issue.getReader().getReader_id());
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Reader not found");
        });

        existBook.setAvailable_quantity(existBook.getAvailable_quantity()+1);
        existBook.setIssue_quantity(existBook.getIssue_quantity()-1);
        existingIssue.setBook(existBook);
        existingIssue.setReader(existReader);
        existingIssue.setReturn_date(returnDate);
        repo.save(existingIssue);
        logger.info("Book returned successfully: issue ID: {}, fee: {}", existingIssue.getIssue_id(), fee);
        return "Book returned successfully : Amount you required to deposit is : " + fee + " (Adding Fine : "+ fine + " )" ;
    }

    public List<Issue> getAllIssues() {
        return repo.findAll();
    }


}
