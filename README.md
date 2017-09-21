##Chirp SDK for Android

The Chirp SDK for Android allows you to send and receive chirps from within your Android application. It communicates directly with the audio hardware to generate and receive chirps, played from the device's speaker.

For small tokens of data (50 bits or less), transmission can take place entirely offline by encoding a 10-character alphanumeric identifier as a series of audio tones. This is useful for triggering events, opening pre-created content, or sending short numeric/string values.

For larger data structures, a chirp can encapsulate a JSON dictionary comprising of arbitrary key-value pairs (which may themselves be strings, numbers, arrays or dictionaries). This is transmitted to the Chirp API server, which assigns the data a unique 10-character identifier. This code is then played as audio via the device's speaker, and received by any nearby devices that hear the tone.

Read more: [Anatomy of a Chirp](http://developers.chirp.io/docs/chirps-shortcodes); [Online and Offline Operation](http://developers.chirp.io/docs/online-and-offline-operation).

##Overview

Welcome to the Chirp Android SDK. Contained within it are two sample apps that demonstrate how to use the SDK within your application to guide you in development. 

It also contains the Chip Android SDK documentation for further reference, inside the `Documentation` directory. Further developer documentation and guides are available on the [Chirp Developer Hub](https://developer.chirp.io).

## Getting Started

To get started with the Chirp SDK, you'll first need to register for Chirp application credentials. Sign up to the developer console on the [Chirp Admin Centre](https://admin.chirp.io/). It may take up to one business day for your application to be approved.

##Using the Chirp Android SDK

Once you've got credentials, set up your project as follows.

Copy the `chirp-sdk-release.aar` file to your `app/libs` directory. Add the following to the `dependencies` block of your Module `build.gradle` Gradle script:

``` java
compile 'io.chirp.sdk:chirp-sdk-release:2.5.2@aar'

compile 'com.squareup.retrofit2:retrofit:2.3.0'
compile 'com.squareup.retrofit2:converter-gson:2.3.0'
compile 'joda-time:joda-time:2.3'
compile 'com.j256.ormlite:ormlite-core:4.48'
compile 'com.j256.ormlite:ormlite-android:4.48'
```

To instruct Gradle where to find the local .aar file, add `flatDir` section to the `repositories` block. (You'll need to add a `repositories` block if one does not already exist.)

``` java
repositories {
    flatDir {
        dirs 'libs'
    }
}
```

To declare that your app requires audio permissions, add the following to your `AndroidManifest.xml`, inside the bottom of the `<manifest>` element.

``` xml
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS"/>
<uses-permission android:name="android.permission.RECORD_AUDIO"/>
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

Chirp SDK requires at minimum of Android 4.0.3

##Chirp SDK Overview

Here is the Chirp SDK in a nutshell. It will be explained in greater detail in the later sections.

``` java
ChirpSDK chirpSDK = new ChirpSDK(context, API_KEY, API_SECRET);

chirpSDK.setListener(new ChirpSDKListener() {

    @Override
    public void onChirpHeard(Chirp chirp) {

    }
});


chirpSDK.start();
```

More information in reference format can be found below. Also see the JavaDocs in the documentation folder.

##The Chirp SDK Constructor

You create an instance of the ChirpSDK as follows. It takes a reference to the Android Context, the API Key and secret.

``` java
ChirpSDK chirpSDK = new ChirpSDK(context, API_KEY, API_SECRET);
```

##Listening for chirps

In order to listen for chirps, you'll first need to set a listener callback.

``` java
chirpSDK.setListener(new ChirpSDKListener() {

    @Override
    public void onChirpHeard(Chirp chirp) {

    }

    @Override
    public void onChirpError(ChirpError chirpError) {

    }
});
```
    
Start audio processing with the following:

``` java
chirpSDK.start();
```

When the device hears a Chirp tone, the `onChirpHeard(chirp)` callback method will triggered.

##Stop audio processing

Stop audio processing with the following:

``` java
chirpSDK.stop();
```

##Play a Chirp

To play a chirp:

``` java
chirpSDK.chirp(new Chirp("8ehqbur4bk"));
```

##Create a Chirp

Create your JSON either by:

``` java
JSONObject jsonObj = new JSONObject();
jsonObj.put("key", "value");
jsonObj.put("more", "data");
```

Then pass it to the Chirp constructor: 

``` java
Chirp chirp = new Chirp(jsonObj);
```

Then create the Chirp encapsulating the JSON object by calling `create(chirp)`

``` java
chirpSDK.create(chirp, new CallbackCreate() {

    @Override
    public void onCreateResponse(Chirp chirp) {
    }

    @Override
    public void onCreateError(ChirpError chirpError) {

    }
});
```

The Chirp will be received via the `onCreateResponse(chirp)` callback method. Any errors will be returned via `onCreateError(chirpError)`.


##Read a Chirp's associated data

You can read the data associated with a Chirp by calling `read(chirp)`. Demonstrated as follows:

``` java
chirpSDK.read(chirp, new CallbackRead() {

    @Override
    public void onReadResponse(Chirp chirp) {

    }

    @Override
    public void onReadError(ChirpError chirpError) {

    }
});
```

The Chirp will be received via the `onReadResponse(chirp)` callback method. Any errors will be returned via `onReadError(chirpError)`.


##Streaming mode

This will broadcast a chirp repeatedly to the current audio output. It must be subsequently followed by a call to stopStreaming, which ends the stream.

You can broadcast a Chirp in streaming mode as follows:

``` java
chirpSDK.startStreaming(new Chirp("8ehqbur4bk"));
```

To stop streaming a Chirp:

``` java
chirpSDK.stopStreaming();
```

For the receiver to receive a streaming chirp, you must put the receiver in streaming mode, as follows:

``` java
chirpSDK.setStreamingMode(ChirpStreamingMode.ChirpStreamingModeOn);
```

##Change protocol

You can change the Chirp protocol to use ultrasound as follows:

``` java
ChirpProtocolResult chirpProtocolResult = chirpSDK.setProtocolNamed(ChirpProtocolName.ChirpProtocolNameUltrasonic);
```

Read more here: [Chirp Protocols](http://developers.chirp.io/docs/chirp-protocols)

## More information

Complete documentation for the Chirp SDK is included within this download, inside the `Documentation` directory.

Further developer documentation and guides are available on the [Chirp Developer Hub](https://developer.chirp.io).
