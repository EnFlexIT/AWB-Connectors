package de.enflexit.connector.mqtt;

import java.util.ArrayList;

public class MQTTSubscription {
	private String topic;
	private ArrayList<MQTTSubscriber> subscribers;

	public ArrayList<MQTTSubscriber> getSubscribers() {
		return subscribers;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	public void addSubscriber(MQTTSubscriber listener) {
		if (this.getSubscribers().contains(listener)==false) {
			this.getSubscribers().add(listener);
		}
	}
	
	public void removeSubscriber(MQTTSubscriber subscriber) {
		if (this.getSubscribers().contains(subscriber)==true) {
			this.getSubscribers().remove(subscriber);
		}
		
	}
	
}
