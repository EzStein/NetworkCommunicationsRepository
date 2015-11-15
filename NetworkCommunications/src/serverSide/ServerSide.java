package serverSide;

import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.util.ArrayList;

import clientSide.*;
import crypto.*;

/**
 * Runs on the Server side. Accepts communications from clients and broadcasts them to everyone in the chat area.
 * @author Ezra Stein
 * @version 1.0
 * @since 2015
 */
public class ServerSide implements Runnable
{
	private final String endOfText = "\u0003";
	private Thread thisThread;
	private ClientInfo clientInfo;
	private ChatArea chatArea;
	private CommandReader commandReader;
	private Socket socket, commandSocket;
	private ObjectOutputStream out;
	private ObjectOutputStream commandOut;
	private ObjectInputStream in;
	private ObjectInputStream commandIn;
	private ClientAccepter clientAccepter;
	
	/**
	 * Constructs a Server with a reference to the GUI that created it.
	 * @param socket - the socket endpoint that this thread will handle.
	 * @param commandSocket - this socket is used to send and receive info that should not be treated as part of the chat area such as commands.
	 * @param clientAccepter - A reference to the client accepter that made this socket client.
	 */
	public ServerSide(Socket socket, Socket commandSocket, ClientAccepter clientAccepter)
	{
		this.socket = socket;
		this.commandSocket = commandSocket;
		this.clientAccepter = clientAccepter;
		this.clientInfo =  new ClientInfo();
		this.clientInfo.setSignedIn(false);
	}
	
	/**
	 * Initializes the client with certain info regarding the number of chat areas and clients.
	 * Gives the client an initial set of text that was already in the chat area.
	 * Waits for input and forwards that input to each client in the chat.
	 * When the client exits, it removes it from the room and closes the sockets.
	 */
	@Override
	public void run()
	{
		thisThread = Thread.currentThread();
		try
		{
			initializeClient();
			while(true)
			{
				selectChatArea();
				if(! chatArea.getChatAreaInfo().getText().equals(""))
				{
					out.writeObject(chatArea.getChatAreaInfo().getText());
				}
				chatArea.send(CryptoFunctions.toString(Encryptor.ECBMode(CryptoFunctions.toBinary(clientInfo.getUsername() + " @ " + clientInfo.getIPAddress().toString() + " joined\n"),
						CryptoFunctions.toBaseTwo(chatArea.getChatAreaInfo().getKey()),Encryptor.DES_ENCRYPTION)));
				
				String input;
				while(true)
				{
					input = (String) in.readObject();
					if(input == null)
					{
						
						throw new SocketTerminatedException();
					}
					else if(input.equals(endOfText))
					{
						break;
					}
					chatArea.send(input);
				}
				/*You have left the chat area, but have not quit the program*/
				chatArea.remove(this);
				chatArea.send(CryptoFunctions.toString(Encryptor.ECBMode(CryptoFunctions.toBinary(clientInfo.getUsername() + " @ " + clientInfo.getIPAddress().toString() + " left\n"),
						CryptoFunctions.toBaseTwo(chatArea.getChatAreaInfo().getKey()),Encryptor.DES_ENCRYPTION)));
				
				for(ServerSide client : clientAccepter.getAllClients())
				{
					if(client.getClientInfo().getSignedIn() == true)
					{
						if(chatArea.getChatAreaInfo().getClientsVisible() == null)
						{
							client.getCommandOutputStream().writeObject("REMOVE CLIENT FROM CHAT AREA");
							client.getCommandOutputStream().writeObject(chatArea.getChatAreaInfo().getId());
							client.getCommandOutputStream().writeObject(new ClientInfo(clientInfo));
						
						}
						else if(chatArea.getChatAreaInfo().getClientsVisible().contains(client.getClientInfo().getUsername()))
						{
							client.getCommandOutputStream().writeObject("REMOVE CLIENT FROM CHAT AREA");
							client.getCommandOutputStream().writeObject(chatArea.getChatAreaInfo().getId());
							client.getCommandOutputStream().writeObject(new ClientInfo(clientInfo));
						}
					}
				}
				chatArea = null;
			}
			
			
		}
		catch(EOFException eof)
		{
			System.out.println("THIS SHOULD NOT OCCUR");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch(SocketTerminatedException ste)
		{
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(chatArea != null)
				{
					chatArea.send(CryptoFunctions.toString(Encryptor.ECBMode(CryptoFunctions.toBinary(clientInfo.getUsername() + " @ " + socket.getInetAddress().toString() + " left\n"),
							CryptoFunctions.toBaseTwo(chatArea.getChatAreaInfo().getKey()),Encryptor.DES_ENCRYPTION)));
					chatArea.remove(this);
					for(ServerSide client : clientAccepter.getAllClients())
					{
						if(client.getClientInfo().getSignedIn() == true)
						{
							if(chatArea.getChatAreaInfo().getClientsVisible() == null)
							{
								client.getCommandOutputStream().writeObject("REMOVE CLIENT FROM CHAT AREA");
								client.getCommandOutputStream().writeObject(chatArea.getChatAreaInfo().getId());
								client.getCommandOutputStream().writeObject(new ClientInfo(clientInfo));
							
							}
							else if(chatArea.getChatAreaInfo().getClientsVisible().contains(client.getClientInfo().getUsername()))
							{
								client.getCommandOutputStream().writeObject("REMOVE CLIENT FROM CHAT AREA");
								client.getCommandOutputStream().writeObject(chatArea.getChatAreaInfo().getId());
								client.getCommandOutputStream().writeObject(new ClientInfo(clientInfo));
							}
						}
					}
					chatArea = null;
				}
				
				clientAccepter.getAllClients().remove(this);
				clientAccepter.getServerInfo().getAllClients().remove(clientInfo);
				for(ServerSide client : clientAccepter.getAllClients())
				{
					if(client.getClientInfo().getSignedIn() == true)
					{
						client.getCommandOutputStream().writeObject("REMOVE CLIENT");
						client.getCommandOutputStream().writeObject(new ClientInfo(clientInfo));
					}
				}
				/*The Client has closed and the program should exit
				 * Sends a null argument to the client indicating that it should also exit
				 * Immediately after this is sent the client will close its own streams.*/
				out.writeObject(null);
				in.close();
				out.close();
				socket.close();
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
	}
	
	/**
	 * Sends info to the client about the chat rooms.
	 * Receives a number indicating which chat room the client will join.
	 * Adds the client to specific chat room.
	 * @throws SocketTerminatedException - When the socket has terminated all readLine() methods will return null and this exception is thrown.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public void initializeClient() throws SocketTerminatedException, IOException, ClassNotFoundException
	{
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
		commandOut = new ObjectOutputStream(commandSocket.getOutputStream());
		commandIn = new ObjectInputStream(commandSocket.getInputStream());
		commandReader = new CommandReader();
		new Thread(commandReader).start();
		
		while(! signIn());
		clientInfo.setSignedIn(true);
		/*Adds it to the server info */
		clientAccepter.getServerInfo().getAllClients().add(clientInfo);
		
		commandOut.writeObject("ADD CLIENT");
		commandOut.writeObject(new ClientInfo(clientInfo));
		for(ServerSide client : clientAccepter.getAllClients())
		{
			if(client.getClientInfo().getSignedIn() == true)
			{
				if(! client.getClientInfo().getUsername().equals(clientInfo.getUsername()))
				{
					commandOut.writeObject("ADD CLIENT");
					commandOut.writeObject(new ClientInfo(clientInfo));
					client.getCommandOutputStream().writeObject("ADD CLIENT");
					client.getCommandOutputStream().writeObject(new ClientInfo(clientInfo));
				}
			}
		}
		
		sendServerInfo();
		
	}
	
	/**
	 * Sends the server info to the client on the commandOut stream.
	 * @throws IOException
	 */
	public void sendServerInfo() throws IOException
	{
		ServerInfo sendingInfo = new ServerInfo();
		for(ClientInfo info : clientAccepter.getServerInfo().getAllClients())
		{
			sendingInfo.getAllClients().add(info);
		}
		for(ClientInfo info: clientAccepter.getServerInfo().getClientsInDataBase().values())
		{
			sendingInfo.getClientsInDataBase().put(info.getUsername(), info);
		}
		
		for(ChatAreaInfo info : clientAccepter.getServerInfo().getChatAreas().values())
		{
			info = new ChatAreaInfo(info);
			if(info.getClientsVisible() == null)
			{
				info.setKey(RSA.encrypt(info.getKey(), clientInfo.getPublicKey()));
				sendingInfo.getChatAreas().put(info.getId(), info);
			}
			else if(info.getClientsVisible().contains(clientInfo.getUsername()))
			{
				info.setKey(RSA.encrypt(info.getKey(), clientInfo.getPublicKey()));
				sendingInfo.getChatAreas().put(info.getId(), info);
			}
		}
		commandOut.writeObject("UPDATE CLIENT");
		commandOut.writeObject(new ServerInfo(sendingInfo));
	}
	
	/**
	 * Chooses either to log in or create an account
	 * @return True if the client successfully signed in and false otherwise.
	 * @throws SocketTerminatedException
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public boolean signIn() throws SocketTerminatedException, IOException, ClassNotFoundException
	{
		String input;
		boolean repeat = true;
		
		if(((input = (String)in.readObject())!=null))
		{
			
			if(input.equals("LOGIN"))
			{
				repeat = verifyLogin();
			}
			else if(input.equals("CREATEACCOUNT"))
			{
				repeat = createAccount();
			}
		}
		else
		{
			throw new SocketTerminatedException();
		}
		return repeat;
	}
	
	/**
	 * Used to check the login info.
	 * @return True if the client successfully logged in and false otherwise.
	 * @throws SocketTerminatedException - When the socket has terminated all readLine() methods will return null and this exception is thrown.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public boolean verifyLogin() throws SocketTerminatedException, IOException, ClassNotFoundException
	{
		if((clientInfo = (ClientInfo)in.readObject()) == null)
		{
			throw new SocketTerminatedException();
		}
		
			if(verifyPassword(clientInfo.getUsername(), clientInfo.getPassword()))
			{
				if(clientAccepter.getServerInfo().getAllClients().contains(clientInfo))
				{
					out.writeObject("ALREADY LOGGED ON");
					return false;
				}
				else
				{
					out.writeObject("VERIFIED");
					return true;
				}
			}
			else
			{
				out.writeObject("DENIED");
				return false;
			}
	}
	
	/**
	 * Given a username and password this will verify if the pair exists in the database.
	 * @param username - username to be checked.
	 * @param password - password to be checked.
	 * @return true if there is a pair. False otherwise.
	 */
	public boolean verifyPassword(String username, String password)
	{
		if(clientAccepter.getServerInfo().getClientsInDataBase().get(username) != null)
		{
			if(clientAccepter.getServerInfo().getClientsInDataBase().get(username).getPassword().equals(password))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Reads in the username and password and writes that information to the LOGIN Database if it does not already exist.
	 * @return True if account successfully was created, false otherwise.
	 * @throws SocketTerminatedException
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public boolean createAccount() throws SocketTerminatedException, IOException, ClassNotFoundException
	{
		
		if((clientInfo = (ClientInfo)in.readObject())==null)
		{
			throw new SocketTerminatedException();
		}
		if(clientAccepter.getServerInfo().getClientsInDataBase().containsKey(clientInfo.getUsername()) || createIPList().contains(clientInfo.getIPAddress()))
		{
			out.writeObject("INVALID");
			return false;
		}
		else
		{
			clientAccepter.add(clientInfo);
			out.writeObject("VERIFIED");
			return true;
		}
	}
	
	/**
	 * Called to select the chat area.
	 * @throws IOException
	 * @throws SocketTerminatedException
	 * @throws ClassNotFoundException 
	 */
	public void selectChatArea() throws IOException, SocketTerminatedException, ClassNotFoundException
	{
		String input;
		/*Expects a number indicating the chat room Id that it wants to join.*/
		if((input = (String)in.readObject()) == null)
		{
			throw new SocketTerminatedException();
		}
		chatArea = clientAccepter.getChatAreas().get(input);
		chatArea.add(this);
		
		for(ServerSide client : clientAccepter.getAllClients())
		{
			if(client.getClientInfo().getSignedIn() == true)
			{
				if(chatArea.getChatAreaInfo().getClientsVisible() == null)
				{
					client.getCommandOutputStream().writeObject("ADD CLIENT TO CHAT AREA");
					client.getCommandOutputStream().writeObject(chatArea.getChatAreaInfo().getId());
					client.getCommandOutputStream().writeObject(new ClientInfo(clientInfo));
				
				}
				else if(chatArea.getChatAreaInfo().getClientsVisible().contains(client.getClientInfo().getUsername()))
				{
					client.getCommandOutputStream().writeObject("ADD CLIENT TO CHAT AREA");
					client.getCommandOutputStream().writeObject(chatArea.getChatAreaInfo().getId());
					client.getCommandOutputStream().writeObject(new ClientInfo(clientInfo));
				}
			}
		}
	}
	
	/**
	 * Tells the client that it should close.
	 */
	public void close()
	{
		try
		{
			commandOut.writeObject("CLOSE");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the output stream for this client.
	 * @return the output stream for this client.
	 */
	public ObjectOutputStream getOutputStream()
	{
		return out;
	}
	
	/**
	 * Returns the command output stream to send commands to other clients.
	 * @return the command output stream to send commands to other clients.
	 */
	public ObjectOutputStream getCommandOutputStream()
	{
		return commandOut;
	}
	
	/**
	 * Returns the socket of this client.
	 * @return the socket of this client.
	 */
	public Socket getSocket()
	{
		return socket;
	}
	
	/**
	 * Returns the clientInfo;
	 * @return the clientInfo
	 */
	public ClientInfo getClientInfo()
	{
		return clientInfo;
	}
	
	/**
	 * This thread runs the commandSocket.
	 * It reads its input and acts on its commands.
	 */
	private class CommandReader implements Runnable
	{
		@Override
		public void run()
		{
			String input;
			try
			{
				while((input = (String) commandIn.readObject()) != null)
				{
					if(input.equals("CREATE NEW CHAT AREA"))
					{
						createNewChatArea();
					}
					else if(input.equals("REQUEST UPDATE"))
					{
						requestUpdate();
					}
				}
				throw new SocketTerminatedException();
			}
			catch(EOFException eof)
			{
				System.out.println("COMMAND: SHOULD NOT OCCUR");
			}
			catch(SocketTerminatedException ste)
			{
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch(ClassNotFoundException cnfe)
			{
				cnfe.printStackTrace();
			}
			finally
			{
				try
				{
					/*Waits for the main thread to finish executing*/
					try {
						thisThread.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					commandOut.writeObject(null);
					commandIn.close();
					commandOut.close();
					commandSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public void requestUpdate() throws IOException
		{
			sendServerInfo();
		}
		
		public void createNewChatArea() throws SocketTerminatedException, IOException, ClassNotFoundException
		{
			ChatAreaInfo info;
			if((info = (ChatAreaInfo) commandIn.readObject()) == null)
			{
				throw new SocketTerminatedException();
			}
			info.setKey(RandomFunctions.randomBigInteger(64, new SecureRandom()));
			clientAccepter.getServerInfo().getChatAreas().put(info.getId(), info);
			clientAccepter.getChatAreas().put(info.getId(),new ChatArea(clientAccepter,info));
			ChatAreaInfo sendingInfo = new ChatAreaInfo(info);
			for(ServerSide client : clientAccepter.getAllClients())
			{
				if(client.getClientInfo().getSignedIn())
				{
					if(info.getClientsVisible() == null)
					{
						sendingInfo.setKey(RSA.encrypt(info.getKey(), client.getClientInfo().getPublicKey()));
						client.getCommandOutputStream().writeObject("ADD CHAT AREA");
						client.getCommandOutputStream().writeObject(new ChatAreaInfo(sendingInfo));
					}
					else if(info.getClientsVisible().contains(client.getClientInfo().getUsername()))
					{
						sendingInfo.setKey(RSA.encrypt(info.getKey(), client.getClientInfo().getPublicKey()));
						client.getCommandOutputStream().writeObject("ADD CHAT AREA");
						client.getCommandOutputStream().writeObject(new ChatAreaInfo(sendingInfo));
					}
				}
			}		
		}
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
		if(!(object instanceof ServerSide))
		{
			return false;
		}
		ServerSide other = (ServerSide) object;
		if(other.getClientInfo().equals(clientInfo))
		{
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode()
	{
		int hash = 3;
		hash = hash + clientInfo.hashCode();
		return hash;
	}
	
	/**
	 * Creates a list of IPAddresses.
	 * @return the list of inetAddresses of all the clients in the dataBase.
	 */
	public ArrayList<InetAddress> createIPList()
	{
		ArrayList<InetAddress> ipAddresses = new ArrayList<InetAddress>();
		for(ClientInfo info: clientAccepter.getServerInfo().getClientsInDataBase().values())
		{
			ipAddresses.add(info.getIPAddress());
		}
		return ipAddresses;
	}
}
