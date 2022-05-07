package app.exceptions;

/**
 * Thrown if the given parameters of a rest call are invalid. Most of the times you want to use subclasses of this.
 * 
 * @author Florian Jungermann
 *
 */
public class ParameterException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6019471595221811686L;

	public ParameterException() {
		// TODO Auto-generated constructor stub
	}

	public ParameterException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ParameterException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public ParameterException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ParameterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
