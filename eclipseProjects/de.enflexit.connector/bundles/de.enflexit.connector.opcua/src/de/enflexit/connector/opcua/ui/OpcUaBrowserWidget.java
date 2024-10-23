package de.enflexit.connector.opcua.ui;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;

import de.enflexit.connector.opcua.BundleHelper;
import de.enflexit.connector.opcua.OpcUaConnector;
import de.enflexit.connector.opcua.OpcUaConnectorListener;

/**
 * The Class OpcUaBrowserWidget.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaBrowserWidget extends JScrollPane implements TreeSelectionListener, OpcUaConnectorListener {

	private static final long serialVersionUID = -4990198119008390049L;
	
	private OpcUaConnector opcUaConnector;
	
	private JTree jTreeOpcUaNodes;
	
	private JPopupMenu treePopoUp;
	private JMenuItem jMenuItemAddToDataView;
	
	
	/**
	 * Instantiates a new OpcUaBrowserWidget.
	 * @param opcUaConnector the current OpcUaConnector
	 */
	public OpcUaBrowserWidget(OpcUaConnector opcUaConnector) {
		this.opcUaConnector = opcUaConnector;
		this.opcUaConnector.addConnectionListener(this);
		this.initialize();
	}
	/**
	 * Initialize.
	 */
	private void initialize() {
		this.setViewportView(this.getJTreeOpcUaNodes());
	}
	
	/**
	 * Returns the OPC UA browser model of the current connector.
	 * @return the opc UA browser model
	 */
	private OpcUaBrowserTreeModel getOpcUaBrowserModel() {
		return this.opcUaConnector.getOpcUaBrowserTreeModel();
	}
	/**
	 * Returns the JTree of the OPC/UA nodes.
	 * @return the JTree opc ua nodes
	 */
	private JTree getJTreeOpcUaNodes() {
		if (jTreeOpcUaNodes==null) {
			jTreeOpcUaNodes = new JTree(this.getOpcUaBrowserModel());
			jTreeOpcUaNodes.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			jTreeOpcUaNodes.addTreeSelectionListener(this);
			jTreeOpcUaNodes.addKeyListener(this.getKeyAdapter());

			jTreeOpcUaNodes.setDragEnabled(true);
			jTreeOpcUaNodes.setTransferHandler(new OpcUaBrowserTransferHandler(this.opcUaConnector));
			
			jTreeOpcUaNodes.addMouseListener(this.getMouseAdapter());
			
			jTreeOpcUaNodes.setCellRenderer(new OpcUaBrowserTreeCellRenderer());
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
		
		// --- Get currently selected tree node -----------
		OpcUaTreeNode selectedNode = (OpcUaTreeNode) this.getJTreeOpcUaNodes().getLastSelectedPathComponent();
		if (selectedNode==null) return;

		// --- Continue browsing if required --------------
		this.getOpcUaBrowserModel().browseNode(selectedNode, true, selectedNode.getLevel() + 2);
		
		// --- Inform about UaNode selection --------------
		UaNode uaNode = selectedNode.getUaNode();
		if (uaNode!=null) {
			this.opcUaConnector.setBrowserUaNode(uaNode);
		}
	}
	
	/**
	 * Returns the mouse adapter for this JTree.
	 * @return the mouse adapter
	 */
	private MouseAdapter getMouseAdapter() {
		
		MouseAdapter ma = new MouseAdapter() {
			
			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
			 */
			@Override
			public void mousePressed(MouseEvent me) {
			
				if (SwingUtilities.isRightMouseButton(me)) {
					// --- Display the context menu -----------------
					JTree myTree = (JTree) me.getSource();
					TreePath path = myTree.getPathForLocation(me.getX(), me.getY());
					Rectangle pathBounds = myTree.getUI().getPathBounds(myTree, path);
					if (pathBounds!=null && pathBounds.contains(me.getX(), me.getY())) {
						myTree.setSelectionPath(path);
						myTree.scrollPathToVisible(path);
						OpcUaBrowserWidget.this.getPopupMenu().show (myTree, pathBounds.x, pathBounds.y + pathBounds.height);
					}
				}
			} // end mousePressed
		};
		return ma;
	}
	/**
	 * Returns the key adapter.
	 * @return the key adapter
	 */
	private KeyAdapter getKeyAdapter() {
		
		KeyAdapter ka = new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent ke) {
				
				if (ke.getKeyCode()==KeyEvent.VK_CONTEXT_MENU ) {
					// --------------------------------------------
					// --- Show context menu ----------------------
					// --------------------------------------------
					JTree browserTree = OpcUaBrowserWidget.this.getJTreeOpcUaNodes();
					Rectangle pathBounds = browserTree. getPathBounds(browserTree.getSelectionPath());
					OpcUaBrowserWidget.this.getPopupMenu().show (browserTree, pathBounds.x, pathBounds.y + pathBounds.height);
				}
			} // key released
		};
		return ka;
	}
	
	/**
	 * Returns the JPopupMenu of the browser tree.
	 * @return the popup menu
	 */
	private JPopupMenu getPopupMenu() {
		if (treePopoUp==null) {
			treePopoUp = new JPopupMenu();
			treePopoUp.add(this.getJMenuItemAddToDataView());
		}
		return treePopoUp;
	}
	/**
	 * Returns the JMenuItem to add the current UaNode to the data view.
	 * @return the j menu item add to data view
	 */
	private JMenuItem getJMenuItemAddToDataView() {
		if (jMenuItemAddToDataView==null) {
			jMenuItemAddToDataView = new JMenuItem("Add to 'Data View' list");
			jMenuItemAddToDataView.setIcon(BundleHelper.getImageIcon("ListPlus.png"));
			jMenuItemAddToDataView.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					JTree browserTree = OpcUaBrowserWidget.this.getJTreeOpcUaNodes();
					OpcUaTreeNode treeNode = (OpcUaTreeNode) browserTree.getLastSelectedPathComponent();
					UaNode uaNode = treeNode.getUaNode();
					OpcUaBrowserWidget.this.opcUaConnector.getOpcUaDataAccess().addOpcUaNode(uaNode);
				}
			});
		}
		return jMenuItemAddToDataView;
	}
	
	
	// --------------------------------------------------------------
	// --- ConnectorListerner events --------------------------------
	/* (non-Javadoc)
	 * @see de.enflexit.connector.opcua.OpcUaConnectorListener#onConnection()
	 */
	@Override
	public void onConnection() {
		this.getOpcUaBrowserModel().reBuildTreeModel();
		this.getJTreeOpcUaNodes().setEnabled(true);
	}
	/* (non-Javadoc)
	 * @see de.enflexit.connector.opcua.OpcUaConnectorListener#onDisconnection()
	 */
	@Override
	public void onDisconnection() {
		this.getOpcUaBrowserModel().clearTreeModel();
		this.getJTreeOpcUaNodes().setEnabled(false);
	}
	/* (non-Javadoc)
	 * @see de.enflexit.connector.opcua.OpcUaConnectorListener#onSessionInactive()
	 */
	@Override
	public void onSessionInactive() {
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
	 * @see de.enflexit.connector.opcua.OpcUaConnectorListener#onBrowserUaNodeSelection()
	 */
	@Override
	public void onBrowserUaNodeSelection() {
		// --- Nothing to do here, since this is the browser ---
	}
	
}
