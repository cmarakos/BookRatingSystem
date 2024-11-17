package com.example.bookratingsystem;

import com.example.bookratingsystem.model.ReviewEntity;
import com.example.bookratingsystem.model.dto.BookDto;
import com.example.bookratingsystem.model.dto.BookReviewDto;
import com.example.bookratingsystem.service.BookService;
import com.example.bookratingsystem.service.IntegrationService;
import com.example.bookratingsystem.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookDtoServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @MockBean
    private IntegrationService integrationService;

    @MockBean
    private ReviewService reviewService;

    @BeforeEach
    void setup() {
    }

    @Test
    void testSearchBooks_Success() {
        // Arrange
        String title = "Test";
        Pageable pageable = PageRequest.of(0, 10);
        List<BookDto> bookDtos = List.of(
                new BookDto(1, "Test Book", null, List.of("en"), 150)
        );

        when(integrationService.fetchBookSearchResponse(title)).thenReturn(bookDtos);

        // Act
        Page<BookDto> result = bookService.searchBooks(title, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Book", result.getContent().get(0).getTitle());
        verify(integrationService, times(1)).fetchBookSearchResponse(title);
    }

    @Test
    void testSearchBooks_Pagination_MultiplePages() {
        // Arrange
        String title = "Test";
        Pageable pageable = PageRequest.of(0, 2);
        List<BookDto> bookDtos = List.of(
                new BookDto(1, "Book 1", null, List.of("en"), 100),
                new BookDto(2, "Book 2", null, List.of("en"), 200)
        );

        when(integrationService.fetchBookSearchResponse(title)).thenReturn(bookDtos);

        // Act
        Page<BookDto> result = bookService.searchBooks(title, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Book 1", result.getContent().get(0).getTitle());
        assertEquals("Book 2", result.getContent().get(1).getTitle());
        verify(integrationService, times(1)).fetchBookSearchResponse(title);
    }

    @Test
    void testSearchBooks_NotFound() {
        // Arrange
        String title = "InvalidBook";
        when(integrationService.fetchBookSearchResponse(title))
                .thenThrow(new RuntimeException("No books found"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookService.searchBooks(title, PageRequest.of(0, 10)));
        assertEquals("No books found", exception.getMessage());
        verify(integrationService, times(1)).fetchBookSearchResponse(title);
    }

    @Test
    void testGetBookDetails_Success() {
        // Arrange
        int bookId = 1;
        BookDto mockBookDto = new BookDto(bookId, "Test Book", null, List.of("en"), 150);
        List<ReviewEntity> mockReviewEntities = List.of(
                new ReviewEntity(1L, bookId, 5, "Great book", null),
                new ReviewEntity(2L, bookId, 4, "Enjoyable read", null)
        );

        when(integrationService.fetchBookDetails(bookId)).thenReturn(mockBookDto);
        when(reviewService.getReviewsByBookId(bookId)).thenReturn(mockReviewEntities);

        // Act
        BookReviewDto result = bookService.getBookDetails(bookId);

        // Assert
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertEquals(4.5, result.getRating());
        assertEquals(2, result.getReviews().size());
        verify(integrationService, times(1)).fetchBookDetails(bookId);
        verify(reviewService, times(1)).getReviewsByBookId(bookId);
    }

    @Test
    void testGetBookDetails_NotFound() {
        // Arrange
        int bookId = 1;
        when(integrationService.fetchBookDetails(bookId))
                .thenThrow(new RuntimeException("Book not found"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookService.getBookDetails(bookId));
        assertEquals("Book not found", exception.getMessage());
        verify(integrationService, times(1)).fetchBookDetails(bookId);
        verifyNoInteractions(reviewService);
    }

    @Test
    void testGetBookDetails_NoReviews() {
        // Arrange
        int bookId = 1;
        BookDto mockBookDto = new BookDto(bookId, "Test Book", null, List.of("en"), 150);

        when(integrationService.fetchBookDetails(bookId)).thenReturn(mockBookDto);
        when(reviewService.getReviewsByBookId(bookId)).thenReturn(List.of());

        // Act
        BookReviewDto result = bookService.getBookDetails(bookId);

        // Assert
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertNull(result.getRating());
        assertTrue(result.getReviews().isEmpty());
        verify(integrationService, times(1)).fetchBookDetails(bookId);
        verify(reviewService, times(1)).getReviewsByBookId(bookId);
    }

}
