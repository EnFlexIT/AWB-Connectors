package de.enflexit.connector.examples.mqtt;

import de.enflexit.connector.core.manager.ConnectorManager;
import de.enflexit.connector.mqtt.MQTTConnector;
import de.enflexit.connector.mqtt.MQTTConnectorConfiguration;
import de.enflexit.connector.mqtt.MQTTMessageWrapper;
import de.enflexit.connector.mqtt.MQTTSubscriber;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Simple example Agent for testing base MQTT.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class PingPongAgentMQTT extends Agent implements MQTTSubscriber {

	private static final long serialVersionUID = -3343659599481843591L;
	
	private static final String MQTT_TOPIC = "pingpong";
	
	private String sendTopic;
	private String receiveTopic;
	
	private MQTTConnector mqttConnector;
	
	private int ppCounter;
	
	/* (non-Javadoc)
	 * @see jade.core.Agent#setup()
	 */
	@Override
	protected void setup() {
		
		System.out.println("Starting " + this.getClass().getSimpleName() + " " + this.getLocalName());
		
		if (this.getMqttConnector()==null) {
			System.err.println("[" +this.getClass().getSimpleName() + " " + this.getLocalName() + "] MQTT connector not found!");
		} else {
			this.getMqttConnector().subscribe(this.getReceiveTopic(), this);
			
			if (this.getLocalName().equals("Ping")) {
				String payloadString = "Ping " + this.ppCounter;
				this.getMqttConnector().publish(this.getSendTopic(), payloadString);
				this.ppCounter++;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see jade.core.Agent#takeDown()
	 */
	@Override
	protected void takeDown() {
		this.getMqttConnector().unsubscribe(this.getReceiveTopic(), this);
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

	/* (non-Javadoc)
	 * @see de.enflexit.connector.mqtt.MQTTSubscriber#handleMessage(de.enflexit.connector.mqtt.MQTTMessageWrapper)
	 */
	@Override
	public void handleMessage(MQTTMessageWrapper incomingMessage) {
		this.addBehaviour(new SendReplyBehaviour(incomingMessage));
	}
	
	private MQTTConnector getMqttConnector() {
		if (mqttConnector==null) {
			mqttConnector = (MQTTConnector) ConnectorManager.getInstance().getConnectorByHostAndProtocol("localhost", MQTTConnectorConfiguration.PROTOCOL_NAME);
		}
		return mqttConnector;
	}
	
	private class SendReplyBehaviour extends OneShotBehaviour {
		
		private static final long serialVersionUID = 2353505537244674128L;

		private MQTTMessageWrapper incomingMessage;

		public SendReplyBehaviour(MQTTMessageWrapper incomingMessage) {
			this.incomingMessage = incomingMessage;
		}

		@Override
		public void action() {
			String content = incomingMessage.getPayloadString();
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
			PingPongAgentMQTT.this.getMqttConnector().publish(PingPongAgentMQTT.this.getSendTopic(), responseContent);
		}
		
	}

}
