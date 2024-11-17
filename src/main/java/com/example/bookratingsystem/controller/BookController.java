package com.example.bookratingsystem.controller;

import com.example.bookratingsystem.model.dto.BookDto;
import com.example.bookratingsystem.model.dto.BookRatingDto;
import com.example.bookratingsystem.model.dto.MonthlyAverageRatingDto;
import com.example.bookratingsystem.model.dto.ReviewDto;
import com.example.bookratingsystem.service.BookService;
import com.example.bookratingsystem.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Page<BookDto>> searchBooks(
            @RequestParam String title,
            Pageable pageable) {
        return ResponseEntity.ok(bookService.searchBooks(title, pageable));
    }

    @PostMapping("/review")
    public ResponseEntity<?> addReview(@Valid @RequestBody ReviewDto reviewDto) {
        return ResponseEntity.ok(reviewService.addReview(reviewDto));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<?> getBookDetails(@PathVariable int bookId) {
        return ResponseEntity.ok(bookService.getBookDetails(bookId));
    }


    @GetMapping("/top")
    public ResponseEntity<List<BookRatingDto>> getTopBooks(@RequestParam int n) {
        return ResponseEntity.ok(bookService.getTopBooks(n));
    }

    @GetMapping("/{bookId}/average-rating-per-month")
    public ResponseEntity<List<MonthlyAverageRatingDto>> getAverageRatingPerMonth(@PathVariable Integer bookId) {
        List<MonthlyAverageRatingDto> averages = bookService.getAverageRatingPerMonth(bookId);
        return ResponseEntity.ok(averages);
    }

}

