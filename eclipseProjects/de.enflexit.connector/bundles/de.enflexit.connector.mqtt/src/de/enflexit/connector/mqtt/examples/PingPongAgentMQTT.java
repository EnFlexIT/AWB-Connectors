package de.enflexit.connector.mqtt.examples;

import java.util.function.Consumer;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientBuilder;
import com.hivemq.client.mqtt.MqttClientState;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.MqttVersion;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAckReasonCode;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;

import de.enflexit.connector.mqtt.ConnectorConfigurationMQTT;
import jade.core.Agent;

/**
 * Simple example Agent for testing base MQTT.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class PingPongAgentMQTT extends Agent{

	private static final long serialVersionUID = -3343659599481843591L;
	
	private static final String MQTT_TOPIC = "pingpong";
	
	private ConnectorConfigurationMQTT mqttConfiguration;
	
	private Mqtt5BlockingClient mqttClient;
	private Consumer<Mqtt5Publish> consumer;
	
	private String sendTopic;
	private String receiveTopic;
	
	private int ppCounter;
	
	/* (non-Javadoc)
	 * @see jade.core.Agent#setup()
	 */
	@Override
	protected void setup() {
		
		System.out.println("Starting " + this.getClass().getSimpleName() + " " + this.getLocalName());
		
		Mqtt5ConnAck connAck = this.getMqttClient().connect();
		if (connAck.getReasonCode() == Mqtt5ConnAckReasonCode.SUCCESS) {
			System.out.println(this.getLocalName() + " successfully connected!");
			// --- Subscribe to the topic ----------------
			this.getMqttClient().subscribeWith().topicFilter(this.getReceiveTopic()).send();
			this.getMqttClient().toAsync().publishes(MqttGlobalPublishFilter.SUBSCRIBED, this.getConsumer());
			
			if (this.getLocalName().equals("Ping")) {
				String payloadString = "Ping " + this.ppCounter;
				this.ppCounter++;
				Mqtt5Publish message = Mqtt5Publish.builder().topic(this.getSendTopic()).payload(payloadString.getBytes()).build();
				this.getMqttClient().publish(message);
			}
		} else {
			System.err.println(this.getLocalName() + " connection failed!");
		}
	}
	
	private ConnectorConfigurationMQTT getMqttConfiguration() {
		if (mqttConfiguration==null) {
			mqttConfiguration = new ConnectorConfigurationMQTT();
			mqttConfiguration.setUrlOrIP("localhost");
			mqttConfiguration.setPort(1883);
			mqttConfiguration.setMqttVersion(MqttVersion.MQTT_5_0);
		}
		return mqttConfiguration;
	}
	
	private Consumer<Mqtt5Publish> getConsumer() {
		if (consumer==null) {
			consumer = new Consumer<Mqtt5Publish>() {
				
				/* (non-Javadoc)
				 * @see java.util.function.Consumer#accept(java.lang.Object)
				 */
				@Override
				public void accept(Mqtt5Publish mqttMessage) {
					String content = new String(mqttMessage.getPayloadAsBytes());
					System.out.println(PingPongAgentMQTT.this.getLocalName() + " received " + content);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String responseContent;
					if (content.startsWith("Ping")) {
						responseContent = "Pong " + PingPongAgentMQTT.this.ppCounter;
					} else {
						responseContent = "Ping " + PingPongAgentMQTT.this.ppCounter;
					}
					PingPongAgentMQTT.this.ppCounter++;
					Mqtt5Publish responseMessage = Mqtt5Publish.builder().topic(PingPongAgentMQTT.this.getSendTopic()).payload(responseContent.getBytes()).build();
					if (PingPongAgentMQTT.this.getMqttClient().getState()==MqttClientState.CONNECTED) {
						PingPongAgentMQTT.this.getMqttClient().publish(responseMessage);
					} else {
						System.err.println(PingPongAgentMQTT.this + " could not publish " + responseContent + ", not connected");
					}
				}
			};
		}
		return consumer;
	}
	
	private Mqtt5BlockingClient getMqttClient() {
		if (mqttClient==null) {
			MqttClientBuilder clientBuilder = MqttClient.builder().identifier(this.getLocalName()).serverHost(this.getMqttConfiguration().getUrlOrIP()).serverPort(this.getMqttConfiguration().getPort());
			mqttClient = clientBuilder.useMqttVersion5().build().toBlocking();
		}
		return mqttClient;
	}
	
	/* (non-Javadoc)
	 * @see jade.core.Agent#takeDown()
	 */
	@Override
	protected void takeDown() {
		this.getMqttClient().disconnect();
	}
	
	private String getSendTopic() {
		if (sendTopic==null) {
			if (this.getLocalName().equals("Ping")) {
				sendTopic = MQTT_TOPIC + "/ping";
			} else {
				sendTopic = MQTT_TOPIC + "/pong";
			}
		}
		return sendTopic;
	}
	
	private String getReceiveTopic() {
		if (receiveTopic==null) {
			if (this.getLocalName().equals("Ping")) {
				receiveTopic = MQTT_TOPIC + "/pong";
			} else {
				receiveTopic = MQTT_TOPIC + "/ping";
			}
		}
		return receiveTopic;
	}

}
