package de.enflexit.connector.core;

public abstract class AbstractConnector {
	
	private ConnectorConfiguration connectorConfiguration;
	
	protected ConnectorManager connectorManager;
	
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
	 * Gets the connector configuration.
	 * @return the connector configuration
	 */
	public ConnectorConfiguration getConnectorConfiguration() {
		return connectorConfiguration;
	}
	
	/**
	 * Sets the connector configuration.
	 * @param connectorConfiguration the new connector configuration
	 */
	public void setConnectorConfiguration(ConnectorConfiguration connectorConfiguration) {
		this.connectorConfiguration = connectorConfiguration;
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
