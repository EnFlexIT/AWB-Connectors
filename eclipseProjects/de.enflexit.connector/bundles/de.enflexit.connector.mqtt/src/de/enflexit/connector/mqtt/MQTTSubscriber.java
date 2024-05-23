package de.enflexit.connector.mqtt;

/**
 * This interface must be implemented to listen to updates from an MQTT subscription managed by an {@link MQTTConnector}.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public interface MQTTSubscriber {
	public void handleMessage(MQTTMessageWrapper messageWrapper);
}
