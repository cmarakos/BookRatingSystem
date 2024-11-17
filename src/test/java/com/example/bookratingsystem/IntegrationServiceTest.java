package com.example.bookratingsystem;

import com.example.bookratingsystem.constant.Constant;
import com.example.bookratingsystem.model.dto.Book;
import com.example.bookratingsystem.model.dto.BookSearchResponse;
import com.example.bookratingsystem.service.IntegrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IntegrationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private IntegrationService integrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchBookSearchResponse_Success() {
        // Arrange
        String title = "Java";
        String url = Constant.GUTENDEX_API_URL + title;

        BookSearchResponse mockResponse = new BookSearchResponse();
        mockResponse.setBooks(List.of(
                new Book(1, "Java Basics", null, List.of("en"), 100),
                new Book(2, "Advanced Java", null, List.of("en"), 200)
        ));
        mockResponse.setNext(null);

        when(restTemplate.getForObject(url, BookSearchResponse.class)).thenReturn(mockResponse);

        // Act
        List<Book> books = integrationService.fetchBookSearchResponse(title);

        // Assert
        assertNotNull(books);
        assertEquals(2, books.size());
        assertEquals("Java Basics", books.get(0).getTitle());
        verify(restTemplate, times(1)).getForObject(url, BookSearchResponse.class);
    }

    @Test
    void testFetchBookSearchResponse_MultiplePages() {
        // Arrange
        String title = "Java";
        String page1Url = Constant.GUTENDEX_API_URL + title;
        String page2Url = Constant.GUTENDEX_API_URL + title + "&page=2";

        BookSearchResponse page1Response = new BookSearchResponse();
        page1Response.setBooks(List.of(new Book(1, "Java Basics", null, List.of("en"), 100)));
        page1Response.setNext(page2Url);

        BookSearchResponse page2Response = new BookSearchResponse();
        page2Response.setBooks(List.of(new Book(2, "Advanced Java", null, List.of("en"), 200)));
        page2Response.setNext(null);

        when(restTemplate.getForObject(page1Url, BookSearchResponse.class)).thenReturn(page1Response);
        when(restTemplate.getForObject(page2Url, BookSearchResponse.class)).thenReturn(page2Response);

        // Act
        List<Book> books = integrationService.fetchBookSearchResponse(title);

        // Assert
        assertNotNull(books);
        assertEquals(2, books.size());
        assertEquals("Java Basics", books.get(0).getTitle());
        assertEquals("Advanced Java", books.get(1).getTitle());
        verify(restTemplate, times(1)).getForObject(page1Url, BookSearchResponse.class);
        verify(restTemplate, times(1)).getForObject(page2Url, BookSearchResponse.class);
    }

    @Test
    void testFetchBookSearchResponse_NotFound() {
        // Arrange
        String title = "NonExistentBook";
        String url = Constant.GUTENDEX_API_URL + title;

        when(restTemplate.getForObject(url, BookSearchResponse.class)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> integrationService.fetchBookSearchResponse(title));
        assertEquals("Book not found in Gutendex API.", exception.getMessage());
        verify(restTemplate, times(1)).getForObject(url, BookSearchResponse.class);
    }

    @Test
    void testFetchBookDetails_Success() {
        // Arrange
        int bookId = 1;
        String url = Constant.GUTENDEX_BOOK_DETAILS_URL + bookId;

        Book mockBook = new Book(bookId, "Test Book", null, List.of("en"), 150);
        when(restTemplate.getForObject(url, Book.class)).thenReturn(mockBook);

        // Act
        Book result = integrationService.fetchBookDetails(bookId);

        // Assert
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertEquals(150, result.getDownloadCount());
        verify(restTemplate, times(1)).getForObject(url, Book.class);
    }

    @Test
    void testFetchBookDetails_NotFound() {
        // Arrange
        int bookId = 999;
        String url = Constant.GUTENDEX_BOOK_DETAILS_URL + bookId;

        when(restTemplate.getForObject(url, Book.class)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> integrationService.fetchBookDetails(bookId));
        assertEquals("Book not found for ID: " + bookId, exception.getMessage());
        verify(restTemplate, times(1)).getForObject(url, Book.class);
    }
}

