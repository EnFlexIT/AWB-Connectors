package de.enflexit.connector.nymea.ui;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolTip;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import de.enflexit.connector.core.ConnectorEvent;
import de.enflexit.connector.core.ConnectorEvent.Event;
import de.enflexit.connector.core.ConnectorListener;
import de.enflexit.connector.nymea.NymeaConnector;

/**
 * This panels allows to browse the servers API information, as provided by nymea's JSONRPC.Introspect method. 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class BrowseThingsPanel extends JPanel implements ConnectorListener {
	
	private static final long serialVersionUID = -6158151344814720644L;
	
	private static final String NODE_TEXT_EMPTY_TREE = "Start the connection to request the available things from the HEMS";

	private JScrollPane thingsTreeScrollPane;
	private ObjectBrowserTree thingsTree;
	
	private NymeaConnector connector;

	/**
	 * Added for window builder compatibility only. Use the other constructor for actual instantiation.
	 */
	@Deprecated
	public BrowseThingsPanel() {}
	
	/**
	 * Instantiates a new introspection panel.
	 * @param connector the nymea connector to perform the introspection on.
	 */
	public BrowseThingsPanel(NymeaConnector connector) {
		this.connector = connector;
		this.connector.addConnectorListener(this);
		initialize();
	}
	private void initialize() {
		this.setLayout(new BorderLayout(0, 0));
		this.add(getThingsTreeScrollPane(), BorderLayout.CENTER);
	}
	
	private JScrollPane getThingsTreeScrollPane() {
		if (thingsTreeScrollPane == null) {
			thingsTreeScrollPane = new JScrollPane();
			thingsTreeScrollPane.setViewportView(getThingsTree());
		}
		return thingsTreeScrollPane;
	}
	private ObjectBrowserTree getThingsTree() {
		if (thingsTree == null) {
			thingsTree = new ObjectBrowserTree();
			thingsTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(NODE_TEXT_EMPTY_TREE)));
			thingsTree.setToolTipText("Right click on a node to copy its value to the clipboard");
			thingsTree.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent me) {
					if (SwingUtilities.isRightMouseButton(me)) {
						DefaultMutableTreeNode clickedNode = BrowseThingsPanel.this.getClickedNode(me.getX(), me.getY());
						if (clickedNode!=null && clickedNode.isLeaf()==true) {
							String nodeContent = (String) clickedNode.getUserObject();
							String[] parts = nodeContent.split(": ");
							if (parts.length==2) {
								String nodeValue = parts[1];
								StringSelection stringSelection = new StringSelection(nodeValue);
								Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
								clipboard.setContents(stringSelection, null);
								BrowseThingsPanel.this.showSuccessMessage(nodeValue, me.getXOnScreen(), me.getYOnScreen());
							}
							
						} else {
							System.err.println("Couldn't identify node!");
						}
					}
				}
			});
		}
		return thingsTree;
	}
	
	/**
	 * Reloads tree model.
	 */
	private void reloadTreeModel() {
		
		DefaultMutableTreeNode rootNode = null;
		
		List<?> thingsList = this.connector.getNymeaClient().getAvailableThings();
		if (thingsList!=null) {
			rootNode = new DefaultMutableTreeNode("Available things for the connected HEMS system");
			
			for (int i=0; i<thingsList.size(); i++) {
				Map<?,?> thingDetails = (Map<?, ?>) thingsList.get(i);
				DefaultMutableTreeNode thingNode = new DefaultMutableTreeNode(thingDetails.get("name"));
				ObjectBrowserTree.addMapContentChildNodes((Map<?, ?>) thingsList.get(i), thingNode);
				rootNode.add(thingNode);
			}
			
			this.getThingsTree().setModel(new DefaultTreeModel(rootNode));
		} else {
			JOptionPane.showMessageDialog(this, "Requesting things from the HEMS failed! Please check your conenciton settings!", "Unable to get things details!", JOptionPane.ERROR_MESSAGE);
		}
		
		this.getThingsTree().setModel(new DefaultTreeModel(rootNode));
		this.getThingsTree().repaint();
	}

	@Override
	public void onConnectorEvent(ConnectorEvent connectorEvent) {
		if (connectorEvent.getSource()==this.connector) {
			if (connectorEvent.getEvent()==Event.CONNECTED) {
				this.reloadTreeModel();
			} else if (connectorEvent.getEvent()==Event.DISCONNECTED) {
				this.getThingsTree().setModel(new DefaultTreeModel(new DefaultMutableTreeNode(NODE_TEXT_EMPTY_TREE)));
			}
		}
	}
	
	
	private DefaultMutableTreeNode getClickedNode(int clickX, int clickY) {
        TreePath path = this.getThingsTree().getPathForLocation(clickX, clickY);
        if (path!=null) {
        	Object node = path.getLastPathComponent();
        	if (node instanceof DefaultMutableTreeNode) {
        		return (DefaultMutableTreeNode) node;
        	}
        }
        return null;
	}
	
	private void showSuccessMessage(String copiedText, int posX, int posY) {
		
		JToolTip toolTip = this.getThingsTree().createToolTip();
		toolTip.setTipText("Copied " + copiedText + " to the clipboard.");
		
		Popup popup = PopupFactory.getSharedInstance().getPopup(this.getThingsTree(), toolTip, posX, posY);
		popup.show();
	    // create a timer to hide the popup later
	    Timer timer = new Timer(1500, new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	popup.hide();

	        }
	    });
	    timer.setRepeats(false);
	    timer.start();
		//TODO show in a popup/tooltip or similar 
	}
	
}
