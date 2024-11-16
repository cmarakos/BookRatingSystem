package com.example.bookratingsystem.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class BookReview extends Book {
    Double rating;
    List<String> reviews;

    public BookReview(Book book, Double rating, List<String> reviews) {
        super();
        this.setId(book.getId());
        this.setTitle(book.getTitle());
        this.setLanguages(book.getLanguages());
        this.setAuthors(book.getAuthors());
        this.setDownloadCount(book.getDownloadCount());
        this.setRating(rating);
        this.setReviews(reviews);
    }
}
