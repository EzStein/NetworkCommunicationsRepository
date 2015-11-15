package clientSide;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Contains data information that will be placed in the West area.
 * @author Ezra Stein
 * @version 1.0
 * @since 2015
 *
 */
public class DataPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private ClientGUI clientGUI;
	private JList<String> listBox;
	private Vector<String> listData;
	private JList<String> infoBox;
	private Vector<String> infoData;
	private JTextArea infoArea;
	private JScrollPane scrollPaneList;
	private JScrollPane scrollPaneInfo;
	private JButton button;
	private HashMap<String, String> chatAreaNameToId;
	
	/**
	 * Constructs a datapanel with a reference to the GUI that created it.
	 * @param clientGUI - The GUI that contains this panel.
	 */
	public DataPanel(ClientGUI clientGUI)
	{
		this.clientGUI = clientGUI;
		chatAreaNameToId = new HashMap<String, String>();
		createContent();
	}
	
	
	/**
	 * Creates the content for this JPanel
	 */
	public void createContent()
	{
		setLayout(new BorderLayout());
		listData = new Vector<String>();
		listData.addElement("Signed In Users");
		listBox = new JList<String>(listData);
		listBox.addListSelectionListener(new ChatAreaListListener());
		scrollPaneList = new JScrollPane(listBox);
		this.add(scrollPaneList, BorderLayout.WEST);
		
		infoData = new Vector<String>();
		infoBox = new JList<String>(infoData);
		infoBox.setListData(infoData);
		infoBox.addListSelectionListener(new ClientListListener());
		scrollPaneInfo = new JScrollPane(infoBox);
		infoBox.setPreferredSize(new Dimension(70,200));
		this.add(scrollPaneInfo, BorderLayout.CENTER);
		
		infoArea = new JTextArea(5,30);
		infoArea.setEditable(false);
		this.add(new JScrollPane(infoArea), BorderLayout.EAST);
		
		button = new JButton("Select");
		button.addActionListener(this);
		this.add(button, BorderLayout.SOUTH);
	}

	private class ChatAreaListListener implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if(!e.getValueIsAdjusting() && listBox.getSelectedValue() != null)
			{
				infoData = new Vector<String>();
				if(listBox.getSelectedIndex() == 0)
				{
					for(ClientInfo signedInUser : clientGUI.getClient().getServerInfo().getAllClients())
					{
						infoData.add(signedInUser.getUsername() + "\n");
					}
				}
				else
				{
					for(ClientInfo client : clientGUI.getClient().getServerInfo().getChatAreas().get(chatAreaNameToId.get(listBox.getSelectedValue())).getClients())
					{
						infoData.add(client.getUsername() + "\n");
					}
				}
				
				infoBox.setListData(infoData);
			}
		}
	}
	
	private class ClientListListener implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if(!e.getValueIsAdjusting() && infoBox.getSelectedValue() != null)
			{
				infoArea.setText("");
				if(listBox.getSelectedIndex() != 0 && listBox.getSelectedValue() != null)
				{
					infoArea.append(clientGUI.getClient().getServerInfo().getChatAreas().get(chatAreaNameToId.get(listBox.getSelectedValue())).toFormattedString());
				}
				infoArea.append(clientGUI.getClient().getServerInfo().getClientsInDataBase().get(((String) infoBox.getSelectedValue()).replaceAll("\n", "")).toString());
			}
		}
	}
	
	/**
	 * When the total client List is changed the method is called to update the data area.
	 */
	public void updateData()
	{
		
		if(listBox.getSelectedValue() != null)
		{
			infoData = new Vector<String>();
			if(listBox.getSelectedIndex() == 0)
			{
				for(ClientInfo signedInUser : clientGUI.getClient().getServerInfo().getAllClients())
				{
					infoData.add(signedInUser.getUsername());
				}
			}
			else
			{
				for(ClientInfo client : clientGUI.getClient().getServerInfo().getChatAreas().get(chatAreaNameToId.get(listBox.getSelectedValue())).getClients())
				{
					infoData.add(client.getUsername());
				}
			}
			SwingUtilities.invokeLater(new Runnable(){public void run(){infoBox.setListData(infoData);}});
		}
		
	}
	
	/**
	 * Adds a value to the listBox.
	 * @param addedValue - The string to be added to the list box.
	 */
	public void addValueAndParse(String addedValue)
	{
		String[] parts = addedValue.split(" ");
		String name = "";
		String id = parts[0];
		for(int i = 1; i<= parts.length -1; i++)
		{
			name += parts[i] + " ";
		}
		chatAreaNameToId.put(name, id);
		listData.addElement(name);
		SwingUtilities.invokeLater(new Runnable(){public void run(){listBox.setListData(listData);}});
	}
	
	/**
	 * Removes a value from the listBox.
	 * @param removedValue - The string to be removed to the list box.
	 */
	public void removeValueAndParse(String removedValue)
	{
		String[] parts = removedValue.split(" ");
		String name = "";
		String id = parts[0];
		for(int i = 1; i<= parts.length -1; i++)
		{
			name += parts[i] + " ";
		}
		chatAreaNameToId.remove(name, id);
		listData.removeElement(name);
		SwingUtilities.invokeLater(new Runnable(){public void run(){listBox.setListData(listData);}});
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == button)
		{
			if(button.getText().equals("Select"))
			{
				if(listBox.getSelectedValue() != null && !clientGUI.inChatArea())
				{
					if(listBox.getSelectedIndex() == 0)
					{
						return;
					}
					String out = listBox.getSelectedValue();
					clientGUI.getClient().send(chatAreaNameToId.get(out));
					clientGUI.getFrame().setTitle(clientGUI.getClient().getServerInfo().getChatAreas().get(chatAreaNameToId.get(out)).getName());
					clientGUI.enterChatArea();
				}
			}
			else
			{
				clientGUI.leaveChatArea();
			}
		}
	}
	
	/**
	 * Returns the select/leaveChatArea button.
	 * @return the select/leaveChatArea button.
	 */
	public JButton getButton()
	{
		return button;
	}
}
