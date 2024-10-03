package com.example.lms.controller;

import com.example.lms.exception.FileEmptyException;
import com.example.lms.response.ResultResponse;
import com.example.lms.service.CSVService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/csv")
public class CSVController {

    @Autowired
    private CSVService service;

    @PostMapping(value = "/upload" , consumes = {"multipart/form-data"})
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file , @RequestParam("type") String type) throws FileEmptyException {
        if (file.isEmpty()) {
            log.warn("Received empty file upload attempt");
            throw new FileEmptyException("File is empty");
        }
        try {
            service.processCSV(file, type);
            log.info("CSV data uploaded successfully for type: {}", type);
            return ResponseEntity.ok("CSV data uploaded successfully!");
        }
        catch (Exception e) {
            log.error("Error processing CSV: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing CSV: " + e.getMessage());
        }
    }

    @GetMapping("/data")
    public ResultResponse downloadAndUploadCSV() throws IOException {
        return service.downloadAndUpload();
    }
}
