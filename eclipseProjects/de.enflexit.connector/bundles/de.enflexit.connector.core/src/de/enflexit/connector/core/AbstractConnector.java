package de.enflexit.connector.core;

import javax.swing.JComponent;
import javax.swing.JPanel;
import de.enflexit.common.properties.Properties;

/**
 * Abstract superclass for connectors to different communication protocols.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public abstract class AbstractConnector {
	
	public static final String PROPERTY_KEY_CONNECTOR_NAME = "Connector.name";
	public static final String PROPERTY_KEY_CONNECTOR_PROTOCOL = "Connector.protocol";
	public static final String PROPERTY_KEY_CONNECTOR_START_ON = "Connector.startOn";
	public static final String PROPERTY_KEY_SERVER_HOST = "Server.host" ;
	public static final String PROPERTY_KEY_SERVER_PORT = "Server.port";
	
	public enum StartOn {
		AwbStart,
		ProjectLoaded,
		JadeStartup,
		ManualStart;
	}
	
	
	private Properties connectorProperties;
	
	/**
	 * Returns the connector properties.
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
	 * This method should provide an initial set of properties for this type of connectors, containing all required keys, and useful default values if possible.
	 * @return the initial properties
	 */
	public abstract Properties getInitialProperties();

	
	/**
	 * Establishes the connection.
	 * @return true, if successful
	 */
	public abstract boolean connect();

	/**
	 * Checks if this connector instance is currently connected.
	 * @return 
	 */
	public abstract boolean isConnected();
	
	/**
	 * Closes the connection.
	 */
	public abstract void disconnect();
	
	
	/**
	 * Gets the protocol name.
	 * @return the protocol name
	 */
	public abstract String getProtocolName();
	
	
	/**
	 * Checks when this connector is supposed to be started.
	 * @return the start on
	 */
	public StartOn getStartOn() {
		// --- Default case if nothing else is configured -----------
		StartOn startOn = StartOn.ManualStart;
		
		// --- Try to get the configured start level from the properties
		String startLevelFromProperties = this.getConnectorProperties().getStringValue(PROPERTY_KEY_CONNECTOR_START_ON);
		if (startLevelFromProperties!=null && startLevelFromProperties.isBlank()==false) {
			startOn = StartOn.valueOf(startLevelFromProperties);
		}
		return startOn;
	}

	
	/**
	 * Gets the configuration UI component. This default implementation just returns the base panel for editing 
	 * connector properties. Override this method if you want to provide a custom configuration UI component for 
	 * your connector implementation.
	 * 
	 * @param baseConfigPanel the base configuration panel
	 * @return the configuration UI component
	 */
	public JComponent getConfigurationUIComponent(JPanel baseConfigPanel) {
		return baseConfigPanel;
	}
	
	/**
	 * Overwrite to dispose the UI (if any).
	 */
	public void disposeUI() { }
	
}
