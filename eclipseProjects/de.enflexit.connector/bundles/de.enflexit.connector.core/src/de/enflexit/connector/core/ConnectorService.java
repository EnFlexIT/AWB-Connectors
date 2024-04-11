package de.enflexit.connector.core;

import de.enflexit.connector.core.manager.ConnectorManager;

/**
 * This interface specifies the necessary methods to integrate a communication protocol connector into the {@link ConnectorManager}.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public interface ConnectorService {
	
	/**
	 * Gets the name of the communication protocol that is provided by this service.
	 * @return the protocol name
	 */
	public String getProtocolName();
	
	/**
	 * Gets a new connector instance for the provided communication protocol. 
	 * @return the new connector instance
	 */
	public AbstractConnector getNewConnectorInstance();

	/**
	 * Gets an initial set of properties, that should specify keys for all required/expected settings, and provide reasonable default values if possible. 
	 * @return the initial properties
	 */
	public AbstractConnectorProperties getInitialProperties();
}
