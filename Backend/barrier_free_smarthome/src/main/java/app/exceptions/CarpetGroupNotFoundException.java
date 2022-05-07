package app.exceptions;

/**
 * This exception is thrown when a CarpetGroup can not bee found.
 * 
 * @author Florian Jungermann
 *
 */
public class CarpetGroupNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7351469411486394081L;

	public CarpetGroupNotFoundException() {
		super();
	}

	public CarpetGroupNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public CarpetGroupNotFoundException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public CarpetGroupNotFoundException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public CarpetGroupNotFoundException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
	
}
