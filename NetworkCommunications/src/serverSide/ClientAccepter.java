package serverSide;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
import clientSide.*;
import crypto.*;

/**
 * Accepts clients and assigns each to a socket handled by a separate thread called a serverSide.
 * @author Ezra Stein
 * @version 1.0
 * @since 2015
 */
public class ClientAccepter implements Runnable
{
	private ServerGUI serverGUI;
	private ServerSocket socketAccepter;
	private ServerInfo serverInfo = null;
	
	
	/**
	 * A list of all clients (signed in or otherwise) who are currently in communication with the server.
	 */
	private ArrayList<ServerSide> allClients;
	private ArrayList<Thread> clientThreads;
	private HashMap<String, ChatArea> chatAreas;
	
	/**
	 * Creates a ClientAccepter with a reference to the GUI that created it.
	 * @param serverGUI
	 */
	public ClientAccepter(ServerGUI serverGUI)
	{
		this.serverGUI = serverGUI;
		chatAreas = new HashMap<String, ChatArea>();
		allClients = new ArrayList<ServerSide>();
		clientThreads = new ArrayList<Thread>();
		
		
		serverInfo = new ServerInfo();
		ChatAreaInfo chatAreaInfo = new ChatAreaInfo();
		serverInfo.getChatAreas().put(chatAreaInfo.getId(), chatAreaInfo);
		
		ChatArea chatArea = new ChatArea("General Chat", this, RandomFunctions.randomBigInteger(64, new SecureRandom()), chatAreaInfo);
		chatAreas.put(chatArea.getChatAreaInfo().getId(), chatArea);
		
		/*Should this be called outside of the EDT?*/
		readFiles();
	}
	
	/**
	 * Reads in the LoginFile and puts its info into hashes.
	 */
	public void readFiles()
	{
		ObjectInputStream reader = null;
		ClientInfo client;
		try
		{
			/*Will this file path function in a JAR file*/
			reader = new ObjectInputStream(new FileInputStream(new File("src/serverSide/LoginFile.txt")));
			while(true)
			{
				client = (ClientInfo) reader.readObject();
				serverInfo.getClientsInDataBase().put(client.getUsername(), client);
			}
			
		}
		catch(EOFException eofe)
		{
		}
		catch (IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				if(reader != null)
				{
					reader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Continually runs accepting sockets. Then it creates
	 * a Client to hold the socket and runs it on a separate thread.
	 */
	@Override
	public void run()
	{
		try
		{
			socketAccepter = new ServerSocket(20001);
			while(true)
			{
				Socket socket = socketAccepter.accept();
				Socket commandSocket = socketAccepter.accept();
				
				ServerSide client = new ServerSide(socket, commandSocket, this);
				Thread thread = new Thread(client);
				thread.start();
				clientThreads.add(thread);
				allClients.add(client);
			}
		}
		catch (IOException e1)
		{
		}
		finally
		{
			try
			{
				if(socketAccepter != null && !socketAccepter.isClosed())
				{
					socketAccepter.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		
	}
	
	/**
	 * Closes the all clients and ends the program.
	 */
	public void close()
	{
		for(ServerSide client: allClients)
		{
			client.close();
		}
		try
		{
			if(socketAccepter != null && !socketAccepter.isClosed())
			{
				socketAccepter.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Thread t : clientThreads)
		{
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
	/**
	 * Adds this user to the LOGIN database and to the hash. Creates an account.
	 * @param client - the client to be added to the database;
	 * @throws IOException 
	 */
	public void add(ClientInfo client) throws IOException
	{
		ObjectOutputStream writer = null;
		BufferedReader reader = null;
		serverInfo.getClientsInDataBase().put(client.getUsername(),client);
		for(ServerSide s: allClients)
		{
			if(s.getClientInfo().getSignedIn())
			{
				s.getCommandOutputStream().writeObject("ADD CLIENT TO DATABASE");
				s.getCommandOutputStream().writeObject(new ClientInfo(client));
			}
		}
		try
		{
			/*Will this file path function in a JAR file*/
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("src/serverSide/LoginFile.txt"))));
			if(reader.readLine() == null)
			{
				writer = new ObjectOutputStream(new FileOutputStream( new File("src/serverSide/LoginFile.txt"), true));
				writer.writeObject(new ClientInfo(client));
			}
			else
			{
				writer = new AppendObjectOutputStream(new FileOutputStream( new File("src/serverSide/LoginFile.txt"), true));
				((AppendObjectOutputStream)writer).writeObject(new ClientInfo(client));
			}
			
			
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		finally
		{
			try {
				if(writer != null)
				{
					writer.close();
					
				}
				if(reader != null)
				{
					reader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns the list of chat areas.
	 * @return  the list of chat areas.
	 */
	public HashMap<String, ChatArea> getChatAreas()
	{
		return chatAreas;
	}
	
	/**
	 * Returns a reference to the GUI of this server.
	 * @return a reference to the GUI of this server.
	 */
	public ServerGUI getGUI()
	{
		return serverGUI;
	}
	
	
	/**
	 * Returns a list of all the clients. This includes clients that have not yet signed in.
	 * @return a list of all the clients.
	 */
	public ArrayList<ServerSide> getAllClients()
	{
		return allClients;
	}
	
	/**
	 * Returns the ServerInfo object for the server.
	 * @return the ServerInfo object for the server.
	 */
	public ServerInfo getServerInfo()
	{
		return serverInfo;
	}
}
