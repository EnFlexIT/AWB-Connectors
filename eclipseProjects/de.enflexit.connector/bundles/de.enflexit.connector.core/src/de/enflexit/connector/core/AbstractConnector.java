package de.enflexit.connector.core;

import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import de.enflexit.common.properties.Properties;
import de.enflexit.connector.core.ConnectorEvent.Event;
import de.enflexit.connector.core.manager.ConnectorManager;

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
	
	private ArrayList<ConnectorListener> connectorListeners;
	
	public enum StartOn {
		AwbStart,
		ProjectLoaded,
		JadeStartup,
		ManualStart;
	}
	
	private Properties connectorProperties;


	/**
	 * Gets the protocol name.
	 * @return the protocol name
	 */
	public abstract String getProtocolName();
	
	
	/**
	 * This method should provide an initial set of properties for this type of connectors, containing all required keys, and useful default values if possible.
	 * @return the initial properties
	 */
	public abstract Properties getInitialProperties();

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
	 * Saves the current connector properties.
	 */
	public void saveSettings() {
		ConnectorManager.getInstance().updateConnectorProperties(this.getConnectorName(), this.getConnectorProperties());
	}
	
	/**
	 * Returns the connector name, located in the local connector {@link Properties}.
	 * @return the connector name
	 */
	public String getConnectorName() {
		if (this.getConnectorProperties()==null) return null;
		return this.getConnectorProperties().getStringValue(PROPERTY_KEY_CONNECTOR_NAME);
	}
	
	/**
	 * Opens the connection.
	 * @return true, if successful
	 */
	public final boolean openConnection() {
		boolean success = this.connect();
		if (success==true) {
			this.notifyListeners(new ConnectorEvent(this, Event.CONNECTED));
		}
		return success;
	}
	
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
	public final void closeConnection() {
		this.disconnect();
		this.notifyListeners(new ConnectorEvent(this, Event.DISCONNECTED));
	}
	
	/**
	 * Closes the connection.
	 */
	public abstract void disconnect();
	
	
	/**
	 * Checks when this connector is supposed to be started.
	 * @return the start on
	 */
	public StartOn getStartOn() {
		
		// --- Default case if nothing else is configured ---------------------
		StartOn startOn = StartOn.ManualStart;
		
		// --- Try to get the configured start level from the properties ------
		String startLevelFromProperties = this.getConnectorProperties().getStringValue(PROPERTY_KEY_CONNECTOR_START_ON);
		if (startLevelFromProperties!=null && startLevelFromProperties.isBlank()==false) {
			startOn = StartOn.valueOf(startLevelFromProperties);
		}
		return startOn;
	}
	/**
	 * Sets the new start on.
	 * @param newStartOn the new start on
	 */
	public void setStartOn(StartOn newStartOn) {
		if (newStartOn==null) return;
		this.getConnectorProperties().setStringValue(PROPERTY_KEY_CONNECTOR_START_ON, newStartOn.toString());
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
	
	private ArrayList<ConnectorListener> getConnectorListeners() {
		if (connectorListeners==null) {
			connectorListeners = new ArrayList<>();
		}
		return connectorListeners;
	}
	
	/**
	 * Adds a connector listener to the list.
	 * @param listener the listener
	 */
	public void addConnectorListener(ConnectorListener listener) {
		if (this.getConnectorListeners().contains(listener)==false) {
			this.getConnectorListeners().add(listener);
		}
	}
	
	/**
	 * Removes a connector listener from the  list.
	 * @param listener the listener
	 */
	public void removeConnectorListener(ConnectorListener listener) {
		if (this.getConnectorListeners().contains(listener)==true) {
			this.getConnectorListeners().remove(listener);
		}
		
	}

	/**
	 * Notifies all listeners about a {@link ConnectorEvent}.
	 * @param connectorEvent the connector event
	 */
	protected void notifyListeners(ConnectorEvent connectorEvent) {
		for (ConnectorListener listener : this.getConnectorListeners()) {
			listener.onConnectorEvent(connectorEvent);
		}
	}
	
}
