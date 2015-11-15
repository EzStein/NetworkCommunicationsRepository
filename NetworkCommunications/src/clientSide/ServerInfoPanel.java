package clientSide;

import java.awt.*;
import javax.swing.*;

/**
 * This is a panel to display information about the Server's state. Used for debugging.
 * @author Ezra Stein
 * @version 1.0
 * @since 2015
 */
public class ServerInfoPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private JTextArea infoArea;
	
	/**
	 * Constructs this panel.
	 */
	public ServerInfoPanel()
	{
		this.setLayout(new BorderLayout());
		infoArea = new JTextArea();
		this.add(new JScrollPane(infoArea), BorderLayout.CENTER);
	}
	
	/**
	 * Returns a reference to the infoArea.
	 * @return a reference to the infoArea.
	 */
	public JTextArea getInfoArea()
	{
		return infoArea;
	}
}
