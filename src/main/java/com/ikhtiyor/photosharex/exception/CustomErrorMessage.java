package com.ikhtiyor.photosharex.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public record CustomErrorMessage(HttpStatus httpStatus, String message, LocalDateTime timestamp) {

    public static CustomErrorMessage of(HttpStatus httpStatus, String message) {
        return new CustomErrorMessage(httpStatus, message, LocalDateTime.now());
    }
}
