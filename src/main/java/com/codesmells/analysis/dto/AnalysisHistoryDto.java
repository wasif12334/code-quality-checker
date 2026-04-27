package com.codesmells.analysis.dto;

import java.time.LocalDateTime;

public class AnalysisHistoryDto {

    private Long id;
    private String codeSnippet;
    private Double score;
    private LocalDateTime date;

    public AnalysisHistoryDto() {
    }

    public AnalysisHistoryDto(Long id, String codeSnippet, Double score, LocalDateTime date) {
        this.id = id;
        this.codeSnippet = codeSnippet;
        this.score = score;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeSnippet() {
        return codeSnippet;
    }

    public void setCodeSnippet(String codeSnippet) {
        this.codeSnippet = codeSnippet;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}