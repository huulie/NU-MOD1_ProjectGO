package exceptions;

/**
 * Exception to indicate that client is unavailable.
 * @author huub.lievestro
 *
 */
public class ClientUnavailableException extends Exception {

	private static final long serialVersionUID = 4267719248820368586L;

	public ClientUnavailableException(String msg) {
		super(msg);
	}
}
