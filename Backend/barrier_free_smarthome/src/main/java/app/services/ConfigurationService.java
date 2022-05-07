package app.services;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.ableitner.barrierfreeSmarthome.common.plugin.IPluginV1;
import app.Carpet;
import app.Repositories.CarpetActionRepository;
import app.Repositories.CarpetConfigurationRepository;
import app.Repositories.CarpetGroupRepository;
import app.exceptions.CarpetActionNotFoundException;
import app.exceptions.CarpetGroupNotFoundException;
import app.exceptions.NoCarpetConfigurationLoadedException;
import app.exceptions.PluginByIdNotFoundException;
import app.models.CarpetAction;
import app.models.CarpetConfiguration;
import app.models.CarpetCoordinate;
import app.models.CarpetGroup;

/**
 * 
 * Service used to alter, create or delete the CarpetConfiguration.
 *
 */
@Service
public class ConfigurationService {

    Logger logger = LoggerFactory.getLogger(ConfigurationService.class);

    private Carpet carpet;
    private PluginService pluginService;

    @Autowired
    private CarpetConfigurationRepository carpetConfRepo;
    @Autowired
    private CarpetGroupRepository carpetGroupRepository;
    @Autowired
    private CarpetActionRepository carpetActionRepository;

    public ConfigurationService(Carpet carpet, PluginService pluginService) {
        this.carpet = carpet;
        this.pluginService = pluginService;
    }

    
    public enum ActionType {
    	ONENTER, ONEXIT
    }
    
    /**
     * Adds an CarpetAction to a group.
     * 
     * @param groupId Id of the CarpetGroup.
     * @param pluginId Id of the plugin.
     * @param paramsMap The parameters of the CarpetAction.
     * @param actionName The name of the CarpetAction.
     * @param actionType The type of the CarpetAction.
     * @return
     * @throws NoCarpetConfigurationLoadedException
     * @throws CarpetGroupNotFoundException
     * @throws PluginByIdNotFoundException
     */
    @Transactional
    public long addActionToGroup(Long groupId, String pluginId,  ConcurrentMap<String, String> paramsMap, String actionName, ActionType actionType) throws NoCarpetConfigurationLoadedException, CarpetGroupNotFoundException, PluginByIdNotFoundException {
    	logger.debug("Adding action " + actionName + " to group...");
    	
    	if (carpet.getCarpetConfiguration() == null) {
        	String msg = "Can not add CarpetAction " + actionName + " to CarpetGroup " + groupId + ". No CarpetConfiguration loaded.";
    		logger.error(msg);
    		throw new NoCarpetConfigurationLoadedException(msg);
    	}
    	
    	logger.trace("Getting CarpetGroup with id {}.", groupId);
        CarpetGroup carpetGroup = carpet.getGroupWithId(groupId);

        if (carpetGroup == null) {
        	String msg = "CarpetGroup with id " + groupId + " not found.";
        	logger.error(msg);
        	throw new CarpetGroupNotFoundException(msg);
        }
        
        logger.trace("Getting plugin with id {}...", pluginId);
        IPluginV1 plugin = this.pluginService.getPluginById(pluginId);
        if (plugin == null) {
        	String msg = "Plugin given by the id " + pluginId + " could not been found.";
        	logger.error(msg);
        	throw new PluginByIdNotFoundException(msg);
        }
        

        if (paramsMap == null) {
        	throw new IllegalArgumentException("paramsMap must not be null!");
        }
        
        // At this point (plugin != null && paramsMap != null && carpetGroup != null) should always be true
        
        logger.trace("Creating new CarpetAction with name {} and saving it...", actionName);
        CarpetAction carpetAction = new CarpetAction(plugin, paramsMap, actionName);
        CarpetAction saved_action = carpetActionRepository.saveAndFlush(carpetAction); // Saving returns the updated instance of the object.
        
        logger.trace("Id of new CarpetAction is {}.", saved_action.getId());
        
        if (actionType == ActionType.ONEXIT) {
        	logger.trace("Saving action as step off action...");
            carpetGroup.addStepOffCarpetAction(saved_action);
        } else {
        	logger.trace("Saving action as step on action...");
            carpetGroup.addStepOnCarpetAction(saved_action);
        }
        
        logger.trace("Saving the changed CarpetGroup...");
        logger.trace("CarpetGroup id is {}.", carpetGroup.getID());
        
        carpetGroupRepository.saveAndFlush(carpetGroup);
        
        logger.trace("Returning new action id...");
        return saved_action.getId();
    }

    /**
     * Changes the parameters for a CarpetAction.
     * 
     * @param actionId The id of the CarpetAction.
     * @param params The parameter the CarpetAction now should have.
     * @throws NoCarpetConfigurationLoadedException
     * @throws CarpetActionNotFoundException
     */
    public void changeParamsForAction(Long actionId, ConcurrentMap<String, String> params) throws NoCarpetConfigurationLoadedException, CarpetActionNotFoundException {
        logger.debug("Changing the parameters for action {}...", actionId );
        
        if (carpet.getCarpetConfiguration() == null) {
        	String msg = "Can not change parameters for CarpetAction " + actionId + ". No CarpetConfiguration loaded.";
    		throw new NoCarpetConfigurationLoadedException(msg);
    	}
        
        CarpetAction carpetAction;
    	try {
    		carpetAction = carpetActionRepository.findById(actionId).get();
    		// Make sure this carpet action is actually part of the current CarpetConfiguration or belongs to the current user? Otherwise it is possible to delete stuff from other users.
    	} catch (NoSuchElementException e) {
    		String msg = "No CarpetAction with id " + actionId + " found.";
    		throw new CarpetActionNotFoundException(msg);
    	}
    	
    	carpetAction.setParameters(params);
    	carpetActionRepository.saveAndFlush(carpetAction);  
    }

    
    /**
     * Delete a CarpetAction.
     * 
     * @param actionId The id of the CarpetAction.
     * @throws NoCarpetConfigurationLoadedException
     * @throws CarpetActionNotFoundException
     */
    public void deleteAction(Long actionId) throws NoCarpetConfigurationLoadedException, CarpetActionNotFoundException {
    	logger.debug("Trying to delete action " + actionId);
    	
    	if (carpet.getCarpetConfiguration() == null) {
        	String msg = "Can not delete CarpetAction " + actionId + ". No CarpetConfiguration loaded.";
    		throw new NoCarpetConfigurationLoadedException(msg);
    	}   	
        
    	CarpetAction carpetAction;
    	try {
    		carpetAction = carpetActionRepository.findById(actionId).get();
    		// Make sure the given carpet action actually belongs to the current user (or is part of the loaded carpetconfiguration)?
    	} catch (NoSuchElementException e) {
    		String msg = "No CarpetAction with id " + actionId + " found.";
    		throw new CarpetActionNotFoundException(msg);
    	}
    	    	
    	// Get the CarpetGroup this CarpetAction belongs to
    	CarpetGroup parentCarpetGroup = carpetAction.getParentCarpetGroup();
    	parentCarpetGroup.removeCarpetAction(carpetAction);
    	carpetAction.setParentCarpetGroup(null);
    	carpetActionRepository.delete(carpetAction);
        carpetGroupRepository.saveAndFlush(parentCarpetGroup);
    }


    /** Resets the Group's CarpetCoordinates to the given coordinates. Note: This removes any other CarpetCoordinates before.
     * 
     * @param groupId
     * @param fields
     * @return
     */
    public void changeGroupFields(Long groupId, ArrayList<CarpetCoordinate> fields) throws NoCarpetConfigurationLoadedException, CarpetGroupNotFoundException {  
        if (carpet.getCarpetConfiguration() == null) {
        	String msg = "Can not change group fields for CarpetGroup " + groupId + ". No CarpetConfiguration loaded.";
    		throw new NoCarpetConfigurationLoadedException(msg);
    	}
        
        CarpetGroup group = carpet.getGroupWithId(groupId);
        
        if (group == null) {
        	throw new CarpetGroupNotFoundException("Error changing group fields for group " + groupId + ". CarpetGroup with this id not found.");
        }
         
    	group.getFields().clear();
        
    	for (CarpetCoordinate field: fields) {
    		group.addFieldToGroup(field);
        }
        carpetConfRepo.save(carpet.getCarpetConfiguration());
    }


    /** Deletes a CarpetGroup from the current CarpetConfiguration.
     * 
     * @param groupId The Id of the group that should be deleted.
     * @throws CarpetGroupNotFoundException If no CarpetGroup with the given id is found in the current configuration. Note: It can be that there is a CarpetGroup with this id in the database. But it will only be deleted if this is the CarpetGroup of the current user. This prevents from removing CarpetGroups of other users.
     * 
     */
    public void deleteGroup(Long groupId) throws NoCarpetConfigurationLoadedException, CarpetGroupNotFoundException {
    	logger.debug("Deleting group with id: {}", groupId);
    	
    	if (carpet.getCarpetConfiguration() == null) {
    		throw new NoCarpetConfigurationLoadedException("Can not delete group " + groupId + ": No CarpetConfigurationen loaded.");
    	}
    	
        CarpetGroup group = carpet.getGroupWithId(groupId);
        
        if (group == null) {
        	// Remove CarpetGroup only if it is the CarpetGroup of the current CarpetConfiguration.
        	// This prevents from deleting other user's CarpetGroups.       	
        	throw new CarpetGroupNotFoundException("No CarpetGroup with id " + groupId + " found in current CarpetConfiguration of user " + carpet.getCarpetConfiguration().getUserID() + ".");
        }
        
        logger.trace("Size of list: {}", carpet.getCarpetGroups().size());
        
        CarpetConfiguration carpetConf = group.getParentConfiguration();
        carpetConf.removeCarpetGroup(group);
        group.setParentConfiguration(null);
        
        carpetConfRepo.saveAndFlush(carpetConf);
        
        logger.trace("Now, Size of list: {}",  carpet.getCarpetGroups().size());
    }


    /** Create a new CarpetGroup, apply it to the given fields and save it in the CarpetConfiguration.
     * 
     * @param groupName The name of the new group
     * @param fields The fields to should be applied to.
     * @return The ID of the new created group.
     * 
     * @throws NoCarpetConfigurationLoadedException
     */
    public long createGroup(String groupName, ArrayList<CarpetCoordinate> fields) throws NoCarpetConfigurationLoadedException {
    	logger.debug("Creating group with name: {}", groupName);

    	if (carpet.getCarpetConfiguration() == null) {
    		logger.error("Can not create group " + groupName + ": No CarpetConfigurationen loaded.");
    		throw new NoCarpetConfigurationLoadedException("Can not create group " + groupName + ": No CarpetConfigurationen loaded.");
    	}
    	
        CarpetGroup group = new CarpetGroup();
        group.setParentConfiguration(carpet.getCarpetConfiguration());
        group.setGroupName(groupName);

        // Add all the CarpetCoordinates
        for (CarpetCoordinate field: fields) {
                    group.addFieldToGroup(field);      
        }
            
        //carpetConfRepo.saveAndFlush(carpet.getCarpetConfiguration());
        group = carpetGroupRepository.saveAndFlush(group);
        carpet.addCarpetGroup(group);
        return group.getID();
       
    }

    /** Gets the CarpetConfiguration of the user and loads it to the Carpet. If there is no CarpetConfiguration for this user yet, a new CarpetConfiguration will be created.
     * 
     * @param userId The Id of ther user.
     * @return Loaded CarpetConfiguration. This is the first found CarpetConfiguration which belongs to the specified user.
     */
    public CarpetConfiguration loadUserConfig(String userId) {

    	logger.debug("Load user config for userid {}", userId);
    	
        CarpetConfiguration carpet_config = null;
        List<CarpetConfiguration> config = carpetConfRepo.findByUserID(userId);
        if (config.size() > 0) {
        	logger.trace("Carpet config for userid {} found.", userId);
            carpet_config = config.get(0); // What, if there are more than one configurations per user?

        } else {
        	logger.debug("No carpet config for userid {} found. Creating a new one...", userId);
            carpet_config = new CarpetConfiguration(userId);
            logger.trace("Saving the new created carpet configd...");
            carpetConfRepo.save(carpet_config);
        }

        logger.debug("Setting the plugins for the actions.");
        for (CarpetGroup group : carpet_config.getCarpetGroups()) {
        	logger.trace("Current carpet group: {}", group.getGroupName());
            for (CarpetAction action : group.getActionsStepOn()) {
            	logger.trace("Setting plugin for stepon action:   " + action.getActionName());
                action.setPlugin(pluginService.getPluginById(action.getPluginID()));
            }
            for (CarpetAction action : group.getActionsStepOff()) {
                logger.trace("Setting plugin for stepoff action:  " + action.getActionName());
                action.setPlugin(pluginService.getPluginById(action.getPluginID()));
            }
        }

        carpet.setCarpetConfiguration(carpet_config);
        logger.debug("Plugins were set");

        return carpet_config;
    }

}
