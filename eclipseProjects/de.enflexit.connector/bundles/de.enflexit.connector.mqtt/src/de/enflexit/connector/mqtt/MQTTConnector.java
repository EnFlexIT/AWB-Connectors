package de.enflexit.connector.mqtt;

import java.util.function.Consumer;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientBuilder;
import com.hivemq.client.mqtt.MqttClientState;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.MqttVersion;
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAckReturnCode;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAckReasonCode;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;

import de.enflexit.connector.core.AbstractConnector;

public class MQTTConnector extends AbstractConnector {
	
	private String clientID;
	
	private MqttClient client;
	
	private ConnectorConfigurationMQTT configuraiton;

	/**
	 * Gets the MQTT client instance.
	 * @return the client
	 */
	private MqttClient getClient() {
		if (client==null) {
			if (this.configuraiton==null) {
				System.err.println("[" + this.getClass().getSimpleName() + "] MQTT configuration not specified!");
				return null;
			}
			MqttClientBuilder clientBuilder = MqttClient.builder().identifier(this.clientID).serverHost(this.configuraiton.getUrlOrIP()).serverPort(this.configuraiton.getPort());
			switch(this.configuraiton.getMqttVersion()) {
			case MQTT_3_1_1:
				client = clientBuilder.useMqttVersion3().build().toBlocking();
				break;
			case MQTT_5_0:
				client = clientBuilder.useMqttVersion5().build().toBlocking();
				break;
			}
		}
		return client;
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#connect()
	 */
	@Override
	public boolean connect() {
		switch (this.configuraiton.getMqttVersion()) {
		case MQTT_3_1_1:
			return this.connectV3();
		case MQTT_5_0:
			return this.connectV5();
		default:
			return false;
		}
	}
	
	private boolean connectV3() {
		Mqtt3BlockingClient mqtt3Client = (Mqtt3BlockingClient) this.getClient();
		Mqtt3ConnAck connAck = mqtt3Client.connect();
		if (connAck.getReturnCode()==Mqtt3ConnAckReturnCode.SUCCESS) {
			return true;
		} else {
			System.err.println("[" + this.getClass().getSimpleName() + "] Connection failed: " + connAck.getReturnCode());
			return false;
		}
	}
	
	private boolean connectV5() {
		Mqtt5BlockingClient mqtt5Client = (Mqtt5BlockingClient) this.getClient();
		Mqtt5ConnAck connAck = mqtt5Client.connect();
		if (connAck.getReasonCode() == Mqtt5ConnAckReasonCode.SUCCESS) {
			return true;
		} else {
			System.err.println("[" + this.getClass().getSimpleName() + "] Connection failed: " + connAck.getReasonString());
			return false;
		}
	}
	
	/**
	 * Checks if is connected.
	 * @return true, if is connected
	 */
	public boolean isConnected() {
		return this.getClient().getState()==MqttClientState.CONNECTED;
	}
	
	/**
	 * Disconnects from the broker.
	 */
	public void disconnect() {
		switch (this.configuraiton.getMqttVersion()) {
		case MQTT_3_1_1:
			Mqtt3BlockingClient clientV3 = (Mqtt3BlockingClient) this.getClient();
			clientV3.disconnect();
			break;
		case MQTT_5_0:
			Mqtt5BlockingClient clientV5 = (Mqtt5BlockingClient) this.getClient();
			clientV5.disconnect();
			break;
		}
	}
	
	/**
	 * Publishes the specified message string to the specified topic.
	 * @param topic the topic
	 * @param messageString the message string
	 */
	public void publish(String topic, String messageString) {
		switch (this.configuraiton.getMqttVersion()) {
		case MQTT_3_1_1:
			Mqtt3BlockingClient clientV3 = (Mqtt3BlockingClient) this.getClient();
			Mqtt3Publish messageV3 = Mqtt3Publish.builder().topic(topic).payload(messageString.getBytes()).build();
			clientV3.publish(messageV3);
			break;
		case MQTT_5_0:
			Mqtt5BlockingClient clientV5 = (Mqtt5BlockingClient) this.getClient();
			Mqtt5Publish messageV5 = Mqtt5Publish.builder().topic(topic).payload(messageString.getBytes()).build();
			clientV5.publish(messageV5);
			break;
		}
		
	}
	
	public static ConnectorConfigurationMQTT getDefaultConfiguration() {
		ConnectorConfigurationMQTT defaultConfiguratin = new ConnectorConfigurationMQTT();
		defaultConfiguratin.setUrlOrIP("localhost");
		defaultConfiguratin.setPort(1883);
		defaultConfiguratin.setMqttVersion(MqttVersion.MQTT_3_1_1);
		return defaultConfiguratin;
	}
	
	public void subscribe (String topic) {
		switch (this.configuraiton.getMqttVersion()) {
		case MQTT_3_1_1:
			Mqtt3BlockingClient clientV3 = (Mqtt3BlockingClient) this.getClient();
			clientV3.subscribeWith().topicFilter(topic).send();
			clientV3.toAsync().publishes(MqttGlobalPublishFilter.ALL, new Mqtt3Consumer());
			break;
		case MQTT_5_0:
			Mqtt5BlockingClient clientV5 = (Mqtt5BlockingClient) this.getClient();
			clientV5.subscribeWith().topicFilter(topic).send();
			clientV5.toAsync().publishes(MqttGlobalPublishFilter.ALL, new Mqtt5Consumer());
			break;
		}
	}
	
	
	private class Mqtt3Consumer implements Consumer<Mqtt3Publish> {

		@Override
		public void accept(Mqtt3Publish t) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class Mqtt5Consumer implements Consumer<Mqtt5Publish> {
		
		@Override
		public void accept(Mqtt5Publish t) {
			// TODO Auto-generated method stub
			
		}
		
	}

	
	
}
