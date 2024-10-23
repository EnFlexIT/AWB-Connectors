package de.enflexit.connector.opcua.ui;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.TransferHandler;

import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;

import de.enflexit.connector.opcua.BundleHelper;
import de.enflexit.connector.opcua.OpcUaConnector;

/**
 * The Class OpcUaBrowserTransferHandler.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaBrowserTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 3623171916708554041L;

	private DataFlavor dfUaNode = new DataFlavor(UaNode.class, "Milo UaNode-Instance");
	private OpcUaConnector opcUaConnector;
	
	/**
	 * Instantiates a new opc ua browser transfer handler.
	 * @param opcUaConnector the OpcUaConnector
	 */
	public OpcUaBrowserTransferHandler(OpcUaConnector opcUaConnector) {
		this.opcUaConnector = opcUaConnector;
	}
	
	// ----------------------------------------------------
	// --- Export methods ----------------------- Start ---
	// ----------------------------------------------------
	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
	 */
	@Override
	public int getSourceActions(JComponent c) {
		return COPY;
	}
	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	 */
	@Override
	protected Transferable createTransferable(JComponent c) {
		
		if ((c instanceof JTree)==false) return null;
		
		JTree browserTree = (JTree) c;
		if (browserTree.getSelectionPath()==null) return null;
		
		OpcUaTreeNode uaTreeNode = (OpcUaTreeNode) browserTree.getSelectionPath().getLastPathComponent();
		final UaNode uaNode = uaTreeNode.getUaNode();
		
		return new Transferable() {
			/* (non-Javadoc)
			 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
			 */
			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[]{OpcUaBrowserTransferHandler.this.dfUaNode};
			}
			/* (non-Javadoc)
			 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
			 */
			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return flavor!=null && flavor.getRepresentationClass().equals(UaNode.class);
			}
			/* (non-Javadoc)
			 * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
			 */
			@Override
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				return uaNode;
			}
		};
	}
	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent, java.awt.datatransfer.Transferable, int)
	 */
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		// --- Nothing to do here -------------------------
	}
	// ----------------------------------------------------
	// --- Export methods ----------------------- End -----
	// ----------------------------------------------------
	
	
	// ----------------------------------------------------
	// --- Import methods ----------------------- Start ---
	// ----------------------------------------------------
	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#canImport(javax.swing.TransferHandler.TransferSupport)
	 */
	@Override
	public boolean canImport(TransferSupport support) {
		
		Component comp = support.getComponent();
		DataFlavor[] transferFlavors = support.getDataFlavors();

		if ((comp instanceof JTable)==false) return false;
		
		JTable targetTable = (JTable) comp;
		boolean correctTable = BundleHelper.getParentComponentByClass(targetTable, OpcUaDataView.class)!=null; 
		boolean correctFlavor = transferFlavors!=null && transferFlavors[0].getRepresentationClass().equals(UaNode.class);
		return correctTable & correctFlavor;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#importData(javax.swing.TransferHandler.TransferSupport)
	 */
	@Override
	public boolean importData(TransferSupport support) {
		
		UaNode uaNodeTrans = null;
		try {
			uaNodeTrans = (UaNode) support.getTransferable().getTransferData(this.dfUaNode);
			this.opcUaConnector.getOpcUaDataAccess().addOpcUaNode(uaNodeTrans);
			return true;
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	// ----------------------------------------------------
	// --- Import methods ----------------------- End -----
	// ----------------------------------------------------
	
}
	
	