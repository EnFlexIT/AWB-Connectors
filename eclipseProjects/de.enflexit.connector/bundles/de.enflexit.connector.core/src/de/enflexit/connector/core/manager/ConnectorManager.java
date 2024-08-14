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
import de.enflexit.connector.core.AbstractConnectorConfiguration;
import de.enflexit.connector.core.AbstractConnectorConfiguration.StartOn;
import de.enflexit.connector.core.ConnectorService;

/**
 * The ConnectorManager manages all {@link AbstractConnector}s used within one Java VM..
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorManager {
	
	public static final String CONNECTOR_ADDED = "Connector added";
	public static final String CONNECTOR_REMOVED = "Connector removed";
	public static final String CONNECTOR_RENAMED = "Connector renamed";
	
	private static final String DEFAULT_CONFIG_FILE_NAME = "ConnectorProperties.json";
	
	private static ConnectorManager instance;
	private HashMap<String, AbstractConnector> availableConnectors;
	
	private ArrayList<PropertyChangeListener> listeners;
	
	private HashMap<String, ConnectorService> availableConnectorServices;
	
	private ConnectorPropertiesTreeMap configuredConnectors; 
	
	private StartOn currentStartOnLevel;
	
	private boolean debug = false;
	
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
		return new ArrayList<>(this.getConfiguredConnectors().keySet());
	}
	
	/**
	 * Gets the connector with the specified name.
	 * @param connectorName the connector name
	 * @return the connector
	 */
	public AbstractConnector getConnectorByName(String connectorName) {
		AbstractConnector connector = this.getAvailableConnectors().get(connectorName);
		return connector;
	}
	
	/**
	 * Gets the connector with the specified host and protocol, if there is one.
	 * @param host the host
	 * @param protocol the protocol
	 * @return the connector, or null if not found
	 */
	public AbstractConnector getConnectorByHostAndProtocol(String host, String protocol) {
		for (AbstractConnector connector : this.getAvailableConnectors().values()) {
			if (connector.getProtocolName().equals(protocol) && connector.getConnectorConfiguration().getUrlOrIP().equals(host)) {
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
			this.getConfiguredConnectors().put(connectorName, connector.getConnectorProperties());
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
		this.getConfiguredConnectors().remove(connectorName);
		
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
		this.configuredConnectors.storeToJsonFile(jsonFile);
	}
	
	private void loadConfigurationsFromDefaultFile() {
		this.loadConfigurationFromJSON(this.getDefaultConfigFile());
	}

	/**
	 * Loads stored configurations from  a JSON file.
	 * @param jsonFile the json file
	 */
	public void loadConfigurationFromJSON(File jsonFile) {
		this.configuredConnectors = ConnectorPropertiesTreeMap.loadFromJsonFile(jsonFile);
		for (String connectorName : this.getConfiguredConnectors().keySet()) {
			AbstractConnector connectorInstance = this.instantiateConnector(connectorName);
			if (connectorInstance!=null) {
				this.getAvailableConnectors().put(connectorName, connectorInstance);
			}
		}
		
	}
	
	/**
	 * Instantiate connector.
	 * @param connectorName the connector name
	 * @return the abstract connector
	 */
	private AbstractConnector instantiateConnector(String connectorName) {
		AbstractConnector connectorInstance = null;
		Properties connectorProperties = this.getConfiguredConnectors().get(connectorName);
		if (connectorProperties!=null) {
			String protocolName = connectorProperties.getStringValue(AbstractConnectorConfiguration.PROPERTY_KEY__CONNECTOR_PROTOCOL);
			ConnectorService service = this.getConnectorServiceForProtocol(protocolName);
			if (service != null) {
				connectorInstance = service.getNewConnectorInstance();
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
	
	public void newConnectorServiceAdded(ConnectorService connectorService) {
		
		this.debugPrint("A new connector service for " + connectorService.getProtocolName() + " was added.");
		this.debugPrint("Current startlevel is " + this.currentStartOnLevel);
		
		ArrayList<Properties> connectorsForProtocol = this.getConnectorsByProtocol(connectorService.getProtocolName());
		for (Properties connProperties : connectorsForProtocol) {
			
			String conectorName = connProperties.getStringValue(AbstractConnectorConfiguration.PROPERTY_KEY_CONNECTOR_NAME);
			AbstractConnector connector = this.instantiateConnector(conectorName);
			
			StartOn startOn = StartOn.valueOf(connProperties.getStringValue(AbstractConnectorConfiguration.PROPERTY_KEY_CONNECTOR_START_ON));
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
	private ConnectorPropertiesTreeMap getConfiguredConnectors() {
		if (configuredConnectors==null) {
			configuredConnectors = new ConnectorPropertiesTreeMap();
		}
		return configuredConnectors;
	}
	
	/**
	 * Gets the properties for the specified connector.
	 * @param connectorName the connector name
	 * @return the connector properies
	 */
	public Properties getConnectorProperies(String connectorName) {
		return this.getConfiguredConnectors().get(connectorName);
	}
	
	/**
	 * Updates the properties for the specified connector.
	 * @param connectorName the connector name
	 * @param newProperties the new properties
	 */
	public void updateConnectorProperties(String connectorName, Properties newProperties) {
		this.getConfiguredConnectors().put(connectorName, newProperties);
		this.saveConfigurationsToDefaultFile();
	}
	
	/**
	 * Gets all configured connectors with the specified protocol.
	 * @param protocolName the protocol name
	 * @return the connectors by protocol
	 */
	private ArrayList<Properties> getConnectorsByProtocol(String protocolName){
		ArrayList<Properties> foundConnectors = new ArrayList<>();
		for (Properties connectorProperties : this.getConfiguredConnectors().values()) {
			if (connectorProperties.getStringValue(AbstractConnectorConfiguration.PROPERTY_KEY__CONNECTOR_PROTOCOL).equals(protocolName)) {
				foundConnectors.add(connectorProperties);
			}
		}
		return foundConnectors;
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
