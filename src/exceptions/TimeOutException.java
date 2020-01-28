package exceptions;

/**
 * Exception to indicate a time-out during gameplay.
 * @author huub.lievestro
 *
 */
public class TimeOutException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8229312498937968065L;

	public TimeOutException(String msg) {
		super(msg);
	}

}
