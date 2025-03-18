package com.ikhtiyor.photosharex.notification.email;

public record EmailNotificationEvent(
    String to,
    String title,
    String message
) {

    @Override
    public String toString() {
        return "EmailNotificationEvent{" +
            "to='" + to + '\'' +
            ", title='" + title + '\'' +
            ", message='" + message + '\'' +
            '}';
    }
}
