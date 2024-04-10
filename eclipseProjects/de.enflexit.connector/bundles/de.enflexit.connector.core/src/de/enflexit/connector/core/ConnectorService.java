package de.enflexit.connector.core;


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
	 * Gets a new instance of the relevant configuration class for the protocol
	 * @return the new configuration instance
	 */
	public ConnectorConfiguration getNewConfigurationInstance();
}
