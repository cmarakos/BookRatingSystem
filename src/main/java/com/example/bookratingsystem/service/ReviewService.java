package com.example.bookratingsystem.service;

import com.example.bookratingsystem.model.Review;
import com.example.bookratingsystem.model.dto.BookIdRatingDto;
import com.example.bookratingsystem.model.dto.MonthlyAverageRatingDto;
import com.example.bookratingsystem.model.dto.ReviewDto;
import com.example.bookratingsystem.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    // Add a new review and rating for a specific book
    public Review addReview(ReviewDto reviewDto) {
        return reviewRepository.save(
                Review.builder()
                        .bookId(reviewDto.getBookId())
                        .reviewText(reviewDto.getReviewText())
                        .rating(reviewDto.getRating())
                        .build());
    }

    // Retrieve all reviews for a specific book by its ID
    public List<Review> getReviewsByBookId(int bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    public List<BookIdRatingDto> getTopNBookId(int n) {
        return reviewRepository.findTopBooksByAverageRating(n);

    }

    public List<MonthlyAverageRatingDto> findAverageRatingPerMonth(Integer bookId) {
        return reviewRepository.findAverageRatingPerMonth(bookId);
    }
}

