package com.example.bookratingsystem;

import com.example.bookratingsystem.model.Review;
import com.example.bookratingsystem.model.dto.BookReview;
import com.example.bookratingsystem.model.dto.BookSearchResponse;
import com.example.bookratingsystem.service.BookService;
import com.example.bookratingsystem.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
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
                ]
            }
        """;

    mockServer.expect(requestTo("https://gutendex.com/books?search=Test"))
        .andRespond(withSuccess(apiResponse, MediaType.APPLICATION_JSON));

    // Call the service method
    BookSearchResponse response = bookService.searchBooks("Test");

    // Verify the response
    assertNotNull(response);
    assertEquals(1, response.getBooks().size());
    assertEquals("Test Book", response.getBooks().get(0).getTitle());
  }

  @Test
  void testSearchBooks_NotFound() {
    // Mock 404 response
    mockServer.expect(requestTo("https://gutendex.com/books?search=InvalidBook"))
        .andRespond(withStatus(HttpStatus.NOT_FOUND));

    // Expect an exception
    assertThrows(HttpClientErrorException.NotFound.class, () -> bookService.searchBooks("InvalidBook"));
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
        new Review(1L, 1, 5,"Great book"),
        new Review(2L, 1, 4,"Enjoyable read")
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
