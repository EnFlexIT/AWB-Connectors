package de.enflexit.connector.opcua;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.UaRuntimeException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;

import de.enflexit.common.properties.Properties;
import de.enflexit.common.properties.PropertyValue;

/**
 * The Class OpcUaDataAccess.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaDataAccess {

	public static final String DATA_NODE_ID_KEY_PREFIX = OpcUaConnector.PROP_DATA + ".c) NodeIDs.";
	
	private boolean isDebug = false;

	private OpcUaConnector opcUaConnector;
	
	private boolean isStartedDataAcquisition;
	private UInteger subscriptionID;
	
	private List<String> nodeIdListOrdered;
	private ConcurrentHashMap<String, DataValue> valueHashMap;
	
	private List<OpcUaDataAccessSubscriptionListener> listener;
	
	
	/**
	 * Instantiates a new OpcUaDataAccess instance.
	 * @param opcUaConnector the OpcUaConnector
	 */
	public OpcUaDataAccess(OpcUaConnector opcUaConnector) {
		this.opcUaConnector = opcUaConnector;
		this.getNodeIdListOrdered();
	}
	
	/**
	 * Returns the ordered list of ID's .
	 * @return the id list ordered
	 */
	public List<String> getNodeIdListOrdered() {
		if (nodeIdListOrdered==null) {
			nodeIdListOrdered = new ArrayList<>();
			
			// --- Extract list position -------------------------------- 
			TreeMap<Integer, String> sortingTreeMap = new TreeMap<>();
			Properties props = this.opcUaConnector.getConnectorProperties();
			for (String propKey : props.getIdentifierList()) {
				if (propKey.startsWith(DATA_NODE_ID_KEY_PREFIX)==true) {
					String nodeID = propKey.substring(DATA_NODE_ID_KEY_PREFIX.length());
					Integer ordinalPos = props.getIntegerValue(propKey);
					sortingTreeMap.put(ordinalPos, nodeID);
				}
			}
			
			// --- Refill ordered list according to property keys -------
			for (Integer ordinal : sortingTreeMap.keySet()) {
				String nodeID = sortingTreeMap.get(ordinal);
				nodeIdListOrdered.add(nodeID);
			}
		}
		return nodeIdListOrdered;
	}
	
	/**
	 * Returns the value hash map.
	 * @return the value hash map
	 */
	public ConcurrentHashMap<String, DataValue> getValueHashMap() {
		if (valueHashMap==null) {
			valueHashMap = new ConcurrentHashMap<>();
		}
		return valueHashMap;
	}
	
	/**
	 * Adds the specified UaNode.
	 * @param uaNodeToAdd the ua node to add
	 */
	public void addOpcUaNode(UaNode uaNodeToAdd) {
		
		if (uaNodeToAdd==null) return;
		if (this.isDebug==true) System.out.println("Add UaNode " + uaNodeToAdd.getDisplayName());
		
		// --- Check if ID is already used ----------------
		String parseableNodeID = uaNodeToAdd.getNodeId().toParseableString();
		if (this.getNodeIdListOrdered().contains(parseableNodeID)==true) {
			if (this.isDebug==true) {
				System.err.println("[" + this.getClass().getSimpleName() + "] The Node with the ID '" + parseableNodeID + "' is already monitored.");
			}
			return;
		}
		
		// --- Define this value in the properties --------
		Integer newPos = this.getNodeIdListOrdered().size() + 1;
		this.opcUaConnector.getConnectorProperties().setIntegerValue(DATA_NODE_ID_KEY_PREFIX + parseableNodeID, newPos);
		this.getNodeIdListOrdered().add(parseableNodeID);
		
		// --- Dynamically add to monitored values --------
		if (this.isStartedDataAcquisition==true) {
			this.addUaNodeToMonitoring(uaNodeToAdd);
		} else {
			this.startDataAcquisition();
		}
		
		// --- Save the connector settings ----------------
		this.opcUaConnector.saveSettings();
	}
	
	/**
	 * Removes the specified UaNode.
	 * @param uaNodeToRemove the ua node to remove
	 */
	public void removeOpcUaNode(UaNode uaNodeToRemove) {

		if (uaNodeToRemove==null) return;
		if (this.isDebug==true) System.out.println("Remove UaNode " + uaNodeToRemove.getDisplayName());
		
		// --- Dynamically remove from monitored values ---
		if (this.isStartedDataAcquisition==true) {
			this.removeUaNodeFromMonitoring(uaNodeToRemove);
		}
		
		// --- Find & remove corresponding property -------
		String parseableNodeID = uaNodeToRemove.getNodeId().toParseableString();
		this.getNodeIdListOrdered().remove(parseableNodeID);
		this.getValueHashMap().remove(parseableNodeID);
		
		// --- Rearrange order of nodeID counter ----------
		this.opcUaConnector.getConnectorProperties().remove(DATA_NODE_ID_KEY_PREFIX + parseableNodeID);
		
		// --- Update position values ---------------------
		Properties props = this.opcUaConnector.getConnectorProperties();
		for (int i = 0; i < this.getNodeIdListOrdered().size(); i++) {
			String propKey = DATA_NODE_ID_KEY_PREFIX + this.getNodeIdListOrdered().get(i);
			PropertyValue propValue = props.getPropertyValue(propKey);
			Integer newPos = i+1;
			propValue.setValue(newPos);
			propValue.setValueString(newPos.toString());
		}  

		// --- Save the connector settings ----------------
		this.opcUaConnector.saveSettings();
	}
	
	
	/**
	 * Starts the data acquisition.
	 */
	public void startDataAcquisition() {
		
		if (this.opcUaConnector==null || this.opcUaConnector.isConnected()==false) return;
		
        try {
            
			// --- Collect NodeId's & ReadValueId's -----------------
			List<ReadValueId> readValueIdList = new ArrayList<>();
			for (String nodeIDString : this.getNodeIdListOrdered()) {
				try {
					NodeId nodeID = NodeId.parse(nodeIDString);
					readValueIdList.add(new ReadValueId(nodeID, AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE));
					
				} catch (Exception ex) {
					String statuscode = ""; 
					if (ex instanceof UaRuntimeException) {
						statuscode = ((UaRuntimeException)ex).getStatusCode().toString() + ", ";
					}
					System.err.println("[" + this.getClass().getSimpleName() + "] Error while parsing to NodeId with string identifier '" + nodeIDString + "' => " + statuscode + ex.getClass().getName());
					//ex.printStackTrace();
				}
			}

			// ------------------------------------------------------
			// --- Exit since their is nothing to monitor -----------
			// ------------------------------------------------------
			if (readValueIdList.size()==0) return;
    		
			
			// --- Create UaSubsription and MonitoringParameters ----
        	UaSubscription subscription = this.getUaSubscription();

        	Integer samplingInterval = this.opcUaConnector.getConnectorProperties().getIntegerValue(OpcUaConnector.PROP_DATA_MONITORING_SAMPLING_INTERVAL);
        	Integer queueSize = this.opcUaConnector.getConnectorProperties().getIntegerValue(OpcUaConnector.PROP_DATA_MONITORING_QUEUE_SIZE);
        	Boolean isDiscardOldest = this.opcUaConnector.getConnectorProperties().getBooleanValue(OpcUaConnector.PROP_DATA_MONITORING_DISCARD_OLDEST);
        	
        	MonitoringMode monitoringMode = MonitoringMode.valueOf(this.opcUaConnector.getConnectorProperties().getStringValue(OpcUaConnector.PROP_DATA_MONITORING_MODE));
        	
        	// --- Create list of MonitoredItemCreateRequest --------
        	UInteger clientHandle = null;
			List<MonitoredItemCreateRequest> monitoredItemList = new ArrayList<>();
        	for (ReadValueId readValueID : readValueIdList) {
        		
        		if (clientHandle==null) {
        			clientHandle = subscription.nextClientHandle();
        		} else {
        			clientHandle = UInteger.valueOf(clientHandle.intValue() + 1);
        		}
        		MonitoringParameters parameters = new MonitoringParameters(clientHandle, samplingInterval.doubleValue(), null, UInteger.valueOf(queueSize), isDiscardOldest);
        		monitoredItemList.add(new MonitoredItemCreateRequest(readValueID, monitoringMode, parameters));
        	}
        	
			// --- Define local callback method ---------------------
			UaSubscription.ItemCreationCallback itemCreationCallback = (item, id) -> item.setValueConsumer(this::onSubscriptionValue);

			// --- Check monitored items ----------------------------
	        List<UaMonitoredItem> items = subscription.createMonitoredItems(TimestampsToReturn.Both, monitoredItemList, itemCreationCallback).get();
	        for (UaMonitoredItem item : items) {
	            if (item.getStatusCode().isGood()==true) {
	            	if (this.isDebug) System.out.println("[" + this.getClass().getSimpleName() + "] UaMonitoredItem created for nodeId '" + item.getReadValueId().getNodeId() + "'");
	            } else {
	            	System.err.println("[" + this.getClass().getSimpleName() + "] Failed to create item for nodeId '" + item.getReadValueId().getNodeId() + "' => " + item.getStatusCode().toString());
	            }
	        }
	        this.isStartedDataAcquisition = true;
	        
		} catch (InterruptedException | ExecutionException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Adds the specified UaNode to the running monitoring.
	 * @param uaNode the {@link UaNode} to add
	 */
	private void addUaNodeToMonitoring(UaNode uaNode) {
		
    	try {
    		
    		// --- Get settings from properties ---------------------
    		Integer samplingInterval = this.opcUaConnector.getConnectorProperties().getIntegerValue(OpcUaConnector.PROP_DATA_MONITORING_SAMPLING_INTERVAL);
    		Integer queueSize = this.opcUaConnector.getConnectorProperties().getIntegerValue(OpcUaConnector.PROP_DATA_MONITORING_QUEUE_SIZE);
    		Boolean isDiscardOldest = this.opcUaConnector.getConnectorProperties().getBooleanValue(OpcUaConnector.PROP_DATA_MONITORING_DISCARD_OLDEST);
    		
    		MonitoringMode monitoringMode = MonitoringMode.valueOf(this.opcUaConnector.getConnectorProperties().getStringValue(OpcUaConnector.PROP_DATA_MONITORING_MODE));
    		
    		
    		NodeId nodeID = uaNode.getNodeId();
    		ReadValueId readValueID = new ReadValueId(nodeID, AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);

    		// --- Create UaSubsription and MonitoringParameters ----
			UaSubscription subscription = this.getUaSubscription();
			UInteger clientHandle = subscription.nextClientHandle();
			MonitoringParameters parameters = new MonitoringParameters(clientHandle, samplingInterval.doubleValue(), null, UInteger.valueOf(queueSize), isDiscardOldest);

			List<MonitoredItemCreateRequest> monitoredItemList = new ArrayList<>();
			monitoredItemList.add(new MonitoredItemCreateRequest(readValueID, monitoringMode, parameters));
			
			// --- Define local callback method ---------------------
			UaSubscription.ItemCreationCallback itemCreationCallback = (item, id) -> item.setValueConsumer(this::onSubscriptionValue);
			
			List<UaMonitoredItem> items = subscription.createMonitoredItems(TimestampsToReturn.Both, monitoredItemList, itemCreationCallback).get();
			for (UaMonitoredItem item : items) {
	            if (item.getStatusCode().isGood()==true) {
	            	if (this.isDebug) System.out.println("[" + this.getClass().getSimpleName() + "] UaMonitoredItem created for nodeId '" + item.getReadValueId().getNodeId() + "'");
	            } else {
	            	System.err.println("[" + this.getClass().getSimpleName() + "] Failed to create item for nodeId '" + item.getReadValueId().getNodeId() + "' => " + item.getStatusCode().toString());
	            }
	        }
						
		} catch (InterruptedException | ExecutionException ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * Removes the specified UaNode from the running monitoring.
	 * @param uaNode the {@link UaNode} to remove
	 */
	private void removeUaNodeFromMonitoring(UaNode uaNode) {
		
		try {
			
			UaSubscription subscription = this.getUaSubscription();
			if (subscription==null) return;
			
			List<UaMonitoredItem> itemsToDelete = new ArrayList<>();
			for (UaMonitoredItem monItem : subscription.getMonitoredItems()) {
				if (monItem.getReadValueId().getNodeId().equals(uaNode.getNodeId())==true) {
					itemsToDelete.add(monItem);
				}
			}
			subscription.deleteMonitoredItems(itemsToDelete);
			
		} catch (InterruptedException | ExecutionException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Returns the current UaSubscription. If required, it will create the UaSubscription.
	 * @return the ua subscription
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	private UaSubscription getUaSubscription() throws InterruptedException, ExecutionException {
		
		UaSubscription subscription = null;
		if (this.subscriptionID==null) {
			// --- Get required configuration from settings -------------------
			Properties props = this.opcUaConnector.getConnectorProperties();
			Double publishingInterval = props.getIntegerValue(OpcUaConnector.PROP_DATA_SUBSCRIPTION_PUBLISHING_INTERVAL).doubleValue();
			UInteger requestedLifetimeCount = UInteger.valueOf(props.getIntegerValue(OpcUaConnector.PROP_DATA_SUBSCRIPTION_LIFE_TIME_COUNT));
	        UInteger requestedMaxKeepAliveCount = UInteger.valueOf(props.getIntegerValue(OpcUaConnector.PROP_DATA_SUBSCRIPTION_MAX_KEEP_ALIVE_COUNT));
	        UInteger maxNotificationsPerPublish = UInteger.valueOf(props.getIntegerValue(OpcUaConnector.PROP_DATA_SUBSCRIPTION_MAX_NOTIFICATIONS_PER_PUBLISH));
	        UByte priority = UByte.valueOf(props.getIntegerValue(OpcUaConnector.PROP_DATA_SUBSCRIPTION_PRIORITY));
			// --- Create subscription ----------------------------------------
			subscription = this.opcUaConnector.getOpcUaClient().getSubscriptionManager().createSubscription(publishingInterval, requestedLifetimeCount, requestedMaxKeepAliveCount, maxNotificationsPerPublish, true, priority).get();
			this.subscriptionID = subscription.getSubscriptionId();
			
		} else {
			for (UaSubscription uaSubscription : this.opcUaConnector.getOpcUaClient().getSubscriptionManager().getSubscriptions()) {
				if (uaSubscription.getSubscriptionId().equals(this.subscriptionID)==true) {
					subscription = uaSubscription;
					break;
				}
			}
		}
		return subscription;
	}
	
	/**
	 * Stops the data acquisition.
	 */
	public void stopDataAcquisition() {
	
		this.opcUaConnector.getOpcUaClient().getSubscriptionManager().deleteSubscription(this.subscriptionID);
		this.subscriptionID = null;
        this.isStartedDataAcquisition = false;
        this.valueHashMap = null;
	}
	
	
	/**
	 * On subscription value.
	 *
	 * @param item the item
	 * @param value the value
	 */
	private void onSubscriptionValue(UaMonitoredItem item, DataValue value) {
		
		// --- Remind as last value in local storage ----------------
		this.getValueHashMap().put(item.getReadValueId().getNodeId().toParseableString(), value);
	
		// --- Inform listener about update -------------------------
		try {
			this.getOpcUaDataAccessSubscriptionListener().forEach(listener -> listener.onSubscriptionValueUpdate(item, value));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    }
	/**
	 * Return the list of OpcUaDataAccessSubscriptionListener.
	 * @return the opc ua data access subscription listener
	 */
	private List<OpcUaDataAccessSubscriptionListener> getOpcUaDataAccessSubscriptionListener() {
		if (listener==null) {
			listener = new ArrayList<>();
		}
		return listener;
	}
	/**
	 * Adds the specified OpcUaDataAccessSubscriptionListener.
	 * @param listener the listener to add
	 */
	public void addOpcUaDataAccessSubscriptionListener(OpcUaDataAccessSubscriptionListener listener) {
		if (this.getOpcUaDataAccessSubscriptionListener().contains(listener)==false) {
			this.getOpcUaDataAccessSubscriptionListener().add(listener);
		}
	}
	/**
	 * Removes the specified OpcUaDataAccessSubscriptionListener.
	 * @param listener the listener to remove
	 */
	public void removeOpcUaDataAccessSubscriptionListener(OpcUaDataAccessSubscriptionListener listener) {
		if (this.getOpcUaDataAccessSubscriptionListener().contains(listener)==true) {
			this.getOpcUaDataAccessSubscriptionListener().remove(listener);
		}
	}

}
