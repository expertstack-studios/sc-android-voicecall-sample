package com.sc.scvoicecallsample.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sc.scvoicecallsample.SCVoiceCallApp

class ScFirebaseMessagingService: FirebaseMessagingService() {

    private val securedVoiceCallSDK = SCVoiceCallApp.instance.securedVoiceCallSDK

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        securedVoiceCallSDK.savePushToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (securedVoiceCallSDK.isVoiceSDKPush(message))
        {
            securedVoiceCallSDK.processingIncomingPush(message)
        }
    }
}