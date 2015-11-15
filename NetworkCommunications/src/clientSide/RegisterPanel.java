package clientSide;
import java.awt.event.*;
import javax.swing.*;

import crypto.*;

/**
 * Contains the components used for signing up as a new account.
 * @author Ezra Stein
 * @version 1.0
 * @since 2015
 */
public class RegisterPanel extends JPanel implements ActionListener
{
	private ClientSide clientSide;
	private ClientGUI clientGUI;
	private JLabel usernameLabel;
	private JTextField usernameField;
	private JLabel passwordLabel;
	private JPasswordField passwordField;
	private JButton createAccountButton;

	/**
	 * ?
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructs a register panel.
	 * @param clientSide
	 * @param clientGUI
	 */
	public RegisterPanel(ClientSide clientSide, ClientGUI clientGUI)
	{
		this.clientSide = clientSide;
		this.clientGUI = clientGUI;
		
		createContent();
	}
	
	/**
	 * Adds the content for this panel.
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
		createAccountButton = new JButton("CREATE ACCOUNT");
		createAccountButton.addActionListener(this);
		add(createAccountButton);
	}
	
	/**
	 * Called when the "CREATE ACCOUNT" button is pressed.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		new Thread(new Runnable(){public void run(){
			if(usernameField.getText().length() < 3)
			{
				JOptionPane.showMessageDialog(new JFrame(), "Your username must be atleast three characters", "Enter Username", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			if(passwordField.getPassword().length < 3)
			{
				JOptionPane.showMessageDialog(new JFrame(), "Your password must be atleast three characters", "Enter Password", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			clientSide.send("CREATEACCOUNT");
			clientGUI.getClient().getClientInfo().setUsername(usernameField.getText());
			clientGUI.getClient().getClientInfo().setPassword(SHA.hash(CryptoFunctions.toString(passwordField.getPassword()) + usernameField.getText()));
			clientSide.send(new ClientInfo(clientGUI.getClient().getClientInfo()));
		}}).start();
		
	}
}
