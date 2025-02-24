package de.enflexit.connector.nymea.ui;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.util.HashMap;
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
	private ObjectBrowserTree introspectionTree;
	
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
	private ObjectBrowserTree getIntrospectionTree() {
		if (introspectionTree == null) {
			introspectionTree = new ObjectBrowserTree();
			introspectionTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Start the connection to load the model")));
		}
		return introspectionTree;
	}
	

	/**
	 * Reloads tree model.
	 */
	private void reloadTreeModel() {
		
		DefaultMutableTreeNode rootNode = null;
		
		HashMap<String, Object> introspectionResults = this.connector.getNymeaClient().sendIntrospectionRequest();
		if (introspectionResults!=null) {
			rootNode = new DefaultMutableTreeNode("API info from " + this.connector.getConnectorSettings().getServerHost());
			ObjectBrowserTree.addMapContentChildNodes(introspectionResults, rootNode);
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
