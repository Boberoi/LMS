package com.example.lms.controller;

import com.example.lms.exception.FileEmptyException;
import com.example.lms.response.MetaDataResponse;
import com.example.lms.response.ResultResponse;
import com.example.lms.service.CSVService;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CSVController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CSVControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CSVService csvService;

    @InjectMocks
    private CSVController csvController;

    private MultipartFile validFile;
    private MultipartFile emptyFile;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(csvController).build();
        validFile = new MockMultipartFile("file", "test.csv", "text/csv", "some,data".getBytes());
        emptyFile = new MockMultipartFile("file", "", "text/csv", new byte[0]);
    }

    @DisplayName("Test downloadAndUploadCSV success")
    @Test
    public void testDownloadAndUploadCSV_Success() throws Exception {

        MetaDataResponse metaDataResponse = new MetaDataResponse();
        ResultResponse resultResponse = new ResultResponse();
        metaDataResponse.setCode("200");
        metaDataResponse.setMessage("File successfully uploaded to S3 Bucket");
        metaDataResponse.setNoOfRecords("3");
        resultResponse.setMetaDataResponse(metaDataResponse);
        resultResponse.setResult("File 1");

        when(csvService.downloadAndUpload()).thenReturn(resultResponse);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/csv/data")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.jsonPath("$.metaDataResponse.code", is("200")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.metaDataResponse.message", is("File successfully uploaded to S3 Bucket")));
    }

    @DisplayName("Test downloadAndUploadCSV for Exception")
    @Test
    public void testDownloadAndUploadCSV_Exception() throws Exception {

        MetaDataResponse metaDataResponse = new MetaDataResponse();
        ResultResponse resultResponse = new ResultResponse();
        metaDataResponse.setCode("400");
        metaDataResponse.setMessage("Failed to uploaded to S3 Bucket");
        metaDataResponse.setNoOfRecords("0");
        resultResponse.setMetaDataResponse(metaDataResponse);
        resultResponse.setResult(null);
        when(csvService.downloadAndUpload()).thenReturn(resultResponse);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/csv/data")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.jsonPath("$.metaDataResponse.code", is("400")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.metaDataResponse.message", is("Failed to uploaded to S3 Bucket")));
    }

    @Test
    public void testUploadFile_Success() throws Exception {
        String type = "testType";

        ResponseEntity<String> response = csvController.uploadFile(validFile, type);

        verify(csvService).processCSV(validFile, type);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("CSV data uploaded successfully!", response.getBody());
    }

    @Test
    public void testUploadFile_EmptyFile() {
        String type = "testType";

        Exception exception = assertThrows(FileEmptyException.class, () -> {
            csvController.uploadFile(emptyFile, type);
        });

        assertEquals("File is empty", exception.getMessage());
    }

    @Test
    public void testUploadFile_ServiceException() throws Exception {
        String type = "testType";
        doThrow(new RuntimeException("Service error")).when(csvService).processCSV(any(MultipartFile.class), any(String.class));

        ResponseEntity<String> response = csvController.uploadFile(validFile, type);

        verify(csvService).processCSV(validFile, type);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error processing CSV: Service error", response.getBody());
    }

}
