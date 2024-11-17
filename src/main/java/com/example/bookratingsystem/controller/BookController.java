package com.example.bookratingsystem.controller;

import com.example.bookratingsystem.model.Review;
import com.example.bookratingsystem.model.dto.Book;
import com.example.bookratingsystem.service.BookService;
import com.example.bookratingsystem.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final ReviewService reviewService;

    public BookController(BookService bookService, ReviewService reviewService) {
        this.bookService = bookService;
        this.reviewService = reviewService;
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Book>> searchBooks(
            @RequestParam String title,
            Pageable pageable) {
        return ResponseEntity.ok(bookService.searchBooks(title, pageable));
    }

    @PostMapping("/review")
    public ResponseEntity<?> addReview(@Valid @RequestBody Review review) {
        return ResponseEntity.ok(reviewService.addReview(review));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<?> getBookDetails(@PathVariable int bookId) {
        return ResponseEntity.ok(bookService.getBookDetails(bookId));
    }
}

