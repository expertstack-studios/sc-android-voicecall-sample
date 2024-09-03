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
 ```kotlin  
[versions]
firebaseBom = "33.1.2"
gms = "4.4.2"
scVoice = "1.0.5"

[libraries]
firebase-bom = {group = "com.google.firebase", name = "firebase-bom", version.ref = "firebaseBom"}
firebase-messaging-ktx = { group = "com.google.firebase", name = "firebase-messaging-ktx" }
sc-voice = { module = "com.securedcalls:sc-voice", version.ref = "scVoice" }

[plugins]
gms = { id = "com.google.gms.google-services", version.ref = "gms" }
```

2. Open your project level build.gradle file and add below plugins.
 ```kotlin  
plugins {
    alias(libs.plugins.gms) apply false
}
 ```

## Adding required permissions into AndroidManifest.xml file

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

## Adding Config.dat file downloaded from SecuredCalls portal

   1. Go to your Android Studio project target.
   2. Select the **"File"** tab.
   3. Click on **"New -> Folder -> Assets Folder"** option then select 'Target source set' option and click **"Finish"**.
   4. Now you can see **'assets'** folder will be created on path **'app/src/man/assets'**
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
  
