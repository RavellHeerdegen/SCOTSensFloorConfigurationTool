package app.exceptions;

/**
 * This exception is thrown if there is no CarpetConfiguration loaded yet.
 * 
 * @author Florian Jungermann
 *
 */
public class NoCarpetConfigurationLoadedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7886113038460527299L;

	public NoCarpetConfigurationLoadedException() {
		// TODO Auto-generated constructor stub
	}

	public NoCarpetConfigurationLoadedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public NoCarpetConfigurationLoadedException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public NoCarpetConfigurationLoadedException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public NoCarpetConfigurationLoadedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
