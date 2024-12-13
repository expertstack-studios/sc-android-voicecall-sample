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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.es.sc.voice.views.compose.pages.NonDismissibleBottomDialogSheet
import com.es.sc.voice.views.compose.pages.PermissionRequiredContent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(), SecuredVoiceCallBack {
    private lateinit var securedVoiceCallSDK: SecuredVoiceCallSDK
    private val userIdentifier = "userIdentifier"
    private val callbackIdentifier = "callbackIdentifier"
    private var needToCheckPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        securedVoiceCallSDK = SCVoiceCallApp.instance.securedVoiceCallSDK
        if (securedVoiceCallSDK.isConsumerRegistered()) {
            needToCheckPermission = true
        }
        lifecycleScope.launch { securedVoiceCallSDK.initializeSDKOnLaunch(null) } //Use this function to initialize SDK session on app launch
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

                if (securedVoiceCallSDK.isConsumerRegistered()) {
                    Button(
                        onClick = {
                            if (securedVoiceCallSDK.isInternetAvailable) {
                                MainScope().launch {
                                    securedVoiceCallSDK.initializeSDKOnLaunch(object : SecuredVoiceCallBack {
                                        override fun onLoginSuccess() {
                                        }

                                        override fun onLoginError(message: String) {
                                        }

                                        override fun onVoiceSessionSuccess() {
                                            securedVoiceCallSDK.startOutBoundCall(null, callbackIdentifier)
                                        }

                                        override fun onVoiceSessionError(message: String) {
                                        }

                                        override fun onCallStarted() {
                                        }

                                        override fun onCallFailed() {
                                        }
                                    })
                                }
                            }
                        },
                        modifier = Modifier,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue, contentColor = Color.White)
                    ) {
                        Text(text = "Callback call No.- $callbackIdentifier")
                    }
                }

                val showPermissionRequiredBottomSheet by PermissionState.showPermissionRequiredBottomSheet
                val hasMicrophoneAndPhonePermission by PermissionState.hasMicrophoneAndPhonePermission
                val hasContactPermission by PermissionState.hasContactPermission
                val hasNotificationPermission by PermissionState.hasNotificationPermission

                NonDismissibleBottomDialogSheet(
                    showBottomSheet = showPermissionRequiredBottomSheet,
                    onDismissRequest = {
                        PermissionState.showPermissionRequiredBottomSheet.value = false
                    },
                ) {
                    PermissionRequiredContent(
                        modifier = Modifier,
                        hasMicrophonePhonePermission = hasMicrophoneAndPhonePermission,
                        hasContactPermission = hasContactPermission,
                        hasNotificationPermission = hasNotificationPermission,
                        onRequestMicrophonePhonePermission = {
                            if (securedVoiceCallSDK.isPermissionDeniedTwice(securedVoiceCallSDK.MICROPHONE_PERMISSION_DENIED)) {
                                securedVoiceCallSDK.openAppPermissionsSettings(this@MainActivity)
                                needToCheckPermission = true
                            } else {
                                securedVoiceCallSDK.requestMicrophoneAndPhonePermission(this@MainActivity, true)
                            }
                        },
                        onRequestContactPermission = {
                            if (securedVoiceCallSDK.isPermissionDeniedTwice(securedVoiceCallSDK.CONTACT_PERMISSION_DENIED)) {
                                securedVoiceCallSDK.openAppPermissionsSettings(this@MainActivity)
                                needToCheckPermission = true
                            } else {
                                securedVoiceCallSDK.requestContactPermission(this@MainActivity, true)
                            }
                        },
                        onRequestNotificationPermission = {
                            if (securedVoiceCallSDK.isPermissionDeniedTwice(securedVoiceCallSDK.NOTIFICATION_PERMISSION_DENIED)) {
                                securedVoiceCallSDK.openAppPermissionsSettings(this@MainActivity)
                                needToCheckPermission = true
                            } else {
                                securedVoiceCallSDK.requestNotificationPermission(this@MainActivity, true)
                            }
                        }
                    )
                }
            }
        }
    }

    private fun checkPermissionsToShowPermissionSheet() {
        if (securedVoiceCallSDK.isConsumerRegistered()) {
            if (!securedVoiceCallSDK.shouldShowPermissionSheet || securedVoiceCallSDK.areAllPermissionsGranted) {
                PermissionState.showPermissionRequiredBottomSheet.value = false
            } else {
                PermissionState.showPermissionRequiredBottomSheet.value = true
                PermissionState.hasMicrophoneAndPhonePermission.value = securedVoiceCallSDK.hasMicrophoneAndPhonePermission()
                PermissionState.hasContactPermission.value = securedVoiceCallSDK.hasContactPermission()
                PermissionState.hasNotificationPermission.value = securedVoiceCallSDK.hasNotificationPermission()
            }
        }
    }

    object PermissionState {
        var showPermissionRequiredBottomSheet = mutableStateOf(false)
        var hasMicrophoneAndPhonePermission = mutableStateOf(false)
        var hasContactPermission = mutableStateOf(false)
        var hasNotificationPermission = mutableStateOf(false)
    }

    private fun registerConsumerNumber(userIdentifier: String, securedVoiceCallBack: SecuredVoiceCallBack) {
        securedVoiceCallSDK.setSecuredCallBack(securedVoiceCallBack)
        securedVoiceCallSDK.login(userIdentifier)
    }

    private fun checkPermissions() {
        if (securedVoiceCallSDK.hasMicrophoneAndPhonePermission()) {
            if (securedVoiceCallSDK.hasContactPermission()) {
                if (securedVoiceCallSDK.hasNotificationPermission()) {
                    lifecycleScope.launch { securedVoiceCallSDK.initializeSDKOnLaunch(this@MainActivity) }
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

            securedVoiceCallSDK.PERMISSIONS_REQUEST_MICROPHONE_PHONE_POPUP -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults.contains(PackageManager.PERMISSION_DENIED)) {
                        securedVoiceCallSDK.handlePermissionDenied(securedVoiceCallSDK.MICROPHONE_PERMISSION_DENIED)
                    } else {
                        checkPermissionsToShowPermissionSheet()
                    }
                }
                return
            }

            securedVoiceCallSDK.PERMISSIONS_REQUEST_WRITE_CONTACTS_POPUP -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults.contains(PackageManager.PERMISSION_DENIED)) {
                        securedVoiceCallSDK.handlePermissionDenied(securedVoiceCallSDK.CONTACT_PERMISSION_DENIED)
                    } else {
                        checkPermissionsToShowPermissionSheet()
                    }
                }
                return
            }

            securedVoiceCallSDK.PERMISSIONS_REQUEST_POST_NOTIFICATIONS_POPUP -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults.contains(PackageManager.PERMISSION_DENIED)) {
                        securedVoiceCallSDK.handlePermissionDenied(securedVoiceCallSDK.NOTIFICATION_PERMISSION_DENIED)
                    } else {
                        checkPermissionsToShowPermissionSheet()
                    }
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

    override fun onCallStarted() {
        Log.d("onCallStarted", "success")
    }

    override fun onCallFailed() {
        Log.d("onCallStarted", "success")
    }

    override fun onVoiceSessionSuccess() {
        setContent {
            setScreenContent()
        }
        needToCheckPermission = true
    }

    override fun onResume() {
        super.onResume()
        if (needToCheckPermission) {
            checkPermissionsToShowPermissionSheet()
        }
    }
}