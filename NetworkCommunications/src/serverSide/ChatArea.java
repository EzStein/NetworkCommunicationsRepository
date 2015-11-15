package serverSide;

import java.io.*;
import java.math.*;
import java.util.*;
import clientSide.*;

/**
 * Contains all the ServerSides in a given chatArea as well as all the text of that chat area.
 * @author Ezra Stein
 * @version 1.0;
 * @since 2015
 */
public class ChatArea
{
	private final long timeoutTime = 10*60*1000; /*10 minutes*/
	private ArrayList<ServerSide> clients;
	//private ArrayList<String> clientNames;
	private ClientAccepter clientAccepter;
	private ChatAreaInfo chatAreaInfo;
	private Timer timer;
	/**
	 * Constructs a chatArea.
	 * @param name - The name of this chatArea.
	 * @param clientAccepter - A reference to the object that created this.
	 * @param key - The key used for encrypting all messages across this chat area.
	 * @param chatAreaInfo - The information that this chat area is required to maintain.
	 * The object passes to this constructor is already part of the ServerInfo framework.
	 */
	public ChatArea(String name, ClientAccepter clientAccepter, BigInteger key, ChatAreaInfo chatAreaInfo)
	{
		clients = new ArrayList<ServerSide>();
		chatAreaInfo.setName(name);
		chatAreaInfo.setKey(key);
		chatAreaInfo.setText("");
		this.clientAccepter = clientAccepter;
		this.chatAreaInfo = chatAreaInfo;
		timer = new Timer();
		timer.schedule(new chatAreaRemover(), timeoutTime);
	}
	
	/**
	 * Creates a chat area with less info.
	 * @param clientAccepter
	 * @param chatAreaInfo
	 */
	public ChatArea(ClientAccepter clientAccepter, ChatAreaInfo chatAreaInfo)
	{
		clients = new ArrayList<ServerSide>();
		chatAreaInfo.setText("");
		this.clientAccepter = clientAccepter;
		this.chatAreaInfo = chatAreaInfo;
		timer = new Timer();
		timer.schedule(new chatAreaRemover(), timeoutTime);
	}
	
	/**
	 * Adds a client to this chat area.
	 * @param client - The client to be added.
	 */
	public void add(ServerSide client)
	{
		timer.cancel();
		chatAreaInfo.getClients().add(client.getClientInfo());
		clients.add(client);
	}
	
	/**
	 * Removes the client from this chat area.
	 * @param client - The client to be removed.
	 */
	public void remove(ServerSide client)
	{
		chatAreaInfo.getClients().remove(client.getClientInfo());
		clients.remove(client);
		if(clients.size() == 0)
		{
			timer.cancel();
			timer = new Timer();
			timer.schedule(new chatAreaRemover(), timeoutTime);
		}
	}
	
	/**
	 * Adds the text to the chatArea and sends it to all clients.
	 * Should not be used after deserialization.
	 * @param message - message to be sent.
	 */
	public void send(String message)
	{
		chatAreaInfo.setText(chatAreaInfo.getText().concat(message));
		clientAccepter.getGUI().updateInfo();
		for(ServerSide client:clients)
		{
			try {
				client.getOutputStream().writeObject(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns the list of clients in this area.
	 * @return the list of clients in this area.
	 */
	public ArrayList<ServerSide> getClients()
	{
		return clients;
	}
	
	/**
	 * Returns the chatAreaInfo of this chatArea.
	 * @return the chatAreaInfo.
	 */
	public ChatAreaInfo getChatAreaInfo()
	{
		return chatAreaInfo;
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
		if(!(object instanceof ChatArea))
		{
			return false;
		}
		ChatArea chatArea = (ChatArea) object;
		if(chatArea.getChatAreaInfo().equals(chatAreaInfo))
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
		hash = hash + chatAreaInfo.hashCode();
		return hash;
	}
	
	private class chatAreaRemover extends TimerTask
	{
		@Override
		public void run()
		{
			if(! chatAreaInfo.getName().equals("General Chat"))
			{
				clientAccepter.getChatAreas().remove(chatAreaInfo.getId(), this);
				clientAccepter.getServerInfo().getChatAreas().remove(chatAreaInfo.getId(), chatAreaInfo);
				for(ServerSide client: clientAccepter.getAllClients())
				{
					if(client.getClientInfo().getSignedIn())
					{
						try {
							if(chatAreaInfo.getClientsVisible() == null)
							{
								client.getCommandOutputStream().writeObject("REMOVE CHAT AREA");
								client.getCommandOutputStream().writeObject(chatAreaInfo);
							
							}
							else if(chatAreaInfo.getClientsVisible().contains(client.getClientInfo().getUsername()))
							{
								client.getCommandOutputStream().writeObject("REMOVE CHAT AREA");
								client.getCommandOutputStream().writeObject(chatAreaInfo);
							}
							
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
