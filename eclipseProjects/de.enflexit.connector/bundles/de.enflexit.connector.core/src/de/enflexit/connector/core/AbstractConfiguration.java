package de.enflexit.connector.core;

/**
 * This class contains base configuration data, that is required by any kind of connector.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public abstract class AbstractConfiguration {
	
	private String urlOrIP;
	private int port;
	
	/**
	 * Gets the configured URL or IP for this connection.
	 * @return the URL or IP
	 */
	public String getUrlOrIP() {
		return urlOrIP;
	}
	
	/**
	 * Sets the URL or IP.
	 * @param urlOrIP the new URL or IP for this connection.
	 */
	public void setUrlOrIP(String urlOrIP) {
		this.urlOrIP = urlOrIP;
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
	
	/**
	 * Gets the default port for an insecure connection.
	 * @return the default port
	 */
	public abstract int getDefaultPort();
	/**
	 * Gets the default port for a secured connection.
	 * @return the default port secured
	 */
	public abstract int getDefaultPortSecured();
	
}
