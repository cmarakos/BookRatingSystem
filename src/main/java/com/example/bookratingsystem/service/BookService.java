package com.example.bookratingsystem.service;

import com.example.bookratingsystem.model.ReviewEntity;
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
    public Page<BookDto> searchBooks(String title, Pageable pageable) {

        List<BookDto> bookDtos = integrationService.fetchBookSearchResponse(title);

        return paginate(pageable, bookDtos);
    }

    // Fetch book details by book ID, along with aggregated reviews and average rating
    public BookReviewDto getBookDetails(int bookId) {
        // Fetch book details from Gutendex API
        BookDto bookDtoDetails = integrationService.fetchBookDetails(bookId);

        // Retrieve reviews from local database
        List<ReviewEntity> reviewEntities = reviewService.getReviewsByBookId(bookId);

        // Calculate average rating
        OptionalDouble averageRating = reviewEntities.stream()
                .mapToInt(ReviewEntity::getRating)
                .average();

        // Prepare response with Gutendex data and review data
        return new BookReviewDto(
                bookDtoDetails,
                averageRating.isPresent() ? averageRating.getAsDouble() : null,
                reviewEntities.stream().map(ReviewEntity::getReviewText).collect(Collectors.toList())
        );
    }

    public List<BookRatingDto> getTopBooks(int n) {
        log.info("Fetching top {} books based on average rating.", n);

        // Step 1: Fetch top N book IDs from the repository
        List<BookIdRatingDto> bookIdRatingDtos = reviewService.getTopNBookId(n);

        // Step 2: Fetch book details for each book ID
        List<BookRatingDto> topBooks = new ArrayList<>();

        bookIdRatingDtos.forEach(
                record -> {
                    BookDto bookDtoDetails = integrationService.fetchBookDetails(record.getBookId());
                    topBooks.add(
                            BookRatingDto.builder()
                                    .bookName(bookDtoDetails.getTitle())
                                    .rating(record.getRating())
                                    .build()
                    );
                }
        );

        return topBooks;
    }

    public List<MonthlyAverageRatingDto> getAverageRatingPerMonth(Integer bookId) {
        log.info("Fetching monthly average rating for bookId: {}", bookId);
        return reviewService.findAverageRatingPerMonth(bookId);
    }

    private Page<BookDto> paginate(Pageable pageable, List<BookDto> bookDtoList) {
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
        int start = Math.min(pageNumber * pageSize, bookDtoList.size());
        int end = Math.min(start + pageSize, bookDtoList.size());

        // Create sublist for the requested page
        List<BookDto> paginatedBookDtos = bookDtoList.subList(start, end);

        // Return the paginated response as a PageImpl object
        return new PageImpl<>(paginatedBookDtos, pageable, bookDtoList.size());
    }
}

