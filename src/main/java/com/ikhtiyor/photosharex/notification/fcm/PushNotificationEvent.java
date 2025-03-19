package com.ikhtiyor.photosharex.notification.fcm;

import java.util.List;

public record PushNotificationEvent(
    String title,
    String message,
    List<String> deviceToken
) {

    public static PushNotificationEvent fromComment(NotificationEvent event) {
        return new PushNotificationEvent(
            event.title(),
            event.message(),
            event.deviceToken()
        );
    }

    public static PushNotificationEvent fromLike(NotificationEvent event) {
        return new PushNotificationEvent(
            event.title(),
            event.message(),
            event.deviceToken()
        );
    }
}
