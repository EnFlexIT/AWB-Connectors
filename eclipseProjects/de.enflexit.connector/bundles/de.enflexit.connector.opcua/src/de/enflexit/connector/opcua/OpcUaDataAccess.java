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
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;

import de.enflexit.common.properties.Properties;

/**
 * The Class OpcUaDataAccess.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaDataAccess {

	public static final String DATA_NODE_ID_KEY_PREFIX = "Data.NodeID.";
	
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
			this.updateNodeIdListOrdered();
		}
		return nodeIdListOrdered;
	}
	/**
	 * Updates the ordered ID list .
	 */
	private void updateNodeIdListOrdered() {

		// --- Clear ordered list first -----------------------------
		this.getNodeIdListOrdered().clear();
		
		// --- Extract list position -------------------------------- 
		TreeMap<Integer, String> sortingTreeMap = new TreeMap<>();
		Properties props = this.opcUaConnector.getConnectorProperties();
		for (String propKey : props.getIdentifierList()) {
			if (propKey.startsWith(DATA_NODE_ID_KEY_PREFIX)==true) {
				String ordinalPosString = propKey.substring(DATA_NODE_ID_KEY_PREFIX.length());
				Integer ordinalPos = Integer.parseInt(ordinalPosString);
				String mnodeID = props.getStringValue(propKey);
				sortingTreeMap.put(ordinalPos, mnodeID);
			}
		}
		
		// --- Refill ordered list according to property keys -------
		for (Integer ordinal : sortingTreeMap.keySet()) {
			String nodeID = sortingTreeMap.get(ordinal);
			this.getNodeIdListOrdered().add(nodeID);
		}
	}
	
	/**
	 * Rearrange the node IDs in the connector properties.
	 */
	private void rearrangeNodeIDsInProperties() {
		
		List<String> dataNodeIdList = new ArrayList<>();
		
		// --- Find current ID keys -----------------------
		Properties props = this.opcUaConnector.getConnectorProperties();
		for (String propKey : props.getIdentifierList()) {
			if (propKey.startsWith(DATA_NODE_ID_KEY_PREFIX)==true) {
				dataNodeIdList.add(propKey);
			}
		}
		
		// --- Remove current ID keys ---------------------
		dataNodeIdList.forEach(dataNodeIdKey -> props.remove(dataNodeIdKey));
		
		// --- Add the new ID keys and their value --------
		for (int i = 0; i < this.getNodeIdListOrdered().size(); i++) {
			String propKey = DATA_NODE_ID_KEY_PREFIX + (i+1);
			String propValue = this.getNodeIdListOrdered().get(i);
			props.setStringValue(propKey, propValue);
		}  
		
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
		String parseableID = uaNodeToAdd.getNodeId().toParseableString();
		if (this.getNodeIdListOrdered().contains(parseableID)==true) {
			if (this.isDebug==true) {
				System.err.println("[" + this.getClass().getSimpleName() + "] The Node with the ID '" + parseableID + "' is already monitored.");
			}
			return;
		}
		
		// --- Define this value in the properties --------
		Integer newPos = this.getNodeIdListOrdered().size() + 1;
		this.opcUaConnector.getConnectorProperties().setStringValue(DATA_NODE_ID_KEY_PREFIX + newPos, parseableID);
		this.updateNodeIdListOrdered();
		
		// --- Dynamically add to monitored values --------
		if (this.isStartedDataAcquisition==true) {
			this.addUaNodeToMonitoring(uaNodeToAdd);
		}
	}
	
	/**
	 * Removes the specified UaNode.
	 * @param uaNodeToRemove the ua node to remove
	 */
	public void removeOpcUaNode(UaNode uaNodeToRemove) {

		if (uaNodeToRemove==null) return;
		if (this.isDebug==true) System.out.println("Remove UaNode " + uaNodeToRemove.getDisplayName());
		
		// --- Find & remove corresponding property -------
		String nodeIDString = uaNodeToRemove.getNodeId().toParseableString();
		if (this.getNodeIdListOrdered().remove(nodeIDString) == false) return;
		this.getValueHashMap().remove(nodeIDString);
		
		// --- Rearrange order of nodeID counter ----------
		this.rearrangeNodeIDsInProperties();
		
		// --- Dynamically remove from monitored values ---
		if (this.isStartedDataAcquisition==true) {
			this.removeUaNodeFromMonitoring(uaNodeToRemove);
		}
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
        	
        	// --- Create list of MonitoredItemCreateRequest --------
        	UInteger clientHandle = null;
			List<MonitoredItemCreateRequest> monitoredItemList = new ArrayList<>();
        	for (ReadValueId readValueID : readValueIdList) {
        		
        		if (clientHandle==null) {
        			clientHandle = subscription.nextClientHandle();
        		} else {
        			clientHandle = UInteger.valueOf(clientHandle.intValue() + 1);
        		}
        		MonitoringParameters parameters = new MonitoringParameters(clientHandle, 1000.0, null, UInteger.valueOf(10), true);
        		monitoredItemList.add(new MonitoredItemCreateRequest(readValueID, MonitoringMode.Reporting, parameters));
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
    		
    		NodeId nodeID = uaNode.getNodeId();
    		ReadValueId readValueID = new ReadValueId(nodeID, AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);

    		// --- Create UaSubsription and MonitoringParameters ----
			UaSubscription subscription = this.getUaSubscription();
			UInteger clientHandle = subscription.nextClientHandle();
			MonitoringParameters parameters = new MonitoringParameters(clientHandle, 1000.0, null, UInteger.valueOf(10), true);

			List<MonitoredItemCreateRequest> monitoredItemList = new ArrayList<>();
			monitoredItemList.add(new MonitoredItemCreateRequest(readValueID, MonitoringMode.Reporting, parameters));
			
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
			subscription = this.opcUaConnector.getOpcUaClient().getSubscriptionManager().createSubscription(1000.0).get();
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
