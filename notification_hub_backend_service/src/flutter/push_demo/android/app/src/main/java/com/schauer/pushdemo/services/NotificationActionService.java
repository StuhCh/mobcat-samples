package com.schauer.pushdemo.services;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class NotificationActionService {

    public static final String NOTIFICATION_ACTION_CHANNEL = "com.schauer.pushdemo/notificationaction";
    public static final String TRIGGER_ACTION = "triggerAction";
    public static final String GET_LAUNCH_ACTION = "getLaunchAction";

    private MethodChannel notificationActionChannel;
    public String launchAction = null;

    public NotificationActionService(FlutterEngine flutterEngine) {
        notificationActionChannel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), NOTIFICATION_ACTION_CHANNEL);
        notificationActionChannel.setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(MethodCall call, MethodChannel.Result result) {
                handleNotificationActionCall(call, result);
            }
        });
    }

    public void triggerAction(String action) {
        notificationActionChannel.invokeMethod(TRIGGER_ACTION, action);
    }

    private void handleNotificationActionCall(MethodCall call, MethodChannel.Result result) {
        if (GET_LAUNCH_ACTION.equals(call.method)) {
            result.success(launchAction);
        } else {
            result.notImplemented();
        }
    }
}
