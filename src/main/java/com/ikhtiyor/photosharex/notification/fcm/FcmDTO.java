package com.ikhtiyor.photosharex.notification.fcm;

public record FcmDTO(
    String to,
    String title,
    String message,
    String deviceToken
) {

}
