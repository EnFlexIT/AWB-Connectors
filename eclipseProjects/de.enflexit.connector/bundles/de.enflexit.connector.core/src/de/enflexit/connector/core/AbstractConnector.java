package de.enflexit.connector.core;

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
	
	
	
}
