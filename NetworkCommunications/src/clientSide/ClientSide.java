package clientSide;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.math.*;
import java.net.*;
import java.util.*;

import crypto.*;

import javax.swing.*;

/**
 * Controls the client side of the client server interactions.
 * @author Ezra Stein
 * @version 1.0
 * @since 2015
 */
public class ClientSide implements Runnable
{
	private final String endOfText = "\u0003";
	private ClientGUI clientGUI;
	private ClientInfo clientInfo;
	private Object lock = new Object();
	private Object lock2 = new Object();
	Socket socket;
	private Socket commandSocket;
	private CommandReader commandReader;
	private ObjectOutputStream commandOut;
	private ObjectInputStream commandIn;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private BigInteger decryptionExponent;//, modulus, encryptionExponent;
	private ChatAreaInfo currentChatArea;
	private ServerInfo serverInfo;
	private boolean closed = false;
	private final String IP;
	
	/**
	 * Creates a client with a reference to the GUI that created it.
	 * @param clientGUI
	 * @param IP The IP address to connect to.
	 */
	public ClientSide(ClientGUI clientGUI, String IP)
	{
		this.IP = IP;
		this.clientGUI = clientGUI;
		clientInfo = new ClientInfo();
		new Thread(new Updater()).start();
	}
	
	/**
	 * Creates a socket connection.
	 * Parses the initialization info that is sent from the server.
	 * Listens for incoming messages and prints them on the GUI textArea.
	 */
	public void run()
	{
		try
		{
			try
			{
				socket = new Socket(IP,20001);
				commandSocket = new Socket(IP,20001);
			}
			catch(ConnectException ce)
			{
				JOptionPane.showMessageDialog(new JFrame(), "The server is offline. The program will now close.", "Connection Error",
						JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
			
			out =  new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			
			commandOut =  new ObjectOutputStream(commandSocket.getOutputStream());
			commandIn = new ObjectInputStream(commandSocket.getInputStream());
			
			commandReader = new CommandReader();
			new Thread(commandReader).start();
			
			clientInfo.setIPAddress(socket.getLocalAddress());
			clientInfo.setSignedIn(false);
			createPublicPrivateKeys();
			while(! login());
			clientInfo.setSignedIn(true);
			synchronized(lock)
			{
				try
				{
					while(serverInfo == null)
					{
						lock.wait();
					}
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			clientGUI.loginComplete();
			for(ChatAreaInfo chatAreaInfo : serverInfo.getChatAreas().values())
			{
				clientGUI.getDataPanel().addValueAndParse(chatAreaInfo.getId() + " " + chatAreaInfo.getName() + "\n");
			}
			
			String input;
			
			System.out.println("0");
			while(((input = (String) in.readObject()) != null))
			{
				System.out.println("1");
				synchronized(lock2)
				{
					try
					{
						while(currentChatArea == null)
						{
							lock2.wait();
						}
					}
					catch(InterruptedException ie)
					{
						ie.printStackTrace();
					}
				}
				System.out.println("2");
				boolean[] b = CryptoFunctions.toBits(input);
				System.out.println("3");
				b = Decryptor.ECBMode(b, CryptoFunctions.toBaseTwo(RSA.encrypt(currentChatArea.getKey(),clientInfo.getPublicKey().getModulus(),decryptionExponent)), Decryptor.DES_DECRYPTION);
				System.out.println("4");
				input = CryptoFunctions.toChars(b);
				System.out.println("5");
				final String input2 = input;
				System.out.println("6");
					SwingUtilities.invokeLater( new Runnable(){
						@Override
						public void run()
						{
							System.out.println("7");
							System.out.println(clientGUI.getChatArea());
							System.out.println(input2);
							clientGUI.getChatArea().append(input2);
						}
						}
						);
					System.out.println("8");
			}
			throw new SocketTerminatedException();
		}
		catch(EOFException eof)
		{
			System.out.println("CLIENT: This should not Occur");
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
				in.close();
				out.close();
				socket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Called when the user needs to login.
	 * @return True if the login was successful, and false otherwise.
	 * @throws ClassNotFoundException 
	 * @throws SocketTerminatedException 
	 * @throws IOException 
	 */
	public boolean login() throws ClassNotFoundException, SocketTerminatedException, IOException
	{
		String input = "";
		if((input = (String) in.readObject())==null)
		{
			throw new SocketTerminatedException();
		}
		if(input.equals("DENIED"))
		{
			JOptionPane.showMessageDialog(new JFrame(), "Either your username or your password was incorrect!", "LOGIN VERIFICATION", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		else if(input.equals("INVALID"))
		{
			JOptionPane.showMessageDialog(new JFrame(), "You have already created an account on this IPAdrress or that username already exists", "CREATE AN ACCOUNT", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		else if(input.equals("ALREADY LOGGED ON"))
		{
			JOptionPane.showMessageDialog(new JFrame(), "You are already logged on from another location!", "Log On", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		else if(input.equals("VERIFIED"))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Called by the GUI when it wants to send a message.
	 * @param message - Message to be sent.
	 */
	public void send(Object message)
	{
		try {
			out.writeObject(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Requests an update, a rewrite of the entire serverInfo object.
	 */
	public void requestUpdate()
	{
		try {
			commandOut.writeObject("REQUEST UPDATE");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Called when the user wants to leave the chat area.
	 */
	public void leaveChatArea()
	{
		try
		{
			out.writeObject(endOfText);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a new chat area with the given name.
	 * @param name - the name of the new chat area.
	 * @param clientsVisible 
	 */
	public void createNewChatArea(String name, ArrayList<String> clientsVisible)
	{
		clientGUI.setTabbedPaneSelectedIndex(0);
		ChatAreaInfo chatAreaInfo = new ChatAreaInfo();
		chatAreaInfo.setName(name);
		
		chatAreaInfo.setClientsVisible(clientsVisible);
		try
		{
			commandOut.writeObject("CREATE NEW CHAT AREA");
			commandOut.writeObject(new ChatAreaInfo(chatAreaInfo));
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Called when this client needs to be closed. First sends a null value to both the command and normal output streams
	 * causing the server to throw a SocketTerminatedException.
	 * At this point the server will send a null value to both this command in and normal input streams
	 * causing the client to throw a SocketTerminatedException.
	 */
	public void close()
	{
		try
		{
			out.writeObject(null);
			commandOut.writeObject(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		closed = true;
	}
	
	/**
	 * Returns whether this client has closed or not.
	 * @return whether this client has closed or not.
	 */
	public boolean isClosed()
	{
		return closed;
	}
	
	/**
	 * If this program has been run before than there already exists a private key for this machine.
	 * Otherwise, it will create a private key and a public key, and will send that public key for storage on the server.
	 */
	public void createPublicPrivateKeys()
	{
		/*Will this work in JAR format*/
		/*This will not work on windows*/
		File dir = new File(System.getProperty("user.home") + "/Documents/ClientServerProgram");
		dir.mkdirs();
		File privateKeyFile = new File(dir, "PrivateKey.txt");
		if(privateKeyFile.exists() && !privateKeyFile.isDirectory())
		{
			try
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(privateKeyFile)));
				BigInteger modulus = new BigInteger(reader.readLine());
				BigInteger encryptionExponent = new BigInteger(reader.readLine());
				clientInfo.setPublicKey(new PublicKey(modulus,encryptionExponent));
				decryptionExponent = new BigInteger(reader.readLine());
				reader.close();
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
		else
		{
			try
			{
				BigInteger[] keys = RSA.GenerateKeys();
				BigInteger modulus = keys[0];
				BigInteger encryptionExponent = keys[1];
				clientInfo.setPublicKey(new PublicKey(modulus,encryptionExponent));
				decryptionExponent = keys[2];
				PrintWriter writer = new PrintWriter(new FileOutputStream(privateKeyFile));
				writer.println(modulus.toString());
				writer.println(encryptionExponent.toString());
				writer.println(decryptionExponent.toString());
				writer.close();
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		System.out.println("DONE WITH FILE IO");
	}
	
	/**
	 * Returns the current chat area that this client is in.
	 * @return the current chat area that this client is in.
	 */
	public ChatAreaInfo getCurrentChatArea()
	{
		return currentChatArea;
	}
	
	/**
	 * Returns all the serverInfo of the whole program as visible to this client.
	 * @return all the serverInfo of the whole program as visible to this client.
	 */
	public ServerInfo getServerInfo()
	{
		return serverInfo;
	}
	
	/**
	 * Returns the clientInfo for this client.
	 * @return the clientInfo for this client.
	 */
	public ClientInfo getClientInfo()
	{
		return clientInfo;
	}
	
	/**
	 * Returns the decryption exponent for this client.
	 * @return the decryption exponent for this client.
	 */
	public BigInteger getDecryptionExponent()
	{
		return decryptionExponent;
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
					if(input.equals("REMOVE CLIENT FROM CHAT AREA"))
					{
						removeClientFromChatArea();
					}
					else if(input.equals("ADD CLIENT TO CHAT AREA"))
					{
						addClientToChatArea();
					}
					else if(input.equals("ADD CHAT AREA"))
					{
						addChatArea();
					}
					else if(input.equals("REMOVE CHAT AREA"))
					{
						removeChatArea();
					}
					else if(input.equals("REMOVE CLIENT"))
					{
						removeClient();
					}
					else if(input.equals("ADD CLIENT"))
					{
						addClient();
					}
					else if(input.equals("UPDATE CLIENT"))
					{
						updateClient();
					}
					else if(input.equals("ADD CLIENT TO DATABASE"))
					{
						addClientToDataBase();
					}
					else if(input.equals("CLOSE"))
					{
						JOptionPane.showMessageDialog(new JFrame(), "The Server has died! This program will no longer function.", "ERROR", JOptionPane.ERROR_MESSAGE);
						close();
					}
				}
				throw new SocketTerminatedException();
			}
			catch(EOFException eof)
			{
				System.out.println("Client Command: THIS SHOULD NOT OCCUR");
			}
			catch(SocketTerminatedException ste)
			{
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (ClassNotFoundException cnfe)
			{
				cnfe.printStackTrace();
			}
			finally
			{
				try
				{
					commandIn.close();
					commandOut.close();
					commandSocket.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * Called when the server has removed a client from the chat area.
		 * @throws IOException
		 * @throws ClassNotFoundException 
		 * @throws SocketTerminatedException 
		 */
		public void removeClientFromChatArea() throws IOException, ClassNotFoundException, SocketTerminatedException
		{
			String id;
			id = (String) commandIn.readObject();
			if((id == null))
			{
				throw new SocketTerminatedException();
			}
			
			ClientInfo input2;
			if((input2= (ClientInfo) commandIn.readObject()) == null)
			{
				throw new SocketTerminatedException();
			}
			serverInfo.getChatAreas().get(id).getClients().remove(input2);
			clientGUI.getDataPanel().updateData();
		}
		
		/**
		 * Called when the server has added a client to a chat area.
		 * @throws IOException
		 * @throws ClassNotFoundException 
		 * @throws SocketTerminatedException 
		 */
		public void addClientToChatArea() throws IOException, ClassNotFoundException, SocketTerminatedException
		{
			String id;
			if((id =(String) commandIn.readObject())==null)
			{
				throw new SocketTerminatedException();
			}
			ClientInfo client;
			if((client = (ClientInfo)commandIn.readObject())==null)
			{
				throw new SocketTerminatedException();
			}
			if(client.equals(clientInfo))
			{
				currentChatArea = serverInfo.getChatAreas().get(id);
				synchronized(lock2)
				{
					lock2.notifyAll();
				}
			}
			serverInfo.getChatAreas().get(id).getClients().add(client);
			clientGUI.getDataPanel().updateData();
		}
		
		/**
		 * Called When a client is added to the server.
		 * @throws IOException 
		 * @throws ClassNotFoundException 
		 * @throws SocketTerminatedException 
		 */
		public void addClient() throws IOException, ClassNotFoundException, SocketTerminatedException
		{
			ClientInfo client;
			if((client= (ClientInfo) commandIn.readObject()) == null)
			{
				throw new SocketTerminatedException();
			}
			if(client.getUsername().equals(clientInfo.getUsername()))
			{
				return;
			}
			serverInfo.getAllClients().add(client);
			clientGUI.getDataPanel().updateData();
		}
		
		/**
		 * Called when a client is removed from the server.
		 * @throws IOException 
		 * @throws ClassNotFoundException 
		 * @throws SocketTerminatedException 
		 */
		public void removeClient() throws IOException, ClassNotFoundException, SocketTerminatedException
		{
			ClientInfo client;
			if((client= (ClientInfo) commandIn.readObject()) == null)
			{
				throw new SocketTerminatedException();
			}
			serverInfo.getAllClients().remove(client);
			clientGUI.getDataPanel().updateData();
				
		}
		
		/**
		 * Adds a chat area.
		 * @throws IOException
		 * @throws ClassNotFoundException
		 * @throws SocketTerminatedException
		 */
		public void addChatArea() throws IOException, ClassNotFoundException, SocketTerminatedException
		{
			ChatAreaInfo chatAreaInfo;
			if((chatAreaInfo = (ChatAreaInfo) commandIn.readObject()) == null)
			{
				throw new SocketTerminatedException();
			}
			
			serverInfo.getChatAreas().put(chatAreaInfo.getId(), chatAreaInfo);
			clientGUI.getDataPanel().addValueAndParse(chatAreaInfo.getId() + " " + chatAreaInfo.getName() + "\n");
		}
		
		/**
		 * Removes a chat area.
		 * @throws IOException
		 * @throws ClassNotFoundException
		 * @throws SocketTerminatedException
		 */
		public void removeChatArea() throws IOException, ClassNotFoundException, SocketTerminatedException
		{
			ChatAreaInfo chatAreaInfo;
			if((chatAreaInfo = (ChatAreaInfo) commandIn.readObject()) == null)
			{
				throw new SocketTerminatedException();
			}
			
			serverInfo.getChatAreas().remove(chatAreaInfo.getId(), chatAreaInfo);
			clientGUI.getDataPanel().removeValueAndParse(chatAreaInfo.getId() + " " + chatAreaInfo.getName() + "\n");
		}
		
		public void updateClient() throws IOException, SocketTerminatedException, ClassNotFoundException
		{
			ServerInfo si = (ServerInfo) commandIn.readObject();
			if(si == null)
			{
				throw new SocketTerminatedException();
			}
			serverInfo = si;
			synchronized(lock)
			{
				lock.notifyAll();
			}
		}
		
		public void addClientToDataBase() throws SocketTerminatedException, IOException, ClassNotFoundException
		{
			ClientInfo info = (ClientInfo) commandIn.readObject();
			if(info == null)
			{
				throw new SocketTerminatedException();
			}
			serverInfo.getClientsInDataBase().put(info.getUsername(), info);
		}
	}
	
	private class Updater implements Runnable
	{

		@Override
		public void run()
		{
			while(true)
			{
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(serverInfo != null && clientGUI.getServerInfoPanel() != null)
				{
					clientGUI.getServerInfoPanel().getInfoArea().setText(serverInfo.toString());
				}
			}
			
		}
		
	}
}
