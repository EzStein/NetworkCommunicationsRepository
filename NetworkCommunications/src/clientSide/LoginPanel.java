package clientSide;
import javax.swing.*;

import crypto.*;
import java.awt.event.*;

/**
 * Contains the components for logging in to the server.
 * @author Ezra Stein
 * @version 1.0
 * @since 2015
 */
public class LoginPanel extends JPanel implements ActionListener
{
	ClientSide clientSide;
	ClientGUI clientGUI;
	JLabel usernameLabel;
	JTextField usernameField;
	JLabel passwordLabel;
	JPasswordField passwordField;
	JButton loginButton;

	/**
	 * ?
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructs a Login panel.
	 * @param clientSide
	 * @param clientGUI
	 */
	public LoginPanel(ClientSide clientSide, ClientGUI clientGUI)
	{
		//setPreferredSize(new Dimension(300,400));
		this.clientSide = clientSide;
		this.clientGUI = clientGUI;
		
		createContent();
	}
	
	/**
	 * Creates the content for this panel.
	 */
	public void createContent()
	{
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		usernameLabel = new JLabel("Enter Username: ");
		add(usernameLabel);
		usernameField = new JTextField(10);
		add(usernameField);
		passwordLabel = new JLabel("Enter Password: ");
		add(passwordLabel);
		passwordField = new JPasswordField(10);
		add(passwordField);
		loginButton = new JButton("LOGIN");
		loginButton.addActionListener(this);
		add(loginButton);
	}
	
	/**
	 * Called when the "LOGIN" button is pressed.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		new Thread(new Runnable() {public void run(){
			
			clientSide.send("LOGIN");
			clientGUI.getClient().getClientInfo().setUsername(usernameField.getText());
			clientGUI.getClient().getClientInfo().setPassword(SHA.hash(CryptoFunctions.toString(passwordField.getPassword()) + usernameField.getText()));
			clientSide.send(new ClientInfo(clientGUI.getClient().getClientInfo()));
			
			
		}}).start();
		
	}
	
	
}
