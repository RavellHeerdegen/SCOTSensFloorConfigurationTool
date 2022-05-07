# Philips Hue Plugin

With this plugin it is possible to connect to a Philips Hue Gateway and control connected bulbs.

Note: Not all bulbs support all colors or support colors at all.

## Required shared attributes
- Key: 
	/[plugin_id]/ip
- Type: String
- Value: A valid string containing the ip address. For example: 192.168.0.20

- Key: 
	/[plugin_id]/auth
- Type: String
- Value: A valid string containing the authentication key.


## Parameters
- objectId: The objectId parameter is mandatory.
	- Key: "objectId"
 	- Value: (A valid String which is the id of the bulb)
- Turn lamp on or off
	- Key: "lamp_on"
	- Value: "true" or "false"
- Set brightness
	- Key: "brightness"
	- Value: (String which is a parse-able integer between 1 and 255)
- Set color:
	- Key: "color"
	- Value: "blue" or "red" or "green" or "white" or "yellow"