package com.ikhtiyor.photosharex.notification.email;

public record EmailDTO(
    String to,
    String title,
    String message
) {

}
