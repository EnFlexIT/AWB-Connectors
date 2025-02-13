package de.enflexit.connector.nymea.ui;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
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
public class IntrospectionPanel extends JPanel implements ConnectorListener {
	
	private static final long serialVersionUID = -6158151344814720644L;
	
	private static final String NODE_TEXT_EMPTY_TREE = "Start the connection to load API information from the server";

	private JScrollPane introspectionTreeScrollPane;
	private JTree introspectionTree;
	
	private NymeaConnector connector;

	/**
	 * Added for window builder compatibility only. Use the other constructor for actual instantiation.
	 */
	@Deprecated
	public IntrospectionPanel() {}
	
	/**
	 * Instantiates a new introspection panel.
	 * @param connector the nymea connector to perform the introspection on.
	 */
	public IntrospectionPanel(NymeaConnector connector) {
		this.connector = connector;
		this.connector.addConnectorListener(this);
		initialize();
	}
	private void initialize() {
		this.setLayout(new BorderLayout(0, 0));
		this.add(getIntrospectionTreeScrollPane(), BorderLayout.CENTER);
	}
	
	private JScrollPane getIntrospectionTreeScrollPane() {
		if (introspectionTreeScrollPane == null) {
			introspectionTreeScrollPane = new JScrollPane();
			introspectionTreeScrollPane.setViewportView(getIntrospectionTree());
		}
		return introspectionTreeScrollPane;
	}
	private JTree getIntrospectionTree() {
		if (introspectionTree == null) {
			introspectionTree = new JTree();
			introspectionTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Start the connection to load the model")));
		}
		return introspectionTree;
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
			
			if (value instanceof String) {
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
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(key);
				this.addListContentChildNodes((ArrayList<?>) value, childNode);
				parentNode.add(childNode);
			} else {
				// --- Unknown type - add error message (should not occur)
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(key + ": " + " Unexpected data type " + value.getClass().getSimpleName());
				parentNode.add(childNode);
			}
		}
	}
	
	private void addListContentChildNodes(ArrayList<?> list, DefaultMutableTreeNode parentNode) {
		for (Object listElement : list) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
			if (listElement instanceof String) {
				childNode.setUserObject(listElement);
			} else {
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
		
		HashMap<String, Object> introspectionResults = this.connector.getNymeaClient().sendIntrospectionRequest();
		if (introspectionResults!=null) {
			rootNode = new DefaultMutableTreeNode("API info from " + this.connector.getConnectorSettings().getServerHost());
			this.addMapContentChildNodes(introspectionResults, rootNode);
			this.getIntrospectionTree().setModel(new DefaultTreeModel(rootNode));
		} else {
			JOptionPane.showMessageDialog(this, "Loading introspection data from the server failed! Please check your conenciton settings!", "Unable to load server infos!", JOptionPane.ERROR_MESSAGE);
		}
		
		this.getIntrospectionTree().setModel(new DefaultTreeModel(rootNode));
		this.getIntrospectionTree().repaint();
	}

	@Override
	public void onConnectorEvent(ConnectorEvent connectorEvent) {
		if (connectorEvent.getSource()==this.connector) {
			if (connectorEvent.getEvent()==Event.CONNECTED) {
				this.reloadTreeModel();
			} else if (connectorEvent.getEvent()==Event.DISCONNECTED) {
				this.getIntrospectionTree().setModel(new DefaultTreeModel(new DefaultMutableTreeNode(NODE_TEXT_EMPTY_TREE)));
			}
		}
	}
}
