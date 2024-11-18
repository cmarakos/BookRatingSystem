package com.example.bookratingsystem;

import com.example.bookratingsystem.model.Review;
import com.example.bookratingsystem.model.dto.*;
import com.example.bookratingsystem.service.BookService;
import com.example.bookratingsystem.service.IntegrationService;
import com.example.bookratingsystem.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private ReviewService reviewService;

    @Mock
    private IntegrationService integrationService;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchBooks_Success() {
        // Arrange
        String title = "Java";
        Pageable pageable = PageRequest.of(0, 10);
        List<BookDto> bookDtos = new ArrayList<>();
        bookDtos.add(new BookDto(1, "Java Programming", List.of(new AuthorDto("Author 1", 1950, null)), List.of("en"), 12345));
        bookDtos.add(new BookDto(2, "Advanced Java", List.of(new AuthorDto("Author 2", 1970, null)), List.of("en"), 54321));

        when(integrationService.fetchBookSearchResponse(title)).thenReturn(bookDtos);

        // Act
        Page<BookDto> result = bookService.searchBooks(title, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals("Java Programming", result.getContent().get(0).getTitle());
        verify(integrationService, times(1)).fetchBookSearchResponse(title);
    }

    @Test
    void testSearchBooks_Pagination() {
        // Arrange
        String title = "Java";
        Pageable pageable = PageRequest.of(1, 2); // Page 1 (second page), size 2
        List<BookDto> bookDtos = List.of(
                new BookDto(1, "Java Basics", List.of(new AuthorDto("Author 1", 1950, null)), List.of("en"), 1000),
                new BookDto(2, "Java Intermediate", List.of(new AuthorDto("Author 2", 1970, null)), List.of("en"), 2000),
                new BookDto(3, "Java Advanced", List.of(new AuthorDto("Author 3", 1980, null)), List.of("en"), 3000)
        );

        when(integrationService.fetchBookSearchResponse(title)).thenReturn(bookDtos);

        // Act
        Page<BookDto> result = bookService.searchBooks(title, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size()); // Only one book on the second page
        assertEquals(3, result.getTotalElements()); // Total of 3 books
        assertEquals("Java Advanced", result.getContent().get(0).getTitle()); // Book on the second page
        verify(integrationService, times(1)).fetchBookSearchResponse(title);
    }

    @Test
    void testGetBookDetails_Success() {
        // Arrange
        int bookId = 1;

        BookDto mockBookDto = new BookDto(1, "Java Programming", List.of(new AuthorDto()), List.of("en"), 99999);
        when(integrationService.fetchBookDetails(bookId)).thenReturn(mockBookDto);

        List<Review> mockReviewEntities = List.of(new Review(1L, bookId, 5, "Great book!", null));
        when(reviewService.getReviewsByBookId(bookId)).thenReturn(mockReviewEntities);

        // Act
        BookReviewDto result = bookService.getBookDetails(bookId);

        // Assert
        assertNotNull(result);
        assertEquals(mockBookDto.getId(), result.getId());
        assertEquals(1, result.getReviews().size());
        assertEquals(5.0, result.getRating());
        verify(integrationService, times(1)).fetchBookDetails(bookId);
        verify(reviewService, times(1)).getReviewsByBookId(bookId);
    }

    @Test
    void testGetBookDetails_NotFound() {
        // Arrange
        int bookId = 1;

        when(integrationService.fetchBookDetails(bookId)).thenThrow(new RuntimeException("Book not found in Gutendex API."));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookService.getBookDetails(bookId));
        assertEquals("Book not found in Gutendex API.", exception.getMessage());
        verify(integrationService, times(1)).fetchBookDetails(bookId);
        verifyNoInteractions(reviewService);
    }

    @Test
    void testGetBookDetails_NoReviews() {
        // Arrange
        int bookId = 1;

        BookDto mockBookDto = new BookDto(1, "Java Programming", List.of(new AuthorDto()), List.of("en"), 99999);
        when(integrationService.fetchBookDetails(bookId)).thenReturn(mockBookDto);

        when(reviewService.getReviewsByBookId(bookId)).thenReturn(Collections.emptyList());

        // Act
        BookReviewDto result = bookService.getBookDetails(bookId);

        // Assert
        assertNotNull(result);
        assertEquals(mockBookDto.getId(), result.getId());
        assertNull(result.getRating());
        assertTrue(result.getReviews().isEmpty());
        verify(integrationService, times(1)).fetchBookDetails(bookId);
        verify(reviewService, times(1)).getReviewsByBookId(bookId);
    }

    @Test
    void testGetTopBooks_Success() {
        // Arrange
        int n = 2;

        // Mock top book IDs and their ratings
        List<BookIdRatingDto> topBookIds = List.of(
                new BookIdRatingDto(1, 4.5),
                new BookIdRatingDto(2, 4.2)
        );

        when(reviewService.getTopNBookId(n)).thenReturn(topBookIds);

        // Mock book details for each book ID
        when(integrationService.fetchBookDetails(1)).thenReturn(new BookDto(1, "Book One", null, List.of("en"), 123));
        when(integrationService.fetchBookDetails(2)).thenReturn(new BookDto(2, "Book Two", null, List.of("en"), 456));

        // Act
        List<BookRatingDto> result = bookService.getTopBooks(n);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Book One", result.get(0).getBookName());
        assertEquals(4.5, result.get(0).getRating());
        assertEquals("Book Two", result.get(1).getBookName());
        assertEquals(4.2, result.get(1).getRating());

        verify(reviewService, times(1)).getTopNBookId(n);
        verify(integrationService, times(1)).fetchBookDetails(1);
        verify(integrationService, times(1)).fetchBookDetails(2);
    }

}
