package com.ikhtiyor.photosharex.notification.fcm;

import java.util.List;

public interface NotificationEvent {

    String title();

    String message();

    List<String> deviceToken();
}
