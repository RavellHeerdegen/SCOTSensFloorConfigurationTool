package plugins.tradfri;
import app.exceptions.InvalidValueException;
import app.exceptions.KeyNotFoundException;
import de.ableitner.barrierfreeSmarthome.common.SharedAttribute;
import de.ableitner.barrierfreeSmarthome.common.plugin.AbstractPlugin;
import de.ableitner.barrierfreeSmarthome.common.plugin.PluginApiVersionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentMap;

/*
 * Test login properties: test_identity_2
 * {"9091":"MTwP1RzGNkbPMUfm","9029":"1.10.0036"}
 * */

/**
 * @author Florian Jungermann
 * 
 * This plugin connects to the IKEA Tradfri Gateway and can control tradfri lights.
 * 
 * It makes use of the ThingML-Tradfri Code found here (Apache 2.0 License): https://github.com/ffleurey/ThingML-Tradfri
 *
 */
public class TradfriPlugin extends AbstractPlugin {

	Logger logger = LoggerFactory.getLogger(TradfriPlugin.class);
	
	// Strings which can be used as keys
	public static final String KEY_LAMPON = "lamp_on";
	public static final String KEY_BRIGHTNESS = "brightness"; // The brightness is a value between 1 and 254 (note: 255 not allowed)
	public static final String KEY_COLOR_HEX = "color_hex"; // Only use the provided color hex Strings below
	public static final String KEY_BULB_ID = "objectId";
	public static final String KEY_COLOR = "color";
	
	// Strings which can be used as values in the parameters
	public static final String VALUE_LAMPON_TRUE = "true";
	public static final String VALUE_LAMPON_FALSE = "false";
	
	// Available Colors. Only these colors are possible. Every other Hex code will result in white
	// These can be used as input for the color_hex parameter
	public static final String VALUE_COLOR_1 = "dcf0f8";
	public static final String VALUE_COLOR_2 = "eaf6fb";
	public static final String VALUE_COLOR_3 = "f5faf6"; //cold white
	public static final String VALUE_COLOR_4 = "f2eccf";
	public static final String VALUE_COLOR_5 = "f1e0b5"; //normal white
	public static final String VALUE_COLOR_6 = "efd275"; //warm white
	public static final String VALUE_COLOR_7 = "ebb63e";
	public static final String VALUE_COLOR_8 = "e78834";
	public static final String VALUE_COLOR_9 = "e57345";
	public static final String VALUE_COLOR_10 = "da5d41";
	public static final String VALUE_COLOR_11 = "dc4b31"; // red
	public static final String VALUE_COLOR_12 = "e491af";
	public static final String VALUE_COLOR_13 = "e8bedd";
	public static final String VALUE_COLOR_14 = "d9337c";
	public static final String VALUE_COLOR_15 = "c984bb";
	public static final String VALUE_COLOR_16 = "8f2686";
	public static final String VALUE_COLOR_17 = "4a418a"; //
	public static final String VALUE_COLOR_18 = "6c83ba"; //blue
	public static final String VALUE_COLOR_19 = "a9d62b"; //green
	public static final String VALUE_COLOR_20 = "d6e44b"; // light green
	
	// These can be used with the color parameter
	public static final String VALUE_COLOR_RED = "red";
	public static final String VALUE_COLOR_BLUE = "blue";
	public static final String VALUE_COLOR_GREEN = "green";
	public static final String VALUE_COLOR_WHITE = "white";
	public static final String VALUE_COLOR_YELLOW = "yellow";
	
	
	
	private TradfriGateway gateway;
	
    /**
     * Constructor calls super constructor.
     */
    public TradfriPlugin() {
        super();
        logger.info("Tradfri Plugin dynamically loaded.");
        
        gateway = new TradfriGateway();
    }

    @Override
    public String getPluginId() {
        return "tradfri_light";
    }

    
    
    /**
     * Triggers the action given by the parameters.
     * 
     * @param sharedAttributes: 
     * 
     * 
     * @param parameters: The parameters for what to do.
     * 
     */
    @Override
    public void action(ConcurrentMap<String, SharedAttribute> sharedAttributes,
            ConcurrentMap<String, String> parameters) {
    	
    	logger.info("--- Tradfri plugin action called ---");
    	
    	String gatewayIp;
    	String securityIdentity;
    	String securityKey;
    	
    	try {
    		gatewayIp = (String)sharedAttributes.get("/" + this.getPluginId() + "/gateway_ip").getValue();
        	securityIdentity = (String)sharedAttributes.get("/" + this.getPluginId() + "/gateway_identity").getValue();
        	securityKey = (String)sharedAttributes.get("/" + this.getPluginId() + "/gateway_secret").getValue();
    	} catch (NullPointerException e) {
    		throw new KeyNotFoundException("One of the mandatory keys in the sharedAttributes not found!", e);
    	} catch (ClassCastException e) {
    		throw new InvalidValueException("Invalid value in one of the sharedAttributes or something is wrong with a key of the sharedAttributes.", e);
    	}
    	
    	
    	// Configuring the gateway connector
    	gateway.setGateway_ip(gatewayIp);
    	gateway.setSecurityIdentity(securityIdentity);
    	gateway.setSecurity_key(securityKey);
    	gateway.initCoap();
    	
    	if (!parameters.containsKey(KEY_BULB_ID)) throw new KeyNotFoundException("Mandatory key 'objectId' not found in parameters!");
    	LightBulb lightBulb = new LightBulb(Integer.parseInt(parameters.get(KEY_BULB_ID)), gateway);

		if (parameters.getOrDefault(KEY_LAMPON, "").equals(VALUE_LAMPON_FALSE)) {
			this.setLampOn(lightBulb, false);
			//this.changeLampBrightness(lightBulb, 0);
		} else {

			if (parameters.getOrDefault(KEY_LAMPON, "").equals(VALUE_LAMPON_TRUE)) {
				this.setLampOn(lightBulb, true);
			}

			if (parameters.containsKey(KEY_BRIGHTNESS)) {
				int b = Integer.parseInt(parameters.get(KEY_BRIGHTNESS));

				if (b < 0) {
					logger.info("Parameter value for brightness was set to {} but only values between 0 and 255 allowed. Setting value to 0.", b);
					b = 0;
				}

				if (b == 255) {
					b = 254; // For some reason the tradfri does not accept a value of 255 for the brightness. This special case we do not want to log.
				}

				if (b > 255) {
					logger.info("Parameter value for brightness was set to {} but only values between 0 and 255 allowed. Setting value to 255.", b);
					b = 254; // For some reason the tradfri does not accept a value of 255 for the brightness.
				}

				this.changeLampBrightness(lightBulb, b);
			}
			if (parameters.containsKey(KEY_COLOR_HEX)) {
				String hex = parameters.get(KEY_COLOR_HEX);
				this.changeLampColor(lightBulb, hex);
			} else if (parameters.containsKey(KEY_COLOR)) {
				switch (parameters.get(KEY_COLOR)) {
					case (VALUE_COLOR_BLUE):
						this.changeLampColor(lightBulb, VALUE_COLOR_18);
						break;
					case (VALUE_COLOR_GREEN):
						this.changeLampColor(lightBulb, VALUE_COLOR_19);
						break;
					case (VALUE_COLOR_RED):
						this.changeLampColor(lightBulb, VALUE_COLOR_11);
						break;
					case (VALUE_COLOR_WHITE):
						this.changeLampColor(lightBulb, VALUE_COLOR_5);
						break;
					case (VALUE_COLOR_YELLOW):
						this.changeLampColor(lightBulb, VALUE_COLOR_7);
						break;
					default:
						this.changeLampColor(lightBulb, VALUE_COLOR_5); // Default is changing color to white
				}
			}
		}
    	
    	// Important! Apply changes to the bulb
    	lightBulb.updateBulb();
    }

    @Override
    public PluginApiVersionEnum getApiVersion() {
        return null;
    }
    
    private void changeLampColor(LightBulb lightBulb, String hex) {
    	logger.info("Tradfri action 'change color' called on bulb {}.", lightBulb.getId());
    	lightBulb.setColor(hex);
    }
    
    private void changeLampBrightness(LightBulb lightBulb, int b) {
    	logger.info("Tradfri action 'set brightness' called on bulb {} with brightness value of {}.", lightBulb.getId(), b);
    	lightBulb.setIntensity(b);
    	
    }
    
    private void setLampOn(LightBulb lightBulb, boolean on) {
    	logger.info("Tradfri action 'lamp on or off' called on bulb {}.", lightBulb.getId());
    	lightBulb.setOn(on);
    }
    
    private void switchLampState(LightBulb lightBulb) {
    	logger.info("Tradfri action 'switch' called on bulb {}.", lightBulb.getId());
		lightBulb.setOn(!lightBulb.isOn());
    }
}