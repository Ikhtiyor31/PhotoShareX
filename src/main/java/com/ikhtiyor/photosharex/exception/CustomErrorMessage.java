package com.ikhtiyor.photosharex.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public class CustomErrorMessage {

    private HttpStatus httpStatus;
    private String message;
    private LocalDateTime timestamp;

    public CustomErrorMessage(
        HttpStatus httpStatus,
        String message,
        LocalDateTime timestamp
    ) {
        this.httpStatus = httpStatus;
        this.timestamp = timestamp;
        this.message = message;
    }

    public static CustomErrorMessage of(HttpStatus httpStatus, String message) {
        return new CustomErrorMessage(httpStatus, message, LocalDateTime.now());
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
