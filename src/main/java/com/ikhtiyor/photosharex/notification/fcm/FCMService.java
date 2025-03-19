package com.ikhtiyor.photosharex.notification.fcm;


import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.ikhtiyor.photosharex.exception.FCMNotificationException;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class FCMService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FCMService.class);

    public void sendNotification(PushNotificationEvent pushNotificationEvent) {
        try {
            Message message = Message.builder()
                .setNotification(Notification.builder()
                    .setTitle(pushNotificationEvent.title())
                    .setBody(pushNotificationEvent.message())
                    .build())
                .setToken(pushNotificationEvent.deviceToken().get(0))
                .build();
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException ex) {
            throw new FCMNotificationException("Failed to send FCM notification", ex);
        }
    }

    @Async("taskExecutor")
    public void sendNotifications(PushNotificationEvent notificationEvent) {
        MulticastMessage multicastMessage = MulticastMessage.builder()
            .addAllTokens(notificationEvent.deviceToken())
            .setNotification(Notification.builder()
                .setTitle(notificationEvent.title())
                .setBody(notificationEvent.message())
                .build())
            .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance()
                .sendEachForMulticast(multicastMessage);
            LOGGER.info("FCM Batch Response: Success - {}, Failure - {}",
                response.getSuccessCount(), response.getFailureCount());

            List<String> failedTokens = getFailedTokens(response, notificationEvent.deviceToken());
            if (!failedTokens.isEmpty()) {
                LOGGER.warn("Retrying failed notifications for tokens: {}", failedTokens);
                retryFailedNotifications(notificationEvent, failedTokens);
            }

        } catch (FirebaseMessagingException ex) {
            LOGGER.error("Bulk notification sending failed", ex);
        }

    }

    @Retryable(
        retryFor = FirebaseMessagingException.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 3000)
    )
    public void retryFailedNotifications(
        PushNotificationEvent event, List<String> failedTokens
    ) throws FirebaseMessagingException {
        MulticastMessage retryMessage = MulticastMessage.builder()
            .addAllTokens(failedTokens)
            .setNotification(Notification.builder()
                .setTitle(event.title())
                .setBody(event.message())
                .build())
            .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance()
                .sendEachForMulticast(retryMessage);
            LOGGER.info("Retry Response: Success - {}, Failure - {}",
                response.getSuccessCount(), response.getFailureCount());

            List<String> stillFailedTokens = getFailedTokens(response, failedTokens);
            if (!stillFailedTokens.isEmpty()) {
                LOGGER.error("Final failure for tokens: {}", stillFailedTokens);
            }
        } catch (FirebaseMessagingException ex) {
            LOGGER.error("Retry attempt failed", ex);
            throw ex;
        }
    }

    private List<String> getFailedTokens(BatchResponse response, List<String> deviceTokens) {
        return response.getResponses().stream()
            .filter(sendResponse -> !sendResponse.isSuccessful())
            .map(sendResponse -> deviceTokens.get(response.getResponses().indexOf(sendResponse)))
            .collect(Collectors.toList());
    }

    @Recover
    public void recover(
        FirebaseMessagingException ex,
        PushNotificationEvent event,
        List<String> failedTokens
    ) {
        LOGGER.error("Final retry failed for tokens: {}, error: {}", failedTokens, ex.getMessage());
    }
}
