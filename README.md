# Android-SonoNet-SDK

Minimum requirements: Android 5.0, API Level 21

## How to use

Select File -> New -> New Module... at the menu panel within your project. A window will appear where you select „Import .JAR /.AAR package“. In the following dialog you have to enter the path to the SonoNet-SDK-4.0.aar file.

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
implementation 'org.altbeacon:android-beacon-library:2.15.1'
implementation 'androidx.room:room-runtime:2.2.0'
implementation 'com.google.android.gms:play-services-location:17.0.0'
```

You also need to modify your AndroidManifest file by adding following permissions:

```java
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

And following service:

```java
<service android:name="com.sonobeacon.system.sonolib.BeaconInfoService"
            android:label="BeaconService" />
```
  

We will provide you with the SDK as well as the Api key and the Location Id.
Note: The location Id is an identifier used to determine a particular location/environment in which beacons can be detected.
E.g. Your retail store is equipped with 5 Sono beacons, thus only those 5 beacons (which are associated to the location) are detected by the SDK. Skip adding the location Id to the SonoNetCredentials if you do not want to detect only certain Sono beacons within one environment.

## Inside your app

### Java

Declare a SonoNet.Control instance in your Activity/Fragment. The ContentView is an UI component that controls the display of content via the SDK. Mainly, the content associated to a beacon is displayed in a web view, whereby individual functions extend and enhance the user experience.
Don't use the ContentView if you want to handle the display of content by yourself.

```java
private SonoNet.Control control;
private ContentView contentView;  /* optional */
```
Then set up the credentials using SonoNetCredentials and initialize SonoNet. Use the builder pattern to create the SonoNet control (locationID is optional):

```java
contentView = findViewById(R.id.contentView);
        
SonoNetCredentials credentials = new SonoNetCredentials("YOUR_API_KEY", "YOUR_LOCATION_ID");  /* REPLACE WITH YOUR CREDENTIALS */
SonoNet.initialize(this, credentials);

control = new SonoNet.Control.Builder(this)
                .withContentView(contentView)   /* optional */
                .withMenu(true)                     /* optional - integration is only possible in conjunction with contentView */
                .isDebugging(true)                  /* optional */
                .notifyMe(true)                     /* optional - if you want to be notified when you enter predefined geographical regions */
                .build();
```

Note: You need to handle and request app permissions by yourself. SonoNet can only be bound if permissions have been granted.
SonoNet requires permission to use both microphone and localization.
The permission to use Bluetooth is only needed for optimizing localization. Bluetooth functionality should be activated if no Location Id is assigned.
Check out the demo app for implementation.

Use BeaconInfo callback to listen to beacon detections (implement SonoNet.BeaconInfoDelegate):

```java
@Override
   public void onBeaconReceivedLinkPayload(WebLink webLink) {
       Log.d("Weblink title: ", webLink.getTitle());
   }
```

### Kotlin

Same applies to Kotlin implementation. Check out the Kotlin demo app.

```kotlin
private var control: SonoNet.Control? = null  
```

```kotlin
val credentials = SonoNetCredentials("YOUR_API_KEY", "YOUR_LOCATION_ID")
SonoNet.initialize(this, credentials)

control = SonoNet.Control.Builder(this)
            .withContentView(contentView)
            .withMenu(true)
            .isDebugging(true)
            .notifyMe(true)
            .build()
            
control?.bind(this)
```

BeaconInfo callback:

```kotlin
override fun onBeaconReceivedLinkPayload(p0: WebLink?) {
        p0?.let { 
            Log.d("TAG", it.title)
        }
    }
```

