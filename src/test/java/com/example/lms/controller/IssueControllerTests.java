package com.example.lms.controller;

import com.example.lms.model.*;
import com.example.lms.service.IssueService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(IssueController.class)
@AutoConfigureMockMvc(addFilters = false)
public class IssueControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IssueService issueService;

    @InjectMocks
    private IssueController issueController;

    @Autowired
    private ObjectMapper objectMapper;
    private ObjectWriter objectWriter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(issueController).build();
    }

    Reader reader = new Reader(1,"Reader1",15,"reader1@gmail.com", "8791401775", "jkb place hyderabad",null);
    Books book = new Books(1,"Atomic Habits","How to build healthy and better habits" ,"S1R4C6" , BigDecimal.valueOf(124) , 5 , 2 , new Category("TextBook"), Arrays.asList(new Author(1,"Author1") , new Author(2,"Author2")),  null );
    Issue issue1 = new Issue(1,reader,new Date(2004-2-2), new Date(2004-2-12) , null ,book);

    @DisplayName("All isues when some available")
    @Test
    public void testGetAllIssues_Success() throws Exception
    {
        List<Issue> issues = Arrays.asList(issue1);
        when(issueService.getAllIssues()).thenReturn(issues);

        ResultActions response = mockMvc.perform( MockMvcRequestBuilders
                .get("/issue/all")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$" , hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].issue_id" , is(1)));
    }
    @DisplayName("All isues when no issue available")
    @Test
    public void testGetAllIssues_NoIssue() throws Exception {
        List<Issue> issues = Arrays.asList();
        when(issueService.getAllIssues()).thenReturn(issues);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/issue/all")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNoContent());
    }
    @DisplayName("Test createIssue Success Case")
    @Test
    public void testCreateIssue_Success() throws Exception {

        when(issueService.createIssue(any(Issue.class))).thenReturn(issue1);
        String content = objectMapper.writeValueAsString(issue1);

        MockHttpServletRequestBuilder mockMvcRequestBuilders = MockMvcRequestBuilders.post("/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockMvcRequestBuilders).andExpect(status().isCreated());
    }

    @DisplayName("Test createIssue Failure Case")
    @Test
    public void testCreateIssue_Failure() throws Exception {

        when(issueService.createIssue(any(Issue.class))).thenReturn(null);
        String content = objectMapper.writeValueAsString(issue1);

        MockHttpServletRequestBuilder mockMvcRequestBuilders = MockMvcRequestBuilders.post("/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockMvcRequestBuilders).andExpect(status().isBadRequest());
    }
    @DisplayName("Test returnBook Success")
    @Test
    public void testReturnBook_Success() throws Exception {
        issue1.setReturn_date(new Date(2024,9,30));
        when(issueService.returnBook(any(Issue.class))).thenReturn("Book returned successfully");
        String content = objectMapper.writeValueAsString(issue1);

        MockHttpServletRequestBuilder mockMvcRequestBuilders = MockMvcRequestBuilders.put("/return")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockMvcRequestBuilders).andExpect(status().isOk());
    }
    @DisplayName("Test returnBook Excepitom")
    @Test
    public void testReturnBook_Exception() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST)).when(issueService).returnBook(any(Issue.class));
        String content = objectMapper.writeValueAsString(issue1);

        MockHttpServletRequestBuilder mockMvcRequestBuilders = MockMvcRequestBuilders.put("/return")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockMvcRequestBuilders).andExpect(status().isBadRequest());
    }
}

