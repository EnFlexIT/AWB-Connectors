package de.enflexit.connector.opcua;

/**
 * Listener interface for an {@link OpcUaConnector}.
 */
public interface OpcUaConnectorListener {

	public enum Event {
		Connect,
		Disconnect,
		SessionInactive,
		SessionActive,
		BrowserUaNodeSelection
	}
	
	
	/**
	 * This event will be invoked if the OPC/UA connect method of the {@link OpcUaConnector} was called. 
	 * To check if a connection was established successfully, check {@link OpcUaConnector#isConnected()}.
	 */
	public void onConnection();
	
	/**
	 * This event will be invoked if the OPC/UA disconnect method of the {@link OpcUaConnector} was called. 
	 */
	public void onDisconnection();
	
	/**
	 * This event will be invoked if the OPC/UA connection was reactivated (e.g. a server comes back to life). 
	 */
	public void onSessionActive();
	
	/**
	 * This event will be invoked if the OPC/UA connection gets lost (e.g. a server takes down or network connection gets lost). 
	 */
	public void onSessionInactive();

	/**
	 * Will be invoked on an UaNode selection. Use the {@link OpcUaConnector#getBrowserUaNode()} to determine the 
	 * currently selected UaNode.
	 */
	public void onBrowserUaNodeSelection();
	
}
