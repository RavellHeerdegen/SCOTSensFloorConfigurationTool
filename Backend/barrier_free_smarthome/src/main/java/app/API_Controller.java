package app;

import app.exceptions.CarpetActionNotFoundException;
import app.exceptions.CarpetGroupNotFoundException;
import app.exceptions.InvalidValueException;
import app.exceptions.KeyNotFoundException;
import app.exceptions.NoCarpetConfigurationLoadedException;
import app.exceptions.ParameterException;
import app.exceptions.PluginByIdNotFoundException;
import app.models.CarpetConfiguration;
import app.models.CarpetCoordinate;
import app.services.ConfigurationService;
import app.services.ConfigurationService.ActionType;
import app.services.PluginService;
import app.services.JsonService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * This class defines and implements the REST interface.
 *
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class API_Controller {

	Logger logger = LoggerFactory.getLogger(API_Controller.class);

    // All Autowired services will be initialized by spring.
    @Autowired
    PluginService pluginService;
    @Autowired
    JsonService jsonService;
    @Autowired
    ConfigurationService configurationService;

    
    /** Initial Loading of all possible IOT objects
     * @return
     */
    @RequestMapping(value = "/objects", method = RequestMethod.GET)
    public List<Map<String, Object>> getAllIotObjects() {
        ArrayList<Map<String, Object>> allObjects = pluginService.getAllObjects();
        return allObjects;
    }


    /** Add action to group
     * @param httpEntity
     * @return
     */
    @RequestMapping(value = "/action", method = RequestMethod.POST)
    public long addActionToGroup(HttpEntity<String> httpEntity) {
        String jsonString = httpEntity.getBody();
        JSONObject jsonObject;
        try {
        	jsonObject = new JSONObject(jsonString);
        } catch (JSONException e1) {
        	logger.error("Syntax error in json input", e1);
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Syntax error in json input.");
        }

        
        Long groupId;
        String pluginId;
        JSONObject jsonParams;
        String activation;
        String actionName;
        try {
        	groupId = jsonObject.getLong("groupId");
            pluginId = jsonObject.getString("pluginId");
            jsonParams = jsonObject.getJSONObject("params");
            activation = jsonObject.getString("activation");
            actionName = jsonObject.getString("actionName");
        } catch (JSONException e1) {
        	String msg = "Mandatory keys not set or values are invalid.";
        	logger.error(msg, e1);
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
        }
              
        ConcurrentMap<String, String> paramsMap;
		try {
			paramsMap = jsonService.parseStringToConcurrentMap(jsonParams.toString());
		} catch (ParameterException e1) {
			String msg = "Can not parse parameters: Syntax error.";
			logger.error(msg, e1.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
		}       
        
        ActionType actionType;
        if (activation.equals("stepOff")) {
        	actionType = ActionType.ONEXIT;
        } else if (activation.equals("stepOn")) {
        	actionType = ActionType.ONENTER;
        } else {
        	String msg = "The 'activation' parameter must be either 'stepOff' or 'stepOn'.";
        	logger.error(msg);
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
        }

        long id = -1;
        
		try {
			id = this.configurationService.addActionToGroup(groupId, pluginId, paramsMap, actionName, actionType);
		} catch (NoCarpetConfigurationLoadedException e) {
			String msg = "No CarpetConfiguration loaded.";
			logger.error(msg, e);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
		} catch (CarpetGroupNotFoundException e) {
			String msg = "No CarpetGroup with id " + groupId + " found. Maybe it belongs to another user?";
			logger.error(msg, e);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
		} catch (PluginByIdNotFoundException e) {
			String msg = "Plugin with id " + pluginId + " can not be found.";
			logger.error(msg, e);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
		}
    
        return id;
    }


    /** 
     * Change the parameters for a CarpetGroup
     * 
     * @param httpEntity
     */
    @RequestMapping(value = "/action", method = RequestMethod.PUT)
    public void changeParamsForAction(HttpEntity<String> httpEntity) {
        String jsonString = httpEntity.getBody();
        JSONObject jsonObject;
        try {
        	jsonObject = new JSONObject(jsonString);
        } catch (JSONException e1) {
        	logger.error("Syntax error in json input", e1);
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Syntax error in json input.");
        }

        JSONObject jsonParams;
        Long actionId;
        try {
        	jsonParams = jsonObject.getJSONObject("params");
            actionId = jsonObject.getLong("actionId");
        } catch (JSONException e1) {
        	String msg = "Mandatory keys not set or values are invalid.";
        	logger.error(msg, e1);
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
        }
             
        ConcurrentMap<String, String> paramsMap;     
        try {
        	paramsMap = jsonService.parseStringToConcurrentMap(jsonParams.toString());
        } catch (ParameterException e1) {
        	String msg = "Can not parse parameters. Syntax error.";
        	logger.error(msg, e1);
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
        }
           
        try {
            this.configurationService.changeParamsForAction(actionId, paramsMap);

        } catch (NoCarpetConfigurationLoadedException e1) {
        	String msg = "Can not change parameters for action " + actionId + ". No CarpetConfiguration loaded.";
        	logger.error(msg, e1);
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
        } catch (CarpetActionNotFoundException e2) {
        	String msg = "Can not change parameters for action " + actionId + " from group. CarpetAction not found. Maybe it belongs to another user?";
        	logger.error(msg, e2);
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
        }
    }

    
    
   
    /** 
     * Remove a CarpetAction from a CarpetGroup.
     * 
     * @param httpEntity
     * @return
     */
    @RequestMapping(value = "/action", method = RequestMethod.DELETE)
    public void deleteActionFromGroup(HttpEntity<String> httpEntity) {
        String jsonString = httpEntity.getBody();
        JSONObject jsonObject;
        try {
        	jsonObject = new JSONObject(jsonString);
        } catch (JSONException e1) {
        	logger.error("Syntax error in json input", e1);
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Syntax error in json input.");
        }
        
        Long actionId;
        try {
        	actionId = jsonObject.getLong("actionId");
        } catch (JSONException e1) {
        	String msg = "Mandatory key not set or value is invalid.";
        	logger.error(msg, e1);
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
        }      
        
        try {
        	this.configurationService.deleteAction(actionId);
        } catch (NoCarpetConfigurationLoadedException e1) {
        	String msg = "Can not delete action " + actionId + " from group. No CarpetConfiguration loaded.";
        	logger.error(msg, e1);
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
        } catch (CarpetActionNotFoundException e2) {
        	String msg = "Can not delete action " + actionId + " from group. CarpetAction not found. Maybe it belongs to another user?";
        	logger.error(msg, e2);
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
        }
    }

    
    /**
     * Loading a Configuration for a specific user
     * 
     * @param userId
     * @return The CarpetConfiguration of the user that was loaded. If the user had no CarpetConfiguration yet this is a new and empty one.
     */
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    public CarpetConfiguration loadUserConfig(@RequestParam String userId) {

        CarpetConfiguration configuration = null;
        try {
            configuration = this.configurationService.loadUserConfig(userId);

        } catch (Exception e) {
        	String msg = "Can not load user config. Unknown error.";
        	logger.error(msg, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, msg);
        }
        return configuration;
    }


    /** Change the fields to which the CarpetGroup belongs.
     * 
     * @param httpEntity
     */
    @RequestMapping(value = "/group", method = RequestMethod.PUT)
    public void changeGroupFields(HttpEntity<String> httpEntity) {
        String jsonString = httpEntity.getBody();
        JSONObject jsonObject;
        try {
        	jsonObject = new JSONObject(jsonString);
        } catch (JSONException e1) {
        	logger.error("Syntax error in json input", e1);
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Syntax error in json input.");
        }

        Long groupId;
        JSONArray jsonFields;
        try {
        	groupId = jsonObject.getLong("groupId");
            jsonFields = jsonObject.getJSONArray("fields");
        } catch (JSONException e1) {
        	String msg = "Mandatory key not set or value is invalid.";
        	logger.error(msg, e1);
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
        }
        
        ArrayList<CarpetCoordinate> fields;
        try {
        	fields = jsonService.jsonArrayToCarpetCoordinateArray(jsonFields);
        } catch (InvalidValueException | KeyNotFoundException e1) {
        	String msg = "Can not parse carpet coordinates. Mandatory key not set or value is invalid.";
        	logger.error(e1.getMessage(), e1);
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
        }
        
        try {
        	this.configurationService.changeGroupFields(groupId, fields);
        } catch (NoCarpetConfigurationLoadedException e1) {
        	String msg = "Can not change group fields for group " + groupId + ". No CarpetConfiguration loaded.";
        	logger.error(msg, e1);
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
        } catch (CarpetGroupNotFoundException e2) {
        	String msg = "Can not change group fields for group " + groupId + ". No CarpetGroup with this id found. Maybe the group belongs to another user?";
        	logger.error(msg, e2);
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
        }
    }


    /**
     * Creates a new CarpetGroup.
     * 
     * @param httpEntity Injected property. 
     * @return The id of the new group.
     */
    @RequestMapping(value = "/group", method = RequestMethod.POST)
    public long createGroup(HttpEntity<String> httpEntity) {
    	// Retrieve the data
        String jsonString = httpEntity.getBody();
        JSONObject jsonObject;
        try {
        	jsonObject = new JSONObject(jsonString);
        } catch (JSONException e1) {
        	logger.error("Syntax error in json input", e1);
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Syntax error in json input.");
        }

        String groupName;
        JSONArray jsonFields;
        
        try {
        	groupName = jsonObject.getString("groupName");
            jsonFields = jsonObject.getJSONArray("fields");
        } catch (JSONException e1) {
        	String msg = "Mandatory key not set or value is invalid.";
        	logger.error(msg, e1);
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
        }
        
        // Convert JSON array and check for all needed parameters.
        ArrayList<CarpetCoordinate> fields;
        try {
        	fields = jsonService.jsonArrayToCarpetCoordinateArray(jsonFields);
        } catch (InvalidValueException | KeyNotFoundException e1) {
        	String msg = "Can not parse carpet coordinates. Mandatory key not set or value is invalid.";
        	logger.error(msg, e1);
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
        }  
        
        // Now everything is checked and can be passed to the service to handle it.
        
        long id = -1;
        try {
            id = this.configurationService.createGroup(groupName, fields);
        } catch (NoCarpetConfigurationLoadedException e1) {
        	String msg = "Can not create group. No CarpetConfiguration loaded.";
        	logger.error(msg, e1);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
        }
        
        return id;
    }
    

    /** Delete a CarpetGroup.
     * 
     * @param httpEntity
     */
    @RequestMapping(value = "/group", method = RequestMethod.DELETE)
    public void deleteGroup(HttpEntity<String> httpEntity) {
        String jsonString = httpEntity.getBody();
        JSONObject jsonObject;
        try {
        	jsonObject = new JSONObject(jsonString);
        } catch (JSONException e1) {
        	logger.error("Syntax error in json input", e1);
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Syntax error in json input.");
        }

        Long groupId;
        try {
        	groupId = jsonObject.getLong("groupId");
        } catch (JSONException e1) {
        	String msg = "Mandatory key 'groupId' is missing.";
        	logger.error(msg, e1);
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
        }

        try {
            this.configurationService.deleteGroup(groupId);
        } catch (NoCarpetConfigurationLoadedException e1) {
        	String msg = "Can not delete group " + groupId + ": No CarpetConfigurationen loaded.";
        	logger.error(msg, e1);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
        } catch (CarpetGroupNotFoundException e2) {
        	String msg = "No CarpetGroup with id " + groupId + " found in current CarpetConfiguration.";
        	logger.error(msg, e2);
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
        }
    }
    
}