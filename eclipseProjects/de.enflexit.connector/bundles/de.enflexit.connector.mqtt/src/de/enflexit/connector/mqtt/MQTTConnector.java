package de.enflexit.connector.mqtt;

import java.util.HashMap;
import java.util.function.Consumer;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientBuilder;
import com.hivemq.client.mqtt.MqttClientState;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.MqttVersion;
import com.hivemq.client.mqtt.exceptions.ConnectionFailedException;
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAckReturnCode;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAckReasonCode;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;

import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.AbstractConnectorConfiguration;

/**
 * This class manages the connection to one MQTT broker. It allows to publish messages and subscribe to topics.
 * Incoming messages will be forwarded to all {@link MQTTSubscriber}s that subscribed to the corresponding topic. 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class MQTTConnector extends AbstractConnector {
	
	public static final String PROTOCOL_NAME = "MQTT";
	
	private MqttClient client;
	
	private MQTTConnectorConfiguration configuration;
	
	private HashMap<String, MQTTSubscription> activeSubscriptions;

	/**
	 * Gets the MQTT client instance.
	 * @return the client
	 */
	private MqttClient getClient() {
		if (client==null) {
			MqttClientBuilder clientBuilder = MqttClient.builder().identifier(this.getMqttConfiguration().getClientID()).serverHost(this.getMqttConfiguration().getUrlOrIP()).serverPort(this.getMqttConfiguration().getPort());
			switch(this.getMqttConfiguration().getMqttVersion()) {
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
		try {
			switch (this.getMqttConfiguration().getMqttVersion()) {
			case MQTT_3_1_1:
				return this.connectV3();
			case MQTT_5_0:
				return this.connectV5();
			default:
				return false;
			}
		} catch (ConnectionFailedException cfe) {
			System.err.println("[" + this.getClass().getSimpleName() + "] Connection failed: " + cfe.getMessage() + " (Exception)");
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
			System.err.println("[" + this.getClass().getSimpleName() + "] Connection failed: " + connAck.getReasonString() + " (Else-Zweig)");
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
		switch (this.getMqttConfiguration().getMqttVersion()) {
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
		switch (this.getMqttConfiguration().getMqttVersion()) {
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
	
	public static MQTTConnectorConfiguration getDefaultConfiguration() {
		MQTTConnectorConfiguration defaultConfiguratin = new MQTTConnectorConfiguration();
		defaultConfiguratin.setUrlOrIP("localhost");
		defaultConfiguratin.setPort(1883);
		defaultConfiguratin.setMqttVersion(MqttVersion.MQTT_3_1_1);
		return defaultConfiguratin;
	}
	
	private HashMap<String, MQTTSubscription> getActiveSubscriptions() {
		if (activeSubscriptions==null) {
			activeSubscriptions = new HashMap<>();
		}
		return activeSubscriptions;
	}
	
	/**
	 * Subscribe to the specified topic.
	 * @param topic the topic
	 */
	public void subscribe (String topic, MQTTSubscriber subscriber) {
		
		// --- CHeck if there is an active subscription for this topic --------
		MQTTSubscription subscription = this.getActiveSubscriptions().get(topic);
		
		// --- If not, create a new one -------------------
		if (subscription==null) {
			subscription = new MQTTSubscription();
			subscription.setTopic(topic);
			subscription.addSubscriber(subscriber);
			this.activeSubscriptions.put(topic, subscription);
			this.startSubscription(subscription);
		} else {
			// --- Add to an existing subscription --------
			subscription.addSubscriber(subscriber);
		}
		
	}
	
	private void startSubscription(MQTTSubscription subscription) {
		switch (this.getMqttConfiguration().getMqttVersion()) {
		case MQTT_3_1_1:
			Mqtt3BlockingClient clientV3 = (Mqtt3BlockingClient) this.getClient();
			clientV3.subscribeWith().topicFilter(subscription.getTopic()).send();
			// --- Register the consumer to process messages --------
			clientV3.toAsync().publishes(MqttGlobalPublishFilter.ALL, new Mqtt3Consumer(subscription));
			break;
		case MQTT_5_0:
			Mqtt5BlockingClient clientV5 = (Mqtt5BlockingClient) this.getClient();
			clientV5.subscribeWith().topicFilter(subscription.getTopic()).send();
			// --- Register the consumer to process messages --------
			clientV5.toAsync().publishes(MqttGlobalPublishFilter.ALL, new Mqtt5Consumer(subscription));
			break;
		}
		
	}
	
	/**
	 * Unsubscribe from the specified topic.
	 * @param topic the topic
	 */
	public void unsubscribe(String topic, MQTTSubscriber subscriber) {
		
		// --- Get the subscription for the specified topic ---------
		MQTTSubscription subscription = this.getActiveSubscriptions().get(topic);
		if (subscription!=null) {
			
			// --- Remove the subscriber ----------------------------
			subscription.removeSubscriber(subscriber);
			
			// --- I fno subscribers left, unsbscribe from the broker
			if (subscription.getSubscribers().isEmpty()) {
				this.stopSubscription(topic);
				this.getActiveSubscriptions().remove(topic);
			}
		}
	}
	
	private void stopSubscription(String topic) {
		switch (this.getMqttConfiguration().getMqttVersion()) {
		case MQTT_3_1_1:
			Mqtt3BlockingClient clientV3 = (Mqtt3BlockingClient) this.getClient();
			clientV3.unsubscribeWith().topicFilter(topic).send();
			break;
		case MQTT_5_0:
			Mqtt5BlockingClient clientV5 = (Mqtt5BlockingClient) this.getClient();
			clientV5.unsubscribeWith().topicFilter(topic).send();
			break;
		}
	}
	
	private MQTTConnectorConfiguration getMqttConfiguration() {
		if (configuration==null) {
			configuration = MQTTConnectorConfiguration.fromProperties((MqttConnectorProperties) this.getConnectorProperties());
		}
		return configuration;
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#getConnectorConfiguration()
	 */
	@Override
	public AbstractConnectorConfiguration getConnectorConfiguration() {
		return this.getMqttConfiguration();
	}
	
	
	// --------------------------------------------------------------------------------------------
	// -- From here, consumer implementations to receive and forward incoming MQTT messages -------
	// --------------------------------------------------------------------------------------------
	
	/**
	 * The Class Mqtt3Consumer.
	 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
	 */
	private class Mqtt3Consumer implements Consumer<Mqtt3Publish> {
		
		private MQTTSubscription subscription;

		/**
		 * Instantiates a new mqtt 3 consumer.
		 * @param subscription the subscription
		 */
		public Mqtt3Consumer(MQTTSubscription subscription) {
			this.subscription = subscription;
		}

		/* (non-Javadoc)
		 * @see java.util.function.Consumer#accept(java.lang.Object)
		 */
		@Override
		public void accept(Mqtt3Publish message) {
			MQTTMessageWrapper messageWrapper = new MQTTMessageWrapper(message);
			if (messageWrapper.getMessageTopic().equals(this.subscription.getTopic())) {
				for (MQTTSubscriber listener : this.subscription.getSubscribers()) {
					listener.handleMessage(messageWrapper);
				}
			}
		}
		
	}
	
	/**
	 * The Class Mqtt5Consumer.
	 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
	 */
	private class Mqtt5Consumer implements Consumer<Mqtt5Publish> {
		
		private MQTTSubscription subscription;
		
		/**
		 * Instantiates a new mqtt 5 consumer.
		 * @param subscription the subscription
		 */
		public Mqtt5Consumer(MQTTSubscription subscription) {
			this.subscription = subscription;
		}

		/* (non-Javadoc)
		 * @see java.util.function.Consumer#accept(java.lang.Object)
		 */
		@Override
		public void accept(Mqtt5Publish message) {
			MQTTMessageWrapper messageWrapper = new MQTTMessageWrapper(message);
			if (messageWrapper.getMessageTopic().equals(this.subscription.getTopic())) {
				for (MQTTSubscriber listener : this.subscription.getSubscribers()) {
					listener.handleMessage(messageWrapper);
				}
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#getProtocolName()
	 */
	@Override
	public String getProtocolName() {
		return PROTOCOL_NAME;
	}
	
}
