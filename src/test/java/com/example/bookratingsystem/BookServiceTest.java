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
import org.springframework.web.client.RestTemplate;

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
    BookSearchResponse mockResponse = new BookSearchResponse();
    String url = Constant.GUTENDEX_API_URL + title;
    when(restTemplate.getForObject(url, BookSearchResponse.class))
        .thenReturn(mockResponse);

    // Test method
    BookSearchResponse result = bookService.searchBooks(title);

    // Assertions
    assertNotNull(result);
    verify(restTemplate, times(1)).getForObject(Constant.GUTENDEX_API_URL + title, BookSearchResponse.class);
    assertEquals(mockResponse, result);
  }

  @Test
  void testSearchBooks_Failure() {
    String title = "Java";
    when(restTemplate.getForObject(Constant.GUTENDEX_API_URL + title, BookSearchResponse.class))
        .thenReturn(null);

    // Test method
    BookSearchResponse result = bookService.searchBooks(title);

    // Assertions
    assertNull(result);
  }

  @Test
  void testGetBookDetails_Success() {
    int bookId = 1;

    // Mock API response
    Book mockBook = new Book(1, "Java Programming", List.of(new Author()), List.of("en"), 99999);
    when(restTemplate.getForObject(Constant.GUTENDEX_BOOK_DETAILS_URL + bookId, Book.class))
        .thenReturn(mockBook);

    // Mock reviews
    List<Review> mockReviews = List.of(new Review(1L,bookId, 5, "Great book!"));
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
    when(restTemplate.getForObject(Constant.GUTENDEX_BOOK_DETAILS_URL + bookId, Book.class))
        .thenReturn(null);

    // Assertions
    RuntimeException exception = assertThrows(RuntimeException.class, () -> bookService.getBookDetails(bookId));
    assertEquals("Book not found in Gutendex API.", exception.getMessage());
  }

  @Test
  void testGetBookDetails_NoReviews() {
    int bookId = 1;

    // Mock API response
    Book mockBook = new Book();
    when(restTemplate.getForObject(Constant.GUTENDEX_BOOK_DETAILS_URL + bookId, Book.class))
        .thenReturn(mockBook);

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
