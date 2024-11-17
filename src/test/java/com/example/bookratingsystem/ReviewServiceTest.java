package com.example.bookratingsystem;

import com.example.bookratingsystem.model.ReviewEntity;
import com.example.bookratingsystem.model.dto.BookIdRatingDto;
import com.example.bookratingsystem.model.dto.MonthlyAverageRatingDto;
import com.example.bookratingsystem.model.dto.ReviewDto;
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
    ReviewEntity reviewEntity = new ReviewEntity(1L, 1342, 4, "A wonderful and timeless story.", null);
    ReviewDto reviewDto = new ReviewDto(1342, 4, "A wonderful and timeless story.");
    when(reviewRepository.save(any(ReviewEntity.class))).thenReturn(reviewEntity);

    ReviewEntity response = reviewService.addReview(reviewDto);

    assertEquals(reviewEntity.getReviewText(), response.getReviewText());
  }

  @Test
  public void testGetReviewsByBookId() {
    int bookId = 1342;
    List<ReviewEntity> mockReviewEntities = Collections.singletonList(new ReviewEntity(1L, bookId, 4, "A wonderful and timeless story.", null));
    when(reviewRepository.findByBookId(bookId)).thenReturn(mockReviewEntities);

    List<ReviewEntity> response = reviewService.getReviewsByBookId(bookId);

    assertEquals(mockReviewEntities, response);
  }

  @Test
  void testGetTopNBookId_Success() {
    // Arrange
    int n = 3;
    List<BookIdRatingDto> mockResults = List.of(
            new BookIdRatingDto(84, 4.8),
            new BookIdRatingDto(2701, 4.5),
            new BookIdRatingDto(1513, 4.2)
    );

    when(reviewRepository.findTopBooksByAverageRating(n)).thenReturn(mockResults);

    // Act
    List<BookIdRatingDto> results = reviewService.getTopNBookId(n);

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
    List<MonthlyAverageRatingDto> mockResults = List.of(
            new MonthlyAverageRatingDto(2023, 1, 4.5),
            new MonthlyAverageRatingDto(2023, 12, 3.8)
    );

    when(reviewRepository.findAverageRatingPerMonth(bookId)).thenReturn(mockResults);

    // Act
    List<MonthlyAverageRatingDto> results = reviewService.findAverageRatingPerMonth(bookId);

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