package de.enflexit.connector.core;

public abstract class AbstractConnector {
	
	private AbstractConfiguration connectorConfiguration;
	
	public abstract void connect();
	public abstract void deconnect();
	public AbstractConfiguration getConnectorConfiguration() {
		return connectorConfiguration;
	}
	public void setConnectorConfiguration(AbstractConfiguration connectorConfiguration) {
		this.connectorConfiguration = connectorConfiguration;
	}
	
	
}
