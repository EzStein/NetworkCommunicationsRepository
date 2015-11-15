package clientSide;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

/**
 * Contains the info for this chat area. Whenever it is passed to a constructor, it should already have
 * been added to the ServerInfo framework.
 * @author Ezra Stein
 * @version 1.0
 * @since 2015
 */
public class ChatAreaInfo implements Serializable
{
	/**
	 * I don't know what this line does, but it prevents a warning.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * These values should reference the same ones in ServerInfo.getAllClients().
	 * They should not be copies.
	 */
	private ArrayList<ClientInfo> clients;
	/**
	 * A list of the usernames of all clients for which this chat area is visible;
	 * If it is null, then all clients are allowed to see it.
	 */
	private ArrayList<String> clientsVisible;
	private String name;
	private BigInteger key;
	private String text;
	private String id;
	
	public ChatAreaInfo()
	{
		clients = new ArrayList<ClientInfo>();
		clientsVisible = null;
		key = new BigInteger("0");
		text = "";
		name = "";
		id = UUID.randomUUID().toString();
	}
	
	/**
	 * Creates a deep copy of this object.
	 * @param chatAreaInfo
	 */
	public ChatAreaInfo(ChatAreaInfo chatAreaInfo)
	{
		this();
		this.name = chatAreaInfo.getName();
		this.key = chatAreaInfo.getKey();
		this.text = chatAreaInfo.getText();
		this.clients = new ArrayList<ClientInfo>();
		this.id = chatAreaInfo.getId();
		for(ClientInfo client : chatAreaInfo.getClients())
		{
			this.clients.add(new ClientInfo(client));
		}
		if(chatAreaInfo.getClientsVisible() == null)
		{
			this.clientsVisible = null;
		}
		else
		{
			this.clientsVisible = new ArrayList<String>();
			for(String string : chatAreaInfo.getClientsVisible())
			{
				this.clientsVisible.add(string);
			}
		}
	}
	
	public ArrayList<ClientInfo> getClients()
	{
		return clients;
	}
	
	public ArrayList<String> getClientsVisible()
	{
		return clientsVisible;
	}
	
	public void setClientsVisible(ArrayList<String> clientsVisible)
	{
		this.clientsVisible = clientsVisible;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public BigInteger getKey()
	{
		return key;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setKey(BigInteger key)
	{
		this.key = key;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(object == null)
		{
			return false;
		}
		if(object == this)
		{
			return true;
		}
		if(!(object instanceof ChatAreaInfo))
		{
			return false;
		}
		ChatAreaInfo chatArea = (ChatAreaInfo) object;
		if(chatArea.getId().equals(this.id))
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
		hash = hash + id.hashCode();
		return hash;
	}
	
	public String toString()
	{
		String out;
		if(clientsVisible == null)
		{
			out = "Chat Area\n" + 
					"\tName: " + name + "\n" +
					"\tID: " + id + "\n" +
					"\tVisible: " + null + "\n" +
					"\tKey: " + key.toString() + "\n";
		}
		else
		{
			out = "Chat Area\n" + 
					"\tName: " + name + "\n" +
					"\tID: " + id + "\n" +
					"\tVisible: " + clientsVisible.toString() + "\n" +
					"\tKey: " + key.toString() + "\n";
		}
		if(clients != null)
		{
			for(ClientInfo ci: clients)
			{
				out+= ci.toString() + "\n";
			}
		}
		return out;
	}
	
	/**
	 * Returns this chatArea in a String form.
	 * @return this chatArea in a String form.
	 */
	public String toFormattedString()
	{
		String out;
		if(clientsVisible == null)
		{
			out = "Chat Area\n" + 
					"\tName: " + name + "\n" +
					"\tID: " + id + "\n" +
					"\tVisible: " + null + "\n" +
					"\tKey: " + key.toString() + "\n";
		}
		else
		{
			out = "Chat Area\n" + 
					"\tName: " + name + "\n" +
					"\tID: " + id + "\n" +
					"\tVisible: " + clientsVisible.toString() + "\n" +
					"\tKey: " + key.toString() + "\n";
		}
		return out;
	}
}
