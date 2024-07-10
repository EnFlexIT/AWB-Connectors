package de.enflexit.connector.mqtt;

import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;

/**
 * This wrapper class is meant to provide a unified handling for MQTT3 and MQTT5 messages, despite they have no common superclass or interface. 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class MQTTMessageWrapper {
	private Mqtt3Publish mqtt3message;
	private Mqtt5Publish mqtt5message;
	
	/**
	 * Instantiates a new MQTT message wrapper for a MQTT3 message.
	 * @param mqtt3message the mqtt 3 message
	 */
	public MQTTMessageWrapper(Mqtt3Publish mqtt3message) {
		this.mqtt3message = mqtt3message;
	}

	/**
	 * Instantiates a new MQTT message wrapper for a MQTT5 message.
	 * @param mqtt5message the mqtt 5 message
	 */
	public MQTTMessageWrapper(Mqtt5Publish mqtt5message) {
		this.mqtt5message = mqtt5message;
	}
	
	/**
	 * Gets the message topic.
	 * @return the message topic
	 */
	public String getMessageTopic() {
		String topicString = "";
		if (this.mqtt3message!=null) {
			topicString = this.mqtt3message.getTopic().toString();
		} else if (this.mqtt5message!=null) {
			topicString = this.mqtt5message.getTopic().toString();
		}
		return topicString;
	}
	
	/**
	 * Gets the unprocessed message payload.
	 * @return the payload bytes
	 */
	public byte[] getPayloadBytes() {
		byte[] payloadBytes = null;
		
		if (this.mqtt3message!=null) {
			payloadBytes = this.mqtt3message.getPayloadAsBytes();
		} else if (mqtt5message!=null) {
			payloadBytes = this.mqtt5message.getPayloadAsBytes();
		}
		
		return payloadBytes;
	}
	
	/**
	 * Gets the message payload as a string.
	 * @return the payload string
	 */
	public String getPayloadString() {
		String payloadString = null;
		if (this.mqtt3message!=null) {
			payloadString = new String(this.mqtt3message.getPayloadAsBytes());
		} else if (mqtt5message!=null) {
			payloadString = new String(this.mqtt5message.getPayloadAsBytes());
		}
		
		return payloadString;
	}
}
