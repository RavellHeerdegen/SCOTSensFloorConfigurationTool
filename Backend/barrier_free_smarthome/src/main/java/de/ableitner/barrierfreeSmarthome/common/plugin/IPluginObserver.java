package de.ableitner.barrierfreeSmarthome.common.plugin;

import de.ableitner.barrierfreeSmarthome.common.BarrierfreeSmarthomeException;

/**
 * This interface defines observers, which can be registered at a plugin
 * {@link #IPluginV1}.
 * 
 * @author Tobias Ableitner
 *
 */
public interface IPluginObserver {

	/**
	 * This method will be called by a plugin, if an action was executed
	 * successfully.
	 * 
	 * @param successMessage
	 *            optional info message, which can also be an empty string or
	 *            null.
	 */
	public void onSuccess(String successMessage);

	/**
	 * This method will be called by a plugin, to inform it observers about
	 * something. For example with some logging messages.
	 * 
	 * @param infoMessage
	 *            a info message, which can also be null or empty.
	 */
	public void onInfo(String infoMessage);

	/**
	 * This method will be called by a plugin, if the execution of an action
	 * failed.
	 * 
	 * @param exception
	 *            the thrown exception. It cannot be null.
	 * @param errorMessage
	 *            an internal error message for debugging. The message can also
	 *            be null or empty.
	 * @param userErrorMessage
	 *            an error message, which can be presented to the user. The
	 *            message can also be null or empty.
	 */
	public void onError(Exception exception, String errorMessage, String userErrorMessage);

	/**
	 * This method will be called by a plugin, if the execution of an action
	 * failed and
	 * {@link #de.ableitner.barrierfreeSmarthome.common.BarrierfreeSmarthomeException}
	 * exception was thrown. Normally the exception contains one error message
	 * for debugging and another one, which can be presented to the user.
	 * 
	 * @param exception
	 *            the thrown exception. It cannot be null.
	 */
	public void onError(BarrierfreeSmarthomeException exception);

}
