package clientSide;

import java.io.*;
import java.util.*;

/**
 * Contains all the info for the Server. Each serverSide object accesses the same instance
 * of this class through the clientAccepter. Each clientSide object should receive a copy
 * of this object every time its status is changed.
 * The server is responsible for maintaining the correct values in its ServerInfo object and for sending
 * that info to the client.
 * The ServerInfo class contains a list of all ClientInfo's that are currently connected (Signed in or not)
 * to the server. As well as a list of all ChatAreaInfos.
 * Each ChatAreaInfo contains a list of ClientInfos that are inside that ChatArea.
 * Each ChatArea is responsible for maintaining the correct values in this ServInfo object
 * and each ServerSide is responsible for maintaining its own ClientInfo.
 * 
 * ClientAccepter - ServerInfo
 * ChatArea - ChatAreaInfo
 * ServerSide - ClientInfo
 * 
 * @author Ezra Stein
 * @version 1.0
 * @since 2015
 */
public class ServerInfo implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * These only contain signed in clients.
	 */
	private ArrayList<ClientInfo> allClients;
	private HashMap<String, ChatAreaInfo> chatAreas;
	private HashMap<String, ClientInfo> clientsInDataBase;
	
	/**
	 * Constructs an empty server info.
	 */
	public ServerInfo()
	{
		allClients = new ArrayList<ClientInfo>();
		chatAreas = new HashMap<String, ChatAreaInfo>();
		clientsInDataBase = new HashMap<String, ClientInfo>();
	}
	
	/**
	 * Performs a deep copy.
	 * @param serverInfo
	 */
	public ServerInfo(ServerInfo serverInfo)
	{
		this.allClients = new ArrayList<ClientInfo>();
		this.chatAreas = new HashMap<String, ChatAreaInfo>();
		this.clientsInDataBase = new HashMap<String, ClientInfo>();
		for(ClientInfo client : serverInfo.getAllClients())
		{
			this.allClients.add(new ClientInfo(client));
		}
		for(ChatAreaInfo chatArea : serverInfo.getChatAreas().values())
		{
			this.chatAreas.put(chatArea.getId(), new ChatAreaInfo(chatArea));
		}
		for(ClientInfo info : serverInfo.getClientsInDataBase().values())
		{
			this.clientsInDataBase.put(info.getUsername(), info);
		}
	}
	
	/**
	 * Returns the list of clients who are signed in.
	 * @return the list of clients who are signed in.
	 */
	public ArrayList<ClientInfo> getAllClients()
	{
		return allClients;
	}
	
	/**
	 * Returns a hash of chatAreas.
	 * @return a hash of chatAreas.
	 */
	public HashMap<String, ChatAreaInfo> getChatAreas()
	{
		return chatAreas;
	}
	
	/**
	 * Returns the hash that contains the usernames and passwords.
	 * This will eventually be changed to make it secure.
	 * @return the hash that contains the usernames and passwords.
	 */
	public HashMap<String,ClientInfo> getClientsInDataBase()
	{
		return clientsInDataBase;
	}
	
	
	public String toString()
	{
		String out = "CLIENTS IN DATABASE:\n";
		if(allClients != null && chatAreas != null && clientsInDataBase != null)
		{
			for(ClientInfo ci : clientsInDataBase.values())
			{
				out += ci.toString() + "\n\n";
			}
			out += "CLIENTS SIGNED IN:\n";
			for(ClientInfo ci : allClients)
			{
				out += ci.toString() + "\n\n";
			}
			out += "CHAT AREAS:\n";
			for(ChatAreaInfo cai : chatAreas.values())
			{
				out += cai.toString() + "\n\n";
			}
		}
		return out;
	}
}
