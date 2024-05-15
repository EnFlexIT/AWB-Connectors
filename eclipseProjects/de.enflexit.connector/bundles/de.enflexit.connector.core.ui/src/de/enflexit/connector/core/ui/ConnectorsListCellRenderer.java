package de.enflexit.connector.core.ui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.manager.ConnectorManager;

/**
 * List cell renderer implementation for the list of configured connectors.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorsListCellRenderer extends DefaultListCellRenderer {
	
	private static final long serialVersionUID = -6478678792183607533L;
	private static final String STATE_ICON_CONNECTED = "StatusGreen.png";
	private static final String STATE_ICON_DISCONNECTED = "StatusRed.png";
	

	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		
		String connectorName = (String) value;
		AbstractConnector connector = ConnectorManager.getInstance().getConnectorByName(connectorName);
		
		String labelString = connectorName + " - " + connector.getProtocolName();
		
		JLabel rendererLabel = (JLabel) super.getListCellRendererComponent(list, labelString, index, isSelected, cellHasFocus);
		rendererLabel.setIcon(this.getConnectionStateIcon(connector.isConnected()));
		
		return rendererLabel;
	}

	/**
	 * Gets an icon that reflects the current connection state.
	 * @param connectionState the connection state
	 * @return the connection state icon
	 */
	private ImageIcon getConnectionStateIcon(boolean connectionState) {
		if (connectionState==true) {
			return BundleHelper.getImageIcon(STATE_ICON_CONNECTED);
		} else {
			return BundleHelper.getImageIcon(STATE_ICON_DISCONNECTED);
		}
	}

}
