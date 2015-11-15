package clientSide;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

/**
 * The client side GUI. Initially displays a login and sign in tabs.
 * Once logged in it has 3-4 tabs. One holds information about the server (dataPanel), another
 * allows you to create 
 * @author Ezra Stein
 * @version 1.0
 * @since 2015
 */
public class ClientGUI extends GUI
{
	/**
	 * The login panel which contains a user name and password field.
	 */
	private LoginPanel loginPanel;
	
	/**
	 * The create account panel which contains a user name and password field.
	 */
	private RegisterPanel registerPanel;
	
	/**
	 * The chat area where the user enters in messages and receives messages.
	 */
	private ChatPanel chatPanel;
	
	private NewChatAreaPanel chatAreaPanel;
	private DataPanel dataPanel;
	private boolean inChatArea = false;
	private Thread thread;
	
	/**
	 * Holds the tabs for each panel.
	 */
	private JTabbedPane tabbedPane;
	
	
	private ServerInfoPanel serverInfoPanel;
	
	/**
	 * Creates a thread that will run the client side program.
	 */
	private ClientSide client;
	
	/**
	 * Contains a reference to this object for use in the inner classes.
	 */
	private ClientGUI clientGUI = this;
	
	/**
	 * Creates a GUI for the client.
	 * @param args - Unused.
	 */
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable(){public void run(){
			String[][] s = {{"Client","Quit"},{"Edit", "Leave Chat Area", "Request Update"}};
			/*String input = JOptionPane.showInputDialog(new JFrame(), "What IP address should we connect to.");
			if(input == null)
			{
				System.exit(0);
			}
			new ClientGUI("No Chat Area",s,input);*/
			new ClientGUI("No Chat Area",s,"72.93.84.140");
			}});
	}
	
	/**
	 * Constructs a ClientGUI.
	 * @param title - The title.
	 * @param menuItems - The menu items.
	 * @param arg - The ip address
	 */
	public ClientGUI(String title, String[][] menuItems, String arg)
	{
		super(title,menuItems);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new FrameListener());
		contentPane.setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane();
		
		client = new ClientSide(this, arg);
		(thread = new Thread(client)).start();
		loginPanel = new LoginPanel(client, this);
		tabbedPane.addTab("Log In", null, loginPanel, "Log In");
		
		registerPanel = new RegisterPanel(client, this);
		tabbedPane.addTab("Create Account", null, registerPanel, "Create Account");
		
		contentPane.add(tabbedPane);
		this.getMenuItem("Leave Chat Area").setEnabled(false);
		this.getMenuItem("Request Update").setEnabled(false);
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * Called when a menuItem is pressed.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("Quit"))
		{
			close();
		}
		else if(e.getActionCommand().equals("Leave Chat Area"))
		{
			leaveChatArea();
		} else if(e.getActionCommand().equals("Request Update"))
		{
			client.requestUpdate();
			chatAreaPanel.updateBoxes();
		}
	}
	
	/**
	 * Creates the content for this GUI.
	 */
	@Override
	public void createContent()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Called when the user wants to leave the chat area.
	 */
	public void leaveChatArea()
	{
		chatPanel.getChatArea().setText("");
		inChatArea = false;
		this.getMenuItem("Leave Chat Area").setEnabled(false);
		tabbedPane.remove(chatPanel);
		dataPanel.getButton().setText("Select");
		contentPane.invalidate();
		contentPane.validate();
		frame.setTitle("No Chat Area");
		client.leaveChatArea();
	}
	
	/**
	 * Called when the client enters a chat area.
	 */
	public void enterChatArea()
	{
		
		inChatArea = true;
		getMenuItem("Leave Chat Area").setEnabled(true);
		tabbedPane.addTab("CHAT PANEL", null, chatPanel, "Chat");
		tabbedPane.setSelectedComponent(chatPanel);
		dataPanel.getButton().setText("Leave Chat Area");
		contentPane.invalidate();
		contentPane.validate();
	}
	
	/**
	 * Returns the chatArea of this GUI.
	 * @return the chatArea of this GUI.
	 */
	public JTextArea getChatArea()
	{
		return chatPanel.getChatArea();
	}
	
	/**
	 * Returns whether the client is in a chat area.
	 * @return true if the client is in a chat area.
	 */
	public boolean inChatArea()
	{
		return inChatArea;
	}
	
	/**
	 * Returns the info area of this GUI.
	 * @return the info area of this GUI
	 */
	public DataPanel getDataPanel()
	{
		return dataPanel;
	}
	
	
	
	/**
	 * Listens for events on this frame and handles closing duties.
	 */
	private class FrameListener implements WindowListener
	{

		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void windowClosing(WindowEvent e)
		{
			close();
		}

		@Override
		public void windowClosed(WindowEvent we)
		{	
		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	/**
	 * Called by the client side when it has successfully logged in.
	 */
	public void loginComplete()
	{
		try {
			SwingUtilities.invokeAndWait(new Runnable(){
				public void run()
				{
					getMenuItem("Request Update").setEnabled(true);
					
					tabbedPane.remove(loginPanel);
					tabbedPane.remove(registerPanel);
					
					dataPanel = new DataPanel(clientGUI);
					chatAreaPanel = new NewChatAreaPanel(clientGUI);
					chatPanel = new ChatPanel(clientGUI);
					serverInfoPanel = new ServerInfoPanel();
					
					tabbedPane.addTab("DATA PANEL", null, dataPanel, "Data");
					tabbedPane.addTab("OTHER", null, chatAreaPanel, "Other");
					tabbedPane.addTab("SERVER INFO", null, serverInfoPanel, "Server Info");
					
					frame.setSize(new Dimension(600,400));
					contentPane.invalidate();
					contentPane.validate();
				}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Called when the program is quitting.
	 */
	public void  close()
	{
		frame.dispose();
		if(!client.isClosed())
		{
			client.close();
		}
		try
		{
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	/**
	 * Returns the frame of this GUI.
	 * @return the frame of this GUI.
	 */
	public JFrame getFrame()
	{
		return frame;
	}
	
	/**
	 * Returns an instance of the client.
	 * @return an instance of the client.
	 */
	public ClientSide getClient()
	{
		return client;
	}
	
	/**
	 * Returns the serverInfoPanel.
	 * @return the serverInfoPanel.
	 */
	public ServerInfoPanel getServerInfoPanel()
	{
		return serverInfoPanel;
	}
	
	/**
	 * Sets the currently viewed panel to the tab with the specified index.
	 * @param index - the index to view.
	 */
	public void setTabbedPaneSelectedIndex(int index)
	{
		tabbedPane.setSelectedIndex(index);
	}
}
