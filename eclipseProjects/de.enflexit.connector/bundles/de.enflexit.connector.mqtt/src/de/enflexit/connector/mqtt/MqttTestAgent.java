package de.enflexit.connector.mqtt;

import com.hivemq.client.mqtt.MqttVersion;

import jade.core.Agent;

public class MqttTestAgent extends Agent {

	private static final long serialVersionUID = 2748580907388903805L;
	
	private MQTTConnector mqttConnector;
	
	@Override
	protected void setup() {
		if (this.getMqttConnector().connect()==true) {
			this.getMqttConnector().publish("test/" + this.getLocalName(), "Hello MQTT World!");
		}
	}
	
	private MQTTConnector getMqttConnector() {
		if (mqttConnector==null) {
			MQTTConfiguration mqttConfig = new MQTTConfiguration();
			mqttConfig.setBrokerURL("localhost");
			mqttConfig.setBrokerPort(1883);
			mqttConfig.setMqttVersion(MqttVersion.MQTT_3_1_1);
			mqttConnector = new MQTTConnector(this.getLocalName(), mqttConfig);
		}
		return mqttConnector;
	}
	
}
