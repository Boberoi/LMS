package com.example.lms.service;

import com.example.lms.model.Author;
import com.example.lms.model.Books;
import com.example.lms.model.Category;
import com.example.lms.repository.AuthorRepo;
import com.example.lms.repository.BookRepo;
import com.example.lms.repository.CategoryRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTests {

    @Mock
    private BookRepo bookRepo;
    @Mock
    private AuthorRepo authorRepo;
    @Mock
    private CategoryRepo categoryRepo;
    @InjectMocks
    private BookService bookService;

    Books Record1 = new Books(1,"Atomic Habits","How to build healthy and better habits" ,"S1R4C6" , BigDecimal.valueOf(124) , 5 , 2 , new Category("TextBook"), Arrays.asList(new Author(1,"Author1") , new Author(2,"Author2")),  null );
    Books Record2 = new Books(2,"Thinking Fast and slow","How to create good mental health" ,"S5R2C4" ,BigDecimal.valueOf(250) , 2 , 0 , new Category("Health"), Arrays.asList(new Author(3,"Author3") , new Author(4,"Author4")),  null );
    Books Record3 = new Books(3,"It","t is a 1986 horror novel by American author" ,"S5R1C5" ,BigDecimal.valueOf(299) , 10, 5, new Category("Horror"), Arrays.asList(new Author(5,"Stephen King") , new Author(1,"Author1")),  null );
    Books Record4 = new Books(4,"The Nun","t is a 1986 horror novel by American author" ,"S5R1C8" ,BigDecimal.valueOf(299) , 10, 5, new Category("Horror"), Arrays.asList(new Author(5,"Stephen King")),  null );

    @DisplayName("Test for all Books when some present")
    @Test
    public void testFindAllBooks_Success() throws Exception {
        List<Books> books = Arrays.asList(Record1, Record2, Record3);
        when(bookRepo.findAll()).thenReturn(books);

        List<Books> response = bookService.findAllBooks();

        assertThat(response).isNotNull();
        assertEquals(3, response.size());
        verify(bookRepo).findAll();
    }

    @DisplayName("Test for all Books when no book is present")
    @Test
    public void testFindAllBooks_NoBook() throws Exception {
        List<Books> books = Arrays.asList();
        when(bookRepo.findAll()).thenReturn(books);

        List<Books> response = bookService.findAllBooks();
        assertEquals(0, response.size());
        verify(bookRepo).findAll();
    }

    @DisplayName("Test for getBookByTitle when book present")
    @Test
    public void testGetBookByTitle_Success() throws Exception {

        List<Books> books = Arrays.asList(Record1);
        when(bookRepo.searchByTitle("Atomic Habits")).thenReturn(books);

        List<Books> response = bookService.getBookByTitle("Atomic Habits");
        assertEquals(1,response.size());
        assertThat(response).isNotNull();
        verify(bookRepo,times(1)).searchByTitle("Atomic Habits");
    }

    @DisplayName("Test for getBooksByTitle when book is not present")
    @Test
    public void testGetBookByTitle_NoBook() throws Exception {
        when(bookRepo.searchByTitle("Stuart Little")).thenReturn(new ArrayList<>());

        List<Books> response = bookService.getBookByTitle("Stuart Little");
        assertEquals(0,response.size());
    }

    @DisplayName("Test for getBooksByCategory when books present")
    @Test
    public void testGetBookByCategory_Success() throws Exception {
        List<Books> books = Arrays.asList(Record3 ,Record4);
        when(bookRepo.findByCategory_Name("Horror")).thenReturn(books);

        List<Books> response = bookService.getBooksByCategory("Horror");
        assertEquals(2,response.size());
        assertThat(response).isNotNull();
        assertEquals("Horror" , response.get(0).getCategory().getName());
    }

    @DisplayName("Test for getBooksByCategory when No Books For This Category are Present")
    @Test
    public void testGetBookByCategory_NoSuchCategory() throws Exception {

        when(bookRepo.findByCategory_Name("Action")).thenReturn(new ArrayList<>());
        List<Books> response = bookService.getBooksByCategory("Action");
        assertEquals(0 , response.size());
    }

    @DisplayName("Test for getBooksByAuthor when books present")
    @Test
    public void testGetBookByAuthor_Success() throws Exception {
        List<Books> books = Arrays.asList(Record3 ,Record4);
        when(bookRepo.findByAuthors_Name("Stephen King")).thenReturn(books);

        List<Books> response = bookService.getBooksByAuthor("Stephen King");
        assertEquals(2,response.size());
        assertThat(response).isNotNull();
    }

    @DisplayName("Test for getBooksByAuthor when No Books For This Author are Present")
    @Test
    public void testGetBookByAuthor_NoSuchAuthor() throws Exception {

        when(bookRepo.findByAuthors_Name("Cloe")).thenReturn(new ArrayList<>());
        List<Books> response = bookService.getBooksByAuthor("Cloe");
        assertEquals(0 , response.size());
    }

    @DisplayName("Test for addBook when valid Book provided")
    @Test
    public void testAddBook_Success() throws Exception {
        when(categoryRepo.findByName("TextBook")).thenReturn(new Category("TextBook"));
        when(authorRepo.findByName("Author1")).thenReturn(new Author(1,"Author1"));
        when(bookRepo.save(any(Books.class))).thenReturn(Record1);

        Books response = bookService.addBook(Record1);
        assertEquals(1,response.getId());
        assertThat(response).isNotNull();
        verify(bookRepo,times(1)).save(any(Books.class));
    }
    @DisplayName("Test for addBook when No Book in that category")
    @Test
    public void testAddBook_NoCategory() throws Exception {
        when(categoryRepo.findByName("TextBook")).thenReturn(null);
        when(authorRepo.findByName("Author1")).thenReturn(new Author(1,"Author1"));
        when(bookRepo.save(any(Books.class))).thenReturn(Record1);

        Books response = bookService.addBook(Record1);
        assertEquals(1,response.getId());
        assertThat(response).isNotNull();
        verify(bookRepo,times(1)).save(any(Books.class));
    }
    @DisplayName("Test for addBook when No Book from that Author")
    @Test
    public void testAddBook_NoAuthor() throws Exception {
        when(categoryRepo.findByName("TextBook")).thenReturn(new Category("TextBook"));
        when(authorRepo.findByName("Author1")).thenReturn(null);
        when(bookRepo.save(any(Books.class))).thenReturn(Record1);

        Books response = bookService.addBook(Record1);
        assertEquals(1,response.getId());
        assertThat(response).isNotNull();
        verify(bookRepo,times(1)).save(any(Books.class));
    }

    @DisplayName("Test for addBook when Null Title")
    @Test
    public void testAddBook_NullTitle() throws Exception {

        Books newBook = Books.builder().id(5).authors(Arrays.asList(new Author( 5,"Sam"))).category(new Category("Horror")).available_quantity(2).issue_quantity(0).price(BigDecimal.valueOf(23)).build();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
           bookService.addBook(newBook);
        });
        assertEquals(HttpStatus.BAD_REQUEST , exception.getStatusCode());
        assertEquals("Invalid Book name: cannot be null", exception.getReason());

    }
    @DisplayName("Test for addBook when Invalid Quantity")
    @Test
    public void testAddBook_InvalidQuantity() throws Exception {

        Books newBook = Books.builder().id(5).title("Something").authors(Arrays.asList(new Author( 5,"Sam"))).category(new Category("Horror")).available_quantity(-2).issue_quantity(0).price(BigDecimal.valueOf(23)).build();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            bookService.addBook(newBook);
        });
        assertEquals(HttpStatus.BAD_REQUEST , exception.getStatusCode());
        assertEquals("Available quantity cannot be less than zero ", exception.getReason());

    }
    @DisplayName("Test for addBook when Invalid Price")
    @Test
    public void testAddBook_InvalidPrice() throws Exception {

        Books newBook = Books.builder().id(5).title("Something").authors(Arrays.asList(new Author( 5,"Sam"))).category(new Category("Horror")).available_quantity(2).issue_quantity(0).price(BigDecimal.valueOf(0)).build();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            bookService.addBook(newBook);
        });
        assertEquals(HttpStatus.BAD_REQUEST , exception.getStatusCode());
        assertEquals("Price cannot be zero or less", exception.getReason());

    }
    @DisplayName("Test for addBook when Id conflict")
    @Test
    public void testAddBook_IdConflict() throws Exception {

        Books newBook = Books.builder().id(5).title("Something").authors(Arrays.asList(new Author( 5,"Sam"))).category(new Category("Horror")).available_quantity(2).issue_quantity(0).price(BigDecimal.valueOf(23)).build();
        when(bookRepo.findById(anyInt())).thenReturn(Optional.of(newBook));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            bookService.addBook(newBook);
        });
        assertEquals(HttpStatus.CONFLICT , exception.getStatusCode());
    }
    @DisplayName("Test for addBook when Title conflict")
    @Test
    public void testAddBook_TitleConflict() throws Exception {
        List<Books> a = Arrays.asList(Record4);
        Books newBook = Books.builder().title("Something").authors(Arrays.asList(new Author( 5,"Sam"))).category(new Category("Horror")).available_quantity(2).issue_quantity(0).price(BigDecimal.valueOf(23)).build();
        when(bookRepo.findByTitle("Something")).thenReturn(a);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            bookService.addBook(newBook);
        });
        assertEquals(HttpStatus.CONFLICT , exception.getStatusCode());
    }
    @DisplayName("Test removeBook for Succsess")
    @Test
    public void testRemoveBook_Success() throws Exception {
        when(bookRepo.existsById(2)).thenReturn(true);
        bookService.removeBook(2);
        verify(bookRepo,times(1)).deleteById(2);
    }

    @DisplayName("Test removeBook for No Book")
    @Test
    public void testRemoveBook_NoBook() throws Exception {
        when(bookRepo.existsById(5)).thenReturn(false);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            bookService.removeBook(5);
        });
        assertEquals(HttpStatus.NOT_FOUND , exception.getStatusCode());
        assertEquals("Book not found", exception.getReason());
    }

    @DisplayName("Test updateBook for Valid Book")
    @Test
    public void testUpdateBook_Success() throws Exception {

        Books newBook = Books.builder().id(5).title("Something").authors(Arrays.asList(new Author( 5,"Sam"))).category(new Category("Horror")).available_quantity(2).issue_quantity(0).price(BigDecimal.valueOf(23)).build();

        when(bookRepo.findById(5)).thenReturn(Optional.of(newBook));
        when(categoryRepo.findByName("Horror")).thenReturn(newBook.getCategory());
        when(authorRepo.findByName("Sam")).thenReturn(new Author(5,"Sam"));
        when(bookRepo.save(any(Books.class))).thenReturn(newBook);

        Books response = bookService.updateBook(newBook);
        assertEquals(5,response.getId());
        assertThat(response).isNotNull();
        verify(bookRepo,times(1)).save(any(Books.class));
    }
    @DisplayName("Test updateBook for No Book")
    @Test
    public void testUpdateBook_NonExistingBook() throws Exception {

        Books newBook = Books.builder().id(5).title("Something").authors(Arrays.asList(new Author( 5,"Sam"))).category(new Category("Horror")).available_quantity(2).issue_quantity(0).price(BigDecimal.valueOf(23)).build();

        when(bookRepo.findById(5)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            bookService.updateBook(newBook);
        });
        assertEquals(HttpStatus.NOT_FOUND , exception.getStatusCode());
    }

    @DisplayName("Test updateBook for Non Existing Category")
    @Test
    public void testUpdateBook_NonExistingCategory() throws Exception {

        Books newBook = Books.builder().id(5).title("Something").authors(Arrays.asList(new Author( 5,"Sam"))).category(new Category("Horror")).available_quantity(2).issue_quantity(0).price(BigDecimal.valueOf(23)).build();

        when(bookRepo.findById(5)).thenReturn(Optional.of(newBook));
        when(categoryRepo.findByName("Horror")).thenReturn(null);
        when(authorRepo.findByName("Sam")).thenReturn(new Author(5,"Sam"));
        when(bookRepo.save(any(Books.class))).thenReturn(newBook);

        Books response = bookService.updateBook(newBook);
        assertEquals(5,response.getId());
        assertThat(response).isNotNull();
        verify(bookRepo,times(1)).save(any(Books.class));
    }
    @DisplayName("Test updateBook for Non Existing Author")
    @Test
    public void testUpdateBook_NonExistingAuthor() throws Exception {

        Books newBook = Books.builder().id(5).title("Something").authors(Arrays.asList(new Author( 5,"Sandy"))).category(new Category("Horror")).available_quantity(2).issue_quantity(0).price(BigDecimal.valueOf(23)).build();

        when(bookRepo.findById(5)).thenReturn(Optional.of(newBook));
        when(categoryRepo.findByName("Horror")).thenReturn(new Category("Horror"));
        when(authorRepo.findByName("Sandy")).thenReturn(null);
        when(bookRepo.save(any(Books.class))).thenReturn(newBook);

        Books response = bookService.updateBook(newBook);
        assertEquals(5,response.getId());
        assertThat(response).isNotNull();
        verify(bookRepo,times(1)).save(any(Books.class));
    }

    @DisplayName("Test updateBook for Null Book")
    @Test
    public void testUpdateBook_NullBook() throws Exception {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            bookService.updateBook(null);
        });
        assertEquals("Book cannot be null", exception.getReason());
        verify(bookRepo,never()).findById(anyInt());
        verify(bookRepo,never()).save(any(Books.class));
    }

    @DisplayName("Test updateBook for Invalid Title")
    @Test
    public void testUpdateBook_InvalidTitle() throws Exception {
        Books newBook = Books.builder().id(5).authors(Arrays.asList(new Author( 5,"Sandy"))).category(new Category("Horror")).available_quantity(2).issue_quantity(0).price(BigDecimal.valueOf(23)).build();
        when(bookRepo.findById(5)).thenReturn(Optional.of(newBook));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            bookService.updateBook(newBook);
        });
        assertEquals(HttpStatus.BAD_REQUEST , exception.getStatusCode());
        assertEquals("Invalid Book name: cannot be null", exception.getReason());
        verify(bookRepo,never()).save(any(Books.class));
    }

    @DisplayName("Test updateBook for NullId")
    @Test
    public void testUpdateBook_NullId() throws Exception {
        Books newBook = Books.builder().title("Something").authors(Arrays.asList(new Author( 5,"Sandy"))).category(new Category("Horror")).available_quantity(2).issue_quantity(0).price(BigDecimal.valueOf(23)).build();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            bookService.updateBook(newBook);
        });
        assertEquals(HttpStatus.BAD_REQUEST , exception.getStatusCode());
        assertEquals("Invalid Book Id: cannot be null or less than zero", exception.getReason());
        verify(bookRepo,never()).save(any(Books.class));
    }


}
