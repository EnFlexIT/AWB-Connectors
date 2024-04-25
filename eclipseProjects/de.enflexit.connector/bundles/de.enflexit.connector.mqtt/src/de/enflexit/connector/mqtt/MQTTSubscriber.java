package de.enflexit.connector.mqtt;

public interface MQTTSubscriber {
	public void handleMessage(MQTTMessageWrapper messageWrapper);
}
