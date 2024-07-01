package de.enflexit.connector.core.ui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

import de.enflexit.common.properties.Properties;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.AbstractConnectorConfiguration;
import de.enflexit.connector.core.manager.ConnectorManager;

/**
 * List cell renderer implementation for the list of configured connectors.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorsListCellRenderer extends DefaultListCellRenderer {
	
	private static final long serialVersionUID = -6478678792183607533L;
	private static final String STATE_ICON_CONNECTED = "StatusGreen.png";
	private static final String STATE_ICON_DISCONNECTED = "StatusRed.png";
	private static final String STATE_ICON_NOT_AVAILABLE = "StatusGrey.png";
	

	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		
		String connectorName = (String) value;
		
		Properties connectorProperties = ConnectorManager.getInstance().getConnectorProperies(connectorName);
		String labelString = connectorName + " - " + connectorProperties.getStringValue(AbstractConnectorConfiguration.CONNECTOR_PROPERTY_PROTOCOL);
		JLabel rendererLabel = (JLabel) super.getListCellRendererComponent(list, labelString, index, isSelected, cellHasFocus);
		
		AbstractConnector connector = ConnectorManager.getInstance().getConnectorByName(connectorName);
		rendererLabel.setIcon(this.getConnectionStateIcon(connector));
		rendererLabel.setToolTipText(labelString + this.getConnectorStateSuffix(connector));
		
		return rendererLabel;
	}

	/**
	 * Gets an icon that reflects the current connection state.
	 * @param connectionState the connection state
	 * @return the connection state icon
	 */
	private ImageIcon getConnectionStateIcon(AbstractConnector connector) {
		
		if (connector==null) {
			return BundleHelper.getImageIcon(STATE_ICON_NOT_AVAILABLE);	
		} else if (connector.isConnected()==true) {
			return BundleHelper.getImageIcon(STATE_ICON_CONNECTED);
		} else {
			return BundleHelper.getImageIcon(STATE_ICON_DISCONNECTED);
		}
	}
	
	private String getConnectorStateSuffix(AbstractConnector connector) {
		if (connector==null) {
			return " - not available";
		} else if (connector.isConnected()==true) {
			return " - connected";
		} else {
			return " - not connected";
		}
	}

}
