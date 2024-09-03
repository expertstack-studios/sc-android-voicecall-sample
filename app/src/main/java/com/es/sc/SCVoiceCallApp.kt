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
        securedVoiceCallSDK.initializeSDK("1ffngl8rvu7sfatcnaemc1b6es9rif61660scm4f2bgo9odvgkca")
       // securedVoiceCallSDK.initializeSDK("1c8386ngg812la83fjc9qs9rj0henhj75qqpmaav5p3dm8r5bbsc")
    }
}