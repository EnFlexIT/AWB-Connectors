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
	
	public MQTTMessageWrapper(Mqtt3Publish mqtt3message) {
		this.mqtt3message = mqtt3message;
	}

	public MQTTMessageWrapper(Mqtt5Publish mqtt5message) {
		this.mqtt5message = mqtt5message;
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
