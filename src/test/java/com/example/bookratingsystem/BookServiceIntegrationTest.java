package com.example.bookratingsystem;

import com.example.bookratingsystem.model.Review;
import com.example.bookratingsystem.model.dto.BookReview;
import com.example.bookratingsystem.service.BookService;
import com.example.bookratingsystem.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest
class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @MockBean
    private ReviewService reviewService;

    @BeforeEach
    void setup() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void testSearchBooks_Success() {
        // Mock API response
        String apiResponse = """
            {
                "results": [
                    {
                        "id": 1,
                        "title": "Test Book",
                        "authors": [{"name": "Author Name", "birth_year": 1970, "death_year": null}],
                        "languages": ["en"],
                        "download_count": 150
                    }
                        ],
                        "next": null
            }
        """;

        mockServer.expect(requestTo("https://gutendex.com/books?search=Test"))
                .andRespond(withSuccess(apiResponse, MediaType.APPLICATION_JSON));

        // Call the service method
        var result = bookService.searchBooks("Test", org.springframework.data.domain.PageRequest.of(0, 10));

        // Verify the response
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Book", result.getContent().get(0).getTitle());
    }

    @Test
    void testSearchBooks_Pagination_MultiplePages() {
        // Mock API responses for multiple pages
        String page1Response = """
                    {
                        "results": [
                            {
                                "id": 1,
                                "title": "Book 1",
                                "authors": [{"name": "Author 1", "birth_year": 1970, "death_year": null}],
                                "languages": ["en"],
                                "download_count": 100
                            }
                        ],
                        "next": "https://gutendex.com/books?search=Test&page=2"
                    }
                """;

        String page2Response = """
                    {
                        "results": [
                            {
                                "id": 2,
                                "title": "Book 2",
                                "authors": [{"name": "Author 2", "birth_year": 1980, "death_year": null}],
                                "languages": ["en"],
                                "download_count": 200
                            }
                        ],
                        "next": null
                    }
                """;

        mockServer.expect(requestTo("https://gutendex.com/books?search=Test"))
                .andRespond(withSuccess(page1Response, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo("https://gutendex.com/books?search=Test&page=2"))
                .andRespond(withSuccess(page2Response, MediaType.APPLICATION_JSON));

        // Call the service method
        var result = bookService.searchBooks("Test", org.springframework.data.domain.PageRequest.of(0, 2));

        // Verify the response
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("Book 1", result.getContent().get(0).getTitle());
        assertEquals("Book 2", result.getContent().get(1).getTitle());
    }

    @Test
    void testSearchBooks_NotFound() {
        // Mock 404 response
        mockServer.expect(requestTo("https://gutendex.com/books?search=InvalidBook"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        // Expect an exception
        assertThrows(HttpClientErrorException.NotFound.class, () -> bookService.searchBooks("InvalidBook", org.springframework.data.domain.PageRequest.of(0, 10)));
    }

    @Test
    void testGetBookDetails_Success() {
        // Mock book details response
        String bookDetailsResponse = """
            {
                "id": 1,
                "title": "Test Book",
                "authors": [{"name": "Author Name", "birth_year": 1970, "death_year": null}],
                "languages": ["en"],
                "download_count": 150
            }
        """;

        // Mock reviews from the review service
        List<Review> mockReviews = List.of(
                new Review(1L, 1, 5, "Great book"),
                new Review(2L, 1, 4, "Enjoyable read")
        );

        when(reviewService.getReviewsByBookId(1)).thenReturn(mockReviews);

        mockServer.expect(requestTo("https://gutendex.com/books/1"))
                .andRespond(withSuccess(bookDetailsResponse, MediaType.APPLICATION_JSON));

        // Call the service method
        BookReview bookReview = bookService.getBookDetails(1);

        // Verify the response
        assertNotNull(bookReview);
        assertEquals("Test Book", bookReview.getTitle());
        assertEquals(4.5, bookReview.getRating());
        assertEquals(2, bookReview.getReviews().size());
    }

    @Test
    void testGetBookDetails_NotFound() {
        // Mock 404 response
        mockServer.expect(requestTo("https://gutendex.com/books/1"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        // Expect an exception
        assertThrows(HttpClientErrorException.NotFound.class, () -> bookService.getBookDetails(1));
    }

    @Test
    void testGetBookDetails_InternalServerError() {
        // Mock 500 response
        mockServer.expect(requestTo("https://gutendex.com/books/1"))
                .andRespond(withServerError());

        // Expect an exception
        assertThrows(HttpServerErrorException.InternalServerError.class, () -> bookService.getBookDetails(1));
    }
}
