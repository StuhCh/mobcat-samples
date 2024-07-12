package com.schauer.pushdemo.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings.Secure;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.messaging.FirebaseMessaging;
import com.schauer.pushdemo.PushNotificationsFirebaseMessagingService;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;

public class DeviceInstallationService {

    public static final String DEVICE_INSTALLATION_CHANNEL = "com.schauer.pushdemo/deviceinstallation";
    public static final String GET_DEVICE_ID = "getDeviceId";
    public static final String GET_DEVICE_TOKEN = "getDeviceToken";
    public static final String GET_DEVICE_PLATFORM = "getDevicePlatform";

    private Context context;
    private MethodChannel deviceInstallationChannel;

    public boolean isPlayServicesAvailable() {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }

    public DeviceInstallationService(Context context, FlutterEngine flutterEngine) {
        this.context = context;
        deviceInstallationChannel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), DEVICE_INSTALLATION_CHANNEL);
        deviceInstallationChannel.setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(MethodCall call, Result result) {
                handleDeviceInstallationCall(call, result);
            }
        });
    }

    @SuppressLint("HardwareIds")
    public String getDeviceId() {
        return Secure.getString(context.getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
    }

    public String getDeviceToken() throws Exception {
        if (!isPlayServicesAvailable()) {
            throw new Exception(getPlayServicesError());
        }

        String token = PushNotificationsFirebaseMessagingService.token;

        if (token == null || token.isEmpty()) {
            throw new Exception("Unable to resolve token for FCM.");
        }

        return token;
    }

    private void getFCMv1Token(final OnSuccessCallback onSuccess, final OnErrorCallback onError) {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token -> {
                    if (token == null || token.isEmpty()) {
                        onError.onError(new Exception("Unable to resolve FCM v1 token."));
                    } else {
                        onSuccess.onSuccess(token);
                    }
                })
                .addOnFailureListener(onError::onError);
    }

    public String getDevicePlatform() {
        return "fcmv1";
    }

    private void handleDeviceInstallationCall(MethodCall call, Result result) {
        switch (call.method) {
            case GET_DEVICE_ID:
                result.success(getDeviceId());
                break;
            case GET_DEVICE_TOKEN:
                getDeviceToken(result);
                break;
            case GET_DEVICE_PLATFORM:
                result.success(getDevicePlatform());
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private void getDeviceToken(Result result) {
        try {
            getFCMv1Token(
                    result::success,
                    error -> result.error("ERROR", error.getMessage(), error)
            );
        } catch (Exception e) {
            result.error("ERROR", e.getMessage(), e);
        }
    }

    private String getPlayServicesError() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
                return GoogleApiAvailability.getInstance().getErrorString(resultCode);
            } else {
                return "This device is not supported";
            }
        }

        return "An error occurred preventing the use of push notifications";
    }

    interface OnSuccessCallback {
        void onSuccess(String token);
    }

    interface OnErrorCallback {
        void onError(Exception error);
    }
}