package de.enflexit.connector.core.manager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import agentgui.core.application.Application;
import de.enflexit.common.ServiceFinder;
import de.enflexit.common.properties.Properties;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.AbstractConnector.StartOn;
import de.enflexit.connector.core.ConnectorService;

/**
 * The ConnectorManager manages all {@link AbstractConnector}s used within one Java VM..
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorManager {
	
	public static final String CONNECTOR_ADDED = "Connector added";
	public static final String CONNECTOR_REMOVED = "Connector removed";
	public static final String CONNECTOR_RENAMED = "Connector renamed";
	
	public static final String CONNECTOR_SETTINGS_SAVED = "Connector settings saved";
	
	private static final String DEFAULT_CONFIG_FILE_NAME = "ConnectorProperties.json";
	
	private HashMap<String, ConnectorService> availableConnectorServices;
	private ConnectorPropertiesTreeMap connectorPropertiesTreeMap; 
	private HashMap<String, AbstractConnector> availableConnectors;
	
	private ArrayList<PropertyChangeListener> listeners;

	private StartOn currentStartOnLevel;
	
	private boolean debug = false;
	
	
	private static ConnectorManager instance;

	/**
	 * Instantiates a new connector manager.
	 */
	private ConnectorManager() {}
	/**
	 * Gets the single instance of ConnectorManager.
	 * @return single instance of ConnectorManager
	 */
	public static ConnectorManager getInstance() {
		if (instance==null) {
			instance = new ConnectorManager();
			instance.loadConfigurationsFromDefaultFile();
			instance.currentStartOnLevel = StartOn.AwbStart;
		}
		return instance;
	}
	
	/**
	 * Gets the available connectors.
	 * @return the available connectors
	 */
	private HashMap<String, AbstractConnector> getAvailableConnectors() {
		if (availableConnectors==null) {
			availableConnectors = new HashMap<>();
		}
		return availableConnectors;
	}
	
	/**
	 * Gets the names of all available connectors.
	 * @return the connector names
	 */
	public ArrayList<String> getConfiguredConnectorNames(){
		return new ArrayList<>(this.getConnectorPropertiesTreeMap().keySet());
	}
	
	/**
	 * Gets the connector with the specified name.
	 * @param connectorName the connector name
	 * @return the connector
	 */
	public AbstractConnector getConnectorByName(String connectorName) {
		return this.getAvailableConnectors().get(connectorName);
	}
	
	/**
	 * Gets the connector with the specified host and protocol, if there is one.
	 * @param host the host
	 * @param protocol the protocol
	 * @return the connector, or null if not found
	 */
	public AbstractConnector getConnectorByHostAndProtocol(String host, String protocol) {
		for (AbstractConnector connector : this.getAvailableConnectors().values()) {
			if (connector.getProtocolName().equals(protocol) && connector.getConnectorProperties().getStringValue(AbstractConnector.PROPERTY_KEY_SERVER_HOST).equals(host)) {
				return connector;
			}
		}
		return null;
	}
	
	/**
	 * Adds a new connector.
	 * @param connectorName the connector name
	 * @param connector the connector
	 */
	public void addNewConnector(String connectorName, AbstractConnector connector) {
		if (this.getConnectorByName(connectorName)!=null) {
			throw new IllegalArgumentException("A connector with the name " + connectorName + " already exists!");
		} else {
			this.getConnectorPropertiesTreeMap().put(connectorName, connector.getConnectorProperties());
			this.getAvailableConnectors().put(connectorName, connector);
			PropertyChangeEvent eventAdded = new PropertyChangeEvent(this, CONNECTOR_ADDED, null, connectorName);
			this.notifyListeners(eventAdded);
			this.saveConfigurationsToDefaultFile();
		}
	}
	
	/**
	 * Removes the specified connector.
	 * @param connectorName the connector name
	 * @return true, if successful
	 */
	public boolean removeConnector(String connectorName) {
		
		AbstractConnector connector = this.getConnectorByName(connectorName);
		
		// --- Can't delete active connectors -------------
		if (connector!=null && connector.isConnected()==true) return false;
		
		// --- Remove the connector -----------------------
		this.getAvailableConnectors().remove(connectorName);
		this.getConnectorPropertiesTreeMap().remove(connectorName);
		
		// --- Notify registered listeners ----------------
		PropertyChangeEvent removedEvent = new PropertyChangeEvent(this, CONNECTOR_REMOVED, connectorName, null);
		this.notifyListeners(removedEvent);
		
		this.saveConfigurationsToDefaultFile();
		
		return true;
	}
	
	/**
	 * Gets the registered {@link PropertyChangeListener}s.
	 * @return the listeners
	 */
	private  ArrayList<PropertyChangeListener> getListeners() {
		if (listeners==null) {
			listeners = new ArrayList<>();
		}
		return listeners;
	}
	/**
	 * Adds the provided listener.
	 * @param listener the listener
	 */
	public void addListener(PropertyChangeListener listener) {
		if (this.getListeners().contains(listener)==false) {
			this.getListeners().add(listener);
		}
	}
	/**
	 * Removes the specified listener.
	 * @param listener the listener
	 */
	public void removeListener(PropertyChangeListener listener) {
		if (this.getListeners().contains(listener)==true) {
			this.getListeners().remove(listener);
		}
	}
	/**
	 * Notifies the registered listener about an event.
	 * @param event the event
	 */
	private void notifyListeners(PropertyChangeEvent event) {
		for (PropertyChangeListener listener : this.getListeners()) {
			listener.propertyChange(event);
		}
	}
	
	/**
	 * Stores the current configuration to a JSON file.
	 * @param jsonFile the json file
	 */
	public void storeConfigurationToJSON(File jsonFile) {
		this.connectorPropertiesTreeMap.storeToJsonFile(jsonFile);
	}
	
	private void loadConfigurationsFromDefaultFile() {
		this.loadConfigurationFromJSON(this.getDefaultConfigFile());
	}

	/**
	 * Loads stored configurations from  a JSON file.
	 * @param jsonFile the json file
	 */
	public void loadConfigurationFromJSON(File jsonFile) {
		this.connectorPropertiesTreeMap = ConnectorPropertiesTreeMap.loadFromJsonFile(jsonFile);
		for (String connectorName : this.getConnectorPropertiesTreeMap().keySet()) {
			AbstractConnector connectorInstance = this.instantiateConnector(connectorName);
			if (connectorInstance!=null) {
				this.getAvailableConnectors().put(connectorName, connectorInstance);
			}
		}
	}
	/**
	 * Instantiate the specified connector.
	 * @param connectorName the connector name
	 * @return the abstract connector
	 */
	private AbstractConnector instantiateConnector(String connectorName) {
		
		AbstractConnector connectorInstance = null;
		Properties connectorProperties = this.getConnectorPropertiesTreeMap().get(connectorName);
		if (connectorProperties!=null) {
			String protocolName = connectorProperties.getStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_PROTOCOL);
			ConnectorService connectorService = this.getConnectorServiceForProtocol(protocolName);
			if (connectorService != null) {
				connectorInstance = connectorService.getNewConnectorInstance();
				connectorInstance.setConnectorProperties(connectorProperties);
				this.getAvailableConnectors().put(connectorName, connectorInstance);
			} else {
				this.debugPrint("No ConnectorService implementation found for " + protocolName);
			}
		} else {
			this.debugPrint("No configuration found for connector name " + connectorName);
		}
		return connectorInstance;
	}
	
	/**
	 * Gets the connector service for the specified protocol.
	 * @param protocol the protocol
	 * @return the connector service, null if not found
	 */
	public ConnectorService getConnectorServiceForProtocol(String protocol) {
		List<ConnectorService> services = ServiceFinder.findServices(ConnectorService.class);
		for (ConnectorService service : services) {
			if (service.getProtocolName().equals(protocol)) {
				return service;
			}
		}
		return null;
	}
	
	/**
	 * Start connections with start level.
	 * @param startOn the start on
	 */
	public void startConnectionsWithStartLevel(StartOn startOn) {
		
		this.debugPrint("Starting all connectors with start level " + startOn);
		for (String connectorName : this.getAvailableConnectors().keySet()) {
			AbstractConnector connector = this.getAvailableConnectors().get(connectorName);
			if (connector.isConnected()==false && connector.getStartOn()==startOn) {
				boolean success = connector.connect();
				if (success==false) {
					System.err.println("[" + this.getClass().getSimpleName() + "] Error connecting " + connectorName + " at " + startOn);
				}
			}
		}
	}
	
	/**
	 * Stops all connectors that are currently connected.
	 */
	protected void stopAllConnectors() {
		for (AbstractConnector connector : this.getAvailableConnectors().values()) {
			if (connector.isConnected()==true) {
				connector.disconnect();
			}
		}
	}
	
	/**
	 * Saves connector configurations to the default configuration file.
	 */
	public void saveConfigurationsToDefaultFile() {

		File configFile = this.getDefaultConfigFile();
		if (configFile!=null) {
			this.storeConfigurationToJSON(configFile);
			this.notifyListeners(new PropertyChangeEvent(this, CONNECTOR_SETTINGS_SAVED, null, null));
		}
	}
	
	/**
	 * Gets the default config file.
	 * @return the default config file
	 */
	private File getDefaultConfigFile() {
		File configFile = null;
		Path propertiesPath = Application.getGlobalInfo().getPathProperty(true);
		configFile = propertiesPath.resolve(DEFAULT_CONFIG_FILE_NAME).toFile();
		return configFile;
	}

	/**
	 * Gets the available connector services.
	 * @return the available connector services
	 */
	public HashMap<String, ConnectorService> getAvailableConnectorServices() {
		if (availableConnectorServices==null) {
			availableConnectorServices = new HashMap<>();
			List<ConnectorService> services = ServiceFinder.findServices(ConnectorService.class);
			for (ConnectorService service : services) {
				availableConnectorServices.put(service.getProtocolName(), service);
			}
		}
		return availableConnectorServices;
	}
	
	/**
	 * This method is called when a new connector is added.
	 * @param connectorService the connector service
	 */
	public void newConnectorServiceAdded(ConnectorService connectorService) {
		
		this.debugPrint("A new connector service for " + connectorService.getProtocolName() + " was added.");
		this.debugPrint("Current startlevel is " + this.currentStartOnLevel);
		
		ArrayList<Properties> connectorsForProtocol = this.getConnectorPropertiesByProtocol(connectorService.getProtocolName());
		for (Properties connProperties : connectorsForProtocol) {
			
			String conectorName = connProperties.getStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_NAME);
			AbstractConnector connector = this.instantiateConnector(conectorName);
			
			StartOn startOn = StartOn.valueOf(connProperties.getStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_START_ON));
			if (startOn.ordinal()<=this.currentStartOnLevel.ordinal()) {
				if (this.debug==true) {
					this.debugPrint("Starting connector " + conectorName + " with start level " + startOn);
				}
				connector.connect();
			} else {
				this.debugPrint("Skipping " + conectorName + " due to higher start level " + startOn);
			}
		}
		
	}
	
	/**
	 * Gets the configured connectors.
	 * @return the configured connectors
	 */
	private ConnectorPropertiesTreeMap getConnectorPropertiesTreeMap() {
		if (connectorPropertiesTreeMap==null) {
			connectorPropertiesTreeMap = new ConnectorPropertiesTreeMap();
		}
		return connectorPropertiesTreeMap;
	}
	
	/**
	 * Gets the properties for the specified connector.
	 * @param connectorName the connector name
	 * @return the connector properties
	 */
	public Properties getConnectorProperties(String connectorName) {
		return this.getConnectorPropertiesTreeMap().get(connectorName);
	}
	
	/**
	 * Updates the properties for the specified connector.
	 * @param connectorName the connector name
	 * @param newProperties the new properties
	 */
	public void updateConnectorProperties(String connectorName, Properties newProperties) {
		
		String newConnectorName = newProperties.getStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_NAME);
		this.getConnectorPropertiesTreeMap().put(newConnectorName, newProperties);
		if (newConnectorName.equals(connectorName)==false) {
			AbstractConnector connectorInstance = this.getAvailableConnectors().remove(connectorName);
			if (connectorInstance!=null) {
				this.getAvailableConnectors().put(newConnectorName, connectorInstance);
			}
			this.getConnectorPropertiesTreeMap().remove(connectorName);
			PropertyChangeEvent connectorRenamedEvent = new PropertyChangeEvent(this, CONNECTOR_RENAMED, connectorName, newConnectorName);
			this.notifyListeners(connectorRenamedEvent);
		}
		this.saveConfigurationsToDefaultFile();
	}
	
	/**
	 * Gets all configured connectors with the specified protocol.
	 * @param protocolName the protocol name
	 * @return the connectors by protocol
	 */
	private ArrayList<Properties> getConnectorPropertiesByProtocol(String protocolName){
		
		ArrayList<Properties> foundConnectors = new ArrayList<>();
		for (Properties connectorProperties : this.getConnectorPropertiesTreeMap().values()) {
			String protocol = connectorProperties.getStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_PROTOCOL);
			if (protocol!=null && protocol.equals(protocolName)) {
				foundConnectors.add(connectorProperties);
			}
		}
		return foundConnectors;
	}
	
	/**
	 * Gets all available connectors for the specified protocol.
	 * @param protocolName the protocol name
	 * @return the connectors by protocol
	 */
	public ArrayList<AbstractConnector> getConnectorsByProtocol(String protocolName){

		ArrayList<AbstractConnector> connectorsList = new ArrayList<>();
		for (AbstractConnector connector : this.getAvailableConnectors().values()) {
			if (connector.getProtocolName().equals(protocolName)) {
				connectorsList.add(connector);
			}
		}
		return connectorsList;
	}

	/**
	 * Sets the current start on level.
	 * @param newStartOnLevel the new current start on level
	 */
	public void setCurrentStartOnLevel(StartOn newStartOnLevel) {
		
		if (newStartOnLevel.ordinal()>this.currentStartOnLevel.ordinal()) {
			this.startConnectionsWithStartLevel(newStartOnLevel);
		}
		this.currentStartOnLevel = newStartOnLevel;
	}
	
	/**
	 * Prints the provided message if debug is set to true
	 * @param message the message
	 */
	private void debugPrint(String message) {
		if (this.debug==true) {
			System.out.println("[" + this.getClass().getSimpleName() + "] " + message);
		}
	}

}
