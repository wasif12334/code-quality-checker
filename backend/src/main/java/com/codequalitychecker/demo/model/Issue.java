package com.codequalitychecker.demo.model;

public class Issue {

    private String type;
    private String message;
    private String severity;

    public Issue() {}

    public Issue(String type, String message, String severity) {
        this.type = type;
        this.message = message;
        this.severity = severity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}