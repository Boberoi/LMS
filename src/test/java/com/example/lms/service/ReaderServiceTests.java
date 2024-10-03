package com.example.lms.service;


import com.example.lms.model.Reader;
import com.example.lms.repository.ReaderRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReaderServiceTests {

    @Mock
    private ReaderRepo readerRepo;
    @InjectMocks
    private ReaderService readerService;

    private Reader reader1 = new Reader(1,"Sanjay",15,"reader1@gmail.com", "8791401775", "jkb place hyderabad",null);
    private Reader reader2 = new Reader(2,"Deepak",25,"reader2@gmail.com", "8791443775", "durgam chevru hyderabad",null);
    private Reader reader3 = new Reader(3,"Gaury",34,"reader3@gmail.com", "9371401775", "sanali sapazio hyderabad",null);
    private Reader reader4 = new Reader(4,"Gaury Sharma",12,"reader4@gmail.com", "9361401775", "sanali sapazio hyderabad",null);

    @DisplayName("Test for all Readers when some present")
    @Test
    public void testFindAllReaders_Success() throws Exception {
        List<Reader> readers = Arrays.asList(reader1,reader2,reader3,reader4);
        when(readerRepo.findAll()).thenReturn(readers);

        List<Reader> response = readerService.findAllReaders();

        assertThat(response).isNotNull();
        assertEquals(4, response.size());
        verify(readerRepo).findAll();
    }

    @DisplayName("Test for all Readers when no reader is present")
    @Test
    public void testFindAllReaders_NoReader() throws Exception {
        List<Reader> readers = Arrays.asList();
        when(readerRepo.findAll()).thenReturn(readers);

        List<Reader> response = readerService.findAllReaders();
        assertEquals(0, response.size());
        verify(readerRepo).findAll();
    }

    @DisplayName("Test for getReaderById when reader present")
    @Test
    public void testFindReaderById_Success() throws Exception {

        when(readerRepo.findById(reader1.getReader_id())).thenReturn(Optional.of(reader1));

        Reader response = readerService.getReaderById(reader1.getReader_id());
        assertEquals(1,response.getReader_id());
        assertThat(response).isNotNull();
        verify(readerRepo,times(1)).findById(reader1.getReader_id());
    }

    @DisplayName("Test for getReaderById when reader is not present")
    @Test
    public void testFindReaderById_NoReader() throws Exception {

        when(readerRepo.findById(5)).thenReturn(Optional.empty());

        Reader response = readerService.getReaderById(5);
        assertThat(response).isNull();
        verify(readerRepo,times(1)).findById(5);
    }

    @DisplayName("Test for getReaderByName when readers present")
    @Test
    public void testFindReaderByName_Success() throws Exception {
        List<Reader> readers = Arrays.asList(reader3 ,reader4);
        when(readerRepo.findByName("Gaury")).thenReturn(readers);

        List<Reader> response = readerService.getReaderByName("Gaury");
        assertEquals(2,response.size());
        assertThat(response).isNotNull();
        verify(readerRepo,times(1)).findByName("Gaury");
    }

    @DisplayName("Test for getReaderByName when reader is not present")
    @Test
    public void testFindReaderByName_NoReader() throws Exception {

        when(readerRepo.findByName("Sam")).thenReturn(new ArrayList<>());

        List<Reader> response = readerService.getReaderByName("Sam");
        assertThat(response.isEmpty());
        verify(readerRepo,times(1)).findByName("Sam");
    }

    @DisplayName("Test for addReader when valid Reader provided")
    @Test
    public void testAddReader_Success() throws Exception {

        when(readerRepo.save(reader3)).thenReturn(reader3);
//        when(readerRepo.findById(3)).thenReturn(Optional.empty());

        Reader response = readerService.addReader(reader3);
        assertEquals(3,response.getReader_id());
        verify(readerRepo,times(1)).save(reader3);
//        verify(readerRepo,times(1)).findById(3);
    }

//    @DisplayName("Test for addReader when Reader already present")
//    @Test
//    public void testAddReader_ReaderPresent() throws Exception {
////        when(readerRepo.findById(3)).thenReturn(Optional.of(reader3));
//
//        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
//            readerService.addReader(reader3);
//        });
//        assertEquals(HttpStatus.BAD_REQUEST , exception.getStatusCode());
//        assertEquals("Reader already exists", exception.getReason());
//    }

//    @DisplayName("Test addReader for null id")
//    @Test
//    public void testAddReader_NullId() throws Exception {
//        Reader r = Reader.builder().age(12).email("sbh@gmail.com").name("Kashish").pno("9374876231").build();
//        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
//            readerService.addReader(r);
//        });
//        assertEquals(HttpStatus.BAD_REQUEST , exception.getStatusCode());
//        assertEquals("Id cannot be empty", exception.getReason());
//    }

    @DisplayName("Test addReader for Invalid Name")
    @Test
    public void testAddReader_InvalidName() throws Exception {
        Reader r = Reader.builder().reader_id(7).age(12).email("sbh@gmail.com").name("Kashish23").pno("9374876231").build();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            readerService.addReader(r);
        });
        assertEquals(HttpStatus.BAD_REQUEST , exception.getStatusCode());
        assertEquals("Invalid reader name: cannot be null or contain numbers", exception.getReason());
    }

    @DisplayName("Test addReader for Invalid Phone Number")
    @Test
    public void testAddReader_InvalidPNo() throws Exception {
        Reader r = Reader.builder().reader_id(7).age(12).email("sbh@gmail.com").name("Kashish").pno("97481678438").build();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            readerService.addReader(r);
        });
        assertEquals(HttpStatus.BAD_REQUEST , exception.getStatusCode());
        assertEquals("Invalid phone number: must be numeric and up to 10 digits", exception.getReason());
    }

    @DisplayName("Test removeReader for Succsess")
    @Test
    public void testRemoveReader_Success() throws Exception {
        when(readerRepo.existsById(3)).thenReturn(true);
        String response= readerService.removeReader(3);
        assertEquals("Reader with id 3 removed" , response);
    }

    @DisplayName("Test removeReader for No Reader")
    @Test
    public void testRemoveReader_NoReader() throws Exception {
        when(readerRepo.existsById(3)).thenReturn(false);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            readerService.removeReader(3);
        });
        assertEquals(HttpStatus.NOT_FOUND , exception.getStatusCode());
        assertEquals("Reader not found", exception.getReason());
    }
    @DisplayName("Test updateReader for Valid Reader")
    @Test
    public void testUpdateReader_Success() throws Exception {
        when(readerRepo.existsById(3)).thenReturn(true);
        when(readerRepo.save(reader3)).thenReturn(reader3);

        Reader response = readerService.updateReader(reader3);
        assertEquals(3,response.getReader_id());
        verify(readerRepo,times(1)).save(reader3);
        verify(readerRepo,times(1)).existsById(3);
    }
    @DisplayName("Test updateReader for No Reader")
    @Test
    public void testUpdateReader_NoReader() throws Exception {
        when(readerRepo.existsById(3)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            readerService.updateReader(reader3);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Reader not found", exception.getReason());
    }
    @DisplayName("Test updateReader for Invalid Reader")
    @Test
    public void testUpdateReader_InvalidReader() throws Exception {

        when(readerRepo.existsById(7)).thenReturn(true);

        Reader r = Reader.builder().reader_id(7).age(12).email("sbh@gmail.com").name("Kashish23").pno("9374876231").build();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            readerService.updateReader(r);
        });
        assertEquals(HttpStatus.BAD_REQUEST , exception.getStatusCode());
        assertEquals("Invalid reader name: cannot be null or contain numbers", exception.getReason());

    }

}
