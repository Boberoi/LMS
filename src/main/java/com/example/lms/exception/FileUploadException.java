package com.example.lms.exception;

import org.hibernate.service.spi.ServiceException;

public class FileUploadException extends SpringBootFileUploadException{
    public FileUploadException(String message) {
        super(message);
    }
}
