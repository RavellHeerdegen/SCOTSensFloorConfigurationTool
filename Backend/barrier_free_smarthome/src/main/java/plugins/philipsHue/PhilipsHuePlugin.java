package plugins.philipsHue;

import de.ableitner.barrierfreeSmarthome.common.plugin.AbstractHttpClientPlugin;
import de.ableitner.barrierfreeSmarthome.common.plugin.PluginApiVersionEnum;
import de.ableitner.barrierfreeSmarthome.common.SharedAttribute;


import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhilipsHuePlugin extends AbstractHttpClientPlugin {
	
	// Strings which can be used as key
	public static final String KEY_LAMPON = "lamp_on";
	public static final String KEY_COLOR = "color";
	public static final String KEY_BRIGHTNESS = "brightness"; // The brightness is a value between 1 and 255
	public static final String KEY_BULB_ID = "objectId";
	
	// Strings which can be used as values in the parameters
	public static final String VALUE_LAMPON_TRUE = "true";
	public static final String VALUE_LAMPON_FALSE = "false";
	
	public static final String VALUE_COLOR_RED = "red";
	public static final String VALUE_COLOR_BLUE = "blue";
	public static final String VALUE_COLOR_GREEN = "green";
	public static final String VALUE_COLOR_WHITE = "white";
	public static final String VALUE_COLOR_YELLOW = "yellow";

	Logger logger = LoggerFactory.getLogger(PhilipsHuePlugin.class);

    private HttpClient httpClient = new HttpClient();

    public PhilipsHuePlugin(){
        super();
        logger.info("Philips_Hue------->Dynamically loaded");
    }

    @Override
    public String getPluginId() {
        return "hue_light";
    }

    @Override
    public void action(ConcurrentMap<String, SharedAttribute> sharedAttributes, ConcurrentMap<String, String> parameters) {
        //The Philips Hue needs 3 parameters to switch the state of an light user, ip and the number of the lamp

        String method = "PUT";


        String on = "\"\"";
        String color = "\"\"";
        String brightness = "\"\"";
        
        if (parameters.getOrDefault(KEY_LAMPON, "").equals(VALUE_LAMPON_TRUE)) {
            on = "true";
        } else if (parameters.getOrDefault(KEY_LAMPON, "").equals(VALUE_LAMPON_FALSE)) {
            on = "false";
        }
        

        if(parameters.containsKey(KEY_COLOR)){
            color = parameters.get(KEY_COLOR);
            switch(color) {
                case VALUE_COLOR_RED:
                    color = "65000";
                    break;
                case VALUE_COLOR_BLUE:
                    color = "43690";
                    break;
                case VALUE_COLOR_GREEN:
                    color = "21845";
                    break;
                case VALUE_COLOR_WHITE:
                    //TODO white is not a color
                    break;
                case VALUE_COLOR_YELLOW:
                    color = "8000";
                    break;
            }
        }
        if(parameters.containsKey(KEY_BRIGHTNESS)){
            brightness = parameters.get(KEY_BRIGHTNESS);
        }

        String body = "{\"on\":" + on + ",\"bri\":" + brightness + ",\"hue\":"+color+"}";

        if(parameters.containsKey(KEY_LAMPON)) {
	        try{
	        	// TODO: Handle errors if mandatory parameters are not set and throw a parameter exception
	            String id = parameters.get(KEY_BULB_ID);
	            String ip = (String)sharedAttributes.get("/"+this.getPluginId()+"/ip").getValue();
	            String auth = (String)sharedAttributes.get("/"+this.getPluginId()+"/auth").getValue();
	
	            httpClient.sendHttpRequest("http://"+ip+"/api/"+auth+"/lights/"+id+"/state",body, method);
	        }catch(Exception e){
	            e.printStackTrace(); // TODO: Do not simply swallow this exception!
	        }
        }


    }

    @Override
    public PluginApiVersionEnum getApiVersion() {
        return null;
    }


}

