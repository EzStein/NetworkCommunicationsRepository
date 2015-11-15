package clientSide;
import java.io.*;
import java.net.*;
import crypto.*;
/**
 * Contains descriptive information about the client meant for easy serialization.
 * It does not contain any sockets, or IO, etc.
 * It is used to send large amounts of information across the server about the client.
 * Each serverSide object maintains a ClientInfo object to hold its information that does not include sockets.
 * @author Ezra Stein
 * @version 1.0
 * @since 2015
 */
public class ClientInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String 				username;
	private InetAddress 		IPAddress;
	private PublicKey 			publicKey;
	private boolean 			signedIn;
	private String	passwordHashCode;
	
	/**
	 * Sets the default values for this object.
	 */
	public ClientInfo()
	{
		this.username = null;
		this.IPAddress = null;
		this.publicKey = null;
		this.signedIn = false;
		this.passwordHashCode = null;
	}
	
	
	/**
	 * Constructs a ClientInfo object with a username and IPAddress.
	 * A client is defined by its username. No two clients are permitted to have the same username.
	 * @param username
	 * @param IPAddress
	 * @param signedIn 
	 * @param publicKey 
	 * @param passwordHashCode 
	 */
	public ClientInfo(String username, InetAddress IPAddress, boolean signedIn, PublicKey publicKey, String passwordHashCode)
	{
		this();
		this.username = username;
		this.IPAddress = IPAddress;
		this.signedIn = signedIn;
		this.publicKey = publicKey;
		this.passwordHashCode = passwordHashCode;
	}
	
	/**
	 * Copies the clientInfo. Preforms a deep copy.
	 * @param client
	 */
	public ClientInfo(ClientInfo client)
	{
		this();
		this.username = client.getUsername();
		this.IPAddress = client.getIPAddress();
		this.signedIn = client.getSignedIn();
		this.publicKey = client.getPublicKey();
		this.passwordHashCode = client.getPassword();
	}
	
	/**
	 * @param username
	 */
	public ClientInfo(String username)
	{
		this();
		this.username = username;
	}
	
	/**
	 * 
	 * @return signedIn
	 */
	public boolean getSignedIn()
	{
		return signedIn;
	}
	
	/**
	 * 
	 * @param signedIn
	 */
	public void setSignedIn(boolean signedIn)
	{
		this.signedIn = signedIn;
	}
	
	/**
	 * 
	 * @return username
	 */
	public String getUsername()
	{
		return username;
	}
	
	/**
	 * 
	 * @param username
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	/**
	 * 
	 * @return IPAddress
	 */
	public InetAddress getIPAddress()
	{
		return IPAddress;
	}
	
	/**
	 * 
	 * @param IPAddress
	 */
	public void setIPAddress(InetAddress IPAddress)
	{
		this.IPAddress = IPAddress;
	}
	
	/**
	 * 
	 * @return publicKey
	 */
	public PublicKey getPublicKey()
	{
		return publicKey;
	}
	
	/**
	 * 
	 * @param publicKey
	 */
	public void setPublicKey(PublicKey publicKey)
	{
		this.publicKey = publicKey;
	}
	
	/**
	 * 
	 * @return passwordHashCode
	 */
	public String getPassword()
	{
		return passwordHashCode;
	}
	
	/**
	 * 
	 * @param password
	 */
	public void setPassword(String password)
	{
		this.passwordHashCode = password;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other == null)
		{
			return false;
		}
		if(other == this)
		{
			return true;
		}
		if(!(other instanceof ClientInfo))
		{
			return false;
		}
		
		ClientInfo otherClientInfo = (ClientInfo) other;
		if(otherClientInfo.getUsername().equals(username))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public int hashCode()
	{
		int hash = 3;
		hash = hash + username.hashCode();
		return hash;
	}
	
	public String toString()
	{
		if(publicKey == null)
		{
			return  "Client\n" +
					"\tusername: " + username + "\n" +
					"\tIPAddress: " + IPAddress + "\n" +
					"\tSignedIn: " + signedIn + "\n" +
					"\tKey: " + "null" + "\n" +
					"\tPassword: " + passwordHashCode;
		}
		return	"Client\n" +
				"\tusername: " + username + "\n" +
				"\tIPAddress: " + IPAddress + "\n" +
				"\tSignedIn: " + signedIn + "\n" +
				"\tKey: " + publicKey.toString() + "\n" +
				"\tPassword: " + passwordHashCode;
		
	}
}
