package com.example.bookratingsystem.service;


import com.example.bookratingsystem.constant.Constant;
import com.example.bookratingsystem.model.dto.Book;
import com.example.bookratingsystem.model.dto.BookSearchResponse;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class IntegrationService {

    private final RestTemplate restTemplate;

    /**
     * Fetches book search response from the Gutendex API for the given title.
     * Results are cached with the key based on the title.
     *
     * @param title the book title to search for.
     * @return the BookSearchResponse containing the search results.
     */
    @Cacheable(value = "bookSearchResponse", key = "#title")
    public List<Book> fetchBookSearchResponse(String title) {
        log.info("Fetching books with title : {}", title);
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

        return bookList;
    }

    /**
     * Fetches book details from the Gutendex API for the given book ID.
     * Results are cached with the key based on the book ID.
     *
     * @param bookId the ID of the book.
     * @return the Book details.
     */
    @Cacheable(value = "bookDetails", key = "#bookId")
    public Book fetchBookDetails(int bookId) {
        log.info("Fetching book details for bookId: {}", bookId);
        String url = Constant.GUTENDEX_BOOK_DETAILS_URL + bookId;
        Book book = restTemplate.getForObject(url, Book.class);
        if (book == null) {
            throw new RuntimeException("Book not found for ID: " + bookId);
        }
        return book;
    }
}

