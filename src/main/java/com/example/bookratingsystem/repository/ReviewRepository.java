package com.example.bookratingsystem.repository;

import com.example.bookratingsystem.model.Review;
import com.example.bookratingsystem.model.dto.BookIdRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByBookId(int bookId);

    @Query("""
                SELECT new com.example.bookratingsystem.model.dto.BookIdRating(r.bookId, AVG(r.rating))
                FROM Review r
                GROUP BY r.bookId
                ORDER BY AVG(r.rating) DESC
            """)
    List<BookIdRating> findTopBooksByAverageRating(@Param("n") int n);
}

