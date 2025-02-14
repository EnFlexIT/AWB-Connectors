package de.enflexit.connector.nymea.ui;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

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
	private JTree thingsTree;
	
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
	private JTree getThingsTree() {
		if (thingsTree == null) {
			thingsTree = new JTree();
			thingsTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(NODE_TEXT_EMPTY_TREE)));
		}
		return thingsTree;
	}
	
	/**
	 * Adds child nodes for the provided map contents to the parent node. Map values are expected
	 * to be either Strings or sub-maps. While strings will be added directly, the method will be
	 * called recursively for sub-maps.  
	 * @param map the map containing the data for the child nodes
	 * @param parentNode the parent node
	 */
	private void addMapContentChildNodes(Map<?,?> map, DefaultMutableTreeNode parentNode) {
		for (Object key : map.keySet()) {
			Object value = map.get(key);
			
			if (value instanceof String || value instanceof Boolean || value instanceof Double || value instanceof Integer) {
				// --- Single string value - add to parent ----------
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(key + ": " + value);
				parentNode.add(childNode);
			} else if (value instanceof Map<?,?>) {
				// --- Map structure - add as sub tree --------------
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(key);
				this.addMapContentChildNodes((Map<?, ?>) value, childNode);
				parentNode.add(childNode);
			} else if (value instanceof ArrayList<?>) {
				// --- List of values - add to the parent node directly
				ArrayList<?> valueList = (ArrayList<?>)value;
				if (valueList.size()>0) {
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(key + " (List)");
					this.addListContentChildNodes((ArrayList<?>) value, childNode);
					parentNode.add(childNode);
				}
			} else {
				// --- Unknown type - add error message (should not occur)
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(key + ": " + " Unexpected data type " + value.getClass().getSimpleName());
				parentNode.add(childNode);
			}
		}
	}
	
	private void addListContentChildNodes(ArrayList<?> list, DefaultMutableTreeNode parentNode) {
		for (int i=0; i<list.size(); i++) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
			Object listElement = list.get(i);
			if (listElement instanceof String) {
				childNode.setUserObject(listElement);
				parentNode.add(childNode);
			} else if (listElement instanceof Map<?,?>) {
				childNode.setUserObject("Entry " + (i+1));
				this.addMapContentChildNodes((Map<?, ?>) listElement, childNode);
			}
			else {
				childNode.setUserObject("Unexpected data type: " + listElement.getClass().getSimpleName());
			}
			parentNode.add(childNode);
		}
	}

	/**
	 * Reloads tree model.
	 */
	private void reloadTreeModel() {
		
		DefaultMutableTreeNode rootNode = null;
		
		ArrayList<?> thingsList = this.connector.getNymeaClient().getAvailableThings();
		if (thingsList!=null) {
			rootNode = new DefaultMutableTreeNode("Available things for the connected HEMS system");
			
			for (int i=0; i<thingsList.size(); i++) {
				Map<?,?> thingDetails = (Map<?, ?>) thingsList.get(i);
				DefaultMutableTreeNode thingNode = new DefaultMutableTreeNode(thingDetails.get("name"));
				this.addMapContentChildNodes((Map<?, ?>) thingsList.get(i), thingNode);
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
}
