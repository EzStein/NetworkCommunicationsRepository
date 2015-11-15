package clientSide;

/**
 * Thrown when an object tries to change an inmutable field after it has already been initialized.
 * e. g. a username may not be changed after it has been set.
 * @author Ezra Stein
 * @version 1.0
 * @since 2015
 */
public class RepeatedAssignmentException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
