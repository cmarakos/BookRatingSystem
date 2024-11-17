package com.example.bookratingsystem.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    int id;
    String title;
    List<AuthorDto> authorDtos;
    List<String> languages;
    int downloadCount;

    @JsonProperty("downloadCount")
    public int getDownloadCount() {
        return downloadCount;
    }

    @JsonProperty("download_count")
    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }
}
