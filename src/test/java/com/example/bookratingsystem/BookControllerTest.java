package com.example.bookratingsystem;


import com.example.bookratingsystem.controller.BookController;
import com.example.bookratingsystem.model.dto.Book;
import com.example.bookratingsystem.model.Review;
import com.example.bookratingsystem.model.dto.BookReview;
import com.example.bookratingsystem.model.dto.BookSearchResponse;
import com.example.bookratingsystem.service.BookService;
import com.example.bookratingsystem.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
    String title = "Pride and Prejudice";
    BookSearchResponse mockedResponse = new BookSearchResponse();
    when(bookService.searchBooks(title)).thenReturn(mockedResponse);

    ResponseEntity<?> response = bookController.searchBooks(title);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(mockedResponse, response.getBody());
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