package de.enflexit.connector.nymea;

import de.enflexit.common.properties.Properties;
import de.enflexit.common.properties.PropertiesEvent;
import de.enflexit.common.properties.PropertiesListener;
import de.enflexit.connector.core.AbstractConnector;
// TODO: Auto-generated Javadoc
/**
 * This class contains the required settings to configure a consolinno/nymea connection.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class NymeaConnectorSettings implements PropertiesListener {
	
	private String serverHost;
	private int serverPort;
	private String userName;
	private String password;
	private String authToken;
	private String clientUUID;
	private String clientName;
	private String serverUUID;

	private Properties properties;
	
	public NymeaConnectorSettings(Properties properties) {
		this.properties = properties;
		this.initializeFromProperties(properties);
		this.properties.addPropertiesListener(this);
	}
	
	private void initializeFromProperties(Properties properties) {
		String serverFromProps = properties.getStringValue(AbstractConnector.PROPERTY_KEY_SERVER_HOST);
		if (serverFromProps!=null) {
			this.setServHost(serverFromProps);
		}
		Integer portFromProps = properties.getIntegerValue(AbstractConnector.PROPERTY_KEY_SERVER_PORT);
		if (portFromProps!=null) {
			this.setServerPort(portFromProps);
		}
		String userNameFromProps = properties.getStringValue(NymeaConnector.PROPERTY_KEY_NYMEA_USERNAME);
		if (userNameFromProps!=null) {
			this.setUserName(userNameFromProps);
		}
		String passwordFromProps = properties.getStringValue(NymeaConnector.PROPERTY_KEY_NYMEA_PASSWORD);
		if (passwordFromProps!=null) {
			this.setPassword(passwordFromProps);
		}
		String tokenFromProps = properties.getStringValue(NymeaConnector.PROPERTY_KEY_NYMEA_AUTH_TOKEN);
		if (tokenFromProps!=null) {
			this.setAuthToken(tokenFromProps);
		}
		String clientUuidFromProps = properties.getStringValue(NymeaConnector.PROPERTY_KEY_NYMEA_CLIENT_UUID);
		if (clientUuidFromProps!=null) {
			this.setClientUUID(clientUuidFromProps);
		}
		String clientNameFromProps = properties.getStringValue(NymeaConnector.PROPERTY_KEY_NYMEA_CLIENT_NAME);
		if (clientNameFromProps!=null) {
			this.setClientName(clientNameFromProps);
		}
		String serverUuidFromProps = properties.getStringValue(NymeaConnector.PROPERTY_KEY_NYMEA_SERVER_UUID);
		if (serverUuidFromProps!=null) {
			this.setServerUUID(serverUuidFromProps);
		}
	}
	
	/**
	 * Gets the server URL.
	 * @return the server URL
	 */
	public String getServerHost() {
		return serverHost;
	}
	/**
	 * Sets the server URL.
	 * @param serverHost the new server URL
	 */
	public void setServHost(String serverHost) {
		this.serverHost = serverHost;
	}
	
	/**
	 * Gets the server port.
	 * @return the server port
	 */
	public int getServerPort() {
		return serverPort;
	}
	/**
	 * Sets the server port.
	 * @param serverPort the new server port
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	
	/**
	 * Gets the user name.
	 * @return the user name
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * Sets the user name.
	 * @param userName the new user name
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * Gets the password.
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * Sets the password.
	 * @param password the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Gets the authentication token.
	 * @return the authentication token
	 */
	public String getAuthToken() {
		return authToken;
	}
	/**
	 * Sets the authentication token.
	 * @param authToken the new authentication token
	 */
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	
	/**
	 * Gets the client UUID.
	 * @return the client UUID
	 */
	public String getClientUUID() {
		return clientUUID;
	}
	/**
	 * Sets the client UUID.
	 * @param clientUUID the new client UUID
	 */
	public void setClientUUID(String clientUUID) {
		this.clientUUID = clientUUID;
	}
	
	/**
	 * Gets the client name.
	 * @return the client name
	 */
	public String getClientName() {
		return clientName;
	}
	/**
	 * Sets the client name.
	 * @param clientName the new client name
	 */
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	/**
	 * Gets the server UUID.
	 * @return the server UUID
	 */
	public String getServerUUID() {
		return serverUUID;
	}
	/**
	 * Sets the server UUID.
	 * @param serverUUID the new server UUID
	 */
	public void setServerUUID(String serverUUID) {
		this.serverUUID = serverUUID;
	}
	
	/**
	 * Creates a new  {@link Properties} instance, that contains the settings of the current {@link NymeaConnectorSettings} object.
	 * @return the properties
	 */
	public Properties toProperties() {
		Properties properties = new Properties();
		//TODO set properties from the current instance
		return properties;
	}
	@Override
	public void onPropertiesEvent(PropertiesEvent propertiesEvent) {
		System.out.println("PropertiesEvent");
	}
	
	
}
