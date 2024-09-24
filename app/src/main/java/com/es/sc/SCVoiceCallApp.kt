package com.es.sc

import android.app.Application
import com.es.sc.voice.main.SecuredVoiceCallSDK

class SCVoiceCallApp: Application() {

    companion object {
        lateinit var instance: SCVoiceCallApp
    }
    init {
        instance = this
    }

    val securedVoiceCallSDK: SecuredVoiceCallSDK = SecuredVoiceCallSDK(this)

    override fun onCreate() {
        super.onCreate()
        securedVoiceCallSDK.initializeSDK("**xxxxxxxSECRETxxxxxxx**")
    }
}
