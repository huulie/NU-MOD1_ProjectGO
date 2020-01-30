package exceptions;

/**
 * Exception to indicate requested field on specific board is invalid.
 * @author huub.lievestro
 * 
 */
public class InvalidFieldException extends Exception {

	private static final long serialVersionUID = 6876679206940148577L;

	public InvalidFieldException(String msg) {
		super(msg);
	}
}
