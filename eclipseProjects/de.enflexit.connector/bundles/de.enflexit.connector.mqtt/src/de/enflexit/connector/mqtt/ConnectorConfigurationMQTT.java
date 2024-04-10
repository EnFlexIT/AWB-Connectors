package de.enflexit.connector.mqtt;

import com.hivemq.client.mqtt.MqttVersion;

import de.enflexit.common.properties.Properties;
import de.enflexit.connector.core.ConnectorConfiguration;

/**
 * This class specifies the configuration of a MQTT connection.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorConfigurationMQTT extends ConnectorConfiguration {
	
	public static final String PROPERTY_MQTT_VERSION = "Mqtt.version";
	
	public enum QosLevel {
		AtMaxOnce, AtLeastOnce, ExactlyOnce
	}
	
	private static final int MQTT_DEFAULT_PORT = 1883;

	private String clientID;
	private MqttVersion mqttVersion;
	
	public static ConnectorConfigurationMQTT fromProperties(MqttConnectorProperties properties) {
		ConnectorConfigurationMQTT config = new ConnectorConfigurationMQTT();
		config.setUrlOrIP(properties.getStringValue(PROPERTY_SERVER_HOST));
		config.setPort(properties.getIntegerValue(PROPERTY_SERVER_PORT));
		config.setClientID(properties.getStringValue(MqttConnectorProperties.PROPERTY_KEY_MQTT_CLIENT_IDENTIFIER));
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
	
	@Override
	public Properties getInitialProperties() {
		Properties properties = new Properties();
		properties.setStringValue(ConnectorConfiguration.PROPERTY_SERVER_HOST, "localhost");
		properties.setIntegerValue(ConnectorConfiguration.PROPERTY_SERVER_PORT, MQTT_DEFAULT_PORT);
		properties.setStringValue(PROPERTY_MQTT_VERSION, MqttVersion.MQTT_5_0.toString());
		
		return properties;
	}
	
}
