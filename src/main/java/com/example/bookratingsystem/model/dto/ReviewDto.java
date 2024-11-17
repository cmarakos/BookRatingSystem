package com.example.bookratingsystem.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    @NotNull
    private int bookId;

    @Min(0)
    @Max(5)
    private int rating;

    @Size(max = 3000)
    private String reviewText;
}
