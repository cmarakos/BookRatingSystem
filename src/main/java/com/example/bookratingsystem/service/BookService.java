package com.example.bookratingsystem.service;

import com.example.bookratingsystem.constant.Constant;
import com.example.bookratingsystem.model.Review;
import com.example.bookratingsystem.model.dto.Book;
import com.example.bookratingsystem.model.dto.BookReview;
import com.example.bookratingsystem.model.dto.BookSearchResponse;
import io.micrometer.common.util.StringUtils;
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
    private final RestTemplate restTemplate;

    // Search for books by title using the Gutendex API
    public Page<Book> searchBooks(String title, Pageable pageable) {

        return fetchPaginatedResponse(title, pageable);
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

    private Page<Book> fetchPaginatedResponse(String title, Pageable pageable) {
        String url = Constant.GUTENDEX_API_URL + title;
        List<Book> bookList = new ArrayList<>();

        while (StringUtils.isNotBlank(url)) {
            BookSearchResponse response = restTemplate.getForObject(url, BookSearchResponse.class);
            if (response == null || response.getBooks() == null) {
                throw new RuntimeException("Book not found in Gutendex API.");
            }
            bookList.addAll(response.getBooks());
            url = response.getNext(); // Update URL for the next page, or null if no more results
        }

        return paginate(pageable, bookList);
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

