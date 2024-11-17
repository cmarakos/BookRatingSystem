package com.example.bookratingsystem.repository;

import com.example.bookratingsystem.model.ReviewEntity;
import com.example.bookratingsystem.model.dto.BookIdRatingDto;
import com.example.bookratingsystem.model.dto.MonthlyAverageRatingDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    List<ReviewEntity> findByBookId(int bookId);

    @Query("""
                SELECT new com.example.bookratingsystem.model.dto.BookIdRatingDto(r.bookId, AVG(r.rating))
                FROM ReviewEntity r
                GROUP BY r.bookId
                ORDER BY AVG(r.rating) DESC
            """)
    List<BookIdRatingDto> findTopBooksByAverageRating(@Param("n") int n);

    @Query("""
                SELECT new com.example.bookratingsystem.model.dto.MonthlyAverageRatingDto(
                    YEAR(r.createdAt),
                    MONTH(r.createdAt),
                    AVG(r.rating)
                )
                FROM ReviewEntity r
                WHERE r.bookId = :bookId
                GROUP BY YEAR(r.createdAt), MONTH(r.createdAt)
                ORDER BY YEAR(r.createdAt), MONTH(r.createdAt)
            """)
    List<MonthlyAverageRatingDto> findAverageRatingPerMonth(@Param("bookId") Integer bookId);

}


