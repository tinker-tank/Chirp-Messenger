##Chirp Messenger

The Chirp Messenger for Android allows you to send and receive chirps from within your Android application. It communicates directly with the audio hardware to generate and receive chirps, played from the device's speaker.

For small tokens of data (50 bits or less), transmission can take place entirely offline by encoding a 10-character alphanumeric identifier as a series of audio tones. This is useful for triggering events, opening pre-created content, or sending short numeric/string values.

For larger data structures, a chirp can encapsulate a JSON dictionary comprising of arbitrary key-value pairs (which may themselves be strings, numbers, arrays or dictionaries). This is transmitted to the API server, which assigns the data a unique 10-character identifier. This code is then played as audio via the device's speaker, and received by any nearby devices that hear the tone.


