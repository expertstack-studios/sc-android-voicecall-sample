package com.es.sc

import android.app.Application
import com.es.sc.voice.main.SecuredVoiceCallSDK
import com.es.sc.voice.main.models.ScSDKConfigModel

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
        securedVoiceCallSDK.initializeSDK(ScSDKConfigModel("**xxxxxxxSECRETxxxxxxx**", false))
    }
}
