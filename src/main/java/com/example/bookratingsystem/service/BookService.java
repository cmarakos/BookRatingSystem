package com.example.bookratingsystem.service;

import com.example.bookratingsystem.model.Review;
import com.example.bookratingsystem.model.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {

    private final ReviewService reviewService;
    private final IntegrationService integrationService;
    private final RestTemplate restTemplate;

    // Search for books by title using the Gutendex API
    public Page<Book> searchBooks(String title, Pageable pageable) {

        List<Book> books = integrationService.fetchBookSearchResponse(title);

        return paginate(pageable, books);
    }

    // Fetch book details by book ID, along with aggregated reviews and average rating
    public BookReview getBookDetails(int bookId) {
        // Fetch book details from Gutendex API
        Book bookDetails = integrationService.fetchBookDetails(bookId);

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

    public List<BookRatingResponse> getTopBooks(int n) {
        log.info("Fetching top {} books based on average rating.", n);

        // Step 1: Fetch top N book IDs from the repository
        List<BookIdRating> bookIdRatings = reviewService.getTopNBookId(n);

        // Step 2: Fetch book details for each book ID
        List<BookRatingResponse> topBooks = new ArrayList<>();

        bookIdRatings.forEach(
                record -> {
                    Book bookDetails = integrationService.fetchBookDetails(record.getBookId());
                    topBooks.add(
                            BookRatingResponse.builder()
                                    .bookName(bookDetails.getTitle())
                                    .rating(record.getRating())
                                    .build()
                    );
                }
        );

        return topBooks;
    }

    public List<MonthlyAverageRating> getAverageRatingPerMonth(Integer bookId) {
        log.info("Fetching monthly average rating for bookId: {}", bookId);
        return reviewService.findAverageRatingPerMonth(bookId);
    }

    private Page<Book> paginate(Pageable pageable, List<Book> bookList) {
        // Validate page and size
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero.");
        }
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number must not be less than zero.");
        }

        // Calculate start and end indices for the page
        int start = Math.min(pageNumber * pageSize, bookList.size());
        int end = Math.min(start + pageSize, bookList.size());

        // Create sublist for the requested page
        List<Book> paginatedBooks = bookList.subList(start, end);

        // Return the paginated response as a PageImpl object
        return new PageImpl<>(paginatedBooks, pageable, bookList.size());
    }
}

