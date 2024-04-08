package de.enflexit.connector.core;

import java.util.HashMap;

public class ConnectorManager {
	private ConnectorManager instance;
	
	private HashMap<String, AbstractConnector> connectorsList;
	
	private ConnectorManager() {}

	public ConnectorManager getInstance() {
		if (instance==null) {
			instance = new ConnectorManager();
		}
		return instance;
	}
	
	public HashMap<String, AbstractConnector> getConnectorsList() {
		if (connectorsList==null) {
			
		}
		return connectorsList;
	}
	
	
	
}
