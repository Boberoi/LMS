package com.example.lms.controller;


import com.example.lms.model.Author;
import com.example.lms.model.Books;
import com.example.lms.model.Category;
import com.example.lms.service.BookService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(BooksController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private ObjectWriter objectWriter;

    @MockBean
    private BookService bookService;

    @InjectMocks
    private BooksController booksController;

    Books Record1 = new Books(1,"Atomic Habits","How to build healthy and better habits" ,"S1R4C6" ,BigDecimal.valueOf(124) , 5 , 2 , new Category("TextBook"), Arrays.asList(new Author(1,"Author1") , new Author(2,"Author2")),  null );
    Books Record2 = new Books(2,"Thinking Fast and slow","How to create good mental health" ,"S5R2C4" ,BigDecimal.valueOf(250) , 2 , 0 , new Category("Health"), Arrays.asList(new Author(3,"Author3") , new Author(4,"Author4")),  null );
    Books Record3 = new Books(3,"It","t is a 1986 horror novel by American author" ,"S5R1C5" ,BigDecimal.valueOf(299) , 10, 5, new Category("Horror"), Arrays.asList(new Author(5,"Stephen King") , new Author(1,"Author1")),  null );

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(booksController).build();
    }

    @DisplayName("Test allBooks with success")
    @Test
    public void testGetAllBooks_Success() throws Exception {

        List<Books> records = Arrays.asList(Record1,Record2,Record3);
        when(bookService.findAllBooks()).thenReturn(records);

        ResultActions response = mockMvc.perform( MockMvcRequestBuilders
                .get("/book/all")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$" , hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].title" , is("It")));
    }

    @DisplayName("Test allBooks for No books")
    @Test
    public void testGetAllBooks_WhenNoBookAvailable() throws Exception {
        List<Books> records = Arrays.asList();
        when(bookService.findAllBooks()).thenReturn(records);

        ResultActions response = mockMvc.perform( MockMvcRequestBuilders
                        .get("/book/all")
                        .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNoContent());
    }

    @DisplayName("Test getBookByTitle with TitleFound ")
    @Test
    public void testGetBookByTitle_Success() throws Exception {
        List<Books> records = Arrays.asList(Record2);
        when(bookService.getBookByTitle(Record2.getTitle())).thenReturn(records);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/book/Thinking Fast and slow")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$" , notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id" , is(2)));
    }

    @DisplayName("Test getBookByTitle with Title NotFound ")
    @Test
    public void testGetBookByTitle_NoBook() throws Exception {
        List<Books> records = Arrays.asList();
        when(bookService.getBookByTitle("King")).thenReturn(records);
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                        .get("/book/King")
                        .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNotFound());
    }

    @DisplayName("Test getBooksByAuthor with Books Found ")
    @Test
    public void testGetBookByAuthor_Success() throws Exception {
        List<Books> sameAuthor = Arrays.asList(Record1,Record3);
        when(bookService.getBooksByAuthor("Author1")).thenReturn(sameAuthor);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/book/author/Author1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$" , notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id" , is(3)));
    }

    @DisplayName("Test getBooksByAuthor with No Books Found ")
    @Test
    public void testGetBookByAuthor_NoBook() throws Exception {
        when(bookService.getBooksByAuthor("Sam")).thenReturn(new ArrayList<>());

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/book/author/Sam")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNotFound());
    }

    @DisplayName("Test getBooksByCategory with Books Found ")
    @Test
    public void testGetBookByCategory_Success() throws Exception
    {
        List<Books> sameCategory = Arrays.asList(Record3);
        when(bookService.getBooksByCategory(Record3.getCategory().getName())).thenReturn(sameCategory) ;

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/book/category/Horror")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id" ,is(3)));
    }

    @DisplayName("Test getBooksByCategory with No Books Found ")
    @Test
    public void testGetBookByCategory_NoBook() throws Exception
    {
        when(bookService.getBooksByCategory("Science")).thenReturn(new ArrayList<>()) ;

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                        .get("/book/category/Science")
                        .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNotFound());
    }

    @DisplayName("Test getBooksById with Books Found ")
    @Test
    public void testGetBookById_Success() throws Exception
    {
        when(bookService.findById(1)).thenReturn(Record1);
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/book/id/1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$" , notNullValue()));
    }

    @DisplayName("Test addBook with Success ")
    @Test
    public void testaddBook() throws Exception {

        ArrayList<Author> authors = new ArrayList<>();
        authors.add(new Author(3,"Author3"));
        authors.add(new Author(6,"Author6"));

        Books newBook  = Books.builder()
                .id(4)
                .price(BigDecimal.valueOf(199)).description("dfsf").location("sd")
                .title("Iron Man")
                .available_quantity(20)
                .issue_quantity(0)
                .category(new Category("Fiction"))
                .authors(authors)
                .build();

        when(bookService.addBook(any(Books.class))).thenReturn(newBook);
        String content = objectMapper.writeValueAsString(newBook);
        MockHttpServletRequestBuilder mockMvcRequestBuilders = MockMvcRequestBuilders.post("/book")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                        .content(content);

        mockMvc.perform(mockMvcRequestBuilders)
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$" , notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title" , is("Iron Man")));

    }

    @DisplayName("Test addBook with Failure")
    @Test
    public void testaddBook_Failure() throws Exception {

        when(bookService.addBook(new Books())).thenReturn(null);
        String content = objectMapper.writeValueAsString(new Books());

        MockHttpServletRequestBuilder mockMvcRequestBuilders = MockMvcRequestBuilders.post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockMvcRequestBuilders)
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Test updateBook with Success")
    @Test
    public void testupdateBook_Sucess() throws Exception {

        ArrayList<Author> authors = new ArrayList<>();
        authors.add(new Author(3,"Author3"));
        authors.add(new Author(6,"Author6"));

        Books updatedBook  = Books.builder()
                .id(4)
                .price(BigDecimal.valueOf(199))
                .title("Iron Man").description("last call")
                .available_quantity(20).location("S1R3C2")
                .issue_quantity(0)
                .category(new Category("Fiction"))
                .authors(authors)
                .build();

        when(bookService.updateBook(any(Books.class))).thenReturn(updatedBook);
        String content = objectMapper.writeValueAsString(updatedBook);

        MockHttpServletRequestBuilder mockMvcRequestBuilders = MockMvcRequestBuilders.put("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockMvcRequestBuilders)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$" , notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title" , is("Iron Man")));
    }

    @DisplayName("Test updateBook with Failure")
    @Test
    public void testupdateBook_Failure() throws Exception {

        when(bookService.updateBook(new Books())).thenReturn(null);
        String content = objectMapper.writeValueAsString(new Books());

        MockHttpServletRequestBuilder mockMvcRequestBuilders = MockMvcRequestBuilders.put("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockMvcRequestBuilders)
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Test removeBook with Books Found ")
    @Test
    public void testRemoveBook_Success() throws Exception
    {
        doNothing().when(bookService).removeBook(3);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .delete("/book/id/3")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(content().string("Book deleted successfully"));

    }

    @DisplayName("Test removeBook with No Books Found ")
    @Test
    public void testRemoveBook_NoBook() throws Exception
    {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,"No Book Found")).when(bookService).removeBook(5) ;

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .delete("/book/id/5")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNotFound());
    }
}
