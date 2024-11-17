package com.example.bookratingsystem;

import com.example.bookratingsystem.constant.Constant;
import com.example.bookratingsystem.model.dto.BookDto;
import com.example.bookratingsystem.model.dto.BookSearchDto;
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

        BookSearchDto mockResponse = new BookSearchDto();
        mockResponse.setBookDtos(List.of(
                new BookDto(1, "Java Basics", null, List.of("en"), 100),
                new BookDto(2, "Advanced Java", null, List.of("en"), 200)
        ));
        mockResponse.setNext(null);

        when(restTemplate.getForObject(url, BookSearchDto.class)).thenReturn(mockResponse);

        // Act
        List<BookDto> bookDtos = integrationService.fetchBookSearchResponse(title);

        // Assert
        assertNotNull(bookDtos);
        assertEquals(2, bookDtos.size());
        assertEquals("Java Basics", bookDtos.get(0).getTitle());
        verify(restTemplate, times(1)).getForObject(url, BookSearchDto.class);
    }

    @Test
    void testFetchBookSearchResponse_MultiplePages() {
        // Arrange
        String title = "Java";
        String page1Url = Constant.GUTENDEX_API_URL + title;
        String page2Url = Constant.GUTENDEX_API_URL + title + "&page=2";

        BookSearchDto page1Response = new BookSearchDto();
        page1Response.setBookDtos(List.of(new BookDto(1, "Java Basics", null, List.of("en"), 100)));
        page1Response.setNext(page2Url);

        BookSearchDto page2Response = new BookSearchDto();
        page2Response.setBookDtos(List.of(new BookDto(2, "Advanced Java", null, List.of("en"), 200)));
        page2Response.setNext(null);

        when(restTemplate.getForObject(page1Url, BookSearchDto.class)).thenReturn(page1Response);
        when(restTemplate.getForObject(page2Url, BookSearchDto.class)).thenReturn(page2Response);

        // Act
        List<BookDto> bookDtos = integrationService.fetchBookSearchResponse(title);

        // Assert
        assertNotNull(bookDtos);
        assertEquals(2, bookDtos.size());
        assertEquals("Java Basics", bookDtos.get(0).getTitle());
        assertEquals("Advanced Java", bookDtos.get(1).getTitle());
        verify(restTemplate, times(1)).getForObject(page1Url, BookSearchDto.class);
        verify(restTemplate, times(1)).getForObject(page2Url, BookSearchDto.class);
    }

    @Test
    void testFetchBookSearchResponse_NotFound() {
        // Arrange
        String title = "NonExistentBook";
        String url = Constant.GUTENDEX_API_URL + title;

        when(restTemplate.getForObject(url, BookSearchDto.class)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> integrationService.fetchBookSearchResponse(title));
        assertEquals("Book not found in Gutendex API.", exception.getMessage());
        verify(restTemplate, times(1)).getForObject(url, BookSearchDto.class);
    }

    @Test
    void testFetchBookDetails_Success() {
        // Arrange
        int bookId = 1;
        String url = Constant.GUTENDEX_BOOK_DETAILS_URL + bookId;

        BookDto mockBookDto = new BookDto(bookId, "Test Book", null, List.of("en"), 150);
        when(restTemplate.getForObject(url, BookDto.class)).thenReturn(mockBookDto);

        // Act
        BookDto result = integrationService.fetchBookDetails(bookId);

        // Assert
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertEquals(150, result.getDownloadCount());
        verify(restTemplate, times(1)).getForObject(url, BookDto.class);
    }

    @Test
    void testFetchBookDetails_NotFound() {
        // Arrange
        int bookId = 999;
        String url = Constant.GUTENDEX_BOOK_DETAILS_URL + bookId;

        when(restTemplate.getForObject(url, BookDto.class)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> integrationService.fetchBookDetails(bookId));
        assertEquals("Book not found for ID: " + bookId, exception.getMessage());
        verify(restTemplate, times(1)).getForObject(url, BookDto.class);
    }
}

