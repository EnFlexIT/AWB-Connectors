package de.enflexit.connector.core;

public interface ConnectorService {
	public String getProtocolName();
	public AbstractConnector getNewConnectorInstance();
}
