package de.ableitner.barrierfreeSmarthome.common.plugin;

import java.util.Locale;
import java.util.concurrent.ConcurrentMap;
import de.ableitner.barrierfreeSmarthome.common.SharedAttribute;

/**
 * This interface describes a plugin, which can be used to integrate some IoT
 * device into the barrier-free smarthome platform.
 * 
 * @author Tobias Ableitner
 *
 */
public interface IPluginV1 extends IPlugin {

	/**
	 * Getter for the id of a plugin. Each plugin needs an unique id.
	 * 
	 * @return an id of the plugin. The returned id will never be null or empty.
	 */
	public String getPluginId();

	/**
	 * Getter for the current locale object of a plugin. A plugin must not have
	 * a locale object.
	 * 
	 * @return the current locale object or null, if no locale object is set
	 */
	public Locale getLocale();

	/**
	 * Setter for the current locale object of a plugin. A plugin must not have
	 * a locale object.
	 * 
	 * @param locale
	 *            the locale object or null
	 */
	public void setLocale(Locale locale);

	/**
	 * Registers a plugin observer at a plugin.
	 * 
	 * @param pluginObserver
	 *            the plugin observer, which should be registered at the plugin.
	 * @return true if the parameter was not null, not already registered and
	 *         successfully registered at the plugin. Otherwise false will be
	 *         returned.
	 */
	public boolean addObserver(IPluginObserver pluginObserver);

	/**
	 * Deregisters a plugin observer from a plugin.
	 * 
	 * @param pluginObserver
	 *            the plugin observer, which should be deregistered at the
	 *            plugin.
	 * @return true if the parameter was not null, registered and successfully
	 *         deregistered at the plugin. Otherwise false will be returned.
	 */
	public boolean removeObserver(IPluginObserver pluginObserver);

	/**
	 * Calls the execution of an action inside a plugin. For example enable or
	 * disabling some light. The method caller can register it self as observer
	 * at the plugin to become informed about success / failure of the action's
	 * execution.
	 * 
	 * @param sharedAttributes
	 *            the set of global attributes, which can be red and changed by
	 *            the plugin.
	 * @param parameters
	 *            the parameters which describe the action and / or which are
	 *            needed for the execution of the action.
	 */
	public void action(ConcurrentMap<String, SharedAttribute> sharedAttributes, ConcurrentMap<String, String> parameters);

}
