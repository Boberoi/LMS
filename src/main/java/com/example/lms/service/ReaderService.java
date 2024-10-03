package com.example.lms.service;


import com.example.lms.model.Reader;
import com.example.lms.repository.ReaderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;


@Service
public class ReaderService {

    private static final Logger logger = LogManager.getLogger(ReaderService.class);
    @Autowired
    private ReaderRepo repo;

    public List<Reader> findAllReaders() {
        logger.info("Fetching all readers");
        List<Reader> readers = repo.findAll();
        logger.info("Found {} readers", readers.size());
        return readers;
    }

    public Reader getReaderById(int id) {
        return repo.findById(id).orElse(null);
    }

    public List<Reader> getReaderByName(String name) {
         return repo.findByName(name);
    }

    public Reader addReader(Reader reader) {
        logger.info("Attempting to add a new reader: {}", reader.toString());
        if (reader.getName() == null || reader.getName().matches(".*\\d.*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid reader name: cannot be null or contain numbers");
        }
        if (reader.getPno() == null || !reader.getPno().matches("\\d+" )|| reader.getPno().length() > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid phone number: must be numeric and up to 10 digits");
        }
        Reader savedReader = repo.save(reader);
        logger.info("Successfully added reader: {}", savedReader);
        return savedReader;
    }

    public String removeReader(int id) {
        if(!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reader not found");
        repo.deleteById(id);
        return "Reader with id " + id + " removed";
    }

    public Reader updateReader(Reader reader) {
        logger.info("Attempting to update reader: {}", reader.toString());
        if(!repo.existsById(reader.getReader_id())) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reader not found");
        if (reader.getName() == null || reader.getName().matches(".*\\d.*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid reader name: cannot be null or contain numbers");
        }
        if (reader.getPno() == null || !reader.getPno().matches("\\d+" )|| reader.getPno().length() > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid phone number: must be numeric and up to 10 digits");
        }
        Reader updatedReader = repo.save(reader);
        logger.info("Successfully updated reader: {}", updatedReader);
        return updatedReader;
    }
}
