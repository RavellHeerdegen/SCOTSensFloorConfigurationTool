package app.models;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.*;

import de.ableitner.barrierfreeSmarthome.common.SharedAttribute;
import de.ableitner.barrierfreeSmarthome.common.plugin.IPluginV1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents an action group on the carpet. One CarpetGroup can have several CarpetActions.
 * A CarpetGroup can be assigned to several coordinates.
 * 
 *
 */
@Entity
public class CarpetGroup {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;


	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parentConfiguration__id")
	@JsonIgnoreProperties("carpetGroups")
	public CarpetConfiguration parentConfiguration;

	private String groupName = "";
	
	@OrderColumn
	@OneToMany(fetch = FetchType.EAGER, targetEntity = CarpetAction.class, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name="group_id_stepon") //name of the column in the action table where to store the group id of the step on actions
	private List<CarpetAction> actionsStepOn = new ArrayList<CarpetAction>();

	@OrderColumn
	@OneToMany(fetch = FetchType.EAGER, targetEntity = CarpetAction.class, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name="group_id_stepoff") //name of the column in the action table where to store the group id of the step on actions
    private List<CarpetAction> actionsStepOff = new ArrayList<CarpetAction>();

	@OneToMany(fetch = FetchType.EAGER, targetEntity = CarpetCoordinate.class, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "CarpetGroupID")
    private List<CarpetCoordinate> fields = new ArrayList<CarpetCoordinate>();
    
	@Transient // Not stored in the db
	private State state = State.UNDEFINED; // Stores the state of the group. Is it active (stepped on)? Or inactive (not stepped on)? Or undefined? 
	
	@Transient // Not stored in the db
	Logger logger = LoggerFactory.getLogger(CarpetGroup.class);
	
	public enum State {
		STEPPEDON, STEPPEDOFF, UNDEFINED
	}
	
    public CarpetGroup(){
    }
    
    public CarpetGroup(String groupName) {
		this.groupName = groupName;
	}
	
	public CarpetGroup(String groupName, List<CarpetCoordinate> fields) {
		this(groupName);
		this.fields = fields;
	}
	
	
	/**
	 * Create a CarpetGroup with a group name, a single step on action on a single coordinate
	 * 
	 * @param groupName
	 * @param stepOncarpetAction
	 * @param assignedCarpetCoordinate
	 */
	public CarpetGroup(String groupName, CarpetAction stepOncarpetAction, CarpetCoordinate assignedCarpetCoordinate) {
		this(groupName);
		this.addStepOnCarpetAction(stepOncarpetAction);
		this.fields.add(assignedCarpetCoordinate);
	}
	
	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public void addFieldToGroup(int x, int y){
		this.getFields().add(new CarpetCoordinate(x,y));
	}
	
	/**
	 * @param c CarpetCoordinate to add
	 */
	public void addFieldToGroup(CarpetCoordinate c) {
		this.getFields().add(c);
	}

	public boolean assignedToField(CarpetCoordinate c) {
		return this.fields.contains(c);
	}
	
	/**
	 * @param x
	 * @param y
	 * @return True if this CarpetGroup is assigned to the field given by the coordinates
	 */
	public boolean assignedToField(int x, int y) {
		return this.assignedToField(new CarpetCoordinate(x,y));
	}
	
    
    /** Triggers all step on CarpetActions for this CarpetGroup. Only triggers if the step on actions were not triggered the last time already.
     *  You can override this behavior anyway, if you set force to true.
     * 
     * @param sharedAttributes
     * @param force If to to force the trigger anyway. Ignores if this was triggered directly before.
     */
    public void triggerStepOn(ConcurrentMap<String, SharedAttribute> sharedAttributes, boolean force){
    	if (force || this.state != State.STEPPEDON) {
    		for(CarpetAction carpetAction : actionsStepOn){
    			carpetAction.triggerConfiguration(sharedAttributes);
    		}
    		this.state = State.STEPPEDON;
    		logger.info("Triggered carpet group " + this.groupName + " step on state.");
    	} else {
    		logger.trace("Step on state of carpet group " + this.groupName + " not triggered because it was triggered before and/or option 'force' was set to false.");
    	}
    }
    
    /** 
     * Triggers all step off CarpetActions for this CarpetGroup. Only triggers if the step off actions were not triggered the last time already.
     *  You can override this behavior anyway, if you set force to true.
     * 
     * @param sharedAttributes
     * @param force If to to force the trigger anyway. Ignores if this was triggered directly before.
     */
    public void triggerStepOff(ConcurrentMap<String, SharedAttribute> sharedAttributes, boolean force) {
    	if (force || this.state != State.STEPPEDOFF) {
	    	for(CarpetAction carpetAction : actionsStepOff){
	            carpetAction.triggerConfiguration(sharedAttributes);
	        }
	    	this.state = State.STEPPEDOFF;
	    	logger.info("Triggered carpet group " + this.groupName + " step off state.");
    	} else {
    		logger.trace("Step off state of carpet group " + this.groupName + " not triggered because it was triggered before and/or option 'force' was set to false.");
    	}
    }

    /**Adds a CarpetAction to the step on list. Automatically sets the parentCarpetGroup attribute of this action.
     * @param plugin
     * @param parameters
     * @param actionName
     */
    public void addStepOnCarpetAction(CarpetAction action) {
    	action.setParentCarpetGroup(this);
    	this.actionsStepOn.add(action);
    	this.state = State.UNDEFINED;
    }
    
    /**Adds a CarpetAction to the step on list. Automatically sets the parentCarpetGroup attribute of this action.
     * @param plugin
     * @param parameters
     * @param actionName
     */
    public void addStepOnCarpetAction(IPluginV1 plugin, ConcurrentMap<String, String> parameters, String actionName){
    	CarpetAction action = new CarpetAction(plugin,parameters, actionName);
    	action.setParentCarpetGroup(this);
        this.actionsStepOn.add(action);
        this.state = State.UNDEFINED;
    }
    
    /**Adds a CarpetAction to the step off list. Automatically sets the parentCarpetGroup attribute of this action.
     * @param plugin
     * @param parameters
     * @param actionName
     */
    public void addStepOffCarpetAction(CarpetAction action) {
    	action.setParentCarpetGroup(this);
    	this.actionsStepOff.add(action);
    	this.state = State.UNDEFINED;
    }
    
    /**Adds a CarpetAction to the step off list. Automatically sets the parentCarpetGroup attribute of this action.
     * @param plugin
     * @param parameters
     * @param actionName
     */
    public void addStepOffCarpetAction(IPluginV1 plugin, ConcurrentMap<String, String> parameters, String actionName){
    	CarpetAction action = new CarpetAction(plugin,parameters, actionName);
    	action.setParentCarpetGroup(this);
        this.actionsStepOff.add(action);
        this.state = State.UNDEFINED;
    }
    
    
    /** Removes the given CarpetAction wherever it can find it in this object.
     * @param action
     */
    public void removeCarpetAction(CarpetAction action) {
    	Collection<CarpetAction> c = new ArrayList<CarpetAction>();
    	c.add(action);
    	this.actionsStepOff.removeAll(c);
    	this.actionsStepOn.removeAll(c);
    }
    
    // Getter and Setter

    public List<CarpetAction> getActionsStepOn() {
        return this.actionsStepOn;
    }

    /**
     * Sets the stepOn CarpetAction list. This automatically sets the parentCarpetGroup attribute on all the actions in the list.
     * 
     * @param carpetActions
     */
    public void setStepOnCarpetActions(List<CarpetAction> carpetActions) {
    	for (CarpetAction action: carpetActions) {
    		action.setParentCarpetGroup(this);
    	}
    	
        this.actionsStepOn = carpetActions;
        this.state = State.UNDEFINED;
    }
    
    public List<CarpetAction> getActionsStepOff() {
        return this.actionsStepOff;
    }

    /**
     * Sets the stepOff CarpetAction list. This automatically sets the parentCarpetGroup attribute on all the actions in the list.
     * 
     * @param carpetActions
     */
    public void setStepOffCarpetActions(List<CarpetAction> carpetActions) {
    	for (CarpetAction action: carpetActions) {
    		action.setParentCarpetGroup(this);
    	}
    	
        this.actionsStepOff = carpetActions;
        this.state = State.UNDEFINED;
    }
    
    public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Long getID() {
		return this.id;
	}

	public List<CarpetCoordinate> getFields() {
		return fields;
	}

	public void setFields(List<CarpetCoordinate> assignedCarpetCoordinates) {
		this.fields = assignedCarpetCoordinates;
		this.state = State.UNDEFINED;
	}
	
	public CarpetConfiguration getParentConfiguration() {
		return parentConfiguration;
	}

	public void setParentConfiguration(CarpetConfiguration parentConfiguration) {
		this.parentConfiguration = parentConfiguration;
	}

}
