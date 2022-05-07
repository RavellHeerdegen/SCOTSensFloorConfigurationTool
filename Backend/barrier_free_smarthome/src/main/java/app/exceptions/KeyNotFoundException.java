package app.exceptions;

/**
 * If a mandatory key is not set.
 * 
 * @author Florian Jungermann
 *
 */
public class KeyNotFoundException extends ParameterException {

	public KeyNotFoundException() {
		// TODO Auto-generated constructor stub
	}

	public KeyNotFoundException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public KeyNotFoundException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public KeyNotFoundException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public KeyNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
