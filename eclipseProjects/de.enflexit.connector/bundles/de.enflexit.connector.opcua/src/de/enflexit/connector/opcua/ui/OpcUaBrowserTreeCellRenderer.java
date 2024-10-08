package de.enflexit.connector.opcua.ui;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import org.eclipse.milo.opcua.sdk.client.model.nodes.objects.CertificateGroupTypeNode;
import org.eclipse.milo.opcua.sdk.client.model.nodes.objects.DataTypeSystemTypeNode;
import org.eclipse.milo.opcua.sdk.client.model.nodes.objects.FolderTypeNode;
import org.eclipse.milo.opcua.sdk.client.model.nodes.objects.ServerCapabilitiesTypeNode;
import org.eclipse.milo.opcua.sdk.client.model.nodes.objects.ServerConfigurationTypeNode;
import org.eclipse.milo.opcua.sdk.client.model.nodes.objects.ServerDiagnosticsTypeNode;
import org.eclipse.milo.opcua.sdk.client.model.nodes.objects.ServerRedundancyTypeNode;
import org.eclipse.milo.opcua.sdk.client.model.nodes.objects.ServerTypeNode;
import org.eclipse.milo.opcua.sdk.client.model.nodes.objects.SessionsDiagnosticsSummaryTypeNode;
import org.eclipse.milo.opcua.sdk.client.model.nodes.objects.TrustListTypeNode;
import org.eclipse.milo.opcua.sdk.client.model.nodes.objects.VendorServerInfoTypeNode;
import org.eclipse.milo.opcua.sdk.client.model.nodes.variables.BaseDataVariableTypeNode;
import org.eclipse.milo.opcua.sdk.client.model.nodes.variables.PropertyTypeNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaObjectNode;

import de.enflexit.connector.opcua.BundleHelper;

/**
 * The Class OpcUaBrowserTreeCellRenderer.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaBrowserTreeCellRenderer implements TreeCellRenderer {

	private JLabel jLabelDisplay;
	private HashMap<String, Icon> iconHashMap;
	
	
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)value;
		UaNode uaNode = (UaNode) treeNode.getUserObject();
		
		this.getJLabelDisplay().setText(uaNode.getBrowseName().getName());
		this.getJLabelDisplay().setIcon(this.getIcon(uaNode));
		
		if (selected==true) {
			this.getJLabelDisplay().setForeground(Color.WHITE);
		} else {
			this.getJLabelDisplay().setForeground(Color.BLACK);
		}
		
		return this.getJLabelDisplay();
	}
	
	/**
	 * Returns the JLabel for the display.
	 * @return the JLabel for the display
	 */
	private JLabel getJLabelDisplay() {
		if (jLabelDisplay==null) {
			jLabelDisplay = new JLabel();
		}
		return jLabelDisplay;
	}
	
	private HashMap<String, Icon> getIconHashMap() {
		if (iconHashMap==null) {
			iconHashMap = new HashMap<>();
		}
		return iconHashMap;
	}
	
	/**
	 * Gets the icon for the specified {@link UaNode}.
	 *
	 * @param uaNode the ua node
	 * @return the icon
	 */
	private Icon getIcon(UaNode uaNode) {
		
		boolean isDebug = true;
		
		// --- As default for everything ----------------
		String imageFileName = "Folder.png";
		if (isDebug==true) imageFileName = null;
		
		String nodeName = uaNode.getBrowseName().getName();
		String nodeClass = uaNode.getClass().getName();
		
		// --- Image depending on NodeType ----------------
		if (uaNode instanceof FolderTypeNode) {
			imageFileName = "Folder.png";
		
		} else if (uaNode instanceof CertificateGroupTypeNode) {
			imageFileName = "CertificateGroup.png";
			
		} else if (uaNode instanceof ServerTypeNode) {
			imageFileName = "Server.png";
		} else if (uaNode instanceof ServerDiagnosticsTypeNode) {
			imageFileName = "ServerDiagnostics.png";
		} else if (uaNode instanceof ServerCapabilitiesTypeNode) {
			imageFileName = "ServerDiagnostics.png";
		} else if (uaNode instanceof ServerConfigurationTypeNode) {
			imageFileName = "ServerConfiguration.png"; 
		} else if (uaNode instanceof ServerRedundancyTypeNode) {
			imageFileName = "ServerRedundancy.png";
		
		} else if (uaNode instanceof VendorServerInfoTypeNode) {
			imageFileName = "ServerInfo.png";

		} else if (uaNode instanceof PropertyTypeNode) {
			imageFileName = "Property.png";
		} else if (uaNode instanceof BaseDataVariableTypeNode) {
			imageFileName = "BaseValue.png";
			
		} else if (uaNode instanceof TrustListTypeNode) {
			imageFileName = "TrustList.png";
			
		} else if (uaNode instanceof SessionsDiagnosticsSummaryTypeNode) {
			imageFileName = "SessionsDiagnostics.png";
			
		} else if (uaNode instanceof UaObjectNode) {
			imageFileName = "UaObject.png";
		} else if (uaNode instanceof DataTypeSystemTypeNode) {
			imageFileName = "DataTypeSystem.png";
			
		} else {
			if (isDebug==true) System.out.println("Not considered in " + this.getClass().getSimpleName() + " yet: " + nodeName + " => " + nodeClass);
		}
		
		// --- Get icon to return ------------------------- 
		Icon icon = null;
		if (imageFileName==null) {
			if (isDebug==true) System.out.println("No image defined for " + nodeName + " => " + nodeClass);
		} else  {
			icon = this.getIconHashMap().get(imageFileName);
			if (icon==null) {
				icon = BundleHelper.getImageIcon(imageFileName);
				this.getIconHashMap().put(imageFileName, icon);
			}
		}
		return icon;
	}
	
	
}
