package de.enflexit.connector.mqtt;

import com.hivemq.client.mqtt.MqttVersion;

public class MQTTConfiguration {
	
	public enum QosLevel {
		AtMaxOnce, AtLeastOnce, ExactlyOnce
	}
	
	private String brokerURL;
	private int brokerPort;
	
	private MqttVersion mqttVersion;

	public String getBrokerURL() {
		return brokerURL;
	}

	public void setBrokerURL(String brokerURL) {
		this.brokerURL = brokerURL;
	}

	public int getBrokerPort() {
		return brokerPort;
	}

	public void setBrokerPort(int brokerPort) {
		this.brokerPort = brokerPort;
	}

	public MqttVersion getMqttVersion() {
		return mqttVersion;
	}

	public void setMqttVersion(MqttVersion mqttVersion) {
		this.mqttVersion = mqttVersion;
	}
	
	
	
	
}
