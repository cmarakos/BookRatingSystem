package com.example.bookratingsystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyAverageRatingDto {
    private Integer year;
    private Integer month;
    private Double averageRating;
}
