package com.schauer.pushdemo.services;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class NotificationRegistrationService {

    public static final String NOTIFICATION_REGISTRATION_CHANNEL = "com.schauer.pushdemo/notificationregistration";
    public static final String REFRESH_REGISTRATION = "refreshRegistration";

    private MethodChannel notificationRegistrationChannel;

    public NotificationRegistrationService(FlutterEngine flutterEngine) {
        notificationRegistrationChannel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), NOTIFICATION_REGISTRATION_CHANNEL);
    }

    public void refreshRegistration() {
        notificationRegistrationChannel.invokeMethod(REFRESH_REGISTRATION, null);
    }
}