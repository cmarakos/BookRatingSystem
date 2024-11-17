package com.example.bookratingsystem.service;

import com.example.bookratingsystem.model.Review;
import com.example.bookratingsystem.model.dto.BookIdRating;
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
    public Review addReview(Review review) {
        return reviewRepository.save(review);
    }

    // Retrieve all reviews for a specific book by its ID
    public List<Review> getReviewsByBookId(int bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    public List<BookIdRating> getTopNBookId(int n) {
        return reviewRepository.findTopBooksByAverageRating(n);

    }
}

