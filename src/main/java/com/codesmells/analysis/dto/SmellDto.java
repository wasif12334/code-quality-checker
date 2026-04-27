package com.codesmells.analysis.dto;

public class SmellDto {

    private String type;
    private String description;
    private int lineNumber;
    private Integer endLineNumber;
    private String severity;

    public SmellDto() {
    }

    public SmellDto(String type, String description, int lineNumber, Integer endLineNumber, String severity) {
        this.type = type;
        this.description = description;
        this.lineNumber = lineNumber;
        this.endLineNumber = endLineNumber;
        this.severity = severity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Integer getEndLineNumber() {
        return endLineNumber;
    }

    public void setEndLineNumber(Integer endLineNumber) {
        this.endLineNumber = endLineNumber;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}