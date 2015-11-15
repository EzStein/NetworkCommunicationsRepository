package clientSide;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import crypto.*;

/**
 * The panel in which clients send and receive information to chat.
 * @author Ezra Stein
 * @version 1.0
 * @since 2015
 */
public class ChatPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private JTextArea chatArea;
	private JTextField inputField;
	private ClientGUI clientGUI;
	/**
	 * 
	 * @param clientGUI
	 */
	public ChatPanel(ClientGUI clientGUI)
	{
		this.clientGUI = clientGUI;
		this.setLayout(new BorderLayout());
		chatArea = new JTextArea(15,15);
		chatArea.setEditable(false);
		chatArea.getDocument().addDocumentListener(new TextAreaDocumentListener());
		this.add(new JScrollPane(chatArea), BorderLayout.CENTER);
		inputField = new JTextField();
		inputField.addActionListener(new InputFieldListener());
		this.add(inputField, BorderLayout.SOUTH);
	}
	
	/**
	 * Returns the chatArea as a textArea.
	 * @return the chatArea as a textArea.
	 */
	public JTextArea getChatArea()
	{
		return chatArea;
	}
		
	/**
	 * Attaches to the inputField and listens for an ENTER character.
	 */
	private class InputFieldListener implements ActionListener
	{
		/**
		 * Called when the input field receives an ENTER character.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			//while(client.getCurrentChatArea() == null);
			clientGUI.getClient().send(CryptoFunctions.toString(Encryptor.ECBMode(CryptoFunctions.toBinary(clientGUI.getClient().getClientInfo().getUsername() + " @ " + clientGUI.getClient().getClientInfo().getIPAddress().toString() + ": " + inputField.getText() + "\n"),
					CryptoFunctions.toBaseTwo(RSA.encrypt(clientGUI.getClient().getCurrentChatArea().getKey(),clientGUI.getClient().getClientInfo().getPublicKey().getModulus(),clientGUI.getClient().getDecryptionExponent())),Encryptor.DES_ENCRYPTION)));
			inputField.setText("");
		}
	}
		
	private class TextAreaDocumentListener implements DocumentListener
	{
		@Override
		public void insertUpdate(DocumentEvent e)
		{
			chatArea.setCaretPosition(chatArea.getDocument().getLength());
		}

		@Override
		public void removeUpdate(DocumentEvent e)
		{
		}
		
		@Override
		public void changedUpdate(DocumentEvent e)
		{
		}
	}
}

