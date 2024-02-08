package com.ikhtiyor.photosharex.exception;

import java.time.LocalDateTime;

public class CustomErrorMessage {

    private int statusCode;
    private LocalDateTime timestamp;
    private String message;

    public CustomErrorMessage(
        int statusCode,
        LocalDateTime timestamp,
        String message) {

        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}
