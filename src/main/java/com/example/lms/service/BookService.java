package com.example.lms.service;

import com.example.lms.controller.BooksController;
import com.example.lms.model.Author;
import com.example.lms.model.Books;
import com.example.lms.model.Category;
import com.example.lms.repository.AuthorRepo;
import com.example.lms.repository.BookRepo;
import com.example.lms.repository.CategoryRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.awt.print.Book;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BookService {
    @Autowired
    private BookRepo repo;
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private AuthorRepo authorRepo;

    public List<Books> findAllBooks() {
        return repo.findAll();
    }

    public List<Books> getBookByTitle(String title) {
        return repo.searchByTitle(title);
    }

    public List<Books> getBooksByCategory(String name) {
        return repo.findByCategory_Name(name);
    }
    public List<Books> getBooksByAuthor(String name) {
        return repo.findByAuthors_Name(name);
    }

    public Books addBook(Books book) {
        log.info("Adding book " + book.toString());
        if (book.getTitle() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Book name: cannot be null");

        if(book.getAvailable_quantity() < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Available quantity cannot be less than zero ");

        if(book.getPrice().compareTo(BigDecimal.ZERO) <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price cannot be zero or less");

        if( (book.getId() != null  && repo.findById(book.getId()).isPresent()) || !repo.findByTitle(book.getTitle()).isEmpty())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Book already exists");

        Category existingCategory = categoryRepo.findByName(book.getCategory().getName());
        if (existingCategory != null) {
            book.setCategory(existingCategory);
        }
        else  book.setCategory(categoryRepo.save(book.getCategory()));

        List<Author> authors = new ArrayList<>();
        for(Author a : book.getAuthors()) {
            Author availabe = authorRepo.findByName(a.getName());
            if(availabe != null)
            {
                if(a.getAuthor_id()!= 0 && availabe.getAuthor_id() != a.getAuthor_id()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Author ID " + a.getAuthor_id() + " already exists with a different name");
                authors.add(availabe);
            }else authors.add(a);
        }
        book.setAuthors(authors);
        book.setIssue_quantity(0);
        Books savedBook = repo.save(book);
        log.info("Book added successfully: {}", savedBook);
        return savedBook;
    }

    public void updateCategory(Books book , Books existingBook)
    {
        Category existingCategory = categoryRepo.findByName(book.getCategory().getName());
        if (existingCategory != null) {
            if(book.getCategory().getCategory_id() != 0 && existingCategory.getCategory_id() != book.getCategory().getCategory_id()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Category ID " + book.getCategory().getCategory_id() + " already exists with a different name Or category name exists with a different id");
            existingBook.setCategory(existingCategory);
        }
        else  existingBook.setCategory(categoryRepo.save(book.getCategory()));
    }

    public void updateAuthor(Books book , Books existingBook)
    {
        List<Author> all = new ArrayList<>(existingBook.getAuthors());
        all.clear();
        List<Author> authors = new ArrayList<>();
        for(Author a : book.getAuthors()) {
            Author availabe = authorRepo.findByName(a.getName());
            if(availabe != null)
            {
                if(a.getAuthor_id()!= 0 && availabe.getAuthor_id() != a.getAuthor_id()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Author ID " + a.getAuthor_id() + " already exists with a different name");
                authors.add(availabe);
            }else authors.add(a);
        }
        existingBook.setAuthors(authors);
    }

    public Books updateBook(Books book) {

        if(book == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book cannot be null");
        if(book.getId() == null || book.getId() <= 0)  throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Book Id: cannot be null or less than zero");
        log.info("Attempting to update book with ID: {}", book.getId());

        Books existBook = repo.findById(book.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        existBook.setDescription(book.getDescription());
        if (book.getTitle() == null)   throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Book name: cannot be null");
        existBook.setTitle(book.getTitle());
        if(book.getPrice().compareTo(BigDecimal.ZERO) <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price cannot be zero or less");
        existBook.setPrice(book.getPrice());
        existBook.setLocation(book.getLocation());
        if(book.getAvailable_quantity() < 0)  throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Available quantity cannot be less than zero ");
        existBook.setAvailable_quantity(book.getAvailable_quantity());

        updateCategory(book, existBook);
        updateAuthor(book, existBook);
        Books updatedBook = repo.save(existBook);
        log.info("Book updated successfully: {}", updatedBook);
        return updatedBook;
    }

    public void removeBook(int id) {
        log.info("Attempting to remove book with ID: {}", id);
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }
        try {
            repo.deleteById(id);
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        log.info("Book removed successfully with ID: {}", id);
    }


    public Books findById(int id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    }
}
