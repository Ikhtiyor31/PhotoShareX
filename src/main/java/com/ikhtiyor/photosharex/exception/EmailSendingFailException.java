package com.ikhtiyor.photosharex.exception;

public class EmailSendingFailException extends RuntimeException {

    public EmailSendingFailException(String message) {
        super(message);
    }

    public EmailSendingFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
