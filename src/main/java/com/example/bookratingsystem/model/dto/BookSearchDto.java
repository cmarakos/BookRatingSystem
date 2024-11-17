package com.example.bookratingsystem.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookSearchDto {

    String next;
    List<BookDto> bookDtos;

    @JsonProperty("books")
    public List<BookDto> getBookDtos() {
        return bookDtos;
    }

    @JsonProperty("results")
    public void setBookDtos(List<BookDto> bookDtos) {
        this.bookDtos = bookDtos;
    }

}
