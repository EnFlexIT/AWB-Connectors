package de.enflexit.connector.mqtt.examples;

import com.hivemq.client.mqtt.MqttVersion;

import de.enflexit.connector.mqtt.MQTTConnectorConfiguration;
import de.enflexit.connector.mqtt.MQTTConnector;
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
			MQTTConnectorConfiguration mqttConfig = new MQTTConnectorConfiguration();
			mqttConfig.setUrlOrIP("localhost");
			mqttConfig.setPort(1883);
			mqttConfig.setClientID(this.getLocalName());
			mqttConfig.setMqttVersion(MqttVersion.MQTT_3_1_1);
			mqttConnector = new MQTTConnector();
//			mqttConnector.setConnectorConfiguration(mqttConfig);
		}
		return mqttConnector;
	}
	
}
