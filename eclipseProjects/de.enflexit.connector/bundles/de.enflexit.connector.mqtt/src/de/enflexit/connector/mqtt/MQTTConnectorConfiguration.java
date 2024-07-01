package de.enflexit.connector.mqtt;

import com.hivemq.client.mqtt.MqttVersion;

import de.enflexit.common.properties.Properties;
import de.enflexit.common.properties.PropertiesEvent;
import de.enflexit.connector.core.AbstractConnectorConfiguration;

/**
 * This class specifies the configuration of a MQTT connection.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class MQTTConnectorConfiguration extends AbstractConnectorConfiguration {
	
	public enum QosLevel {
		AtMaxOnce, AtLeastOnce, ExactlyOnce
	}
	
	public static final String PROTOCOL_NAME = "MQTT";
	
	public static final String PROPERTY_MQTT_VERSION = "Mqtt.version";
	public static final String PROPERTY_MQTT_CLIENT_IDENTIFIER = "Mqtt.clientIdentifier";
	public static final int MQTT_DEFAULT_PORT = 1883;
	

	private String clientID;
	private MqttVersion mqttVersion;
	
	public static MQTTConnectorConfiguration fromProperties(Properties properties) {
		MQTTConnectorConfiguration config = new MQTTConnectorConfiguration();
		config.setUrlOrIP(properties.getStringValue(PROPERTY_KEY_SERVER_HOST));
		config.setPort(properties.getIntegerValue(PROPERTY_KEY_SERVER_PORT));
		config.setClientID(properties.getStringValue(PROPERTY_MQTT_CLIENT_IDENTIFIER));
		config.setMqttVersion(MqttVersion.valueOf(properties.getStringValue(PROPERTY_MQTT_VERSION)));
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
	
	public static Properties getInitialProperties() {
		Properties properties = new Properties();
		properties.setStringValue(CONNECTOR_PROPERTY_PROTOCOL, PROTOCOL_NAME);
		properties.setStringValue(PROPERTY_KEY_CONNECTOR_START_ON, StartOn.ManualStart.toString());
		properties.setStringValue(PROPERTY_KEY_SERVER_HOST, "localhost");
		properties.setIntegerValue(PROPERTY_KEY_SERVER_PORT, MQTT_DEFAULT_PORT);
		properties.setStringValue(PROPERTY_MQTT_VERSION, MqttVersion.MQTT_5_0.toString());
		properties.setStringValue(PROPERTY_MQTT_CLIENT_IDENTIFIER, getMyHostName());
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


	@Override
	public void onPropertiesEvent(PropertiesEvent propertiesEvent) {
		
	}
	
}
