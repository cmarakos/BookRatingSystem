package com.example.bookratingsystem.service;

import com.example.bookratingsystem.constant.Constant;
import com.example.bookratingsystem.model.Review;
import com.example.bookratingsystem.model.dto.Book;
import com.example.bookratingsystem.model.dto.BookReview;
import com.example.bookratingsystem.model.dto.BookSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookService {

    private final ReviewService reviewService;
    private final RestTemplate restTemplate;

    public BookService(ReviewService reviewService) {
        this.reviewService = reviewService;
        this.restTemplate = new RestTemplate();
    }

    // Search for books by title using the Gutendex API
    public BookSearchResponse searchBooks(String title) {
        String url = Constant.GUTENDEX_API_URL + title;
        return restTemplate.getForObject(url, BookSearchResponse.class);
    }

    // Fetch book details by book ID, along with aggregated reviews and average rating
    public BookReview getBookDetails(int bookId) {
        // Fetch book details from Gutendex API
        String url = Constant.GUTENDEX_BOOK_DETAILS_URL + bookId;
        Book bookDetails = restTemplate.getForObject(url, Book.class);

        if (bookDetails == null) {
            throw new RuntimeException("Book not found in Gutendex API.");
        }

        // Retrieve reviews from local database
        List<Review> reviews = reviewService.getReviewsByBookId(bookId);

        // Calculate average rating
        OptionalDouble averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average();

        // Prepare response with Gutendex data and review data
        return new BookReview(
                bookDetails,
                averageRating.isPresent() ? averageRating.getAsDouble() : null,
                reviews.stream().map(Review::getReviewText).collect(Collectors.toList())
        );
    }
}

