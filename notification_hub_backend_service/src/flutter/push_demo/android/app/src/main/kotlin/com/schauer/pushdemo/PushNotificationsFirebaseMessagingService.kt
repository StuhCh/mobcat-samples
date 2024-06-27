package com.schauer.pushdemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.schauer.pushdemo.services.NotificationActionService
import com.schauer.pushdemo.services.NotificationRegistrationService

class PushNotificationsFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        var token: String? = null
        var notificationRegistrationService: NotificationRegistrationService? = null
        var notificationActionService: NotificationActionService? = null
    }

    override fun onCreate() {
        super.onCreate()
        //createNotificationChannel()
    }

    override fun onNewToken(token: String) {
        Handler(Looper.getMainLooper()).post {
            Companion.token = token
            notificationRegistrationService?.refreshRegistration()
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.data.let {
            Handler(Looper.getMainLooper()).post {
                it["action"]?.let { it1 -> notificationActionService?.triggerAction(it1) }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "PushNotifications"
            val descriptionText = "Channel for Push Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("push_notifications_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}