package serverSide;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import clientSide.GUI;

/**
 * Holds all the containers to manage the server.
 * @author Ezra Stein
 * @version 1.0
 * @since 2015
 */
public class ServerGUI extends GUI
{
	/**
	 * Used to except clients and assign each one a new thread (ServerSide) to handle them.
	 */
	private ClientAccepter clientAccepter;
	
	/**
	 * Used primarily for testing and debugging.
	 */
	private JTextArea textArea;
	
	private Thread clientAccepterThread;
	
	/**
	 * Starts the program and creates a GUI on the event dispatch thread.
	 * @param args - Unused.
	 */
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable(){public void run(){
			String[][] s = {{"a","b"},{"c", "d"}};
			new ServerGUI("SERVER",s);
			}});
	}
	
	/**
	 * Constructs a Server with this title and menuItems. The super constructor calls the createContent() method.
	 * @param title - The title.
	 * @param menuItems - The menu items.
	 */
	public ServerGUI(String title, String[][] menuItems)
	{
		super(title, menuItems);
	}
	
	/**
	 * Creates the content for this GUI, and starts the clientAccepter thread and the Updater thread.
	 */
	@Override
	public void createContent()
	{
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new FrameListener());
		clientAccepter = new ClientAccepter(this);
		(clientAccepterThread = new Thread(clientAccepter)).start();
		contentPane.setLayout(new BorderLayout());
		textArea = new JTextArea(30,30);
		textArea.setEditable(false);
		contentPane.add(new JScrollPane(textArea), BorderLayout.CENTER);
		
		new Thread(new Updater()).start();
	}
	
	/**
	 * Called by the server when it needs to update the JTextArea. Mostly used for tests.
	 */
	public void updateInfo()
	{
		if(clientAccepter.getChatAreas().size() >=2)
		{
			//textArea.setText(CryptoFunctions.toChars(CryptoFunctions.toBits(clientAccepter.getChatAreas().get(1).getChatAreaInfo().getText())) + "\n");
		}
	}
	
	/**
	 * Used to catch actions performed on menuItems.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
	}
	
	/**
	 * Runs constantly and is used for testing.
	 * @author Ezra
	 *
	 */
	private class Updater implements Runnable
	{

		@Override
		public void run()
		{
			while(true)
			{
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(clientAccepter.getServerInfo() != null)
				{
					textArea.setText(clientAccepter.getServerInfo().toString());
				}
			}
			
		}
		
	}
	
	/**
	 * Closes this program and all the clients.
	 */
	public void close()
	{
		frame.dispose();
		clientAccepter.close();
		try {
			clientAccepterThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}

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
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub
			
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
}
