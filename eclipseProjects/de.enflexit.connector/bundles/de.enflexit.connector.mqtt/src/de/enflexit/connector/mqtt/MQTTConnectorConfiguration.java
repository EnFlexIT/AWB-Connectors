package de.enflexit.connector.mqtt;

import com.hivemq.client.mqtt.MqttVersion;

import de.enflexit.common.properties.Properties;
import de.enflexit.common.properties.PropertiesEvent;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.AbstractConnectorConfiguration;

/**
 * This class specifies the configuration of a MQTT connection.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class MQTTConnectorConfiguration extends AbstractConnectorConfiguration {
	
	public enum QosLevel {
		AtMaxOnce, AtLeastOnce, ExactlyOnce
	}
	
	public enum AuthMode{
		//TODO extend
		NONE, SIMPLE
	}
	
	public static final String PROTOCOL_NAME = "MQTT";
	
	public static final String PROPERTY_KEY_MQTT_VERSION = "Mqtt.version";
	public static final String PROPERTY_KEY_MQTT_CLIENT_IDENTIFIER = "Mqtt.clientIdentifier";
	public static final String PROPERTY_KEY_MQTT_AUTH_MODE = "Mqtt.auth.mode";
	public static final String PROPERTY_KEY_MQTT_AUTH_USERNAME = "Mqtt.auth.username";
	public static final String PROPERTY_KEY_MQTT_AUTH_PASSWORD = "Mqtt.auth.password";
	
	
	public static final int DEFAULT_MQTT_PORT = 1883;
	public static final int DEFAULT_MQTT_PORT_SECURE = 8883;

	private String clientID;
	private MqttVersion mqttVersion;
	private AuthMode authMode;
	private String username;
	private String password;
	
	public static MQTTConnectorConfiguration fromProperties(Properties properties) {
		MQTTConnectorConfiguration config = new MQTTConnectorConfiguration();
		config.setUrlOrIP(properties.getStringValue(AbstractConnector.PROPERTY_KEY_SERVER_HOST));
		config.setPort(properties.getIntegerValue(AbstractConnector.PROPERTY_KEY_SERVER_PORT));
		config.setClientID(properties.getStringValue(PROPERTY_KEY_MQTT_CLIENT_IDENTIFIER));
		String mqttVersionString = properties.getStringValue(PROPERTY_KEY_MQTT_VERSION);
		if (mqttVersionString!=null) {
			config.setMqttVersion(MqttVersion.valueOf(mqttVersionString));
		}
		String mqttAuthModeString = properties.getStringValue(PROPERTY_KEY_MQTT_AUTH_MODE);
		if (mqttAuthModeString!=null) {
			config.setAuthMode(AuthMode.valueOf(mqttAuthModeString));
		}
		config.setUsername(properties.getStringValue(PROPERTY_KEY_MQTT_AUTH_USERNAME));
		config.setPassword(properties.getStringValue(PROPERTY_KEY_MQTT_AUTH_PASSWORD));
		
		properties.addPropertiesListener(config);
		return config;
	}
	
	/**
	 * Gets the client ID.
	 * @return the client ID
	 */
	public String getClientID() {
		return clientID;
	}
	
	/**
	 * Sets the client ID.
	 * @param clientID the new client ID
	 */
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}
	/**
	 * Gets the configured MQTT version.
	 * @return the mqtt version
	 */
	public MqttVersion getMqttVersion() {
		return mqttVersion;
	}
	/**
	 * Sets the MQTT version to use.
	 * @param mqttVersion the new mqtt version
	 */
	public void setMqttVersion(MqttVersion mqttVersion) {
		this.mqttVersion = mqttVersion;
	}
	
	/**
	 * Gets a set of default properties for new connections.
	 * @return the initial properties
	 */
	public static Properties getDefaultProperties() {
		Properties properties = new Properties();
		properties.setStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_PROTOCOL, PROTOCOL_NAME);
		properties.setStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_START_ON, AbstractConnector.StartOn.ManualStart.toString());
		properties.setStringValue(AbstractConnector.PROPERTY_KEY_SERVER_HOST, "localhost");
		properties.setIntegerValue(AbstractConnector.PROPERTY_KEY_SERVER_PORT, DEFAULT_MQTT_PORT);
		properties.setStringValue(PROPERTY_KEY_MQTT_VERSION, MqttVersion.MQTT_5_0.toString());
		properties.setStringValue(PROPERTY_KEY_MQTT_CLIENT_IDENTIFIER, getMyHostName());
		properties.setStringValue(PROPERTY_KEY_MQTT_AUTH_MODE, AuthMode.NONE.toString());
		return properties;
	}
	
	/**
	 * Gets the local host name, to be used as client identifier.
	 * @return the my host name
	 */
	private static String getMyHostName() {
		String hostname = System.getenv("COMPUTERNAME"); // On Windows
	    if (hostname == null || hostname.isEmpty()) {
	      hostname = System.getenv("HOSTNAME"); // On Unix/Linux
	    }
	    return hostname;
	}
	

	/* (non-Javadoc)
	 * @see de.enflexit.common.properties.PropertiesListener#onPropertiesEvent(de.enflexit.common.properties.PropertiesEvent)
	 */
	@Override
	public void onPropertiesEvent(PropertiesEvent propertiesEvent) {
		System.out.println("[" + this.getClass().getSimpleName() + "] Received properties event");
	}

	/**
	 * Gets the auth mode.
	 * @return the auth mode
	 */
	public AuthMode getAuthMode() {
		return authMode;
	}

	/**
	 * Sets the auth mode.
	 * @param authMode the new auth mode
	 */
	public void setAuthMode(AuthMode authMode) {
		this.authMode = authMode;
	}

	/**
	 * Gets the username.
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username.
	 * @param username the new username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the password.
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 * @param password the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
}
