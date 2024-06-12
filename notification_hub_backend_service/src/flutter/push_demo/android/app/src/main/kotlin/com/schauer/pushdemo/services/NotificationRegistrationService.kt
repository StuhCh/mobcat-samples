package com.schauer.pushdemo.services

import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class NotificationRegistrationService {

    companion object {
        const val NOTIFICATION_REGISTRATION_CHANNEL = "com.schauer.pushdemo/notificationregistration"
        const val REFRESH_REGISTRATION = "refreshRegistration"
    }

    private var notificationRegistrationChannel : MethodChannel

    constructor(flutterEngine: FlutterEngine) {
        notificationRegistrationChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, NOTIFICATION_REGISTRATION_CHANNEL)
    }

    fun refreshRegistration() {
        notificationRegistrationChannel.invokeMethod(REFRESH_REGISTRATION, null)
    }
}