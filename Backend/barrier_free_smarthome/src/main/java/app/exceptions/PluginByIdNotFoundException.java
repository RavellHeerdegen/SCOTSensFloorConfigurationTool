package app.exceptions;

/**
 * This exception is thrown if a plugin with a given id can not be found.
 * 
 * @author Florian Jungermann
 *
 */
public class PluginByIdNotFoundException extends RuntimeException {

	public PluginByIdNotFoundException() {
		// TODO Auto-generated constructor stub
	}

	public PluginByIdNotFoundException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public PluginByIdNotFoundException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public PluginByIdNotFoundException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public PluginByIdNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
