package com.example.bookratingsystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class BookIdRatingDto {
    Integer bookId;
    Double rating;
}
