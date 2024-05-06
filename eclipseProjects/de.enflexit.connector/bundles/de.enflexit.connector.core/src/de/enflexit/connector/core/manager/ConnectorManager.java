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
import de.enflexit.common.properties.PropertiesEvent;
import de.enflexit.common.properties.PropertiesListener;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.AbstractConnectorProperties;
import de.enflexit.connector.core.AbstractConnectorProperties.StartOn;
import de.enflexit.connector.core.ConnectorService;

/**
 * The ConnectorManager manages all {@link AbstractConnector}s used within one Java VM..
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorManager implements PropertiesListener {
	
	public static final String CONNECTOR_ADDED = "Connector added";
	public static final String CONNECTOR_REMOVED = "Connector removed";
	public static final String CONNECTOR_RENAMED = "Connector renamed";
	
	private static final String DEFAULT_CONFIG_FILE_NAME = "ConnectorProperties.json";
	
	private static ConnectorManager instance;
	private HashMap<String, AbstractConnector> availableConnectors;
	
	private ArrayList<PropertyChangeListener> listeners;
	
	private boolean configChanged;
	
	private HashMap<String, ConnectorService> availableConnectorServices;
	
	private ConnectorManager() {}
	
	/**
	 * Gets the single instance of ConnectorManager.
	 * @return single instance of ConnectorManager
	 */
	public static ConnectorManager getInstance() {
		if (instance==null) {
			instance = new ConnectorManager();
			instance.loadConfigurationsFromDefaultFile();
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
	public ArrayList<String> getConnectorNames(){
		return new ArrayList<>(this.getAvailableConnectors().keySet());
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
			this.getAvailableConnectors().put(connectorName, connector);
			connector.getConnectorProperties().addPropertiesListener(this);
			PropertyChangeEvent eventAdded = new PropertyChangeEvent(this, CONNECTOR_ADDED, null, connectorName);
			this.notifyListeners(eventAdded);
			this.setConfigChanged(true);
		}
	}
	
	/**
	 * Removes the specified connector.
	 * @param connectorName the connector name
	 * @return true, if successful
	 */
	public boolean removeConnector(String connectorName) {
		AbstractConnector connector = this.getConnectorByName(connectorName);
		
		// --- Connector not found ------------------------
		if (connector==null) return false;
		
		// --- Can't delete active connectors -------------
		if (connector.isConnected()==true) return false;
		
		// --- Remove the connector -----------------------
		this.getAvailableConnectors().remove(connectorName);
		connector.getConnectorProperties().removePropertiesListener(this);
		
		// --- Notify registered listeners ----------------
		PropertyChangeEvent removedEvent = new PropertyChangeEvent(this, CONNECTOR_REMOVED, connectorName, null);
		this.notifyListeners(removedEvent);
		
		this.setConfigChanged(true);
		
		return true;
	}
	
	/**
	 * Handle connector events.
	 * @param connectorEvent the connector event
	 */
	public void onConnectorEvent(ConnectorEvent connectorEvent) {
		//TODO implement reactions on events.
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
		
		ConnectorPropertiesTreeMap connectorConfiguraitons = new ConnectorPropertiesTreeMap();
		for (String connectorName : this.getAvailableConnectors().keySet()) {
			AbstractConnector connector = this.getAvailableConnectors().get(connectorName);
			connectorConfiguraitons.put(connectorName, connector.getConnectorProperties());
		}
		connectorConfiguraitons.storeToJsonFile(jsonFile);
		this.setConfigChanged(false);
	}

	/**
	 * Loads stored configurations from  a JSON file.
	 * @param jsonFile the json file
	 */
	public void loadConfigurationFromJSON(File jsonFile) {
		ConnectorPropertiesTreeMap loadedConfig = ConnectorPropertiesTreeMap.loadFromJsonFile(jsonFile);
		if (loadedConfig!=null) {
			for (String connectorName : loadedConfig.keySet()) {
				Properties loadedPropertiess = loadedConfig.get(connectorName);
				String serviceClassName = loadedPropertiess.getStringValue(AbstractConnectorProperties.PROPERTY_KEY_CONNECTOR_SERVICE_CLASS);
				ConnectorService service = this.getServiceImplementation(loadedPropertiess.getStringValue(AbstractConnectorProperties.PROPERTY_KEY_CONNECTOR_SERVICE_CLASS));
				if (service != null) {
					AbstractConnector connectorInstance = service.getNewConnectorInstance();
					AbstractConnectorProperties properties = service.getInitialProperties();
					properties.addAll(loadedPropertiess);
					properties.addPropertiesListener(this);
					connectorInstance.setConnectorProperties(properties);
					this.addNewConnector(connectorName, connectorInstance);
				} else {
					System.err.println("[" + this.getClass().getSimpleName() + "] Could not find a ConnectorService implementation for " + serviceClassName);
				}
			}
			// -- Was set to true when adding the connectors during loading
			this.setConfigChanged(false);
		}
	}
	
	/**
	 * Gets the {@link ConnectorService} implementation with the specified class name.
	 * @param serviceClassName the service class name
	 * @return the service implementation
	 */
	private ConnectorService getServiceImplementation(String serviceClassName) {
		List<ConnectorService> services = ServiceFinder.findServices(ConnectorService.class);
		for (ConnectorService service : services) {
			if (service.getClass().getName().equals(serviceClassName)) {
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
	 * Loads connector configurations from the default configuration file.
	 */
	public void loadConfigurationsFromDefaultFile() {
		
		File configFile = this.getDefaultConfigFile();
		if (configFile!=null && configFile.exists()) {
			this.loadConfigurationFromJSON(configFile);
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
	public File getDefaultConfigFile() {
		File configFile = null;
		String propertiesFolder = Application.getGlobalInfo().getPathProperty(true);
		if (propertiesFolder!=null && propertiesFolder.isBlank()==false) {
			Path propertiesPath = new File(propertiesFolder).toPath();
			configFile = propertiesPath.resolve(DEFAULT_CONFIG_FILE_NAME).toFile();
		}
		
		return configFile;
	}

	public boolean isConfigChanged() {
		return configChanged;
	}

	public void setConfigChanged(boolean configChanged) {
		this.configChanged = configChanged;
	}

	@Override
	public void onPropertiesEvent(PropertiesEvent propertiesEvent) {
		this.setConfigChanged(true);
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
	 * Gets the connector service for the specified protocol.
	 * @param protocolName the protocol name
	 * @return the connector service
	 */
	public ConnectorService getConnectorServiceForProtocol(String protocolName) {
		return this.getAvailableConnectorServices().get(protocolName);
	}
	
}
