package com.schauer.pushdemo;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.schauer.pushdemo.services.NotificationActionService;
import com.schauer.pushdemo.services.NotificationRegistrationService;
import com.schauer.pushdemo.services.DeviceInstallationService;

import io.flutter.embedding.android.FlutterActivity;

public class MainActivity extends FlutterActivity {

    private DeviceInstallationService deviceInstallationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getFlutterEngine() != null) {
            deviceInstallationService = new DeviceInstallationService(getContext(), getFlutterEngine());
            PushNotificationsFirebaseMessagingService.notificationActionService = new NotificationActionService(getFlutterEngine());
            PushNotificationsFirebaseMessagingService.notificationRegistrationService = new NotificationRegistrationService(getFlutterEngine());
        }

        if (deviceInstallationService != null && deviceInstallationService.isPlayServicesAvailable()) {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(Task<String> task) {
                            if (!task.isSuccessful()) {
                                return;
                            }
                            PushNotificationsFirebaseMessagingService.token = task.getResult();
                            if (PushNotificationsFirebaseMessagingService.notificationRegistrationService != null) {
                                PushNotificationsFirebaseMessagingService.notificationRegistrationService.refreshRegistration();
                            }
                        }
                    });
        }

        processNotificationActions(getIntent(), true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processNotificationActions(intent, false);
    }

    private void processNotificationActions(Intent intent, boolean launchAction) {
        if (intent.hasExtra("action")) {
            String action = intent.getStringExtra("action");
            if (action != null && !action.isEmpty()) {
                if (launchAction) {
                    if (PushNotificationsFirebaseMessagingService.notificationActionService != null) {
                        PushNotificationsFirebaseMessagingService.notificationActionService.launchAction = action;
                    }
                } else {
                    if (PushNotificationsFirebaseMessagingService.notificationActionService != null) {
                        PushNotificationsFirebaseMessagingService.notificationActionService.triggerAction(action);
                    }
                }
            }
        }
    }
}
