package clientSide;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * A panel to contain the components for creating a new chat area.
 * @author Ezra
 *
 */
public class NewChatAreaPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private ClientGUI clientGUI;
	private JButton newChatAreaButton;
	private JLabel nameLabel, visibilityLabel;
	private JTextField nameField;
	private JPanel checkBoxesPanel;
	private ArrayList<JCheckBox> checkBoxes;
	/**
	 * Creates a panel.
	 * @param clientGUI - A reference to the GUI that created it.
	 */
	public NewChatAreaPanel(ClientGUI clientGUI)
	{
		this.clientGUI = clientGUI;
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		checkBoxes = new ArrayList<JCheckBox>();
		nameLabel = new JLabel("Name:");
		visibilityLabel = new JLabel("Visible To:");
		nameField = new JTextField(10);
		nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, nameField.getPreferredSize().height));
		newChatAreaButton = new JButton("Create New Chat Area");
		newChatAreaButton.addActionListener(new CreateChatAreaButtonListener());
		checkBoxesPanel = new JPanel();
		checkBoxesPanel.setLayout(new BoxLayout(checkBoxesPanel, BoxLayout.PAGE_AXIS));
		for(ClientInfo info: clientGUI.getClient().getServerInfo().getClientsInDataBase().values())
		{
			if(! info.getUsername().equals(clientGUI.getClient().getClientInfo().getUsername()))
			{
				JCheckBox checkBox = new JCheckBox(info.getUsername());
				checkBoxesPanel.add(checkBox);
				checkBoxes.add(checkBox);
			}
		}
		JScrollPane pane = new JScrollPane(checkBoxesPanel);
		this.add(nameLabel);
		this.add(nameField);
		this.add(visibilityLabel);
		this.add(pane);
		this.add(newChatAreaButton);
	}
	
	/**
	 * Updates the checkBoxes, called if the user suspects someone has just been added to the database.
	 */
	public void updateBoxes()
	{
		SwingUtilities.invokeLater(new Runnable() {public void run() {
			checkBoxesPanel.removeAll();
			checkBoxes = new ArrayList<JCheckBox>();
			for(ClientInfo info: clientGUI.getClient().getServerInfo().getClientsInDataBase().values())
			{
				if(! info.getUsername().equals(clientGUI.getClient().getClientInfo().getUsername()))
				{
					JCheckBox checkBox = new JCheckBox(info.getUsername());
					checkBoxesPanel.add(checkBox);
					checkBoxes.add(checkBox);
				}
			}
			checkBoxesPanel.updateUI();
		}} );
	}

	/**
	 * Listens for events on the createChatAreaButton
	 */
	private class CreateChatAreaButtonListener implements ActionListener
	{
		/**
		 * Called when the createChatAreaButton is pressed;
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(nameField.getText().length() < 1)
			{
				JOptionPane.showMessageDialog(new JFrame(), "The chat area name must be at least one character long!", "Create New Chat Area", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			for(ChatAreaInfo dummy: clientGUI.getClient().getServerInfo().getChatAreas().values())
			{
				if(dummy.getName().equals(nameField.getText()))
				{
					JOptionPane.showMessageDialog(new JFrame(), "That chat area name already exists!", "Create New Chat Area", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			ArrayList<String> selectedBoxes = new ArrayList<String>();
			for(JCheckBox checkBox: checkBoxes)
			{
				if(checkBox.isSelected())
				{
					selectedBoxes.add(checkBox.getActionCommand());
				}
			}
			if(selectedBoxes.size() == 0)
			{
				selectedBoxes = null;
			}
			else
			{
				selectedBoxes.add(clientGUI.getClient().getClientInfo().getUsername());
			}
			clientGUI.getClient().createNewChatArea(nameField.getText(),selectedBoxes);
		}
	}
}
