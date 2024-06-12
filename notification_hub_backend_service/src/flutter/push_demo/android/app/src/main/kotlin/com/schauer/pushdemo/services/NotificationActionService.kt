package com.schauer.pushdemo.services

import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class NotificationActionService {
    companion object {
        const val NOTIFICATION_ACTION_CHANNEL = "com.schauer.pushdemo/notificationaction"
        const val TRIGGER_ACTION = "triggerAction"
        const val GET_LAUNCH_ACTION = "getLaunchAction"
    }

    private var notificationActionChannel : MethodChannel
    var launchAction : String? = null

    constructor(flutterEngine: FlutterEngine) {
        notificationActionChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, NOTIFICATION_ACTION_CHANNEL)
        notificationActionChannel.setMethodCallHandler { call, result -> handleNotificationActionCall(call, result) }
    }

    fun triggerAction(action: String) {
        notificationActionChannel.invokeMethod(TRIGGER_ACTION, action)
    }

    private fun handleNotificationActionCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            GET_LAUNCH_ACTION -> {
                result.success(launchAction)
            }
            else -> {
                result.notImplemented()
            }
        }
    }
}