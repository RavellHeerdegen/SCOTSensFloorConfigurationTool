package app.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.ableitner.barrierfreeSmarthome.common.plugin.AbstractPlugin;
import de.ableitner.barrierfreeSmarthome.common.plugin.IPluginV1;
import app.Repositories.CarpetConfigurationRepository;
import app.models.CarpetAction;
import app.models.CarpetConfiguration;
import app.models.CarpetCoordinate;
import app.models.CarpetGroup;
import app.services.ConfigurationService.ActionType;
import plugins.tradfri.TradfriHelperTools;
import plugins.tradfri.TradfriPlugin;

/**
 * This class is for testing only.
 * 
 *
 */
@Service
public class TestService {

	Logger logger = LoggerFactory.getLogger(TestService.class);
	
	public ConfigurationService configurationService;
	public PluginService pluginService;
	
	
    public CarpetConfigurationRepository carpetRepository;
	
    public TestService(ConfigurationService configurationService, PluginService pluginService, CarpetConfigurationRepository carpetRepository){
    	this.configurationService = configurationService;
    	this.pluginService = pluginService;
    	this.carpetRepository = carpetRepository;
    	
    	// Comment the tests in or out as you wish:
    	//fillDBWithTestData();
    	//loadUserConfigFromDB();
    	
    	//newGroupIntoDB();
    	
    	//actionAddDeleteTest();
    	
    	//changeActionParamsTest();
    	
    	//tradfriPluginTest();
    }
    
    
    /**
     * Load user config from database
     */
    public void loadUserConfigFromDB() {
    	logger.info("Test: Load user config from db.");
    	configurationService.loadUserConfig("testuserid");
    }
    
    /**
     * Create a new group and put it into the database
     */
    public void newGroupIntoDB() {
    	logger.info("Test: new Group to db.");
        ArrayList<CarpetCoordinate> array = new ArrayList<CarpetCoordinate>();
        array.add(new CarpetCoordinate(1, 2));
        long id = configurationService.createGroup("testgroup1", array);
        
        System.out.println("Id of the new group is: " + id);
    }
    
    
    /**
     * Tests if an action can be added and deleted
     */
    public void actionAddDeleteTest() {
    	logger.info("Test: adding action and trying to delete afterwards.");
    	
    	ConcurrentMap<String, String> params = new ConcurrentHashMap<String, String>();
    	
    	Long group_id = 1002L; // In order to work this group must exist
    	Long action_id = configurationService.addActionToGroup(group_id, "tradfri_light", params, "testactionname", ActionType.ONENTER);
    	
    	logger.info("Added action id is: " + action_id.toString());
    	logger.info("Now trying to delete this action...");
    	
    	configurationService.deleteAction(action_id);
    }
    
    public void changeActionParamsTest() {
    	long actionId = 1003L;
    	ConcurrentMap<String, String> paramsMap = new ConcurrentHashMap<String, String>();
    	configurationService.changeParamsForAction(actionId, paramsMap);
    }
    
    /**
     * Fills the database with test data
     */
    public void fillDBWithTestData() {
    	logger.info("Test: Fill DB with test data");
    	ConcurrentMap<String, AbstractPlugin> allPlugins = pluginService.getAllPlugins();
    	
        ConcurrentMap<String, String> hue_parameters = new ConcurrentHashMap<String, String>();
        hue_parameters.put("plugin", "hue_light");
        hue_parameters.put("id", "4");
        hue_parameters.put("action", "switch");
        hue_parameters.put("on", "false");
        hue_parameters.put("brightness", "254");

        CarpetAction hue_action = new CarpetAction(allPlugins.get("hue_light"), hue_parameters, "hue_action");
        CarpetGroup cg_hue = new CarpetGroup("hue group", hue_action, new CarpetCoordinate(2, 2));

        CarpetAction hue_action2 = new CarpetAction(allPlugins.get("hue_light"), hue_parameters, "hue_action");
        cg_hue.addStepOffCarpetAction(hue_action2);


        ConcurrentMap<String, String> tradfri_parameters = new ConcurrentHashMap<String, String>();
        tradfri_parameters.put("plugin", "tradfri_light");
        tradfri_parameters.put("bulb_id", "65584");
        tradfri_parameters.put(TradfriPlugin.KEY_LAMPON, TradfriPlugin.VALUE_LAMPON_TRUE);
        tradfri_parameters.put(TradfriPlugin.KEY_BRIGHTNESS, "100");
        tradfri_parameters.put(TradfriPlugin.KEY_COLOR_HEX, TradfriPlugin.VALUE_COLOR_5);

        CarpetAction tradfri_action = new CarpetAction(allPlugins.get("tradfri_light"), tradfri_parameters, "tradfri_action");
        CarpetGroup cg_tradfri = new CarpetGroup("tradfri group", tradfri_action, new CarpetCoordinate(3, 2));
        
        ConcurrentMap<String, String> tradfri_parameters_2 = new ConcurrentHashMap<String, String>();
        tradfri_parameters_2.put("plugin", "tradfri_light");
        tradfri_parameters_2.put("bulb_id", "65584");
        //tradfri_parameters_2.put(TradfriPlugin.KEY_COLOR_HEX, TradfriPlugin.VALUE_COLOR_20);
        //tradfri_parameters_2.put(TradfriPlugin.KEY_BRIGHTNESS, "255");

        CarpetAction tradfri_action2 = new CarpetAction(allPlugins.get("tradfri_light"), tradfri_parameters_2, "tradfri2_action");
        CarpetGroup cg_tradfri2 = new CarpetGroup("tradfri group 2", tradfri_action2, new CarpetCoordinate(3, 1));

        ConcurrentMap<String, String> tradfri_parameters_3 = new ConcurrentHashMap<>();
        tradfri_parameters_3.put("plugin", "tradfri_light");
        tradfri_parameters_3.put("bulb_id", "65584");
        tradfri_parameters_3.put(TradfriPlugin.KEY_LAMPON, TradfriPlugin.VALUE_LAMPON_FALSE);
        //tradfri_parameters_3.put(TradfriPlugin.KEY_BRIGHTNESS, "255");
        
        CarpetAction tradfri_action3 = new CarpetAction(allPlugins.get("tradfri_light"), tradfri_parameters_3, "tradfri3_action");
        CarpetGroup cg_tradfri3 = new CarpetGroup("tradfri group 3", tradfri_action3, new CarpetCoordinate(3, 3));

        List<CarpetGroup> carpetGroups = new ArrayList<CarpetGroup>();
        carpetGroups.add(cg_hue);
        carpetGroups.add(cg_tradfri);
        carpetGroups.add(cg_tradfri2);
        carpetGroups.add(cg_tradfri3);
        
        CarpetConfiguration cconf = new CarpetConfiguration("testuserid");
        cconf.setCarpetGroups(carpetGroups);
        
        carpetRepository.save(cconf);

        //ConcurrentMap<String, SharedAttribute> sharedAttributes = pluginService.getSharedAttributes();
    }
    
    public void tradfriPluginTest() {
    	logger.info("Tradfri plugin test running now...");
    	//TradfriHelperTools.discoverBulbs("192.168.0.192", "test_identity_2", "MTwP1RzGNkbPMUfm");
    	ConcurrentMap<String, String> parameters = new ConcurrentHashMap<String, String>();
    	
    	parameters.put(TradfriPlugin.KEY_BULB_ID, "65586");
    	parameters.put(TradfriPlugin.KEY_LAMPON, "false");
    	parameters.put(TradfriPlugin.KEY_COLOR, TradfriPlugin.VALUE_COLOR_BLUE);
    	//parameters.put(TradfriPlugin.KEY_BRIGHTNESS, "255");
    	
    	IPluginV1 plugin = pluginService.getPluginById("tradfri_light");
    	plugin.action(pluginService.getSharedAttributes(), parameters);
    	
    	logger.info("Tradfri plugin test finished.");
    }
}
