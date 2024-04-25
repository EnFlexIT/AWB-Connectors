package de.enflexit.connector.core;

import de.enflexit.connector.core.AbstractConnectorProperties.StartOn;
import de.enflexit.connector.core.manager.ConnectorEvent;
import de.enflexit.connector.core.manager.ConnectorManager;

/**
 * Abstract superclass for connectors to different communication protocols.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public abstract class AbstractConnector {
	
	protected ConnectorManager connectorManager;
	
	private AbstractConnectorProperties connectorProperties;
	
	public AbstractConnectorProperties getConnectorProperties() {
		return connectorProperties;
	}

	public void setConnectorProperties(AbstractConnectorProperties connectorProperties) {
		this.connectorProperties = connectorProperties;
	}

	/**
	 * Establishes the connection.
	 * @return true, if successful
	 */
	public abstract boolean connect();
	
	/**
	 * Closes the connection.
	 */
	public abstract void disconnect();
	
	
	/**
	 * Checks if this connector instance is currently connected.
	 * @return 
	 */
	public abstract boolean isConnected();
	
	/**
	 * Gets the protocol name.
	 * @return the protocol name
	 */
	public abstract String getProtocolName();
	
	/**
	 * Sets the connector manager.
	 * @param connectorManager the new connector manager
	 */
	public void setConnectorManager(ConnectorManager connectorManager) {
		this.connectorManager = connectorManager;
	}
	
	/**
	 * Notifies the {@link ConnectorManager} about a {@link ConnectorEvent}.
	 * @param event the event
	 */
	protected void notifyManager(ConnectorEvent event) {
		if (this.connectorManager!=null) {
			connectorManager.onConnectorEvent(event);
		}
	}
	
	/**
	 * Checks when this connector is supposed to be started.
	 * @return the start on
	 */
	public StartOn getStartOn() {
		// --- Default case if nothing else is configured -----------
		StartOn startOn = StartOn.ManualStart;
		
		// --- Try to get the configured start level from the properties
		String startLevelFromProperties = this.getConnectorProperties().getStringValue(AbstractConnectorProperties.PROPERTY_KEY_CONNECTOR_START_ON);
		if (startLevelFromProperties!=null && startLevelFromProperties.isBlank()==false) {
			startOn = StartOn.valueOf(startLevelFromProperties);
		}
		return startOn;
	}
	
	/**
	 * Gets the connector configuration.
	 * @return the connector configuration
	 */
	public abstract AbstractConnectorConfiguration getConnectorConfiguration();
	
}
