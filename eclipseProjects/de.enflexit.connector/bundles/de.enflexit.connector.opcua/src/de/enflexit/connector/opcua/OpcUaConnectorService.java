package de.enflexit.connector.opcua;

import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.ConnectorService;

/**
 * The Class OpcUaConnectorService.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaConnectorService implements ConnectorService {
	
	public static final String CONNECTOR_NAME = "OPC UA - Client";

	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.ConnectorService#getProtocolName()
	 */
	@Override
	public String getProtocolName() {
		return CONNECTOR_NAME;
	}

	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.ConnectorService#getNewConnectorInstance()
	 */
	@Override
	public AbstractConnector getNewConnectorInstance() {
		return new OpcUaConnector();
	}
	
}
