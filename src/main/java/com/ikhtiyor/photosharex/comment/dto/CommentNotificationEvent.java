package com.ikhtiyor.photosharex.comment.dto;

import com.ikhtiyor.photosharex.notification.fcm.NotificationEvent;
import java.util.List;

public record CommentNotificationEvent(
    String title,
    String message,
    List<String> deviceToken
) implements NotificationEvent {

}
