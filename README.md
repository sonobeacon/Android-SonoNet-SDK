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
implementation project(':SonoNet-SDK')
```

Kotlin needs to be activated:

```gradle
implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
```

Additionally there are four more dependencies needed in order to fully integrate the SDK, otherwise it won't run:

```gradle
implementation 'com.google.android.material:material:1.0.0'
implementation 'androidx.room:room-runtime:2.2.0'
implementation 'com.google.android.gms:play-services-location:17.0.0'

// workaround for altbeacon library crashes
implementation 'androidx.localbroadcastmanager:localbroadcastmanager:1.0.0'
```

You also need to modify your AndroidManifest file by adding following permissions:

```gradle
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

And following service:

```gradle
<service 
android:name="com.sonobeacon.system.sonolib.BeaconService"
android:enabled="true"
/>
```
  

We will provide you with the SDK as well as the Api key and the Location Id.
Note: The location Id is an identifier used to determine a particular location/environment in which beacons can be detected.
E.g. Your retail store is equipped with 5 Sono beacons, thus only those 5 beacons (which are associated to the location) are detected by the SDK. Skip adding the location Id to the SonoNetCredentials if you do not want to detect only certain Sono beacons within one environment.

## Inside your app

### Setup

#### Kotlin

Declare a SonoNet.Control instance in your Activity/Fragment. The ContentView is an UI component that controls the display of content via the SDK. Mainly, the content associated to a beacon is displayed in a web view, whereby individual functions extend and enhance the user experience.
Don't use the ContentView if you want to handle the display of content by yourself.

```kotlin
private var control: SonoNet.Control? = null  
```

Then set up the credentials using SonoNetCredentials and initialize SonoNet. Use the SonoNet.Control's constructor to create the SonoNet control (locationID is optional):

```kotlin
val credentials = SonoNetCredentials("YOUR_API_KEY", "YOUR_LOCATION_ID")
SonoNet.initialize(this, credentials)

control = SonoNet.Control(
			context = this,			
            contentView = contentView,		/* optional - if you want to use the app's built-in webview to show content */
            withMenu = true,			/* optional - integration is only possible in conjunction with contentView */
            isDebugging = true,			/* optional - if you wish to receive detailed debugging messages */
            notifyMe = true,			/* optional - if you want to be notified when you enter predefined geographical regions */
            bluetoothOnly = false		/* optional - if you don't need beacon detection via microphone, defaults to false */
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
        Log.d("TAG", webLink.title)
    }
```

#### Java

Same applies to Java implementation. Check out the Java demo app.

```java
private SonoNet.Control control;
private ContentView contentView;  /* optional */
```

When implementing the SDK in java, every parameter for SonoNet.Control must be set, you can find reasonable default values below: 

```java
contentView = findViewById(R.id.contentView);

/* REPLACE WITH YOUR CREDENTIALS */
SonoNetCredentials credentials = new SonoNetCredentials("YOUR_API_KEY", "YOUR_LOCATION_ID"); 
SonoNet.Companion.initialize(this, credentials);

control = new SonoNet.Control(
				this,			/* context 		*/
				contentView,		/* ContentView 		*/
				true,			/* withMenu		*/
				true,			/* isDebugging		*/
				true,			/* notifyMe		*/
				false			/* bluetoothOnly	*/
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

The SDK provides the ability to send custom local push notification to the user based on the user's current position. In order to use this, the following implementations need to be made to ensure location services work even when the app is terminated. The notifications can be set up in our backend.

#### Kotlin

In your Application class, define the the broadcastReceiver


```kotlin
private val broadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context?.let {
                SonoNet.regionEvent(it, intent)
            }
        }
    }
```
Configure the receiver, for example in your onCreate()
```kotlin
val filter = IntentFilter()
filter.addAction(RegionState.ENTER.toString())
filter.addAction(RegionState.EXIT.toString())
filter.addAction("ble_enter")
filter.addAction("ble_exit")
registerReceiver(broadcastReceiver, filter)
```

To get this right, these are the correct import statements:
```kotlin
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.Context
import android.content.IntentFilter
import com.sonobeacon.system.sonolib.RegionState
```

#### Java

Java implementation very similar to kotlin's. Call configureReceiver() in onCreate or wherever applicable.

Application class:

```java
   void configureReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(RegionState.ENTER.toString());
        filter.addAction(RegionState.EXIT.toString());
        filter.addAction("ble_enter");
        filter.addAction("ble_exit");
        registerReceiver(broadcastReceiver, filter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SonoNet.Companion.regionEvent(context, intent);
        }
    };
```

Imports:
```java
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
```
