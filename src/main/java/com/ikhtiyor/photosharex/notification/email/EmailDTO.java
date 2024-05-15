package com.ikhtiyor.photosharex.notification.email;

public record EmailDTO(
    String to,
    String title,
    String message
) {

    @Override
    public String toString() {
        return "EmailDTO{" +
            "to='" + to + '\'' +
            ", title='" + title + '\'' +
            ", message='" + message + '\'' +
            '}';
    }
}
