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
 * The Class OpcUaBrowserTreeModel.
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaBrowserTreeModel extends DefaultTreeModel {

	private static final long serialVersionUID = -597747426437325190L;

	private static final int BUILD_MAX_DEPTH = 10;
	
	private OpcUaConnector opcuaConnector;
	private OpcUaTreeNode rootTreeNode;

	private HashMap<UaNode, OpcUaTreeNode> nodeHashMap;
	
	/**
	 * Instantiates a new OpcUaBrowserTreeModel.
	 * @param opcuaConnector the OPCUA connector
	 */
	public OpcUaBrowserTreeModel(OpcUaConnector opcuaConnector) {
		super(null, false);
		this.opcuaConnector = opcuaConnector;
		this.buildTreeModel(BUILD_MAX_DEPTH);
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
		this.buildTreeModel(BUILD_MAX_DEPTH);
	}
	
	/**
	 * Builds the tree model.
	 * @param maxDepth the max depth
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
	private OpcUaTreeNode getRootTreeNode() {
		if (rootTreeNode==null && this.getOpcUaClient()!=null) {
			try {
				UaNode rootUaNode = this.getOpcUaClient().getAddressSpace().getNode(Identifiers.RootFolder);
				rootTreeNode = new OpcUaTreeNode(rootUaNode);
				this.remindNodeMapping(rootUaNode, rootTreeNode);
			} catch (UaException uaEx) {
				uaEx.printStackTrace();
			}
		}
		return rootTreeNode;
	}
	
	
	/**
	 * Browses the specified node in a dedicated thread.
	 *
	 * @param parentTreeNode the parent DefaultMutableTreeNode
	 * @param isBrowseRecursively the indicator to browse recursively or not
	 * @param maxLevel the max level to search recursively
	 */
	public void browseNodeInThread(final DefaultMutableTreeNode parentTreeNode, final boolean isBrowseRecursively, final Integer maxLevel) {
		
		UaNode uaNode = (UaNode) parentTreeNode.getUserObject();
		new Thread(new Runnable() {
			@Override
			public void run() {
				OpcUaBrowserTreeModel.this.browseNode(parentTreeNode, isBrowseRecursively, maxLevel);
			}
		}, this.getClass().getSimpleName() + "BrowseNode" + uaNode.getBrowseName().getName()).start();
	}
	/**
	 * Browses the specified node.
	 *
	 * @param parentTreeNode the parent DefaultMutableTreeNode
	 * @param isBrowseRecursively the indicator to browse recursively or not
	 * @param maxLevel the max level to search recursively
	 */
	public void browseNode(DefaultMutableTreeNode parentTreeNode, boolean isBrowseRecursively, Integer maxLevel) {

		if (parentTreeNode.getChildCount()>0) return;
		if (this.getOpcUaClient()==null) return;
		
		// --- Get required information  ----------------------------
		UaNode parentUaNode = (UaNode) parentTreeNode.getUserObject();
		NodeId parentNodeID = parentUaNode.getNodeId();
		
		try {
        	
			// --- Get the NamespaceTable ---------------------------
			NamespaceTable nsTable = this.getOpcUaClient().getNamespaceTable();
			
        	// --- Define the base of the browsing action -----------
        	BrowseDescription browseDesc = new BrowseDescription(parentNodeID, BrowseDirection.Forward, Identifiers.References, true, UInteger.valueOf(NodeClass.Object.getValue() | NodeClass.Variable.getValue()), UInteger.valueOf(BrowseResultMask.All.getValue()));
            BrowseResult browseResult = this.getOpcUaClient().browse(browseDesc).get();

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
                	OpcUaTreeNode treeNode = this.getNodeHashMap().get(uaNode);
                	if (treeNode==null) {
                		// --- Create a visual TreeNode -------------
                		treeNode = new OpcUaTreeNode(uaNode);
                		parentTreeNode.add(treeNode);
                		this.remindNodeMapping(uaNode, treeNode);
                		
                		// --- Inform about insert action ----------- 
                		int[] childIndices = {parentTreeNode.getChildCount()-1};
                		this.nodesWereInserted(parentTreeNode, childIndices);

                		// --- Recursively browse children ----------
                		int treeNodeLevel = treeNode.getLevel();
                		boolean isDoDeeperBrowsing = maxLevel==null ? true : treeNodeLevel < maxLevel;
                		if (isBrowseRecursively==true && isDoDeeperBrowsing==true) {
                			this.browseNodeInThread(treeNode, isBrowseRecursively, maxLevel);
                		}
                	}
                }
            } // end for
            
        } catch (InterruptedException | ExecutionException e) {
        	System.err.println("Browsing nodeId=" + parentNodeID + " failed: " + e.getMessage());
        }
    }
	
	/**
	 * Returns the UaNode to DefaultMutableTreeNode HashMap.
	 * @return the node hash map
	 */
	public HashMap<UaNode, OpcUaTreeNode> getNodeHashMap() {
		if (nodeHashMap==null) {
			nodeHashMap = new HashMap<>();
		}
		return nodeHashMap;
	}
	/**
	 * Reminds the UaNode to {@link OpcUaTreeNode} mapping.
	 *
	 * @param uaNode the ua node
	 * @param treeNode the tree node
	 */
	private void remindNodeMapping(UaNode uaNode, OpcUaTreeNode treeNode) {
		if (uaNode!=null && treeNode!=null) {
			this.getNodeHashMap().put(uaNode, treeNode);
		}
	}
	
}
