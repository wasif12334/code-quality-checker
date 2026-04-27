package com.codesmells.analysis.dto;

public class LocMetricsDto {

    private int total;
    private int comments;
    private int blanks;
    private double commentRatio;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getBlanks() {
        return blanks;
    }

    public void setBlanks(int blanks) {
        this.blanks = blanks;
    }

    public double getCommentRatio() {
        return commentRatio;
    }

    public void setCommentRatio(double commentRatio) {
        this.commentRatio = commentRatio;
    }
}