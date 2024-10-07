package de.enflexit.connector.mqtt;

import java.util.HashMap;
import java.util.function.Consumer;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientBuilder;
import com.hivemq.client.mqtt.MqttClientState;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.exceptions.ConnectionFailedException;
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3ClientBuilder;
import com.hivemq.client.mqtt.mqtt3.exceptions.Mqtt3ConnAckException;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAckReturnCode;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5ClientBuilder;
import com.hivemq.client.mqtt.mqtt5.exceptions.Mqtt5ConnAckException;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAckReasonCode;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;

import de.enflexit.common.properties.Properties;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.AbstractConnectorConfiguration;
import de.enflexit.connector.mqtt.MQTTConnectorConfiguration.AuthMode;

/**
 * This class manages the connection to one MQTT broker. It allows to publish messages and subscribe to topics.
 * Incoming messages will be forwarded to all {@link MQTTSubscriber}s that subscribed to the corresponding topic. 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class MQTTConnector extends AbstractConnector {
	
	private MqttClient client;
	
	private HashMap<String, MQTTSubscription> activeSubscriptions;
	
	private MQTTConnectorConfiguration connectorConfiguration;
	
	/**
	 * Gets the MQTT client instance.
	 * @return the client
	 */
	private MqttClient getClient() {
		if (client==null) {
			MqttClientBuilder clientBuilder = MqttClient.builder().identifier(this.getConnectorConfiguration().getClientID()).serverHost(this.getConnectorConfiguration().getUrlOrIP()).serverPort(this.getConnectorConfiguration().getPort());
			
			switch(this.getConnectorConfiguration().getMqttVersion()) {
			case MQTT_3_1_1:
				Mqtt3ClientBuilder mqtt3Builder = clientBuilder.useMqttVersion3();
				if (this.getConnectorConfiguration().getAuthMode()==AuthMode.SIMPLE) {
					String userName = this.getConnectorConfiguration().getUsername();
					byte[] password = this.getConnectorConfiguration().getPassword().getBytes();
					mqtt3Builder.simpleAuth().username(userName).password(password).applySimpleAuth();
				}
				client = mqtt3Builder.build().toBlocking();
				break;
			case MQTT_5_0:
				Mqtt5ClientBuilder mqtt5Builder = clientBuilder.useMqttVersion5();
				if (this.getConnectorConfiguration().getAuthMode()==AuthMode.SIMPLE) {
					String userName = this.getConnectorConfiguration().getUsername();
					byte[] password = this.getConnectorConfiguration().getPassword().getBytes();
					mqtt5Builder.simpleAuth().username(userName).password(password).applySimpleAuth();
				}
				client = mqtt5Builder.build().toBlocking();
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
		
		boolean success = false;
		try {
			switch (this.getConnectorConfiguration().getMqttVersion()) {
			case MQTT_3_1_1:
				success = this.connectV3();
				break;
			case MQTT_5_0:
				success = this.connectV5();
				break;
			default:
				return false;
			}
		} catch (ConnectionFailedException cfe) {
			System.err.println("[" + this.getClass().getSimpleName() + "] Connection failed: " + cfe.getMessage() + " (Exception)");
		}
		
		return success;
		
	}
	
	/**
	 * Connect implementation for MQTT 3.1.1
	 * @return true, if successful
	 */
	private boolean connectV3() {
		Mqtt3BlockingClient mqtt3Client = (Mqtt3BlockingClient) this.getClient();
		Mqtt3ConnAck connAck = null;
		try {
			connAck = mqtt3Client.connect();
			if (connAck.getReturnCode()==Mqtt3ConnAckReturnCode.SUCCESS) {
				return true;
			} else {
				System.err.println("[" + this.getClass().getSimpleName() + "] Connection failed: " + connAck.getReturnCode());
				return false;
			}
		} catch (Mqtt3ConnAckException conAckEx) {
			if (conAckEx.getMqttMessage().getReturnCode()==Mqtt3ConnAckReturnCode.NOT_AUTHORIZED) {
				System.err.println("[" + this.getClass().getSimpleName() + "] Authentication failed!");
			}
			return false;
		}
	}
	
	/**
	 * Connect implementation for MQTT 5.0
	 * @return true, if successful
	 */
	private boolean connectV5() {
		Mqtt5BlockingClient mqtt5Client = (Mqtt5BlockingClient) this.getClient();
		
		Mqtt5ConnAck connAck = null;
		try {
			connAck = mqtt5Client.connect();
			if (connAck.getReasonCode() == Mqtt5ConnAckReasonCode.SUCCESS) {
				return true;
			} else {
				System.err.println("[" + this.getClass().getSimpleName() + "] Connection failed: " + connAck.getReasonString() + " (Else-Zweig)");
				return false;
			}
		} catch (Mqtt5ConnAckException conAckEx) {
			if (conAckEx.getMqttMessage().getReasonCode()==Mqtt5ConnAckReasonCode.NOT_AUTHORIZED) {
				System.err.println("[" + this.getClass().getSimpleName() + "] Authentication failed!");
			}
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
		switch (this.getConnectorConfiguration().getMqttVersion()) {
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
	 * Publishes the specified message string to the specified topic. The message is not retained.
	 * @param topic the topic
	 * @param messageString the message string
	 */
	public void publish(String topic, String messageString) {
		this.publish(topic, messageString, false);
	}
	
	/**
	 * Publishes the specified message string to the specified topic. According to the corresponding parameter, the message can be retained.
	 * @param topic the topic
	 * @param messageString the message string
	 * @param isRetain the is retain
	 */
	public void publish(String topic, String messageString, boolean isRetain) {
		switch (this.getConnectorConfiguration().getMqttVersion()) {
		case MQTT_3_1_1:
			Mqtt3BlockingClient clientV3 = (Mqtt3BlockingClient) this.getClient();
			Mqtt3Publish messageV3 = Mqtt3Publish.builder().topic(topic).payload(messageString.getBytes()).retain(isRetain).build();
			clientV3.publish(messageV3);
			break;
		case MQTT_5_0:
			Mqtt5BlockingClient clientV5 = (Mqtt5BlockingClient) this.getClient();
			Mqtt5Publish messageV5 = Mqtt5Publish.builder().topic(topic).payload(messageString.getBytes()).retain(isRetain).build();
			clientV5.publish(messageV5);
			break;
		}
		
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
	
	/**
	 * Starts a subscription to the configured broker.
	 * @param subscription the subscription
	 */
	private void startSubscription(MQTTSubscription subscription) {
		switch (this.getConnectorConfiguration().getMqttVersion()) {
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
	 * Unsubscribes a single subscriber from the specified topic.
	 * @param topic the topic
	 */
	public void unsubscribe(String topic, MQTTSubscriber subscriber) {
		
		// --- Get the subscription for the specified topic ---------
		MQTTSubscription subscription = this.getActiveSubscriptions().get(topic);
		if (subscription!=null) {
			
			// --- Remove the subscriber ----------------------------
			subscription.removeSubscriber(subscriber);
			
			// --- If no subscribers left, unsbscribe from the broker
			if (subscription.getSubscribers().isEmpty()) {
				this.stopSubscription(topic);
				this.getActiveSubscriptions().remove(topic);
			}
		}
	}
	
	/**
	 * Stops the subscription to the specified topic.
	 * @param topic the topic
	 */
	private void stopSubscription(String topic) {
		if (this.isConnected()==true) {
			switch (this.getConnectorConfiguration().getMqttVersion()) {
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
			if (this.subscription.matchesTopic(message.getTopic())) {
				MQTTMessageWrapper messageWrapper = new MQTTMessageWrapper(message);
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
			if (this.subscription.matchesTopic(message.getTopic())) {
				MQTTMessageWrapper messageWrapper = new MQTTMessageWrapper(message);
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
		return MQTTConnectorConfiguration.PROTOCOL_NAME;
	}

	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#getInitialProperties()
	 */
	@Override
	public Properties getInitialProperties() {
		return MQTTConnectorConfiguration.getDefaultProperties();
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#getConfigurationFromProperties(de.enflexit.common.properties.Properties)
	 */
	public AbstractConnectorConfiguration getConfigurationFromProperties(Properties properties) {
		return MQTTConnectorConfiguration.fromProperties(properties);
	}

	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#getConnectorConfiguration()
	 */
	public MQTTConnectorConfiguration getConnectorConfiguration() {
		if (connectorConfiguration==null) {
			connectorConfiguration = (MQTTConnectorConfiguration) this.getConfigurationFromProperties(this.getConnectorProperties());
		}
		return connectorConfiguration;
	}
	
}
