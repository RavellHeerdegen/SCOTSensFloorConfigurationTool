package app.services;

import app.exceptions.InvalidValueException;
import app.exceptions.KeyNotFoundException;
import app.exceptions.ParameterException;
import app.models.CarpetCoordinate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * This service handles JSON inputs and also reads the pluginConfig.json configuration file.
 */
@Service
public class JsonService {

	Logger logger = LoggerFactory.getLogger(JsonService.class);
	
    private String pathToJson = "pluginConfig.json";
    private ObjectMapper mapper = new ObjectMapper();
    private String carpetConnectorIP = "";

    /**
     * @return The ip of the CarpetConnector (device that connects to the real carpet).
     */
    public String getCarpetConnectorIP() {
        return carpetConnectorIP;
    }

    /**
     *  Reads the pluginConfig.json.
     * @return The content of the pluginConfig.json.
     */
    public ArrayList<Map<String,Object>> readPluginConfigJson(){
        Map<String,Object> json = null;
        ArrayList<Map<String,Object>> result = null;

        try {
            //Map Json to object
            json = mapper.readValue(new FileReader(pathToJson), HashMap.class);
            //LoadPlugins
            result = (ArrayList<Map<String,Object>>)json.get("plugins");

            //Load CarpetConnectorIP
            this.carpetConnectorIP = json.get("carpetConnectorIP").toString();

            logger.info("----------->Config Loaded");


        } catch (FileNotFoundException e1) {
        	logger.error("Plugin config file not found! Path: " + this.pathToJson, e1);
            // TODO: Handle error
        } catch (JsonMappingException e2) {
        	logger.error("Can not map json file: " + this.pathToJson, e2);
        	// TODO: Handle error
        } catch (JsonParseException e3) {
        	logger.error("Can not parse json file: " + this.pathToJson, e3);
        	// TODO: Handle error
        } catch ( IOException e4) {
        	logger.error("Plugin config file could not be read! Path: " + this.pathToJson, e4);
        	// TODO: Handle error
        }

        return result;
    }

    /**
     * Helper method for parsing a String in the json format and returns a ConcurrentMap.
     * 
     * @param jsonString
     * @return
     * @throws ParameterException
     */
    public ConcurrentMap<String, String> parseStringToConcurrentMap(String jsonString) throws ParameterException {
        ConcurrentMap<String, String> map = null;
        try {
            //Map Json to object
            map = mapper.readValue(jsonString, ConcurrentMap.class);         
        } catch (JsonParseException e1) {
        	String msg = "Can not parse given string to json. Parse error.";
        	logger.error(msg, e1);
        	throw new ParameterException(msg, e1);
        } catch (JsonMappingException e2) {
        	String msg = "Can not parse given string to json. Mapping error.";
        	logger.error(msg, e2);
        	throw new ParameterException(msg, e2);
        } catch (JsonProcessingException e3) {
        	String msg = "Can not parse given string to json. Processing error.";
        	logger.error(msg, e3);
        	throw new ParameterException(msg, e3);
        }

        return map;

    }
    
    /**
     * Helper method for converting a json array to an ArrayList<CarpetCoordinate>.
     * 
     * @param jsonArray
     * @return
     */
    public ArrayList<CarpetCoordinate> jsonArrayToCarpetCoordinateArray(JSONArray jsonArray) throws InvalidValueException, KeyNotFoundException{
    	ArrayList<CarpetCoordinate> fields = new ArrayList<CarpetCoordinate>();     
        
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject field = jsonArray.getJSONObject(i);
            
            try {
                int x = (int) field.get("x");
                int y = (int) field.get("y");

                if (x >= 0 && y >= 0) {
                    fields.add(new CarpetCoordinate(x, y));
                } else {
                	throw new InvalidValueException("Carpet coordinates must be positive. Given values: x: " + x + ", y:" + y);
                }
            } catch (ClassCastException e1) {
            	String msg = "Error creating group. Invalid values for 'x' and/or 'y'.";
            	throw new InvalidValueException(msg, e1);
            } catch (JSONException e2) {
            	String msg = "Error creating group. Missing mandatory json keys.";
            	throw new KeyNotFoundException(msg, e2);
            }
        }
        
        return fields;
    }

}
