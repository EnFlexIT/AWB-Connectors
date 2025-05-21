package de.enflexit.connector.nymea.rpcClient;

/**
 * This enumeration provides the parameter names, as expected by the nymea/consolinno backend.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public enum RpcParams {
	CLIENT_NAME("clientName"),
	CLIENT_UUID("clientUuid"),
	SERVER_UUID("serverUuid"),
	DEVICE_NAME("deviceName"),
	USER_NAME("username"),
	PASSWORD("password"),
	FROM("from"),
	TO("to"),
	SAMPLE_RATE("sampleRate"),
	THING_IDS("thingIds")
	;
	
	private String name;
	
	private RpcParams(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
