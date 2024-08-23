package de.enflexit.connector.opcua.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import de.enflexit.connector.opcua.OpcUABrowserModel;
import de.enflexit.connector.opcua.OpcUaConnector;
import de.enflexit.connector.opcua.OpcUaConnectorListener;

/**
 * The Class OpcUaBrowserWidget.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaBrowserWidget extends JScrollPane implements TreeSelectionListener, KeyListener, OpcUaConnectorListener {

	private static final long serialVersionUID = -4990198119008390049L;
	
	private OpcUaConnector opcUaConnector;
	private OpcUABrowserModel opcUaBrowserModel;
	
	private JTree jTreeOpcUaNodes;
	
	public OpcUaBrowserWidget(OpcUaConnector opcUaConnector) {
		this.opcUaConnector = opcUaConnector;
		this.opcUaConnector.addConnectionListener(this);
		this.intitialize();
	}
	private void intitialize() {
		this.setViewportView(this.getJTreeOpcUaNodes());
	}
	
	/**
	 * Returns the OPC UA browser model of the current connector.
	 * @return the opc UA browser model
	 */
	private OpcUABrowserModel getOpcUABrowserModel() {
		if (opcUaBrowserModel==null) {
			opcUaBrowserModel = new OpcUABrowserModel(this.opcUaConnector);
		}
		return opcUaBrowserModel;
	}
	/**
	 * Returns the JTree of the OPC/UA nodes.
	 * @return the JTree opc ua nodes
	 */
	private JTree getJTreeOpcUaNodes() {
		if (jTreeOpcUaNodes==null) {
			jTreeOpcUaNodes = new JTree(this.getOpcUABrowserModel());
			jTreeOpcUaNodes.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			jTreeOpcUaNodes.addTreeSelectionListener(this);
			jTreeOpcUaNodes.addKeyListener(this);
			//jTreeOpcUaNodes.addMouseListener(this.getMouseAdapter());

			//this.setCellRenderer(this.getGroupTreeCellRenderer());
			ToolTipManager.sharedInstance().registerComponent(this);

		}
		return jTreeOpcUaNodes;
	}
	// --------------------------------------------------------------
	// --- TreeSelectionListener events -----------------------------
	/* (non-Javadoc)
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	@Override
	public void valueChanged(TreeSelectionEvent tsEvt) {
		
		// TODO Auto-generated method stub
		
	}
	
	
	// --------------------------------------------------------------
	// --- KeyListener events ---------------------------------------
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	// --------------------------------------------------------------
	// --- ConnectorListerner events --------------------------------
	/* (non-Javadoc)
	 * @see de.enflexit.connector.opcua.OpcUaConnectorListener#onConnection()
	 */
	@Override
	public void onConnection() {
		this.getOpcUABrowserModel().reBuildTreeModel();
		this.getJTreeOpcUaNodes().setEnabled(true);
	}
	/* (non-Javadoc)
	 * @see de.enflexit.connector.opcua.OpcUaConnectorListener#onDisconnection()
	 */
	@Override
	public void onDisconnection() {
		this.getJTreeOpcUaNodes().setEnabled(false);
	}
	/* (non-Javadoc)
	 * @see de.enflexit.connector.opcua.OpcUaConnectorListener#onSessionActive()
	 */
	@Override
	public void onSessionActive() {
		this.getJTreeOpcUaNodes().setEnabled(true);
		
	}
	/* (non-Javadoc)
	 * @see de.enflexit.connector.opcua.OpcUaConnectorListener#onSessionInactive()
	 */
	@Override
	public void onSessionInactive() {
		this.getJTreeOpcUaNodes().setEnabled(false);
		
	}
	
}
