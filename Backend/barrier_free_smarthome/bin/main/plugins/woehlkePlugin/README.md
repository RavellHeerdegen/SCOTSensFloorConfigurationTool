# Barrier-Free Smarthome

## Woehlke Plugin
With this plugin it is possible to integrate a wifi socket strip of the company Wöhlkethe into the barrier-free smarthome platform. Therefore the plugin uses the http interface of the socket strip. Further information about this socket strip can be found here:\
https://www.woehlke-websteckdose.de/websteckdose/anwendungsbeispiele

Required shared attribute(s):
- The IP address of the wifi socket strip.
    - Key: "/[plugin id]/ip"
    - Type: String
    - Value: A valid ip address. For example: "192.168.178.47"

The plugin supports the following action(s):
- Enabling and disabling a socket. The following parameters are required:
    - Number of the socket. This parameter decides, which socket is enabled or disabled.
        - Key: "socketNumber"
        - Values: "1", "2" or "3"
    - New state of the socket. This parameters decides, whether the socket is enabled or disabled.
       -   Key: "newState"
       -   Values: "on" or "off"

## VLC Plugin
With this plugin it is possible to integrate the VLC player into the barrier-free smarthome platform. Therefore the plugin uses the http interface of the VLC player. Further information about this http interface can be found here:\
https://wiki.videolan.org/Documentation:Modules/http_intf/ \
https://wiki.videolan.org/VLC_HTTP_requests/

Required shared attributes(s):
- IP address of the VLC player.
    - Key: "/[plugin id]/ip"
    - Type: String
    - Value: A valid ip address. For example: "192.168.178.34"
- Port number of the VLC player's interface.
    - Key: "/[plugin id]/port"
    - Type: String
    - Value: A valid port number. For exmaple "8080"
- Password of the VLC player's interface.
    - Key: "/[plugin id]/password"
    - Type: String
    - Value: A password. For example: "badPassword"
- Volume of the VLC player.
    - Key: "/[plugin id]/volume"
    - Type: String
    - Value: It is an integer value between 0 and 100. For example: "100"
- Mute state of the VLC player.
    - Key: "/[plugin id]/mute"
    - Type: String
    - Value: It is a boolean vaule (true = muted, false = unmuted). For example: "false"
- Fullscreen state of the VLC player.
    - Key: "/[plugin id]/fullscreen"
    - Type: String
    - Value: It is a boolean value (true = fullscreen enabled, flase = fullscreen disabled). For example: "false"
- Id of the last played TV channel item.
    - Key: "/[plugin id]/tv/lastPlayedPlaylistItemId"
    - Type: String
    - Value: It is an integer value greater equals 0. For example: "5"
- Id of the first TV channel in the playlist. It is an integer value greater equals 0.
    - Key: "/[plugin id]/tv/minPlaylistItemId"
    - Type: String
    - Value: It is an integer value greater equals 0. For example: "5"
- Id of the last TV channel in the playlist. It is an integer value greater equals 0.
    - Key: "/[plugin id]/tv/maxPlaylistItemId"
    - Type: String
    - Value: It is an integer value greater equals 0. For example: "29"
- Id of the last played radio channel item.
    - Key: "/[plugin id]/radio/lastPlayedPlaylistItemId"
    - Type: String
    - Value: It is an integer value greater equals 0. For example: "31"
- Id of the first radio channel in the playlist.
    - Key: "/[plugin id]/radio/minPlaylistItemId"
    - Type: String
    - Value: It is an integer value greater equals 0. For example: "30"
- Id of the last radio channel in the playlist.
    - Key: "/[plugin id]/radio/maxPlaylistItemId"
    - Type: String
    - Value: It is an integer value greater equals 0. For example: "58"

The plugin supports the following action(s):
- Play a TV channel by a channel number. The following parameters are required:
    - The name of the action.
        - Key: "methodName"
        - Value: "playTvChannel"
    - The playlist item id of the TV channel, which should be played.
        - Key: "playlistItemId"
        - Value: It is an integer value greater equals 0. For example: "5"
- Play last played TV channel. The following parameters are required:
    - The name of the action.
        - Key: "methodName"
        - Value: "tvPlay"
- Play next TV channel. The following parameters are required:
    - The name of the action.
        - Key: "methodName"
        - Value: "tvChannelUp"
- Play previous TV channel. The following parameters are required:
    - The name of the action.
        - Key: "methodName"
        - Value: "tvChannelDown"
- Play a radio channel by a channel number. The following parameters are required:
    - The name of the action.
        - Key: "methodName"
        - Value: "playRadioChannel"
    - The playlist item id of the radio channel, which should be played.
        - Key: "playlistItemId"
        - Value: It is an integer value greater equals 0. For example: "5"
- Play last played radio channel. The following parameters are required:
    - The name of the action.
        - Key: "methodName"
        - Value: "radioPlay"
- Play next radio channel. The following parameters are required:
    - The name of the action.
        - Key: "methodName"
        - Value: "radioChannelUp"
- Play previous radio channel. The following parameters are required:
    - The name of the action.
        - Key: "methodName"
        - Value: "radioChannelDown"
- Stop playing a TV / radio channel. The following parameters are required:
    - The name of the action.
        - Key: "methodName"
        - Value: "stop"
- Enable fullscreen. The following parameters are required:
    - The name of the action.
        - Key: "methodName"
        - Value: "enableFullscreen"
- Disable fullscreen. The following parameters are required:
    - The name of the action.
        - Key: "methodName"
        - Value: "disbaleFullscreen"
- Mute the sound. The following parameters are required:
    - The name of the action.
        - Key: "methodName"
        - Value: "disableSound"
- Unmute the sound. The following parameters are required:
    - The name of the action.
        - Key: "methodName"
        - Value: "enableSound"
- Increment the volume by a defined value. The following parameters are required:
    - The name of the action.
        - Key: "methodName"
        - Value: "incrementVolumeByValue"
    - The value, by which the volume should be incremented.
        -   Key: "incrementValue"
        -   Value: "10"
- Decrement the volume by a defined value. The following parameters are required:
    - The name of the action.
        - Key: "methodName"
        - Value: "decrementVolumeByValue"
    - The value, by which the volume should be decremented.
        - Key: "decrementValue"
        - Value: "10"  