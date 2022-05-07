package app.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.ableitner.barrierfreeSmarthome.common.plugin.IPluginV1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.ableitner.barrierfreeSmarthome.common.SharedAttribute;
import de.ableitner.barrierfreeSmarthome.common.plugin.AbstractPlugin;
import de.ableitner.barrierfreeSmarthome.common.plugin.DefaultPluginLoader;

/**
 * This service loads the plugins and shared attributes.
 */
@Service
public class PluginService {
	Logger logger = LoggerFactory.getLogger(PluginService.class);

    JsonService jsonService;

    private ArrayList<String> allPluginNames = new ArrayList<>();
    private ConcurrentMap<String, AbstractPlugin> allPlugins;
    private ConcurrentMap<String, SharedAttribute> sharedAttributes = new ConcurrentHashMap<String, SharedAttribute>();


    public PluginService(JsonService jsonService) {
	    this.jsonService = jsonService;
	
	    this.loadSharedAttributes(false);
	    this.loadPlugins(false);
	}

	/** Loads the shared attributes
	 * @param reload If shared attributes are already loaded, you can reload them by setting this to true.
	 */
	private void loadSharedAttributes(boolean reload) {
    	logger.debug("Loading shared attributes...");
    	
    	if (this.sharedAttributes != null) {
    		if (!this.sharedAttributes.isEmpty()) {
    			if (reload) {
    				this.sharedAttributes.clear(); // If the shared attributes shall be reloaded, clear the list first
    			} else {
    				logger.warn("Shared attributes already loaded and reloading is deactivated so not loading again.");
            		return;
    			}  			
    		}	
    	} else {
    		this.sharedAttributes = new ConcurrentHashMap<String, SharedAttribute>(); // if sharedAttributes was not initialized yet do so now
    	}
    	 	
        //Load config from Json File
        ArrayList<Map<String, Object>> pluginsArray = this.jsonService.readPluginConfigJson();
        for (Map<String, Object> pluginConf : pluginsArray) {
            for (Map.Entry<String, Object> entry : pluginConf.entrySet()) {
                sharedAttributes.put("/" + pluginConf.get("pluginId") + "/" + entry.getKey(), new SharedAttribute(entry.getKey(), "String", entry.getValue()));
            }
            this.allPluginNames.add(pluginConf.get("pluginId") + "");
            logger.trace("Adding {} to allPluginNames.", pluginConf.get("pluginId"));
        }
    }

    /** Loads the plugins
	 * @param reload If plugins are already loaded, you can reload them by setting this to true.
     */
    private void loadPlugins(boolean reload) {
		logger.debug("Loading plugins...");
		
		if (this.allPlugins != null) {
			if (!this.allPlugins.isEmpty()) {
				if (reload) {
					this.allPlugins.clear();
				} else {
					logger.warn("Plugins are loaded already and reloading is deactivated so skipping loading them again.");
		    		return;
				}
			}		
		} else {
			this.allPlugins = new ConcurrentHashMap<String, AbstractPlugin>();
		}
		
	    DefaultPluginLoader pluginLoader = new DefaultPluginLoader();
	
	    //Build a map with all possible plugins
	    ArrayList<String> pluginNames = this.allPluginNames;     
	
	    //dynamic loading of plugins
	    for (String pluginId : pluginNames) {
	        // Information about the plugin to use
	        String pluginFilePath = (String) sharedAttributes.getOrDefault("/" + pluginId + "/pluginFilePath", new SharedAttribute("pluginFilePath", "String", "")).getValue();
	        String pluginEntryPointClassPath = (String) sharedAttributes.getOrDefault("/" + pluginId + "/pluginEntryPointClassPath", new SharedAttribute("pluginEntryPointClassPath", "String", "")).getValue();
	        // Load plugin
	        AbstractPlugin plugin = pluginLoader.loadPlugin(pluginFilePath, pluginEntryPointClassPath);
	        this.allPlugins.put(pluginId, plugin);
	
	    }
	    //make all plugins accessable
	    
	}

    /**
     * Returns all pluginIds and all objectIds which use that plugin
     * @return
     */
    public ArrayList<Map<String, Object>> getAllObjects() {
    	logger.debug("Getting all objects...");
    	
        ArrayList<Map<String, Object>> allObjects = new ArrayList<>();

        for (String pluginId : allPluginNames) {
            Map<String, Object> pluginWithIDs = new HashMap<String, Object>();
            pluginWithIDs.put("pluginId", pluginId);
            
            Object o = sharedAttributes.get("/" + pluginId + "/objects").getValue(); // Gets an array list of all objects for this plugin
            if (o instanceof ArrayList) {
            	try {
            		ArrayList<Map<String, Object>> objectIds = (ArrayList<Map<String, Object>>) o;
            		pluginWithIDs.put("objects", objectIds);
                    allObjects.add(pluginWithIDs);
            	} catch(ClassCastException e) {
            		logger.error("Error getting all objects.", e);
            	}
            } else {
            	logger.error("Error getting all objects. Shared attribute not of type 'ArrayList'.");
            } 
        }
        return allObjects;
    }



    /** Gets the plugin by the id.
     * @param id
     * @return The plugin defined by the given id or null if it is not found.
     */
    public IPluginV1 getPluginById(String id) {
    	return this.allPlugins.get(id);
    }

    /** Get the shared attributes. 
     * @param reload Set this to true if you want to reload the shared attributes from the file system.
     * @return
     */
    public ConcurrentMap<String, SharedAttribute> getSharedAttributes(boolean reload) {
    	this.loadSharedAttributes(reload);
        return sharedAttributes;
    }
    
    public ConcurrentMap<String, SharedAttribute> getSharedAttributes() {
        return this.getSharedAttributes(false);
    }

    public void setSharedAttributes(ConcurrentMap<String, SharedAttribute> sharedAttributes) {
        this.sharedAttributes = sharedAttributes;
    }

    /** Get all plugins.
     * @param reload Set this to true if you want to reload the plugins.
     * @return
     */
    public ConcurrentMap<String, AbstractPlugin> getAllPlugins(boolean reload) {
    	this.loadPlugins(reload);
        return allPlugins;
    }
    
    public ConcurrentMap<String, AbstractPlugin> getAllPlugins() {
    	return this.getAllPlugins(false);
    }

    public void setAllPlugins(ConcurrentMap<String, AbstractPlugin> allPlugins) {
        this.allPlugins = allPlugins;
    }


}
