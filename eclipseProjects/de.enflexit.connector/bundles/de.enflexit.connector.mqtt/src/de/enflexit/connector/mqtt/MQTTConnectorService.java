package de.enflexit.connector.mqtt;

import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.ConnectorConfiguration;
import de.enflexit.connector.core.ConnectorService;

public class MQTTConnectorService implements ConnectorService {
	
	private static final String PROTOCOL_NAME = "MQTT";

	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.ConnectorService#getProtocolName()
	 */
	@Override
	public String getProtocolName() {
		return PROTOCOL_NAME;
	}

	@Override
	public AbstractConnector getNewConnectorInstance() {
		return new MQTTConnector();
	}

	@Override
	public ConnectorConfiguration getNewConfigurationInstance() {
		return new ConnectorConfigurationMQTT();
	}

}
