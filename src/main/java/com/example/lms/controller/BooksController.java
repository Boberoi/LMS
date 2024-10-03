package com.example.lms.controller;

import com.example.lms.model.Books;
import com.example.lms.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@Slf4j
public class BooksController {
    
    @Autowired
    private BookService service;

    @GetMapping("/book/all")
    public ResponseEntity<List<Books>> getAllBooks() {
        List<Books> books = service.findAllBooks();
        if (books.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT , "No Books Present");
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/book/{title}")
    public ResponseEntity<List<Books>> getBookByTitle(@PathVariable String title) {
        log.info("get Book by title : {}", title );
        List<Books> book = service.getBookByTitle(title);
        if (!book.isEmpty()) {
            log.info("response : {}", book.toString());
            return new ResponseEntity<>(book, HttpStatus.OK);
        } else {
            log.error("No Book found with title {}", title);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND , "No Book with "+ title + " Present");
        }
    }

    @GetMapping("/book/category/{name}")
    public ResponseEntity<List<Books>> getBookByCategory(@PathVariable String name) {
        log.info("get Book by category : {}", name );
        List<Books> books = service.getBooksByCategory(name);
        if (books.isEmpty()) {
            log.error("No Book found in category {}", name);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND , "No Books Found For this Category");
        }
        log.info("response : {}", books.toString());
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/book/author/{name}")
    public ResponseEntity<List<Books>> getBookByAuthor(@PathVariable String name) {
        log.info("get Book by author : {}", name );
        List<Books> books = service.getBooksByAuthor(name);
        if (books.isEmpty()) {
            log.error("No Book found with author {}", name);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Books by Author " + name + " found");
        }
        log.info("response : {}", books.toString());
        return new ResponseEntity<>(books, HttpStatus.OK);
    }
    @GetMapping("/book/id/{id}")
    public Books getBookById(@PathVariable int id) {
        return service.findById(id);
    }

    @PostMapping("/book")
    public ResponseEntity<Books> addBook(@RequestBody Books book) {
        log.info("add Book : {}", book);
        Books addedBook = service.addBook(book);
        if (addedBook != null) {
            log.info("Book added successfully");
            return new ResponseEntity<>(addedBook, HttpStatus.CREATED);
        }
        else{
            log.error("Something went wrong while adding Book");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/book")
    public ResponseEntity<Books> updateBook(@RequestBody Books book) {
        log.info("update book: {}",book.toString());
        Books updatedBook = service.updateBook(book);
        if(updatedBook != null) {
            log.info("Response : {}",updatedBook.toString());
            return new ResponseEntity<>(updatedBook, HttpStatus.OK);
        }
        else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/book/id/{id}")
    public ResponseEntity<String> removeBook(@PathVariable int id) {
        try {
            log.info("Want to remove Book with id : {}", id);
            service.removeBook(id);
            return new ResponseEntity<>("Book deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}