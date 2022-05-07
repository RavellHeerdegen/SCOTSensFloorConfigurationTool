package app.models;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.*;

import de.ableitner.barrierfreeSmarthome.common.SharedAttribute;
import de.ableitner.barrierfreeSmarthome.common.plugin.IPluginV1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Container for a plugin and its configuration.
 * 
 * @author Florian Jungermann
 *
 */
@Entity
public class CarpetAction {
	public class PluginNotSetException extends RuntimeException {
		private static final long serialVersionUID = -8022572786630179583L;
		public PluginNotSetException(String msg, Throwable err) {
			super(msg, err);
		}
		public PluginNotSetException(String msg) {
			super(msg);
		}
	}

	private Long id;
	private CarpetGroup parentCarpetGroup;
	private String actionName;
	private IPluginV1 plugin; // NEVER set this directly! Always use the setter method!!! Even from inside this class!!! This can be null! This also is not loaded automatically from the database
	private String pluginID;  // NEVER set this directly! Always use the setter method!!! Even from inside this class!!!
	private ConcurrentMap<String, String> parameters;


	/**
	 * @param plugin The plugin used to communicate with the thing
	 * @param parameters The parameters with the configuration to use.
	 */
	public CarpetAction(IPluginV1 plugin, ConcurrentMap<String,String> parameters, String actionName) {
		this.parameters = parameters;
		this.setPlugin(plugin);
		this.setActionName(actionName);
	}
	
	/**
	
	/**
	 * Do not use this constructor directly
	 */
	protected CarpetAction() {
		// Constructor needed for hibernate.
	}
	
	
	/**
	 * Triggers the plugin's action and applies the parameters. Note: The plugin variable has to be set according to the pluginId variable. Otherwise this will throw a PluginNotSetException.
	 * 
	 * @param sharedAttributes
	 */
	public void triggerConfiguration(ConcurrentMap<String, SharedAttribute> sharedAttributes) throws PluginNotSetException {
		if (this.plugin == null) {
			throw new PluginNotSetException("The plugin variable is not set! Please set it using the pluginID which is " + this.pluginID);
		}
		this.plugin.action(sharedAttributes, new ConcurrentHashMap<String, String>(this.parameters));
	}
	

	// Getter and Setter
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	
	/**
	 * @return The plugin this configuration uses.
	 */
	@Transient // Will be ignored by JPA and not synced to the database
	public IPluginV1 getPlugin() {
		return plugin;
	}

	
	/** Sets the plugin. Also sets the pluginID to plugin.getPluginId()
	 * 
	 * @param plugin The plugin this configuration uses
	 */
	public void setPlugin(IPluginV1 plugin) {
		if (plugin != null) {
			this.pluginID = plugin.getPluginId();
		}
		this.plugin = plugin;
	}
	
	/**
	 * @return The plugin's id or an empty string if there is no pluginid.
	 */
	public String getPluginID() {
		return this.pluginID;
	}

	
	/** Sets the plugin id. If there is already a not null plugin which has another id the plugin will be set to null.
	 * 
	 * @param pluginID The id of the plugin
	 */
	public void setPluginID(String pluginID) {
		if (this.plugin != null) {
			if (!this.plugin.getPluginId().equals(pluginID)) {
				this.plugin = null; // Set the plugin to null. This is necessary because the pluginID always has to match plugin.getPluginId()
			}
		}
		this.pluginID = pluginID;
	}

	/** Get the parameters of the action
	 * @return ConcurrentHashMap (can always be casted to a ConcurrentHashMap)
	 */
	@ElementCollection // this is a collection of primitives
    @MapKeyColumn(name="parameter_key") // column name for map "key"
    @Column(name="parameter_value") // column name for map "value"
	@CollectionTable(name="parameters", joinColumns=@JoinColumn(name="CarpetActionID")) // Where to store this Map
	public Map<String, String> getParameters() {
		return this.parameters;
	}

	/** Set the parameters of the action
	 * @param parameters
	 */
	public void setParameters(Map<String, String> parameters) {
		if (parameters == null) {
			this.parameters = null;
		} else if (parameters instanceof ConcurrentMap) {
			this.parameters = (ConcurrentMap<String, String>) parameters;
		} else {
			this.parameters = new ConcurrentHashMap<String, String>(parameters);
		}
	}


	@ManyToOne(fetch = FetchType.EAGER)
	//@JoinColumn(name = "parentCarpetGroup__id")
	//@JoinTable(name="actions_groups", joinColumns = @JoinColumn(name="group_id"), inverseJoinColumns = @JoinColumn(name="action_id"))
	@JsonIgnore
	@JsonProperty(value = "parentgroup")
	public CarpetGroup getParentCarpetGroup() {
		return parentCarpetGroup;
	}

	public void setParentCarpetGroup(CarpetGroup parentCarpetGroup) {
		this.parentCarpetGroup = parentCarpetGroup;
	}

}
