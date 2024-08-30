package com.sc.scvoicecallsample

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.es.sc.voice.main.SecuredVoiceCallBack
import com.es.sc.voice.main.SecuredVoiceCallSDK
import com.sc.scvoicecallsample.ui.theme.SCVoiceCallSampleTheme

class MainActivity : ComponentActivity(), SecuredVoiceCallBack {
    private lateinit var securedVoiceCallSDK: SecuredVoiceCallSDK
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        securedVoiceCallSDK = SCVoiceCallApp.instance.securedVoiceCallSDK

      //  enableEdgeToEdge()
        setContent {
            SCVoiceCallSampleTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Secured Voice Call",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp)
                    )

                    Button(
                        onClick = {
                                registerConsumerNumber("917020599233", this@MainActivity)
                        },
                        modifier = Modifier,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue, contentColor = Color.White)
                    ) {
                        Text(text = "Register Consumer Number")
                    }
                }
            }
        }
    }

    private fun checkPermissions()
    {
        //Check microphone permission
        if (securedVoiceCallSDK.hasMicrophoneAndPhonePermission()) {
            //Microphone permission is given
            //Check Contact permission
            if (securedVoiceCallSDK.hasContactPermission()) {
                //Contact permission is given
                //Check Notification permission
                if (securedVoiceCallSDK.hasNotificationPermission()) {
                    //Notification permission is given
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

        when(requestCode)
        {
            securedVoiceCallSDK.PERMISSIONS_REQUEST_MICROPHONE_PHONE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Microphone permission is given
                    //Check Contact permission
                    if (securedVoiceCallSDK.hasContactPermission()) {
                        //Contact permission is given
                        //Check Notification permission
                        if (securedVoiceCallSDK.hasNotificationPermission()) {
                            securedVoiceCallSDK.registerDevicePushToken()
                            securedVoiceCallSDK.createCallSession(callBack = null)

                        } else {
                            securedVoiceCallSDK.requestNotificationPermission(this@MainActivity)
                        }
                    } else {
                        securedVoiceCallSDK.requestContactPermission(this@MainActivity)
                    }
                }
                return
            }

            securedVoiceCallSDK.PERMISSIONS_REQUEST_WRITE_CONTACTS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Contact permission is given
                    //Check Notification permission
                    if (securedVoiceCallSDK.hasNotificationPermission()) {
                        securedVoiceCallSDK.registerDevicePushToken()
                        securedVoiceCallSDK.createCallSession(callBack = null)

                    } else {
                        securedVoiceCallSDK.requestNotificationPermission(this@MainActivity)
                    }
                }
                return
            }

            securedVoiceCallSDK.PERMISSIONS_REQUEST_POST_NOTIFICATIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Notification permission is given
                    securedVoiceCallSDK.registerDevicePushToken()
                    securedVoiceCallSDK.createCallSession(callBack = null)
                }
                return
            }
        }
    }

    private fun registerConsumerNumber(mobileNumber: String, securedVoiceCallBack: SecuredVoiceCallBack){
        securedVoiceCallSDK.setSecuredCallBack(securedVoiceCallBack)
        securedVoiceCallSDK.login(mobileNumber)
    }

    override fun onError(message: String) {
        Log.d("onError", message)
    }

    override fun onLoginSuccess() {
        Log.d("onLoginSuccess", "success")
        checkPermissions()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SCVoiceCallSampleTheme {
        Greeting("Android")
    }
}