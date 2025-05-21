package de.enflexit.connector.nymea.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JToolTip;
import javax.swing.JTree;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class ObjectBrowserTree extends JTree {

	private static final long serialVersionUID = 4604099367124988768L;
	private static final int DEFAULT_TOOLTIP_VISIBILITY_TIME = 1000;
	
	private boolean enableCopyValues;
	
	/**
	 * Instantiates a new json response browser tree with value copying enabled.
	 */
	public ObjectBrowserTree() {
		this(true);
	}
	
	/**
	 * Instantiates a new json response browser tree.
	 * @param enableCopyValues specifies if copying values should be enabled.
	 */
	public ObjectBrowserTree(boolean enableCopyValues) {
		super();
		this.enableCopyValues = enableCopyValues;
		this.initialize();
	}

	/**
	 * Initialize the tree.
	 */
	private void initialize() {
		
		if (this.enableCopyValues==true) {
			// --- Add a mouse listener to copy node values, and a tooltip to make aware of it.
			this.setToolTipText("Right click on a node to copy its value to the clipboard");
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent me) {
					if (SwingUtilities.isRightMouseButton(me)) {
						DefaultMutableTreeNode clickedNode = ObjectBrowserTree.this.getTreeNodeAtPosition(me.getX(), me.getY());
						if (clickedNode!=null && clickedNode.isLeaf()==true) {
							String nodeContent = (String) clickedNode.getUserObject();
							String[] parts = nodeContent.split(": ");
							if (parts.length==2) {
								String nodeValue = parts[1];
								StringSelection stringSelection = new StringSelection(nodeValue);
								Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
								clipboard.setContents(stringSelection, null);
								ObjectBrowserTree.this.showCopySuccessToolTip(nodeValue, me.getXOnScreen(), me.getYOnScreen(), DEFAULT_TOOLTIP_VISIBILITY_TIME);
							}
							
						} else {
							System.err.println("Couldn't identify node!");
						}
					}
				}
			});
		}
	}

	/**
	 * Gets the tree node at the specified position.
	 * @param posX the x coordinate
	 * @param posY the Y coordinate
	 * @return the tree node, null if not found.
	 */
	private DefaultMutableTreeNode getTreeNodeAtPosition(int posX, int posY) {
        TreePath path = this.getPathForLocation(posX, posY);
        if (path!=null) {
        	Object node = path.getLastPathComponent();
        	if (node instanceof DefaultMutableTreeNode) {
        		return (DefaultMutableTreeNode) node;
        	}
        }
        return null;
	}
	
	/**
	 * Shows a message tooltip after successfully copying a value to the clipboard.
	 * @param copiedText the copied text
	 * @param posX the pos X
	 * @param posY the pos Y
	 * @param timeout the timeout
	 */
	private void showCopySuccessToolTip(String copiedText, int posX, int posY, int timeout) {

		// --- Pepare the tooltip message ---------------------------
		JToolTip successToolTip = this.createToolTip();
		successToolTip.setTipText("Copied " + copiedText + " to the clipboard.");
		
		// --- Show it at the click location ------------------------
		Popup popup = PopupFactory.getSharedInstance().getPopup(this, successToolTip, posX, posY);
		popup.show();
		
	    // --- Set a timer to hide it after the specified timeout ---
	    Timer timer = new Timer(timeout, new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	popup.hide();

	        }
	    });
	    timer.setRepeats(false);
	    timer.start();
	}
	
	public static void addMapContentChildNodes(Map<?, ?> map, DefaultMutableTreeNode parentNode) {
		addMapContentChildNodes(map, parentNode, new ArrayList<String>());
	}
	
	public static void addMapContentChildNodes(Map<?, ?> map, DefaultMutableTreeNode parentNode, List<String> skipList) {
		for (Object key : map.keySet()) {
			
			// --- Ignore map entries if their key is in the skip list --------
			if (skipList.contains(key)) continue;
			
			Object value = map.get(key);
			
			if (value!=null) {
				if (value instanceof String || value instanceof Boolean || value instanceof Double || value instanceof Integer) {
					// --- Single string value - add to parent ----------
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(key + ": " + value);
					parentNode.add(childNode);
				} else if (value instanceof Map<?,?>) {
					// --- Map structure - add as sub tree --------------
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(key);
					addMapContentChildNodes((Map<?, ?>) value, childNode);
					parentNode.add(childNode);
				} else if (value instanceof List<?>) {
					// --- List of values - add to the parent node directly
					List<?> valueList = (List<?>)value;
					if (valueList.size()>0) {
						DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(key);
						addListContentChildNodes((List<?>) value, childNode);
						parentNode.add(childNode);
					}
				} else {
					// --- Unknown type - add error message (should not occur)
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(key + ": " + " Unexpected data type " + value.getClass().getSimpleName());
					parentNode.add(childNode);
				}
			} else {
				System.err.println("[" +  ObjectBrowserTree.class.getSimpleName() + "] The value for " + key + " is null!");
			}
			
		}
	}
	
	public static void addListContentChildNodes(List<?> list, DefaultMutableTreeNode parentNode) {
		// --- If the parent node string ends with an s, remove that to make it singular
		String parentNodeString = (String) parentNode.getUserObject();
		String prefixString = (parentNodeString.endsWith("s")) ? parentNodeString.substring(0, parentNodeString.length()-1) : parentNodeString;
		
		for (int i=0; i<list.size(); i++) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
			Object listElement = list.get(i);
			if (listElement instanceof String || listElement instanceof Boolean || listElement instanceof Double || listElement instanceof Integer) {
				childNode.setUserObject(listElement);
				parentNode.add(childNode);
			} else if (listElement instanceof Map<?,?>) {
				childNode.setUserObject(prefixString + " " + (i+1));
				addMapContentChildNodes((Map<?, ?>) listElement, childNode);
			}
			else {
				childNode.setUserObject("Unexpected data type: " + listElement.getClass().getSimpleName());
			}
			parentNode.add(childNode);
		}
	}

}
