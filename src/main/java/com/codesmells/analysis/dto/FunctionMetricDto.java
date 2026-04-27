package com.codesmells.analysis.dto;

public class FunctionMetricDto {

    private String name;
    private int lineNumber;
    private int endLineNumber;
    private int linesOfCode;
    private int cyclomaticComplexity;
    private int parameters;

    public FunctionMetricDto() {
    }

    public FunctionMetricDto(String name, int lineNumber, int endLineNumber, int linesOfCode, int cyclomaticComplexity, int parameters) {
        this.name = name;
        this.lineNumber = lineNumber;
        this.endLineNumber = endLineNumber;
        this.linesOfCode = linesOfCode;
        this.cyclomaticComplexity = cyclomaticComplexity;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getEndLineNumber() {
        return endLineNumber;
    }

    public void setEndLineNumber(int endLineNumber) {
        this.endLineNumber = endLineNumber;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(int linesOfCode) {
        this.linesOfCode = linesOfCode;
    }

    public int getCyclomaticComplexity() {
        return cyclomaticComplexity;
    }

    public void setCyclomaticComplexity(int cyclomaticComplexity) {
        this.cyclomaticComplexity = cyclomaticComplexity;
    }

    public int getParameters() {
        return parameters;
    }

    public void setParameters(int parameters) {
        this.parameters = parameters;
    }
}