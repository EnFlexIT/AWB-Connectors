package de.enflexit.connector.nymea;

import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.ConnectorService;

/**
 * {@link ConnectorService} implementation for Nymea/Consolino clients-
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class NymeaConnectorService implements ConnectorService {
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.ConnectorService#getProtocolName()
	 */
	@Override
	public String getProtocolName() {
		return NymeaConnector.PROTOCOL_NAME;
	}

	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.ConnectorService#getNewConnectorInstance()
	 */
	@Override
	public AbstractConnector getNewConnectorInstance() {
		return new NymeaConnector();
	}

}
