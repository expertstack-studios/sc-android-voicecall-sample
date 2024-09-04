# SecuredCalls Voice SDK Integration Guide

## Prerequisites

Ensure you have the following for using the SecuredCalls Voice SDK for Android:

- Mac or Windows OS with developer mode enabled
- Android Studio with Jellyfish|2023.3.1 or above.
- Android Gradle Plugin 8.4.0 and above with Gradle version 8.7 and above
- Kotlin version 1.9.25 and above
- At least one physical Android device running Android 8 or later
- **Register on SecuredCalls.com** and obtain the `config.dat` file and secret

## Adding the SDK to Your Project

1. Open your project 'libs.versions.toml' file and add below library and plugin with versions to use in app level 'build.gradle' file

[versions]
 ```kotlin  
firebaseBom = "33.1.2"
gms = "4.4.2"
scVoice = "1.0.5"
```

[libraries]
 ```kotlin  
firebase-bom = {group = "com.google.firebase", name = "firebase-bom", version.ref = "firebaseBom"}
firebase-messaging-ktx = { group = "com.google.firebase", name = "firebase-messaging-ktx" }
sc-voice = { module = "com.securedcalls:sc-voice", version.ref = "scVoice" }
```

[plugins]
 ```kotlin  
gms = { id = "com.google.gms.google-services", version.ref = "gms" }
```

2. Open your app level build.gradle file and add below plugins and dependancies.
 ```kotlin  
plugins {  
    alias(libs.plugins.gms)  
}
 ```
 ```kotlin  
dependencies {  
    implementation(platform(libs.firebase.bom))  
    implementation(libs.firebase.messaging.ktx)  
    implementation(libs.sc.voice)  
}
 ```
 
3. Open your project level build.gradle file and add below plugins.
 ```kotlin  
plugins {
    alias(libs.plugins.gms) apply false
}
 ```
## Adding Config.dat file downloaded from SecuredCalls portal

   1. Go to your Android Studio project target.
   2. Select the **"File"** tab.
   3. Click on **"New -> Folder -> Assets Folder"** option then select 'Target source set' option and click **"Finish"**.
   4. Now you can see **'assets'** folder will be created on path **'app/src/main/assets'**
   5. Now paste the downloaded Config.dat file into assets folder.


## Adding google-services.json file 

  1. Create your app's Google Firebase project with same same package name you have provided while registering app with 'SecuredCalls' portal.
  2. Enable **'Firebase Cloud Messaging API'** in Google cloud developer console for registered app.
  3. Now goto **'Project settings'** select **'General'** Tab and scroll down, You can see your app with **'google-services.json'** file to download.
  4. Paste downloaded **'google-services.json'** file into project's app folder.


## Initialize SecuredVoiceCallSDK in Project's Application class

  1. To initialize **'SecuredVoiceCallSDK'** into you project paste below code into your Application class (e.g. SCVoiceCallApp). Replace **'xxxxxxxSECRETxxxxxxx'** with your actual API key.
  ```kotlin
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
 ```

## Creating a FirebaseMessagingService class and handling Incoming Push in Android

 Follow these steps to create a FirebaseMessagingService class in your Android project. This class allows app to receive the new firebase push message received for Voice call or PSTN calls branding and initiating the call

   #### 1. Create a new FirebaseMessagingService class

   1. Open your Android project.
   2. Right click on project source folder(e.g. notification) and click **'New -> Kotlin Class/File -> Class'** option and enter class name (e.g. ScFirebaseMessagingService)
   
   #### 2. Handling Incoming Voice SDK push in FirebaseMessagingService

   1. Open the FirebaseMessagingService class (e.g. ScFirebaseMessagingService.kt) file and paste below code.

  ```kotlin
   import com.es.sc.SCVoiceCallApp
   import com.google.firebase.messaging.FirebaseMessagingService
   import com.google.firebase.messaging.RemoteMessage
  
   class ScFirebaseMessagingService : FirebaseMessagingService() {

    private val securedVoiceCallSDK = SCVoiceCallApp.instance.securedVoiceCallSDK

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        securedVoiceCallSDK.savePushToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (securedVoiceCallSDK.isVoiceSDKPush(message)) {
            securedVoiceCallSDK.processingIncomingPush(message)
         }
       }
    }
  ```
## Adding required permissions and FirebaseMessagingService class into AndroidManifest.xml file

Add below permissions into AndroidManifest.xml file 
 ```kotlin  
<uses-feature
    android:name="android.hardware.telephony"
    android:required="false" />

<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
<uses-permission android:name="android.permission.CALL_PHONE"/>
<uses-permission android:name="android.permission.WRITE_CONTACTS"/>
<uses-permission android:name="android.permission.READ_CONTACTS"/>
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
   ```
Add below FirebaseMessagingService class (e.g. ScFirebaseMessagingService.kt) into AndroidManifest.xml file 
 ```kotlin  
<service  
  android:name=".notification.ScFirebaseMessagingService"  
  android:exported="false">  
 <intent-filter> <action android:name="com.google.firebase.MESSAGING_EVENT" />  
 </intent-filter></service>
   ```
 ## Handle SecuredVoiceCallBack interface callback for Login and Voice call session Success/Error
 ### Implement SecuredVoiceCallBack interface at Activity level 
 Copy below code for SecuredVoiceCallBack interface callbacks implement at Activity level (e.g. MainActivity.kt).
 
  ```kotlin
  class MainActivity : ComponentActivity(), SecuredVoiceCallBack {
    override fun onLoginError(message: String) {
        //Handle onLoginError callback
    }
    override fun onLoginSuccess() {
        //Handle onLoginSuccess callback
        checkPermissions()
    }
    override fun onVoiceSessionError(message: String) {
        //Handle onVoiceSessionError callback
    }
    override fun onVoiceSessionSuccess() {
        //Handle onVoiceSessionSuccess callback.
    }
  }
 ```
 ## User Login

   ### UserIdentifier, customerCareNumber and SecuredVoiceCallSDK declaration.
   UserIdentifier and customerCareNumber can be any user identifier if you are only using in-app calls. However, if you have configured both in-app and PSTN calls, the userIdentifier and customerCareNumber should be a Mobile number.
   ```kotlin
private lateinit var securedVoiceCallSDK: SecuredVoiceCallSDK
private val userIdentifier = "userIdentifier"
private val customerCareNumber = "customerCareNumber"
   ```

 ### Login Code  
  Provide userIdentifier and SecuredVoiceCallBack interface implementation to handle Login and VoiceCallSession Success/Error callbacks

   ```kotlin
private fun registerConsumerNumber(userIdentifier: String, securedVoiceCallBack: SecuredVoiceCallBack) {  
    securedVoiceCallSDK.setSecuredCallBack(securedVoiceCallBack)  
    securedVoiceCallSDK.login(userIdentifier)  
}
   ```

 ## Handle required permissions callbacks

   #### We need 1. Microphone and Phone 2. Contact and 3. Notification permissions.
   Coppy below code to check above runtime permissions into your app after successful login in previous step.

   ```kotlin
 private fun checkPermissions() {
    if (securedVoiceCallSDK.hasMicrophoneAndPhonePermission()) {
        if (securedVoiceCallSDK.hasContactPermission()) {
            if (securedVoiceCallSDK.hasNotificationPermission()) {
                securedVoiceCallSDK.registerDevicePushToken()
                securedVoiceCallSDK.createCallSession(callBack = this@MainActivity)

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
   ```

   To handle permissions callback copy below code in your Activity class.
  ```kotlin
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
 ```
   ## Re-initialize SDK session on app launch
   You can re-initialize SDK session on your app launch by adding below code in your launcher activity class.
 ```kotlin
 lifecycleScope.launch { securedVoiceCallSDK.initializeSDKOnLaunch() }
 ```
  ## To make Outbound callback to Customer care using Voice call SDK
   Copy below code for making Outbound callback to Customer care
 ```kotlin
 if (securedVoiceCallSDK.isInternetAvailable && securedVoiceCallSDK.isConsumerRegistered()) {
     securedVoiceCallSDK.startOutBoundCall(customerCareNumber)
 }
 ```
By following these steps, you’ll integrate the SecuredCalls Voice SDK effectively, meeting user privacy expectations and handling notifications efficiently.

## Implementation Time Estimates Breakdown

| **Task**                                 | **Description**                                                                    | **Estimated Time** |
|------------------------------------------|------------------------------------------------------------------------------------|--------------------|
| **1. Add the SDK to Your Project** | Add above defined libraries in build.gradle file and sync project.                       | 3 minutes          |
| **2. Add Config.dat file**       | Add Config.dat file downloaded from SecuredCalls portal into assets folder.                | 2 minutes          |
| **3. Add google-services.json file** | Add google-services.json file app folder for enabling firebase cloud messaging.        | 2 minutes          |
| **4. SDK Initialization**             | Initializing the SDK in project's application class with the provided API key.        | 2 minutes          |
| **5. Create FirebaseMessagingService class** | Create FirebaseMessaging class and handle Incoming Voice SDK push.             | 3 minutes          |
| **6. Add permissions to AndroidManifest.xml class** | Add permissions and FirebaseService class to AndroidManifest.xml        | 3 minutes          |
| **7. Handle SecuredVoiceCallBack interface callback** | Handle callbacks for Login and Voice call session.                    | 2 minutes          |
| **8. User Login**               | Add code to login by defining UserIdentifier and customerCareNumber to make outbound call.  | 5 minutes          | 
| **9. Handle permissions callbacks** | Handle permissions granted callback and register the device push with create session.   | 3 minutes          |
| **10. Re-initialize SDK session on app launch** | You can Re-initialize SDK session on app launch.                            | 2 minutes          |
| **11. Make Outbound callback to Customer care** | Provide customerCareNumber to make Outbound callback to Customer Care.      | 3 minutes          |

**Total Estimated Time: 30 minutes**
