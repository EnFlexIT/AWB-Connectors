package de.enflexit.connector.core;

/**
 * Implement this interface to react on connector-related events.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public interface ConnectorListener {
	
	/**
	 * This method is invoked to notify about {@link ConnectorEvent}s.
	 * @param connectorEvent the connector event
	 */
	public void onConnectorEvent(ConnectorEvent connectorEvent);
	
}
