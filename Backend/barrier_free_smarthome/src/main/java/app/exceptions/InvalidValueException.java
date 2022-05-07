package app.exceptions;

/**
 * Thrown if a value for a key is invalid for some reason.
 * 
 * @author Florian Jungermann
 *
 */
public class InvalidValueException extends ParameterException {

	public InvalidValueException() {
		// TODO Auto-generated constructor stub
	}

	public InvalidValueException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public InvalidValueException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidValueException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidValueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
