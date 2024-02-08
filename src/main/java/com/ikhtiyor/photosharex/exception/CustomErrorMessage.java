package com.ikhtiyor.photosharex.exception;

import java.time.LocalDateTime;

public class CustomErrorMessage {

    private LocalDateTime timestamp;
    private String message;

    public CustomErrorMessage(
        LocalDateTime timestamp,
        String message) {
        this.timestamp = timestamp;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}
