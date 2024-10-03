package com.example.lms.controller;


import com.example.lms.model.Books;
import com.example.lms.model.Reader;
import com.example.lms.service.ReaderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = ReaderController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReaderControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ReaderService readerService;
    @InjectMocks
    private ReaderController readerController;

    Reader reader1 = new Reader(1,"Reader1",15,"reader1@gmail.com", "8791401775", "jkb place hyderabad",null);
    Reader reader2 = new Reader(2,"Reader2",25,"reader2@gmail.com", "8791443775", "durgam chevru hyderabad",null);
    Reader reader3 = new Reader(3,"Reader3",34,"reader3@gmail.com", "9371401775", "sanali sapazio hyderabad",null);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(readerController).build();
    }

    @Test
    public void testGetAllReaders_Success() throws Exception {

        List<Reader> readers = Arrays.asList(reader1,reader2,reader3);
        given(readerService.findAllReaders()).willReturn(readers);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                                        .get("/reader/all")
                                        .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$" , hasSize(readers.size())));
    }

    @Test
    public void testGetReaders_NoReader() throws Exception {
        List<Reader> readers = Arrays.asList();
        given(readerService.findAllReaders()).willReturn(readers);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/reader/all")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$" , hasSize(0)));
    }

    @Test
    public void testGetReaderById_Success() throws Exception {

        given(readerService.getReaderById(reader1.getReader_id())).willReturn(reader1);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/reader/id/"+ reader1.getReader_id())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void testGetReaderById_NoReader() throws Exception {

        given(readerService.getReaderById(anyInt())).willReturn(null);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/reader/id/5")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNotFound());
    }

    @Test
    public void testGetReaderByName_success() throws Exception {

        List<Reader> reader = Arrays.asList(reader1,reader2,reader3);
        given(readerService.getReaderByName(reader1.getName())).willReturn(reader);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/reader/name/"+ reader1.getName())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());

    }

    @Test
    public void testGetReaderByName_NoReader() throws Exception {

        given(readerService.getReaderByName(anyString())).willReturn(new ArrayList<>());

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/reader/name/rohan")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNotFound());
    }

    @Test
    public void testRemoveReader_Success() throws Exception {

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                                         .delete("/reader/id/"+ reader1.getReader_id())
                                         .contentType(MediaType.APPLICATION_JSON));
        response.andExpect(status().isOk());
    }
    @Test
    public void testRemoveReader_NoReader() throws Exception {

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Reader not found"))
                .when(readerService).removeReader(1);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .delete("/reader/id/1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNotFound());
    }

    @Test
    public void testAddReader_Success() throws Exception {

        Reader newReader = Reader.builder().reader_id(4).name("Syam").age(12).pno("8793647629").email("syam@gmail.com").build();
        given(readerService.addReader(any(Reader.class))).willReturn(newReader);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                                          .post("/reader").contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(newReader)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.reader_id").value(4))
                .andExpect(jsonPath("$.name").value("Syam"));
    }

    @Test
    public void testAddReader_AlreadyExist() throws Exception {

        when(readerService.addReader(new Reader())).thenReturn(null);
        String content = objectMapper.writeValueAsString(new Reader());

        MockHttpServletRequestBuilder m = MockMvcRequestBuilders.post("/reader")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(m).andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateReader_Success() throws Exception {

        Reader updateReader = Reader.builder().reader_id(4).name("Syam Sharma").age(21).pno("8793647629").email("syam@gmail.com").build();
        given(readerService.updateReader(any(Reader.class))).willReturn(updateReader);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .put("/reader").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReader)));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.reader_id").value(4))
                .andExpect(jsonPath("$.name").value("Syam Sharma"))
                .andExpect(jsonPath("$.age").value(21));
    }

    @Test
    public void testUpdateReader_DontExist() throws Exception {
        when(readerService.updateReader(new Reader())).thenReturn(null);
        String content = objectMapper.writeValueAsString(new Reader());

        MockHttpServletRequestBuilder mockMvcRequestBuilders = MockMvcRequestBuilders.put("/reader")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockMvcRequestBuilders)
                .andExpect(status().isBadRequest());
    }



}
