package com.schauer.pushdemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.schauer.pushdemo.services.NotificationActionService;
import com.schauer.pushdemo.services.NotificationRegistrationService;

public class PushNotificationsFirebaseMessagingService extends FirebaseMessagingService {

    public static String token = null;
    public static NotificationRegistrationService notificationRegistrationService = null;
    public static NotificationActionService notificationActionService = null;

    @Override
    public void onCreate() {
        super.onCreate();
        //createNotificationChannel();
    }

    @Override
    public void onNewToken(String token) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                PushNotificationsFirebaseMessagingService.token = token;
                if (notificationRegistrationService != null) {
                    notificationRegistrationService.refreshRegistration();
                }
            }
        });
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        if (message.getData() != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    String action = message.getData().get("action");
                    if (action != null && notificationActionService != null) {
                        notificationActionService.triggerAction(action);
                    }
                }
            });
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "PushNotifications";
            String descriptionText = "Channel for Push Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("push_notifications_channel", name, importance);
            channel.setDescription(descriptionText);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
