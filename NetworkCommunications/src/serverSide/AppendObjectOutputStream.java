package serverSide;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * An object output stream that will not write a header to file.
 * Instead it will append the object to the end of the file;
 * @author Ezra Stein
 * @version 1.0
 * @since 2015;
 */
public class AppendObjectOutputStream extends ObjectOutputStream
{
	/**
	 * Calls the super constructor.
	 * @param out
	 * @throws IOException
	 */
	public AppendObjectOutputStream(OutputStream out) throws IOException
	{
		super(out);
	}
	
	/**
	 * Prevents the stream from writing out a header on each invocation of writeObject();
	 */
	@Override
	protected void writeStreamHeader() throws IOException
	{
		reset();
	}
}
