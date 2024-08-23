package de.enflexit.connector.opcua;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.NamespaceTable;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseDirection;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseResultMask;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseResult;
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;

/**
 * The Class OpcUABrowserModel.
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUABrowserModel extends DefaultTreeModel {

	private static final long serialVersionUID = -597747426437325190L;

	private OpcUaConnector opcuaConnector;
	private DefaultMutableTreeNode rootTreeNode;

	/**
	 * Instantiates a new OpcUABrowserModel.
	 * @param opcuaConnector the OPCUA connector
	 */
	public OpcUABrowserModel(OpcUaConnector opcuaConnector) {
		super(null, false);
		this.opcuaConnector = opcuaConnector;
		this.reBuildTreeModel();
	}
	/**
	 * Returns the current Milo OpcUaClient.
	 * @return the OpcUaClient
	 */
	private OpcUaClient getOpcUaClient() {
		return this.opcuaConnector.getOpcUaClient();
	}
	/**
	 * Initialize.
	 */
	public void reBuildTreeModel() {
		
		if (this.getOpcUaClient()!=null) {
			this.root = null;
			this.setRoot(this.getRootTreeNode());
			this.browseNode(this.getRootTreeNode(), true);
		}
	}
	
	/**
	 * Returns the root tree node.
	 * @return the root tree node
	 */
	private DefaultMutableTreeNode getRootTreeNode() {
		if (rootTreeNode==null && this.getOpcUaClient()!=null) {
			try {
				UaNode rootNode = this.getOpcUaClient().getAddressSpace().getNode(Identifiers.RootFolder);
				rootTreeNode = new DefaultMutableTreeNode(rootNode);
			} catch (UaException uaEx) {
				uaEx.printStackTrace();
			}
		}
		return rootTreeNode;
	}
	
	/**
	 * Browses the specified node.
	 *
	 * @param parentTreeNode the parent DefaultMutableTreeNode
	 * @param isBrowseRecursively the indicator to browse recursively or not
	 */
	private void browseNode(DefaultMutableTreeNode parentTreeNode, boolean isBrowseRecursively) {

		if (this.getOpcUaClient()==null) return;
		
		// --- If the child nodes were already added, return --------
		if (parentTreeNode.getChildCount()>0) return;

		// --- Get required information  ----------------------------
		UaNode parentUaNode = (UaNode) parentTreeNode.getUserObject();
		NodeId parentNodeID = parentUaNode.getNodeId();
		
		try {
        	
			// --- Get the NamespaceTable ---------------------------
			NamespaceTable nsTable = this.getOpcUaClient().getNamespaceTable();
			
        	// --- Define the base of the browsing action -----------
        	BrowseDescription browse = new BrowseDescription(parentNodeID, BrowseDirection.Forward, Identifiers.References, true, UInteger.valueOf(NodeClass.Object.getValue() | NodeClass.Variable.getValue()), UInteger.valueOf(BrowseResultMask.All.getValue()));
            BrowseResult browseResult = this.getOpcUaClient().browse(browse).get();

            // --- Check each child ---------------------------------
            List<ReferenceDescription> references = Arrays.asList(browseResult.getReferences());
            for (ReferenceDescription rd : references) {
                
                ExpandedNodeId exNodeID = rd.getNodeId();
                NodeId nodeId = null;
                UaNode uaNode = null;
                if (exNodeID.isLocal()==true) {
					try {
						nodeId = exNodeID.toNodeIdOrThrow(nsTable);
						if (nodeId!=null) uaNode = this.getOpcUaClient().getAddressSpace().getNode(nodeId);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
                	
                } else {
                	System.err.println("[" + this.getClass().getSimpleName() + "] Not a local NodeID: " + exNodeID.toString()); 
                }

                if (uaNode!=null) {
                	DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(uaNode);
                	parentTreeNode.add(treeNode);
                	// --- Recursively browse children --------------
                	if (isBrowseRecursively==true) this.browseNode(treeNode, isBrowseRecursively);
                }
            }
            
        } catch (InterruptedException | ExecutionException e) {
        	System.err.println("Browsing nodeId=" + parentNodeID + " failed: " + e.getMessage());
        }
    }

	
}
