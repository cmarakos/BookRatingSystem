package com.example.bookratingsystem;

import com.example.bookratingsystem.model.Review;
import com.example.bookratingsystem.model.dto.BookIdRating;
import com.example.bookratingsystem.model.dto.MonthlyAverageRating;
import com.example.bookratingsystem.repository.ReviewRepository;
import com.example.bookratingsystem.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class ReviewServiceTest {

  @Mock
  private ReviewRepository reviewRepository;

  @InjectMocks
  private ReviewService reviewService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testAddReview() {
    Review review = new Review(1L, 1342, 4, "A wonderful and timeless story.", null);
    when(reviewRepository.save(any(Review.class))).thenReturn(review);

    Review response = reviewService.addReview(review);

    assertEquals(review.getReviewText(), response.getReviewText());
  }

  @Test
  public void testGetReviewsByBookId() {
    int bookId = 1342;
    List<Review> mockReviews = Collections.singletonList(new Review(1L, bookId, 4, "A wonderful and timeless story.", null));
    when(reviewRepository.findByBookId(bookId)).thenReturn(mockReviews);

    List<Review> response = reviewService.getReviewsByBookId(bookId);

    assertEquals(mockReviews, response);
  }

  @Test
  void testGetTopNBookId_Success() {
    // Arrange
    int n = 3;
    List<BookIdRating> mockResults = List.of(
            new BookIdRating(84, 4.8),
            new BookIdRating(2701, 4.5),
            new BookIdRating(1513, 4.2)
    );

    when(reviewRepository.findTopBooksByAverageRating(n)).thenReturn(mockResults);

    // Act
    List<BookIdRating> results = reviewService.getTopNBookId(n);

    // Assert
    assertNotNull(results);
    assertEquals(3, results.size());
    assertEquals(84, results.get(0).getBookId());
    assertEquals(4.8, results.get(0).getRating());
    assertEquals(2701, results.get(1).getBookId());
    assertEquals(4.5, results.get(1).getRating());
    assertEquals(1513, results.get(2).getBookId());
    assertEquals(4.2, results.get(2).getRating());
  }

  @Test
  void testFindAverageRatingPerMonth_Success() {
    // Arrange
    Integer bookId = 84;
    List<MonthlyAverageRating> mockResults = List.of(
            new MonthlyAverageRating(2023, 1, 4.5),
            new MonthlyAverageRating(2023, 12, 3.8)
    );

    when(reviewRepository.findAverageRatingPerMonth(bookId)).thenReturn(mockResults);

    // Act
    List<MonthlyAverageRating> results = reviewService.findAverageRatingPerMonth(bookId);

    // Assert
    assertNotNull(results);
    assertEquals(2, results.size());
    assertEquals(2023, results.get(0).getYear());
    assertEquals(1, results.get(0).getMonth());
    assertEquals(4.5, results.get(0).getAverageRating());
    assertEquals(2023, results.get(1).getYear());
    assertEquals(12, results.get(1).getMonth());
    assertEquals(3.8, results.get(1).getAverageRating());
  }
}