package de.enflexit.connector.mqtt;

import com.hivemq.client.mqtt.MqttVersion;

import de.enflexit.connector.core.AbstractConfiguration;

/**
 * This class specifies the configuration of a MQTT connection.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorConfigurationMQTT extends AbstractConfiguration {
	
	public enum QosLevel {
		AtMaxOnce, AtLeastOnce, ExactlyOnce
	}
	
	private MqttVersion mqttVersion;

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
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.ConnectorConfigurationBase#getDefaultPort()
	 */
	@Override
	public int getDefaultPort() {
		return 1883;
	}
	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.ConnectorConfigurationBase#getDefaultPortSecured()
	 */
	@Override
	public int getDefaultPortSecured() {
		return 8883;
	}
	
}
