package com.ikhtiyor.photosharex.like.dto;

import com.ikhtiyor.photosharex.notification.fcm.NotificationEvent;
import java.util.List;

public record LikeNotificationEvent(
    String title,
    String message,
    List<String> deviceToken
) implements NotificationEvent {

}
