package de.enflexit.connector.opcua;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;

/**
 * The Class OpcUaDataAccess.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaDataAccess {

	private OpcUaConnector opcUaConnector;
	
	/**
	 * Instantiates a new opc ua data access.
	 * @param opcUaConnector the opc ua connector
	 */
	public OpcUaDataAccess(OpcUaConnector opcUaConnector) {
		this.opcUaConnector = opcUaConnector;
	}
	
	/**
	 * Starts the data acquisition.
	 */
	public void startDataAcquisition() {
		
		if (opcUaConnector==null) return;
		
        try {
        	
        	NodeId powerNodeIdGuid = NodeId.parse("ns=2;s=Dynamic/RandomFloat");
            NodeId pressureNodeIdGuid = NodeId.parse("ns=2;s=Dynamic/RandomInt32");
            NodeId windspeedNodeIdGuid = NodeId.parse("ns=2;s=Dynamic/RandomDouble");

            
			// create a subscription @ 1000ms
			UaSubscription subscription = this.opcUaConnector.getOpcUaClient().getSubscriptionManager().createSubscription(1000.0).get();

			// subscribe to the Value attribute of the server's CurrentTime node
			ReadValueId readValueId = new ReadValueId(Identifiers.Server_ServerStatus_CurrentTime, AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);
			ReadValueId readPowerValueId = new ReadValueId(powerNodeIdGuid,AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);
			ReadValueId readPressureValueId = new ReadValueId(pressureNodeIdGuid, AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);
			ReadValueId readSpeedValueId = new ReadValueId(windspeedNodeIdGuid,AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);
			
			UInteger clientHandle = subscription.nextClientHandle();
			//UInteger clientHandle = uint(clientHandles.getAndIncrement());
			
			MonitoringParameters parameters = new MonitoringParameters(clientHandle, 
					1000.0, 				// sampling interval
					null, 					// filter, null means use default
					UInteger.valueOf(10), 	// queue size
					true 					// discard oldest
			);

			MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(readValueId, MonitoringMode.Reporting, parameters);
			MonitoredItemCreateRequest powerRequest = new MonitoredItemCreateRequest(readPowerValueId, MonitoringMode.Reporting, parameters);
			MonitoredItemCreateRequest pressureRequest = new MonitoredItemCreateRequest(readPressureValueId, MonitoringMode.Reporting, parameters);
			MonitoredItemCreateRequest speedRequest = new MonitoredItemCreateRequest(readSpeedValueId, MonitoringMode.Reporting, parameters);
			
			
			// when creating items in MonitoringMode.Reporting this callback is where each item needs to have its
	        // value/event consumer hooked up. The alternative is to create the item in sampling mode, hook up the
	        // consumer after the creation call completes, and then change the mode for all items to reporting.
	        UaSubscription.ItemCreationCallback onItemCreated = (item, id) -> item.setValueConsumer(this::onSubscriptionValue);

	        List<UaMonitoredItem> items = subscription.createMonitoredItems(TimestampsToReturn.Both, newArrayList(request, powerRequest, pressureRequest, speedRequest), onItemCreated).get();
	        
	        for (UaMonitoredItem item : items) {
	            if (item.getStatusCode().isGood()) {
	            	System.out.println("item created for nodeId=" + item.getReadValueId().getNodeId() + "");
	            } else {
	            	System.err.println("failed to create item for nodeId=" + item.getReadValueId().getNodeId() + " (status=" + item.getStatusCode() + ")");
	            }
	        }
			
		} catch (InterruptedException | ExecutionException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * On subscription value.
	 *
	 * @param item the item
	 * @param value the value
	 */
	private void onSubscriptionValue(UaMonitoredItem item, DataValue value) {
		
		System.out.println("subscription value received: item=" + item.getReadValueId().getNodeId() + ", value=" + value.getValue());
		
		
    }

	
	/**
	 * Stops the data acquisition.
	 */
	public void stopDataAcquisition() {
	
		
	}
	
	
}
