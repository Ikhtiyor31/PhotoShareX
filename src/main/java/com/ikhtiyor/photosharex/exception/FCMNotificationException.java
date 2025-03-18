package com.ikhtiyor.photosharex.exception;

public class FCMNotificationException extends RuntimeException {


    public FCMNotificationException(String message) {
        super(message);
    }

    public FCMNotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
