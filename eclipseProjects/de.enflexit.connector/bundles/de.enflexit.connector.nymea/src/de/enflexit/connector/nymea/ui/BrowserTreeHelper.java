package de.enflexit.connector.nymea.ui;

import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

public class BrowserTreeHelper {
	public static void addMapContentChildNodes(Map<?, ?> map, DefaultMutableTreeNode parentNode) {
		for (Object key : map.keySet()) {
			Object value = map.get(key);
			
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
