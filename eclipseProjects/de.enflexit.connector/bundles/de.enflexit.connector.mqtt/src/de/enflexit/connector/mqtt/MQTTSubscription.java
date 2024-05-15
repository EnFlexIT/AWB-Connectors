package de.enflexit.connector.mqtt;

import java.util.ArrayList;

import com.hivemq.client.mqtt.datatypes.MqttTopic;
import com.hivemq.client.mqtt.datatypes.MqttTopicFilter;

/**
 * This class collects the MQTT subscriptions for a specific topic.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class MQTTSubscription {
	private String topic;
	private ArrayList<MQTTSubscriber> subscribers;
	
	private MqttTopicFilter topicFilter;

	protected synchronized ArrayList<MQTTSubscriber> getSubscribers() {
		if (subscribers==null) {
			subscribers = new ArrayList<>();
		}
		return subscribers;
	}

	/**
	 * Gets the topic.
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * Sets the topic.
	 * @param topic the new topic
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	/**
	 * Adds a subscriber.
	 * @param listener the listener
	 */
	public void addSubscriber(MQTTSubscriber listener) {
		if (this.getSubscribers().contains(listener)==false) {
			this.getSubscribers().add(listener);
		}
	}
	
	/**
	 * Removes a subscriber.
	 * @param subscriber the subscriber
	 */
	public void removeSubscriber(MQTTSubscriber subscriber) {
		if (this.getSubscribers().contains(subscriber)==true) {
			this.getSubscribers().remove(subscriber);
		}
	}
	
	/**
	 * Checks if the provided topic string matches this subscription's topic.
	 * @param topicString the topic string
	 * @return true, if successful
	 */
	public boolean matchesTopic(String topicString) {
		return this.matchesTopic(MqttTopic.of(topicString));
	}
	
	/**
	 * Checks if the provided topic matches this subscription's topic.
	 * @param topic the topic
	 * @return true, if successful
	 */
	public boolean matchesTopic(MqttTopic topic) {
		return this.getTopicFilter().matches(topic);
	}
	
	/**
	 * Gets the topic filter.
	 * @return the topic filter
	 */
	private MqttTopicFilter getTopicFilter() {
		if (topicFilter==null) {
			topicFilter = MqttTopicFilter.of(this.getTopic());
		}
		return topicFilter;
	}
}
