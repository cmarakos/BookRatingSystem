package com.example.bookratingsystem;

import com.example.bookratingsystem.constant.Constant;
import com.example.bookratingsystem.model.Review;
import com.example.bookratingsystem.model.dto.Author;
import com.example.bookratingsystem.model.dto.Book;
import com.example.bookratingsystem.model.dto.BookReview;
import com.example.bookratingsystem.model.dto.BookSearchResponse;
import com.example.bookratingsystem.service.BookService;
import com.example.bookratingsystem.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private ReviewService reviewService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchBooks_Success() {
        // Mock API response
        String title = "Java";
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = new ArrayList<>();
        books.add(new Book(1, "Java Programming", List.of(new Author("Author 1", 1950, null)), List.of("en"), 12345));
        books.add(new Book(2, "Advanced Java", List.of(new Author("Author 2", 1970, null)), List.of("en"), 54321));

        BookSearchResponse mockResponse = new BookSearchResponse();
        mockResponse.setBooks(books);
        mockResponse.setNext(null);

        when(restTemplate.getForObject(Constant.GUTENDEX_API_URL + title, BookSearchResponse.class)).thenReturn(mockResponse);

        // Test method
        Page<Book> result = bookService.searchBooks(title, pageable);

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals("Java Programming", result.getContent().get(0).getTitle());
        verify(restTemplate, times(1)).getForObject(Constant.GUTENDEX_API_URL + title, BookSearchResponse.class);
    }

    @Test
    void testSearchBooks_Pagination() {
        // Mock API response for multiple pages
        String title = "Java";
        Pageable pageable = PageRequest.of(1, 2);
        List<Book> booksPage1 = List.of(
                new Book(1, "Java Basics", List.of(new Author("Author 1", 1950, null)), List.of("en"), 1000),
                new Book(2, "Java Intermediate", List.of(new Author("Author 2", 1970, null)), List.of("en"), 2000)
        );
        List<Book> booksPage2 = List.of(
                new Book(3, "Java Advanced", List.of(new Author("Author 3", 1980, null)), List.of("en"), 3000)
        );

        BookSearchResponse mockResponsePage1 = new BookSearchResponse();
        mockResponsePage1.setBooks(booksPage1);
        mockResponsePage1.setNext(Constant.GUTENDEX_API_URL + title + "&page=2");

        BookSearchResponse mockResponsePage2 = new BookSearchResponse();
        mockResponsePage2.setBooks(booksPage2);
        mockResponsePage2.setNext(null);

        when(restTemplate.getForObject(Constant.GUTENDEX_API_URL + title, BookSearchResponse.class)).thenReturn(mockResponsePage1);
        when(restTemplate.getForObject(Constant.GUTENDEX_API_URL + title + "&page=2", BookSearchResponse.class)).thenReturn(mockResponsePage2);

        // Test method
        Page<Book> result = bookService.searchBooks(title, pageable);

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals("Java Advanced", result.getContent().get(0).getTitle());
        verify(restTemplate, times(2)).getForObject(anyString(), eq(BookSearchResponse.class));
    }

    @Test
    void testGetBookDetails_Success() {
        int bookId = 1;

        // Mock API response
        Book mockBook = new Book(1, "Java Programming", List.of(new Author()), List.of("en"), 99999);
        when(restTemplate.getForObject(Constant.GUTENDEX_BOOK_DETAILS_URL + bookId, Book.class)).thenReturn(mockBook);

        // Mock reviews
        List<Review> mockReviews = List.of(new Review(1L, bookId, 5, "Great book!"));
        when(reviewService.getReviewsByBookId(bookId)).thenReturn(mockReviews);

        // Test method
        BookReview result = bookService.getBookDetails(bookId);

        // Assertions
        assertNotNull(result);
        assertEquals(mockBook.getId(), result.getId());
        assertEquals(1, result.getReviews().size());
        assertEquals(5.0, result.getRating());
        verify(restTemplate, times(1)).getForObject(Constant.GUTENDEX_BOOK_DETAILS_URL + bookId, Book.class);
    }

    @Test
    void testGetBookDetails_NotFound() {
        int bookId = 1;

        // Mock API response
        when(restTemplate.getForObject(Constant.GUTENDEX_BOOK_DETAILS_URL + bookId, Book.class)).thenReturn(null);

        // Assertions
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookService.getBookDetails(bookId));
        assertEquals("Book not found in Gutendex API.", exception.getMessage());
    }

    @Test
    void testGetBookDetails_NoReviews() {
        int bookId = 1;

        // Mock API response
        Book mockBook = new Book(1, "Java Programming", List.of(new Author()), List.of("en"), 99999);
        when(restTemplate.getForObject(Constant.GUTENDEX_BOOK_DETAILS_URL + bookId, Book.class)).thenReturn(mockBook);

        // Mock reviews
        when(reviewService.getReviewsByBookId(bookId)).thenReturn(Collections.emptyList());

        // Test method
        BookReview result = bookService.getBookDetails(bookId);

        // Assertions
        assertNotNull(result);
        assertNull(result.getRating());
        assertTrue(result.getReviews().isEmpty());
    }
}
