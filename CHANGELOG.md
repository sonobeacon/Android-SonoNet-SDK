Changelog
=====================

## 5.3.3 - 1 (Dec, 2021)
- update project settings
- update readme to explain how to integrate sdk into newer Android Studio versions.

## 5.3.3 (Apr, 2021)
- improved ble and geofence detections
- restructured dependencies


## 5.2.0 (Dec, 2020)
- better storage management
- new animations in various places, haptic feedback
- SDK now supports displaying a map that indicates the users location based on recent beacon detections
- overall stability improvements

## 5.2.0 (Sep, 2020)
- color and size of text in side menu can now be set when initializing the SDK
- overall stability improvements

## 5.1.0 (Sep, 2020)
- backend moved to [admin.sonobeacon.com](https://admin.sonobeacon.com/)
- detection of new content while content is being displayed is now possible
	- while the user is looking at content, the SDK listens for new content in the background
	- when new content is detected, it can be displayed by a new button

## 5.0.1 (Aug, 2020)
- locationId deprecated, no longer needed
- with the new configuration 'onlyShowMenuEntryOnce', the app can be set to show the menu entry to be displayed on app start either just once or every single time.

## 5.0.0 (Aug, 2020)
- SDK updated to use new [backend](https://app2.sonobeacon.com/sonosystem)
- SDK supports new events triggered from geofences and bluetooth low energy (BLE) beacons.
	- triggering enter and exit urls
	- displaying app content based on locations
	- sending custom push notifications
- supports detections of both SonoBeacons and SonoWatermarks

## 4.0.0 (Oct, 2019)
- initial release
