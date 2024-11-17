package com.example.bookratingsystem;


import com.example.bookratingsystem.controller.BookController;
import com.example.bookratingsystem.model.Review;
import com.example.bookratingsystem.model.dto.Author;
import com.example.bookratingsystem.model.dto.Book;
import com.example.bookratingsystem.model.dto.BookReview;
import com.example.bookratingsystem.service.BookService;
import com.example.bookratingsystem.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class BookControllerTest {

  @Mock
  private BookService bookService;

  @Mock
  private ReviewService reviewService;

  @InjectMocks
  private BookController bookController;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

    @Test
    public void testSearchBooks() {
        // Arrange
        String title = "Pride and Prejudice";
        Pageable pageable = PageRequest.of(0, 10); // Page 0, size 10
        Book mockBook = new Book(1, "MockBook1", List.of(new Author()), List.of("en"), 10);
        Book mockBook2 = new Book(2, "MockBook2", List.of(new Author()), List.of("en"), 10);
        Page<Book> mockedPage = new PageImpl<>(List.of(mockBook, mockBook2), pageable, 2);

        when(bookService.searchBooks(title, pageable)).thenReturn(mockedPage);

        // Act
        ResponseEntity<?> response = bookController.searchBooks(title, pageable);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockedPage, response.getBody());
        verify(bookService, times(1)).searchBooks(title, pageable);
    }


  @Test
  public void testAddReview() {
    Review review = new Review();
    review.setBookId(1342);
    review.setRating(4);
    review.setReviewText("A wonderful and timeless story.");
    Review mockedReview = new Review();
    when(reviewService.addReview(any(Review.class))).thenReturn(mockedReview);

    ResponseEntity<?> response = bookController.addReview(review);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(mockedReview, response.getBody());
  }

  @Test
  public void testGetBookDetails() {
    int bookId = 1342;
    Book mockBook = new Book();
    BookReview mockedBookReview = new BookReview(mockBook, 4.5, Collections.singletonList("A wonderful and timeless story."));
    when(bookService.getBookDetails(bookId)).thenReturn(mockedBookReview);

    ResponseEntity<?> response = bookController.getBookDetails(bookId);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(mockedBookReview, response.getBody());
  }
}