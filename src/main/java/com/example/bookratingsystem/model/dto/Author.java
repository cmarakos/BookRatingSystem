package com.example.bookratingsystem.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Author {

    String name;
    Integer birthYear;
    Integer deathYear;

    @JsonProperty("birthYear")
    public Integer getBirthYear() {
        return birthYear;
    }

    @JsonProperty("deathYear")
    public Integer getDeathYear() {
        return deathYear;
    }

    @JsonProperty("birth_year")
    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    @JsonProperty("death_year")
    public void setDeathYear(Integer deathYear) {
        this.deathYear = deathYear;
    }
}

