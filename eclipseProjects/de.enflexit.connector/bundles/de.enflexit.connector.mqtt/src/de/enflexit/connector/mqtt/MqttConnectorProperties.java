package de.enflexit.connector.mqtt;

import com.hivemq.client.mqtt.MqttVersion;

import de.enflexit.connector.core.AbstractConnectorProperties;

public class MqttConnectorProperties extends AbstractConnectorProperties {
	
	private static final long serialVersionUID = 1701328833652172937L;
	
	public static final int MQTT_DEFAULT_PORT = 1883;
	public static final int MQTT_DEFAULT_PORT_SECURE = 8883;

	public static final String PROPERTY_KEY_MQTT_VERSION = "Mqtt.version";
	public static final String PROPERTY_KEY_MQTT_CLIENT_IDENTIFIER = "Mqtt.clientIdentifier";

	/**
	 * Populates this instance with default values.
	 */
	protected void populateWithDefaultValues() {
		this.setStringValue(PROPERTY_KEY_CONNECTOR_SERVICE_CLASS, MQTTConnectorService.class.getName());
		this.setStringValue(PROPERTY_KEY_CONNECTOR_START_ON, StartOn.ManualStart.toString());
		this.setStringValue(PROPERTY_KEY_SERVER_HOST, "localhost");
		this.setIntegerValue(PROPERTY_KEY_SERVER_PORT, MQTT_DEFAULT_PORT);
		this.setStringValue(PROPERTY_KEY_MQTT_VERSION, MqttVersion.MQTT_5_0.toString());
		this.setStringValue(PROPERTY_KEY_MQTT_CLIENT_IDENTIFIER, this.getMyHostName());
	}
	
	/**
	 * Gets the local host name, to be used as client identifier.
	 * @return the my host name
	 */
	private String getMyHostName() {
		String hostname = System.getenv("COMPUTERNAME"); // On Windows
	    if (hostname == null || hostname.isEmpty()) {
	      hostname = System.getenv("HOSTNAME"); // On Unix/Linux
	    }
	    return hostname;
	}

}
