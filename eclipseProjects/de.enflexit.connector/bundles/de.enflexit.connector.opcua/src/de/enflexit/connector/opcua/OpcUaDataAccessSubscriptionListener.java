package de.enflexit.connector.opcua;

import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;


/**
 * The listener interface for receiving opcUaDataAccessSubscription events.
 * The class that is interested in processing a opcUaDataAccessSubscription
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addOpcUaDataAccessSubscriptionListener<code> method. When
 * the opcUaDataAccessSubscription event occurs, that object's appropriate
 * method is invoked.
 *
 * @see OpcUaDataAccessSubscriptionEvent
 */
public interface OpcUaDataAccessSubscriptionListener {

	/**
	 * Will be invoked on subscription value update.
	 *
	 * @param item the {@link UaMonitoredItem}
	 * @param value the {@link DataValue}
	 */
	public void onSubscriptionValueUpdate(UaMonitoredItem item, DataValue value);
	
}
