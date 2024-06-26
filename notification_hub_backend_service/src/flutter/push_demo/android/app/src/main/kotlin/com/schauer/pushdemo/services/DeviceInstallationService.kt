package com.schauer.pushdemo.services

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings.Secure
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import com.schauer.pushdemo.PushNotificationsFirebaseMessagingService
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

@SuppressLint("HardwareIds")
class DeviceInstallationService {

    companion object {
        const val DEVICE_INSTALLATION_CHANNEL = "com.schauer.pushdemo/deviceinstallation"
        const val GET_DEVICE_ID = "getDeviceId"
        const val GET_DEVICE_TOKEN = "getDeviceToken"
        const val GET_DEVICE_PLATFORM = "getDevicePlatform"
    }

    private var context: Context
    private var deviceInstallationChannel : MethodChannel

    val playServicesAvailable
        get() = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS

    constructor(context: Context, flutterEngine: FlutterEngine) {
        this.context = context
        deviceInstallationChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, DEVICE_INSTALLATION_CHANNEL)
        deviceInstallationChannel.setMethodCallHandler { call, result -> handleDeviceInstallationCall(call, result) }
    }

    fun getDeviceId() : String
        = Secure.getString(context.applicationContext.contentResolver, Secure.ANDROID_ID)

    fun getDeviceToken() : String {
        if(!playServicesAvailable) {
            throw Exception(getPlayServicesError())
        }

        val token = PushNotificationsFirebaseMessagingService.token

        if (token.isNullOrBlank()) {
            throw Exception("Unable to resolve token for FCM.")
        }

        return token
    }

    // Function to retrieve FCM v1 token asynchronously
    private fun getFCMv1Token(onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    if (token.isNullOrEmpty()) {
                        onError(Exception("Unable to resolve FCM v1 token."))
                    } else {
                        onSuccess(token)
                    }
                }
                .addOnFailureListener { e ->
                    onError(e)
                }
    }

    fun getDevicePlatform() : String = "fcmv1"

    private fun handleDeviceInstallationCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            GET_DEVICE_ID -> {
                result.success(getDeviceId())
            }
            GET_DEVICE_TOKEN -> {
                getDeviceToken(result)
            }
            GET_DEVICE_PLATFORM -> {
                result.success(getDevicePlatform())
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    // Adjust your existing method to work with Flutter's MethodChannel
    private fun getDeviceToken(result: MethodChannel.Result) {
        try {
            getFCMv1Token(
                    onSuccess = { token ->
                        result.success(token)
                    },
                    onError = { error ->
                        result.error("ERROR", error.message, error)
                    }
            )
        } catch (e: Exception) {
            result.error("ERROR", e.message, e)
        }
    }

    private fun getPlayServicesError(): String {
        val resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)

        if (resultCode != ConnectionResult.SUCCESS) {
            return if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)){
                GoogleApiAvailability.getInstance().getErrorString(resultCode)
            } else {
                "This device is not supported"
            }
        }

        return "An error occurred preventing the use of push notifications"
    }
}