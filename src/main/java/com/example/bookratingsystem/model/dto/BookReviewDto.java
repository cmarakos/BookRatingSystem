package com.example.bookratingsystem.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class BookReviewDto extends BookDto {
    Double rating;
    List<String> reviews;

    public BookReviewDto(BookDto bookDto, Double rating, List<String> reviews) {
        super();
        this.setId(bookDto.getId());
        this.setTitle(bookDto.getTitle());
        this.setLanguages(bookDto.getLanguages());
        this.setAuthorDtos(bookDto.getAuthorDtos());
        this.setDownloadCount(bookDto.getDownloadCount());
        this.setRating(rating);
        this.setReviews(reviews);
    }
}
