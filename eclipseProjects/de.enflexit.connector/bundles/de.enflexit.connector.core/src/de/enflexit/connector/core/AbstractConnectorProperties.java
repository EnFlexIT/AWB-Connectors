package de.enflexit.connector.core;

import de.enflexit.common.properties.Properties;
import de.enflexit.common.properties.PropertyValue;

/**
 * This class defines basic properties, that are relevant for all types of connectors.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public abstract class AbstractConnectorProperties extends Properties {
	
	public enum StartOn {
		AwbStart,
		ProjectLoaded,
		JadeStartup,
		ManualStart;
	}
	
	private static final long serialVersionUID = -751706637548308352L;
	public static final String PROPERTY_KEY_CONNECTOR_SERVICE_CLASS = "Connector.serviceClass";
	public static final String PROPERTY_KEY_CONNECTOR_NAME = "Connector.name";
	public static final String PROPERTY_KEY_CONNECTOR_START_ON = "Connector.startOn";
	public static final String PROPERTY_KEY_SERVER_HOST = "Server.host" ;
	public static final String PROPERTY_KEY_SERVER_PORT = "Server.port";
	
	public void addAll(Properties properties) {
		for (String identifier : properties.getIdentifierList()) {
			PropertyValue value = properties.getPropertyValue(identifier);
			this.setValue(identifier, value);
		}
	}
	
}
