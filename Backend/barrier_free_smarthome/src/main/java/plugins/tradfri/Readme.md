# Tradfri Plugin

With this plugin it is possible to connect to a IKEA Tradfri Gateway and control connected bulbs.
The plugin makes use of code found [here](https://github.com/ffleurey/ThingML-Tradfri) (Apache 2.0 License) and was adjusted (e.g. to meet the security requirements of the new Gateway Firmware).

Note: Not all IKEA bulbs support all colors or support colors at all.

## Required shared attributes
- Key: 
	/[plugin_id]/gateway_ip
- Type: String
- Value: A valid string containing the ip address. For example: 192.168.0.20

- Key: 
	/[plugin_id]/gateway_identity
- Type: String
- Value: A valid string containing the identity which is used to login to the gateway

- Key: 
	/[plugin_id]/gateway_secret
- Type: String
- Value: A valid string containing the password for the gateway_identity.


## Parameters
- objectId: The objectId parameter is mandatory.
	- Key: "objectId"
 	- Value: (A valid String which is the id of the bulb)
- Turn lamp on or off
	- Key: "lamp_on"
	- Value: "true" or "false"
- Set brightness
	- Key: "brightness"
	- Value: (String which is a parse-able integer between 0 and 255)
- Set color via hex value:
	- Key: "color_hex"
	- Value: A String with the hex value of the color (without leading #). Important: Only the following hex values are allowed.
		- dcf0f8
		- eaf6fb
		- f5faf6
		- f2eccf
		- f1e0b5
		- efd275
		- ebb63e
		- e78834
		- e57345
		- da5d41
		- dc4b31
		- e491af
		- e8bedd
		- d9337c
		- c984bb
		- 8f2686
		- 4a418a
		- 6c83ba
		- a9d62b
		- d6e44b
- Set color via name:
	- Key: "color"
	- Value: "blue" or "red" or "green" or "white" or "yellow"
	
## Helper Class
The class TradfriHelperTools.java provides methods to:
- Get the ids of all registered bulbs.
- Add a new security identity and get the corresponding key.