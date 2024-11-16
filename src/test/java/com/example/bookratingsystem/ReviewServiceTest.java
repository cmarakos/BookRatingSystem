package com.example.bookratingsystem;

import com.example.bookratingsystem.model.Review;
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
    Review review = new Review(1L, 1342, 4, "A wonderful and timeless story.");
    when(reviewRepository.save(any(Review.class))).thenReturn(review);

    Review response = reviewService.addReview(review);

    assertEquals(review.getReviewText(), response.getReviewText());
  }

  @Test
  public void testGetReviewsByBookId() {
    int bookId = 1342;
    List<Review> mockReviews = Collections.singletonList(new Review(1L, bookId, 4, "A wonderful and timeless story."));
    when(reviewRepository.findByBookId(bookId)).thenReturn(mockReviews);

    List<Review> response = reviewService.getReviewsByBookId(bookId);

    assertEquals(mockReviews, response);
  }
}