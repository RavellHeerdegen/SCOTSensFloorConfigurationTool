package app.exceptions;

/**
 * This exception is thrown if a CarpetAction can not be found.
 * 
 * @author Florian Jungermann
 *
 */
public class CarpetActionNotFoundException extends RuntimeException {

	public CarpetActionNotFoundException() {
		// TODO Auto-generated constructor stub
	}

	public CarpetActionNotFoundException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public CarpetActionNotFoundException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public CarpetActionNotFoundException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public CarpetActionNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
