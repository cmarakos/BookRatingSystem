package com.example.bookratingsystem.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BookSearchResponse {

    List<Book> books;

    @JsonProperty("books")
    public List<Book> getBooks() {
        return books;
    }

    @JsonProperty("results")
    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
