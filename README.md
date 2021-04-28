# Android-SonoNet-SDK

## Table of contents
- [Installation](#installation)
- [Inside your App](#inside-your-app)
	- [Setup](#setup)
	- [Location Services](#location-services)

## Installation

Minimum requirements: Android 5.0, API Level 21

Select File -> New -> New Module... at the menu panel within your project. A window will appear where you select „Import .JAR /.AAR package“. In the following dialog you have to enter the path to the SonoNet-SDK.aar file.

Add the SDK to the dependencies section in your build.gradle file:

```gradle
implementation project(':SonoNet-SDK-5.3.3')
```

Kotlin needs to be activated:

```gradle
implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
```

Additionally there are a few more dependencies needed in order to fully integrate the SDK, otherwise it won't run:

```gradle
    implementation 'com.google.android.material:material:1.3.0'
    implementation 'androidx.room:room-runtime:2.3.0'
    implementation 'androidx.room:room-ktx:2.3.0'
    implementation 'com.google.android.gms:play-services-location:18.0.0'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.2'
    def dynamicanimation_version = '1.0.0'
    implementation "androidx.dynamicanimation:dynamicanimation:$dynamicanimation_version"
    implementation 'org.altbeacon:android-beacon-library:2.16.4'
    implementation 'com.squareup.retrofit2:retrofit:2.6.2'
    implementation 'com.squareup.retrofit2:converter-gson:2.6.2'
    implementation 'com.squareup.retrofit2:converter-scalars:2.1.0'
    implementation 'androidx.appcompat:appcompat:1.2.0'
    implementation 'com.google.android.gms:play-services-location:18.0.0'
    implementation 'androidx.recyclerview:recyclerview:1.1.0'
    implementation 'androidx.localbroadcastmanager:localbroadcastmanager:1.0.0'
    implementation 'androidx.room:room-runtime:2.3.0'
    implementation 'androidx.room:room-ktx:2.3.0'
    def lifecycle_version = "2.3.0"
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"
    implementation "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version"
```

You also need to modify your AndroidManifest file by adding the permissions below.

Note: If you wish to use the SDK in *bluetooth-only* mode, permissions for audio recording are not required.

```gradle
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

And following service, receiver and provider inside <application/> tag:

```gradle
<service
    android:name="com.sonobeacon.system.sonolib.core.BeaconService"
    android:enabled="true"
/>
<receiver
    android:name="com.sonobeacon.system.sonolib.data.repositories.location.GeofenceBroadcastReceiver"
    android:enabled="true"
    android:exported="true"
/>
<service
    android:name="com.sonobeacon.system.sonolib.data.repositories.location.GeofenceTransitionsJobIntentService"
    android:exported="true"
    android:permission="android.permission.BIND_JOB_SERVICE"
/>
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.provider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/provider_paths"
    />
</provider>
```

We will provide you with the SDK as well as the Api key and the Location Id.
Note: The location Id is an identifier used to determine a particular location/environment in which beacons can be detected.
E.g. Your retail store is equipped with 5 Sono beacons, thus only those 5 beacons (which are associated to the location) are detected by the SDK. Skip adding the location Id to the SonoNetCredentials if you do not want to detect only certain Sono beacons within one environment.

## Inside your app

### Setup

##### Layout

We recommend to use the ui components of the SDK only on portrait mode, as this is how its intended to be used. To restrict your app to portrait mode, do this on your Activity's onCreate(...)

```kotlin
requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR
```

#### Kotlin

Declare a SonoNet.Control instance in your Activity/Fragment. The ContentView is an UI component that controls the display of content via the SDK. Mainly, the content associated to a beacon is displayed in a web view, whereby individual functions extend and enhance the user experience.
Don't use the ContentView if you want to handle the display of content by yourself.

```kotlin
private var control: SonoNet.Control? = null
```

Then set up the credentials using SonoNetCredentials and initialize SonoNet. Use the SonoNet.Control's constructor to create the SonoNet control (locationID is optional):

```kotlin
/* REPLACE WITH YOUR API KEY */
val credentials = SonoNetCredentials("YOUR_API_KEY")
SonoNet.initialize(this, credentials)

control = SonoNet.Control(
            context = this,
            contentView = contentView,          /* optional - if you want to use the app's built-in webview to show content */
            withMenu = true,                    /* optional - integration is only possible in conjunction with contentView */
            isDebugging = true,                 /* optional - if you wish to receive detailed debugging messages */
            notifyMe = true,                    /* optional - if you want to be notified when you enter predefined geographical regions */
            bluetoothOnly = false,              /* optional - if you don't need beacon detection via microphone, defaults to false */
            showMenuEntryOnlyOnly = true,       /* optional - if the initial menu entry should only be displayed once, default: true */
            menuTextColorAsHexString = "000000",/* optional - specify the desired menu item text color in hex */
            menuFontSize = 14f                  /* optional - specify the desired menu item text size in float */
            )

control?.bind(this)
```

Note: You need to handle and request app permissions by yourself. SonoNet can only be bound if permissions have been granted.
SonoNet requires permission to use both microphone and localization.
The permission to use Bluetooth is only needed for optimizing localization. Bluetooth functionality should be activated if no Location Id is assigned.
Check out the demo app for implementation.

Use BeaconInfo callback to listen to beacon detections (implement SonoNet.BeaconInfoDelegate):

```kotlin
override fun onBeaconReceivedLinkPayload(webLink: WebLink) {
    Log.d("BEACONRECEIVED", webLink.title)
}
```

Since SDK version 5.3.2, the SDK now checks for backend compatibility when initializing. This is done to avoid scenarios where a new backend structure could cause the SDK to malfunction or even crash. When an incompatible backend is detected, the initialize process is aborted and a the below function is invoked.

```kotlin
override fun onApiDeprecated() {
    // get notified when the api is deprecated
    // in this case, stop the sdk from initializing
}
```

Should you get a call to this function, contact [SonoBeacon](mailto:info@sonobeacon.com) to get the newest SDK update to match the new backend.

#### Java

Same applies to Java implementation. Check out the Java demo app.

```java
private SonoNet.Control control;
private ContentView contentView;  /* optional */
```

When implementing the SDK in java, every parameter for SonoNet.Control must be set, you can find reasonable default values below:

```java
contentView = findViewById(R.id.contentView);

/* REPLACE WITH YOUR API KEY */
SonoNetCredentials credentials = new SonoNetCredentials("YOUR_API_KEY");
SonoNet.Companion.initialize(this, credentials);

control = new SonoNet.Control(
                this,           /* context                  */
                contentView,    /* contentView              */
                true,           /* withMenu                 */
                true,           /* isDebugging              */
                true,           /* notifyMe                 */
                false,          /* bluetoothOnly            */
                true,           /* showMenuEntryOnlyOnce    */
                14f,            /* menuFontSize             */
                "000000"        /* menuTextColorAsHexString */
              );
```

BeaconInfo callback:

```java
@Override
   public void onBeaconReceivedLinkPayload(WebLink webLink) {
       Log.d("Weblink title: ", webLink.getTitle());
   }
```

### Location Services

The SDK provides the ability to send custom local push notification to the user based on the user's current position. In order to use this, the following implementations need to be made to ensure location services work even when the app is terminated. The notifications can be set up in our backend. If you plan on targeting Android Q (API Level 29) and up, you need to request ```ACCESS_BACKGROUND_LOCATION``` to make the notifications work even when the app is not in foreground.

#### Kotlin

In your Application class, define the the broadcastReceiver


```kotlin
private val broadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        intent?.action.let {
            SonoNet.regionEvent(context, it ?: "",
                intent?.getStringExtra(getString("reminder_id")) ?: "")
        }
    }
}
```
Configure the receiver, for example in your onCreate()
```kotlin
val filter = IntentFilter()
filter.addAction(EnterAction.ENTER.toString())
filter.addAction(EnterAction.EXIT.toString())
registerReceiver(broadcastReceiver, filter)
```

#### Java

Java implementation is very similar to kotlin's. Call configureReceiver() in onCreate or wherever applicable.

Application class:

```java
void configureReceiver() {
    IntentFilter filter = new IntentFilter();
    filter.addAction(EnterAction.ENTER.toString());
    filter.addAction(EnterAction.EXIT.toString());
    registerReceiver(broadcastReceiver, filter);
}

private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        SonoNet.Companion.regionEvent(context, Objects.requireNonNull(intent.getAction()),
            Objects.requireNonNull(intent.getStringExtra(getString("reminder_id"))));
    }
};
```
configureReceiver() is best called from Application's onCreate().
