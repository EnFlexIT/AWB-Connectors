package de.enflexit.connector.core;

import de.enflexit.common.properties.Properties;
import de.enflexit.common.properties.PropertiesListener;

/**
 * This class contains base configuration data, that is required by any kind of connector.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public abstract class AbstractConnectorConfiguration implements PropertiesListener {
	
	private String name;
	
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
