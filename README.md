# Android-SonoNet-SDK

Minimum requirements: Android 5.0, APi Level 21

## How to use

Select File -> New -> New Module... at the menu panel within your project. A window will appear where you select „Import .JAR /.AAR package“. In the following dialog you have to enter the path to the SonoNet-SDK-1.0.0.aar file.

Add the SDK to the dependencies section in your build.gradle file:

```gradle
implementation project(':SonoNet-SDK')
```
Additionally there are two more dependencies needed in order to fully integrate the SDK, otherwise it won't run:

```gradle
implementation 'org.altbeacon:android-beacon-library:2.15.1'
implementation 'android.arch.persistence.room:runtime:1.1.1'
```

You also need to modify your AndroidManifest file by adding following permissions:

```android
<uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

And following service:

```android
<service android:name="com.sonobeacon.system.sonolib.BeaconInfoService"
            android:label="BeaconService" />
```
  

We will provide you with the SDK as well as the Api key and the Location Id.
Note: The location Id is an identifier used to determine a particular location/environment in which beacons can be detected.
E.g. Your retail store is equipped with 5 Sono beacons, thus only those 5 beacons (which are associated to the location) are detected by the SDK. Skip adding the location Id to the SonoNetCredentials, if you want to detected all Sono beacons.

## Inside your app

### Java

Declare a SonoNet.Control instance in your Activity/Fragment:

```android
private SonoSystem.Control control;
```
Then set up the credentials using SonoNetCredentials and initialize SonoNet. Use the builder pattern to create the SonoNet control:

```android
SonoNetCredentials credentials = new SonoNetCredentials("YOUR_API_KEY", locationID: "YOUR_LOCATION_ID");
        SonoNet.initialize(this, credentials);
        control = new SonoNet.Control.Builder(this)
                .build();
                
control.bind(this);
```

Use BeaconInfo callback to listen to beacon detections (implement SonoNet.BeaconInfoDelegate):

```android
 @Override
    public void onBeaconReceivedLinkPayload(WebLink webLink) {
        Log.d("Weblink title: ", webLink.getTitle());
    }
```

