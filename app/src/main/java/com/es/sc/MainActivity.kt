package com.es.sc

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.es.sc.theme.SCVoiceCallSampleTheme
import com.es.sc.voice.main.SecuredVoiceCallBack
import com.es.sc.voice.main.SecuredVoiceCallSDK
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(), SecuredVoiceCallBack {
    private lateinit var securedVoiceCallSDK: SecuredVoiceCallSDK
    private val userIdentifier = "userIdentifier"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        securedVoiceCallSDK = SCVoiceCallApp.instance.securedVoiceCallSDK
        lifecycleScope.launch { securedVoiceCallSDK.initializeSDKOnLaunch() } //Use this function to initialize SDK session on app launch
        setContent {
            setScreenContent()
        }
    }

    @Composable
    fun setScreenContent() {
        SCVoiceCallSampleTheme {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Secured Voice Call",
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(10.dp)
                )
                Button(
                    onClick = {
                        if (securedVoiceCallSDK.isInternetAvailable && !securedVoiceCallSDK.isConsumerRegistered()) {
                            registerConsumerNumber(userIdentifier, this@MainActivity)
                        }
                    },
                    modifier = Modifier,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue, contentColor = Color.White)
                ) {
                    if (securedVoiceCallSDK.isConsumerRegistered()) {
                        val registeredNumber = securedVoiceCallSDK.getRegisteredMobileNumber()
                        Text(text = "Registered No.- $registeredNumber")
                    } else {
                        Text(text = "Register Consumer Number")
                    }
                }
            }
        }
    }

    private fun registerConsumerNumber(userIdentifier: String, securedVoiceCallBack: SecuredVoiceCallBack) {
        securedVoiceCallSDK.setSecuredCallBack(securedVoiceCallBack)
        securedVoiceCallSDK.login(userIdentifier)
    }

    private fun checkPermissions() {
        if (securedVoiceCallSDK.hasMicrophoneAndPhonePermission()) {
            if (securedVoiceCallSDK.hasContactPermission()) {
                if (securedVoiceCallSDK.hasNotificationPermission()) {
                    securedVoiceCallSDK.registerDevicePushToken()
                    securedVoiceCallSDK.createCallSession(callBack = null)

                } else {
                    securedVoiceCallSDK.requestNotificationPermission(this@MainActivity)
                }
            } else {
                securedVoiceCallSDK.requestContactPermission(this@MainActivity)
            }
        } else {
            securedVoiceCallSDK.requestMicrophoneAndPhonePermission(this@MainActivity)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            securedVoiceCallSDK.PERMISSIONS_REQUEST_MICROPHONE_PHONE,
            securedVoiceCallSDK.PERMISSIONS_REQUEST_WRITE_CONTACTS,
            securedVoiceCallSDK.PERMISSIONS_REQUEST_POST_NOTIFICATIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissions()
                }
                return
            }
        }
    }

    override fun onLoginError(message: String) {
        Log.d("onLoginError", message)
    }

    override fun onLoginSuccess() {
        Log.d("onLoginSuccess", "success")
        checkPermissions()
    }

    override fun onVoiceSessionError(message: String) {
        Log.d("onVoiceSessionError", message)
    }

    override fun onVoiceSessionSuccess() {
        setContent {
            setScreenContent()
        }
    }
}