package com.example.lms.controller;

import com.example.lms.model.Reader;
import com.example.lms.service.ReaderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class ReaderController {

    private static final Logger logger = LogManager.getLogger(ReaderController.class);

    @Autowired
    private ReaderService service;

    @GetMapping("/reader/all")
    public List<Reader> allReaders()
    {
        return service.findAllReaders();
    }

    @GetMapping("/reader/name/{name}")
    public ResponseEntity<List<Reader>> getReaderByName(@PathVariable String name)
    {
        logger.info("getReaderByName called with name {}", name);
        List<Reader> readers = service.getReaderByName(name);
        if(!readers.isEmpty()) return new ResponseEntity<>(readers, HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/reader/id/{id}")
    public ResponseEntity<Reader> getReaderById(@PathVariable int id)
    {
        logger.info("getReaderById called with id {}", id);
        Reader readers = service.getReaderById(id);
        if(readers != null) {
            logger.info("Response : {}", readers);
            return new ResponseEntity<>(readers, HttpStatus.OK);
        }
        else{
            logger.error("No Reader found with id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/reader")
    public ResponseEntity<Reader> addReader(@RequestBody Reader reader)
    {
        logger.info("addReader called with name {}", reader.getName());
        Reader newReader = service.addReader(reader);
        if(newReader != null) return new ResponseEntity<>(newReader,HttpStatus.CREATED);
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request Or Reader Already Exists");
    }

    @PutMapping("/reader")
    public ResponseEntity<Reader> updateReader(@RequestBody Reader reader)
    {
        logger.info("updateReader called with name {}", reader.getName());
        Reader updatedReader = service.updateReader(reader);
        if(updatedReader != null) return new ResponseEntity<>(updatedReader,HttpStatus.OK);
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request Or Reader Doesnt Exists");
    }

    @DeleteMapping("/reader/id/{id}")
    public String removeReader(@PathVariable int id)
    {
        logger.info("removeReader called with id {}", id);
        return service.removeReader(id);
    }
}
