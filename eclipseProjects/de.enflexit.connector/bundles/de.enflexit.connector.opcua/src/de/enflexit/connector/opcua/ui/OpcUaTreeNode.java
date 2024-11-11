package de.enflexit.connector.opcua.ui;

import javax.swing.tree.DefaultMutableTreeNode;

import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;

/**
 * The Class OpcUaTreeNode.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = -7141314660105367749L;

	/**
     * Creates a tree node that has no parent and no children, but which
     * allows children.
     */
    public OpcUaTreeNode() {
        this(null);
    }
    /**
     * Creates a tree node with no parent, no children, but which allows
     * children, and initializes it with the specified user object.
     *
     * @param userObject an Object provided by the user that constitutes
     *                   the node's data
     */
    public OpcUaTreeNode(Object userObject) {
        this(userObject, true);
    }
    /**
     * Creates a tree node with no parent, no children, initialized with
     * the specified user object, and that allows children only if
     * specified.
     *
     * @param userObject an Object provided by the user that constitutes
     *        the node's data
     * @param allowsChildren if true, the node is allowed to have child
     *        nodes -- otherwise, it is always a leaf node
     */
    public OpcUaTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }
    
	/**
	 * Returns the current UaNode, stored in the tree nodes user object.
	 * @return the ua node
	 */
	public UaNode getUaNode() {
		UaNode uaNode = null;
		try {
			uaNode = (UaNode) this.getUserObject();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return uaNode;
	}
	
}
