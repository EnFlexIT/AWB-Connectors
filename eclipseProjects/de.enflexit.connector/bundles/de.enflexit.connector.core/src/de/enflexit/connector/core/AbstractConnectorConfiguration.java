package de.enflexit.connector.core;

import de.enflexit.common.properties.Properties;
import de.enflexit.common.properties.PropertiesListener;
import de.enflexit.connector.core.AbstractConnectorProperties.StartOn;

/**
 * This class contains base configuration data, that is required by any kind of connector.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public abstract class AbstractConnectorConfiguration implements PropertiesListener {
	
	public static final String PROPERTY_SERVER_HOST = "Server.host";
	public static final String PROPERTY_SERVER_PORT = "Server.port";
	
	private String name;
	private String protocol;
	
	private StartOn startOn;
	private String host;
	private int port;
	
	private Properties connectorProperties;
	
	private boolean changed;
	
	/**
	 * Gets the name.
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * Sets the name.
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Gets the protocol.
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}
	/**
	 * Sets the protocol.
	 * @param protocol the new protocol
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	/**
	 * Gets the start on.
	 * @return the start on
	 */
	public StartOn getStartOn() {
		return startOn;
	}
	/**
	 * Sets the start on.
	 * @param startOn the new start on
	 */
	public void setStartOn(StartOn startOn) {
		this.startOn = startOn;
	}
	/**
	 * Gets the configured URL or IP for this connection.
	 * @return the URL or IP
	 */
	public String getUrlOrIP() {
		return host;
	}
	/**
	 * Sets the URL or IP.
	 * @param urlOrIP the new URL or IP for this connection.
	 */
	public void setUrlOrIP(String urlOrIP) {
		this.host = urlOrIP;
	}
	/**
	 * Gets the configured port for this connection.
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	/**
	 * Sets the configured port for this connection.
	 * @param port the new port
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	public Properties getConnectorProperties() {
		return connectorProperties;
	}
	public void setConnectorProperties(Properties connectorProperties) {
		this.connectorProperties = connectorProperties;
	}
	/**
	 * Gets an initial list of properties, that should define all required/expected properties for this kind of connection.
	 * @return the initial properties
	 */
	public abstract Properties getInitialProperties();
	
	/**
	 * Checks if the configuration was changed.
	 * @return true, if is changed
	 */
	public boolean isChanged() {
		return changed;
	}
	
	/**
	 * Specifies if the configuration was changed.
	 * @param changed the new changed
	 */
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	
	
}
