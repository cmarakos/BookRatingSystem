package com.example.bookratingsystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class BookIdRating {
    Integer bookId;
    Double rating;
}
