package com.example.lms.exception;

public class FileDownloadException extends SpringBootFileUploadException{
    public FileDownloadException(String message) {
        super(message);
    }
}
