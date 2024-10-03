package com.example.lms.service;

import com.example.lms.model.*;
import com.example.lms.repository.BookRepo;
import com.example.lms.repository.IssueRepo;
import com.example.lms.repository.ReaderRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IssueServiceTests {

    @Mock
    IssueRepo issueRepo;
    @Mock
    BookRepo bookRepo;
    @Mock
    ReaderRepo readerRepo;
    @InjectMocks
    IssueService issueService;

    Reader reader = new Reader(1,"Reader1",15,"reader1@gmail.com", "8791401775", "jkb place hyderabad",null);
    Books book = new Books(1,"Atomic Habits","How to build healthy and better habits" ,"S1R4C6" , BigDecimal.valueOf(124) , 5 , 2 , new Category("TextBook"), Arrays.asList(new Author(1,"Author1") , new Author(2,"Author2")),  null );
    Issue issue1 = new Issue(1,reader,new Date(2004,2,2), new Date(2004,2,12) , null ,book);

    @DisplayName("All isues when some available")
    @Test
    public void testGetAllIssues_Success() throws Exception
    {
        List<Issue> issues = Arrays.asList(issue1);
        when(issueRepo.findAll()).thenReturn(issues);

        List<Issue> response = issueService.getAllIssues();

        assertEquals(1,response.size());
        verify(issueRepo).findAll();
    }

    @DisplayName("All isues when no issue available")
    @Test
    public void testGetAllIssues_NoIssue() throws Exception {
        List<Issue> issues = Arrays.asList();
        when(issueService.getAllIssues()).thenReturn(issues);

        List<Issue> response = issueService.getAllIssues();

        assertEquals(0,response.size());
        verify(issueRepo).findAll();
    }

    @DisplayName("Test createIssue Success Case")
    @Test
    public void testCreateIssue_Success() throws Exception {

        when(bookRepo.findById(1)).thenReturn(Optional.of(book));
        when(readerRepo.findById(1)).thenReturn(Optional.of(reader));
        when(issueRepo.save(issue1)).thenReturn(issue1);

        Issue response = issueService.createIssue(issue1);
        assertEquals(issue1,response);
        assertEquals(1 , response.getIssue_id());
        verify(issueRepo).save(issue1);
    }

    @DisplayName("Test createIssue No Book")
    @Test
    public void testCreateIssue_NoBook() throws Exception {

        when(bookRepo.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,()-> issueService.createIssue(issue1));
        assertEquals("Book not found", exception.getReason());

    }
    @DisplayName("Test createIssue Invalid Reader")
    @Test
    public void testCreateIssue_InvalidReader() throws Exception {

        when(bookRepo.findById(1)).thenReturn(Optional.of(book));
        when(readerRepo.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,()-> issueService.createIssue(issue1));
        assertEquals("Reader not found", exception.getReason());

    }
    @DisplayName("Test createIssue Book Unavailable")
    @Test
    public void testCreateIssue_BookUnavailable() throws Exception {

        book.setAvailable_quantity(0);

        when(bookRepo.findById(1)).thenReturn(Optional.of(book));
        when(readerRepo.findById(1)).thenReturn(Optional.of(reader));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,()-> issueService.createIssue(issue1));
        assertEquals("Available quantity is less than or equal to 0", exception.getReason());

    }
    @DisplayName("Test returnBook Success")
    @Test
    public void testReturnBook_Success() throws Exception {
        Issue issue2 = new Issue(1,reader,new Date(2004,2,2), new Date(2004,2,12) , null ,book);
        issue2.setReturn_date(new Date(2004,2,13));

        when(issueRepo.findById(1)).thenReturn(Optional.of(issue1));
        when(bookRepo.findById(1)).thenReturn(Optional.of(book));
        when(readerRepo.findById(1)).thenReturn(Optional.of(reader));
        when(issueRepo.save(issue2)).thenReturn(issue2);

        String response = issueService.returnBook(issue2);
        assertEquals("Book returned successfully : Amount you required to deposit is : 60 (Adding Fine : 10 )",response);

    }

    @DisplayName("Test returnBook Non Existing Issue")
    @Test
    public void testReturnBook_NonExistingIssue() throws Exception {

        issue1.setReturn_date(new Date(2004,2,13));
        when(issueRepo.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,()-> issueService.returnBook(issue1));

        assertEquals("Issue not found", exception.getReason());
        verify(issueRepo,never()).save(issue1);
    }

    @DisplayName("Test returnBook Null Return Date")
    @Test
    public void testReturnBook_NullReturnDate() throws Exception {

        when(issueRepo.findById(1)).thenReturn(Optional.of(issue1));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,()-> issueService.returnBook(issue1));

        assertEquals("Return date is null", exception.getReason());
        verify(issueRepo,never()).save(issue1);
    }

    @DisplayName("Test returnBook Non Existing Book")
    @Test
    public void testReturnBook_NonExistingBook() throws Exception {
        Issue issue2 = new Issue(1,reader,new Date(2004,2,2), new Date(2004,2,12) , null ,book);
        issue2.setReturn_date(new Date(2004,2,13));

        when(issueRepo.findById(1)).thenReturn(Optional.of(issue1));
        when(bookRepo.findById(1)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,()-> issueService.returnBook(issue2));

        assertEquals("Book not found", exception.getReason());
        verify(issueRepo,never()).save(issue2);
    }

    @DisplayName("Test returnBook Non Existing Reader")
    @Test
    public void testReturnBook_NonExistingReader() throws Exception {
        Issue issue2 = new Issue(1,reader,new Date(2004,2,2), new Date(2004,2,12) , null ,book);
        issue2.setReturn_date(new Date(2004,2,13));

        when(issueRepo.findById(1)).thenReturn(Optional.of(issue1));
        when(bookRepo.findById(1)).thenReturn(Optional.of(book));
        when(readerRepo.findById(1)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,()-> issueService.returnBook(issue2));

        assertEquals("Reader not found", exception.getReason());
        verify(issueRepo,never()).save(issue2);
    }

}
