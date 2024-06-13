package de.enflexit.connector.core;

import de.enflexit.common.properties.Properties;
import de.enflexit.connector.core.AbstractConnectorConfiguration.StartOn;
import de.enflexit.connector.core.manager.ConnectorManager;

/**
 * Abstract superclass for connectors to different communication protocols.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public abstract class AbstractConnector {
	
	protected ConnectorManager connectorManager;
	
	private Properties connectorProperties;
	
	/**
	 * Gets the connector properties.
	 * @return the connector properties
	 */
	public Properties getConnectorProperties() {
		return connectorProperties;
	}

	/**
	 * Sets the connector properties.
	 * @param connectorProperties the new connector properties
	 */
	public void setConnectorProperties(Properties connectorProperties) {
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
	 * Checks when this connector is supposed to be started.
	 * @return the start on
	 */
	public StartOn getStartOn() {
		// --- Default case if nothing else is configured -----------
		StartOn startOn = StartOn.ManualStart;
		
		// --- Try to get the configured start level from the properties
		String startLevelFromProperties = this.getConnectorProperties().getStringValue(AbstractConnectorConfiguration.PROPERTY_KEY_CONNECTOR_START_ON);
		if (startLevelFromProperties!=null && startLevelFromProperties.isBlank()==false) {
			startOn = StartOn.valueOf(startLevelFromProperties);
		}
		return startOn;
	}
	
	
	/**
	 * This method should provide an initial set of properties for this type of connectors, containing all required keys, and useful default values if possible.
	 * @return the initial properties
	 */
	public abstract Properties getInitialProperties();
	
	public abstract AbstractConnectorConfiguration getConfigurationFromProperties(Properties properties);

	public abstract AbstractConnectorConfiguration getConnectorConfiguration();
	
}
