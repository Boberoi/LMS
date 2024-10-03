package com.example.lms.controller;

import com.example.lms.model.Issue;
import com.example.lms.service.IssueService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.text.ParseException;
import java.util.List;

@RestController
public class IssueController {

    private static final Logger logger = LogManager.getLogger(IssueController.class);

    @Autowired
    private IssueService service;

    @GetMapping("/issue/all")
    public ResponseEntity<List<Issue>> getAllIssues() {
        List<Issue>  all = service.getAllIssues();
        if(all.isEmpty()) return new  ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @PostMapping("/issue")
    public ResponseEntity<Issue> createIssue(@RequestBody Issue issue)
    {
        logger.info("Request to create issue {}", issue);
        Issue newIssue = service.createIssue(issue);
        if(newIssue == null) {
            logger.error("Issue creation failed");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        else{
            logger.info("Issue created : {}", newIssue);
            return new ResponseEntity<>(newIssue, HttpStatus.CREATED);
        }
    }

    @PutMapping("/return")
    public ResponseEntity<String> returnBook(@RequestBody Issue issue) throws ParseException {
        try {
            logger.info("Request to return issue with id : {} on return date: {} ", issue.getIssue_id(), issue.getReturn_date());
            String returnBook = service.returnBook(issue);
            logger.info("Response to return book : {}", returnBook);
            return new ResponseEntity<>(returnBook, HttpStatus.OK);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
}
