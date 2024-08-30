package com.sc.scvoicecallsample

import android.app.Application
import android.content.Context
import com.es.sc.voice.main.SecuredVoiceCallSDK

class SCVoiceCallApp: Application() {

    companion object {
        lateinit var instance: SCVoiceCallApp
        val context: Context
            get() {
                return instance
            }
    }
    init {
        instance = this
    }

    val securedVoiceCallSDK = SecuredVoiceCallSDK(context)

    override fun onCreate() {
        super.onCreate()
        securedVoiceCallSDK.initializeSDK("1ffngl8rvu7sfatcnaemc1b6es9rif61660scm4f2bgo9odvgkca")
    }
}