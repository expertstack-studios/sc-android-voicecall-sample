package com.es.sc.notification

import android.util.Log
import com.es.sc.SCVoiceCallApp
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class ScFirebaseMessagingService: FirebaseMessagingService() {

    private val securedVoiceCallSDK = SCVoiceCallApp.instance.securedVoiceCallSDK

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        securedVoiceCallSDK.savePushToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("onMessageReceived", message.data.toString())
        if (securedVoiceCallSDK.isVoiceSDKPush(message))
        {
            securedVoiceCallSDK.processingIncomingPush(message)
        }
    }
}