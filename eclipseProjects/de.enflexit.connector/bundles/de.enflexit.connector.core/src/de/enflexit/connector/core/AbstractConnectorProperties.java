package de.enflexit.connector.core;

import de.enflexit.common.properties.Properties;

public abstract class AbstractConnectorProperties extends Properties {
	
	public enum StartOn {
		AwbStart,
		ProjectLoaded,
		JadeStartup,
		ManualStart;
	}
	
	private static final long serialVersionUID = -751706637548308352L;
	public static final String PROPERTY_KEY_CONNECTOR_CLASS = "Connector.class";
	public static final String PROPERTY_KEY_CONNECTOR_NAME = "Connector.name";
	public static final String PROPERTY_KEY_CONNECTOR_START_ON = "Connector.startOn";
	public static final String PROPERTY_KEY_SERVER_HOST = "Server.host" ;
	public static final String PROPERTY_KEY_SERVER_PORT = "Server.port";
	
	public abstract int getDefaultPort();
	
}
