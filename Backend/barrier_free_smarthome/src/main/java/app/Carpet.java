package app;

import app.models.CarpetAction;
import app.models.CarpetConfiguration;
import app.models.CarpetCoordinate;
import app.models.CarpetGroup;
import app.services.PluginService;
import de.ableitner.barrierfreeSmarthome.common.SharedAttribute;
import de.ableitner.barrierfreeSmarthome.common.plugin.IPluginV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * This class represents the carpet with its active configuration. This is a spring component. So there is always only one carpet.
 *
 * 
 */
@Component
public class Carpet {
	
	Logger logger = LoggerFactory.getLogger(Carpet.class);
	
    ConcurrentMap<String, SharedAttribute> sharedAttributes;
    private CarpetConfiguration carpetConfiguration;


    /**
     * @param pluginService
     */
    public Carpet(PluginService pluginService) {
        this.sharedAttributes = pluginService.getSharedAttributes();

    }


    /**
     * Change the field that is active. This triggers all the CarpetActions in the CarpetGroups on this position.
     *
     * @param x X position of the field to turn active.
     * @param y Y position of the field to turn active.
     */
    public void changeActiveField(int x, int y) {
    	logger.info("Carpet: Changing active field to {}, {}.", x, y);
    	
        if (this.carpetConfiguration != null) {
        	// Trigger all other carpet groups inactive/step off state
        	List<CarpetGroup> inactiveGroups = getCarpetGroupsNotAtPosition(x, y);
        	for (CarpetGroup cgi : inactiveGroups) {
        		cgi.triggerStepOff(sharedAttributes, false);
        	}
        	
        	// Trigger all carpet groups on that position active/step on state
            List<CarpetGroup> activeGroups = getCarpetGroupsAtPosition(x, y);
            for (CarpetGroup cg : activeGroups) {
                cg.triggerStepOn(sharedAttributes, false);
            }
        }
    }

    /**
     * Change the field that is active. This triggers all the CarpetActions in the CarpetGroups on this position.
     * @param c
     */
    public void changeActiveField(CarpetCoordinate c){
        this.changeActiveField(c.x, c.y);
    }

    /**
     * Adds a CarpetAction to a specific field on the carpet.
     * Adds the CarpetAction only if the field has no CarpetGroup assigned yet or of there is only one CarpetGroup assigned to that position.
     * Returns true of adding was successful, false if not.
     * This changes the CarpetConfiguration the Carpet uses.
     *
     * @param x            X position of the field
     * @param y            Y position of the field
     * @param carpetAction CarpetAction that should be added to this carpet
     * @return Returns true if the CarpetAction was added successfully or false if it was not.
     */
    public boolean addCarpetActionIfEmptyOrOnlyOneCarpetGroup(int x, int y, CarpetAction carpetAction) {
        List<CarpetGroup> cg = this.getCarpetGroupsAtPosition(x, y);

        if (cg.size() > 1) {
            return false; //Returns false if there are more than one CarpetGroup assigned to this position.
        }
        if (cg.size() == 1) {
            // On this position is only one CarpetGroup. Add the CarpetAction to this
            this.carpetConfiguration.getCarpetGroups().get(0).addStepOnCarpetAction(carpetAction);
            return true;
        }
        if (cg.isEmpty()) {
            // Create a new CarpetAction and add it to the position
            // Add the coordinates
            List<CarpetCoordinate> cc = new ArrayList<CarpetCoordinate>();
            cc.add(new CarpetCoordinate(x, y));
            // Create the new CarpetGroup
            CarpetGroup new_cf = new CarpetGroup("No Group name", cc);
            new_cf.addStepOnCarpetAction(carpetAction);
            // Add the new CarpetGroup to the carpet
            this.carpetConfiguration.getCarpetGroups().add(new_cf);
            return true;
        }

        return false;
    }

    /**
     * Adds a CarpetAction to a specific field on the carpet.
     * Adds the CarpetAction only if the field has no CarpetGroup assigned yet or of there is only one CarpetGroup assigned to that position.
     * Returns true of adding was successful, false if not.
     * This changes the CarpetConfiguration the Carpet uses.
     *
     * @param x          X position of the field
     * @param y          Y position of the field
     * @param plugin     The plugin which is used to interpret the parameters.
     * @param parameters The parameters to use.
     */
    public boolean addConfiguredPluginIfEmptyOrOnlyOneCarpetField(int x, int y, IPluginV1 plugin, ConcurrentMap<String, String> parameters) {
        CarpetAction ca = new CarpetAction(plugin, parameters, "actionName");
        return this.addCarpetActionIfEmptyOrOnlyOneCarpetGroup(x, y, ca);
    }

    /**
     * Adds a CarpetGroup to the CarpetConfiguration.
     *
     * @param carpetGroup The CarpetGroup to add.
     */
    public void addCarpetGroup(CarpetGroup carpetGroup) {
        this.carpetConfiguration.addCarpetGroup(carpetGroup);
    }
    
    /** Removes a CarpetGroup from the active CarpetConfiguration.
     * 
     * @param carpetGroup
     */
    public void removeCarpetGroup(CarpetGroup carpetGroup) {
    	this.carpetConfiguration.removeCarpetGroup(carpetGroup);
    }


    /**
     * Get the CarpetGroups assigned to a specific position on the carpet.
     *
     * @param x X Position
     * @param y Y Position
     * @return A List of CarpetGroups on that position
     */
    public List<CarpetGroup> getCarpetGroupsAtPosition(int x, int y) {
        List<CarpetGroup> result = new ArrayList<CarpetGroup>();
        for (CarpetGroup cg : this.carpetConfiguration.getCarpetGroups()) {
            if (cg.assignedToField(x, y)) {
                result.add(cg);
            }
        }
        return result;
    }
    
    /** Get the CarpetGroups not assigned to a specific position.
     * 
     * @param x X Position
     * @param y Y Position
     * @return A List of CarpetGroups that are not on that position
     */
    public List<CarpetGroup> getCarpetGroupsNotAtPosition(int x, int y) {
        List<CarpetGroup> result = new ArrayList<CarpetGroup>();
        for (CarpetGroup cg : this.carpetConfiguration.getCarpetGroups()) {
            if (!cg.assignedToField(x, y)) {
                result.add(cg);
            }
        }
        return result;
    }

    /** Get all CarpetGroups in this Carpet's CarpetConfiguration.
     * 
     * @return
     */
    public List<CarpetGroup> getCarpetGroups() {
        return this.carpetConfiguration.getCarpetGroups();
    }

    /** Returns the CarpetGroup of with the given id. Returns null if there is no CarpetGroup with this id in this Carpet.
     * 
     * @param id
     * @return
     */
    public CarpetGroup getGroupWithId(Long id) {
        if (this.carpetConfiguration != null) {
            for (CarpetGroup field : this.carpetConfiguration.getCarpetGroups()) {
                if (field.getID().equals(id)) {
                    return field;
                }
            }
        }

        return null;
    }

    public ConcurrentMap<String, SharedAttribute> getSharedAttributes() {
        return sharedAttributes;
    }

    public void setSharedAttributes(ConcurrentMap<String, SharedAttribute> sharedAttributes) {
        this.sharedAttributes = sharedAttributes;
    }

    /** Set the CarpetConfiguration the Carpet should use.
     * 
     * @param carpetConfiguration
     */
    public void setCarpetConfiguration(CarpetConfiguration carpetConfiguration) {
        this.carpetConfiguration = carpetConfiguration;
    }

    /** Get the CarpetConfiguration that is currently applied to this Carpet.
     * 
     * @return
     */
    public CarpetConfiguration getCarpetConfiguration() {
        return this.carpetConfiguration;
    }


}



