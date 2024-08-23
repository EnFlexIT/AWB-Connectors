package de.enflexit.connector.opcua.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

import de.enflexit.connector.opcua.OpcUaConnector;

/**
 * The Class OpcUABrowserTreeModel.
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUABrowserTreeModel extends DefaultTreeModel {

	private static final long serialVersionUID = -597747426437325190L;

	private OpcUaConnector opcuaConnector;
	private DefaultMutableTreeNode rootTreeNode;

	private HashMap<UaNode, DefaultMutableTreeNode> nodeHashMap;
	
	/**
	 * Instantiates a new OpcUABrowserTreeModel.
	 * @param opcuaConnector the OPCUA connector
	 */
	public OpcUABrowserTreeModel(OpcUaConnector opcuaConnector) {
		super(null, false);
		this.opcuaConnector = opcuaConnector;
		this.buildTreeModel(2);
	}
	/**
	 * Returns the current Milo OpcUaClient.
	 * @return the OpcUaClient
	 */
	private OpcUaClient getOpcUaClient() {
		return this.opcuaConnector.getOpcUaClient();
	}
	
	/**
	 * Clears the local TreeModel.
	 */
	public void clearTreeModel() {
		this.getRootTreeNode().removeAllChildren();
		this.root = null;
		this.setRoot(null);
		this.getNodeHashMap().clear();
	}
	/**
	 * Re build the TreeModel.
	 */
	public void reBuildTreeModel() {
		this.clearTreeModel();
		this.buildTreeModel(2);
	}
	/**
	 * Builds the tree model.
	 */
	public void buildTreeModel(Integer maxDepth) {
		if (this.getOpcUaClient()!=null) {
			this.setRoot(this.getRootTreeNode());
			this.browseNode(this.getRootTreeNode(), true, maxDepth);
		}
	}
	
	/**
	 * Returns the root tree node.
	 * @return the root tree node
	 */
	private DefaultMutableTreeNode getRootTreeNode() {
		if (rootTreeNode==null && this.getOpcUaClient()!=null) {
			try {
				UaNode rootUaNode = this.getOpcUaClient().getAddressSpace().getNode(Identifiers.RootFolder);
				rootTreeNode = new DefaultMutableTreeNode(rootUaNode);
				this.remindNodeMapping(rootUaNode, rootTreeNode);
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
	 * @param maxLevel the max level to search recursively
	 */
	public void browseNode(DefaultMutableTreeNode parentTreeNode, boolean isBrowseRecursively, Integer maxLevel) {

		if (this.getOpcUaClient()==null) return;
		
		// --- Get required information  ----------------------------
		UaNode parentUaNode = (UaNode) parentTreeNode.getUserObject();
		NodeId parentNodeID = parentUaNode.getNodeId();
		
		try {
        	
			// --- Get the NamespaceTable ---------------------------
			NamespaceTable nsTable = this.getOpcUaClient().getNamespaceTable();
			
        	// --- Define the base of the browsing action -----------
        	BrowseDescription browse = new BrowseDescription(parentNodeID, BrowseDirection.Forward, Identifiers.References, true, UInteger.valueOf(NodeClass.Object.getValue() | NodeClass.Variable.getValue()), UInteger.valueOf(BrowseResultMask.All.getValue()));
            BrowseResult browseResult = this.getOpcUaClient().browse(browse).get();

            // --- Get sorted list of ReferenceDescriptions ---------
            List<ReferenceDescription> references = Arrays.asList(browseResult.getReferences());
            Collections.sort(references, new Comparator<ReferenceDescription>() {
				public int compare(ReferenceDescription rd1, ReferenceDescription rd2) {
					return rd1.getBrowseName().getName().compareTo(rd2.getBrowseName().getName());
				}
			});
            
            // --- Check each child ---------------------------------
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
                	// --- Check if tree node already exists --------
                	DefaultMutableTreeNode treeNode = this.getNodeHashMap().get(uaNode);
                	if (treeNode==null) {
                		// --- Create a visual TreeNode -------------
                		treeNode = new DefaultMutableTreeNode(uaNode);
                		parentTreeNode.add(treeNode);
                		this.remindNodeMapping(uaNode, treeNode);
                	}
                	
                	// --- Recursively browse children --------------
                	int treeNodeLevel = treeNode.getLevel();
                	boolean isDoDeeperBrowsing = maxLevel==null ? true : treeNodeLevel < maxLevel;
                	if (isBrowseRecursively==true && isDoDeeperBrowsing==true) this.browseNode(treeNode, isBrowseRecursively, maxLevel);
                }
            }
            
        } catch (InterruptedException | ExecutionException e) {
        	System.err.println("Browsing nodeId=" + parentNodeID + " failed: " + e.getMessage());
        }
    }

	
	/**
	 * Returns the UaNode to DefaultMutableTreeNode HashMap.
	 * @return the node hash map
	 */
	public HashMap<UaNode, DefaultMutableTreeNode> getNodeHashMap() {
		if (nodeHashMap==null) {
			nodeHashMap = new HashMap<>();
		}
		return nodeHashMap;
	}
	/**
	 * Reminds the UaNode to {@link DefaultMutableTreeNode} mapping.
	 *
	 * @param uaNode the ua node
	 * @param treeNode the tree node
	 */
	private void remindNodeMapping(UaNode uaNode, DefaultMutableTreeNode treeNode) {
		if (uaNode!=null && treeNode!=null) {
			this.getNodeHashMap().put(uaNode, treeNode);
		}
	}
	
}
