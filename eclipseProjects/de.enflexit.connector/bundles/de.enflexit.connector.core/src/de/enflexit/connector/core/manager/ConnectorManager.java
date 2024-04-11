package de.enflexit.connector.core.manager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import de.enflexit.common.ServiceFinder;
import de.enflexit.common.properties.Properties;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.AbstractConnectorProperties;
import de.enflexit.connector.core.ConnectorService;

/**
 * The ConnectorManager manages all {@link AbstractConnector}s used within one Java VM..
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorManager {
	
	public static final String CONNECTOR_ADDED = "Connector added";
	public static final String CONNECTOR_REMOVED = "Connector removed";
	public static final String CONNECTOR_RENAMED = "Connector renamed";
	
	private static ConnectorManager instance;
	private HashMap<String, AbstractConnector> availableConnectors;
	
	private ArrayList<PropertyChangeListener> listeners;
	
	private ConnectorManager() {}

	/**
	 * Gets the single instance of ConnectorManager.
	 * @return single instance of ConnectorManager
	 */
	public static ConnectorManager getInstance() {
		if (instance==null) {
			instance = new ConnectorManager();
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
	public AbstractConnector getConnector(String connectorName) {
		return this.getAvailableConnectors().get(connectorName);
	}
	
	/**
	 * Adds a new connector.
	 * @param connectorName the connector name
	 * @param connector the connector
	 */
	public void addNewConnector(String connectorName, AbstractConnector connector) {
		if (this.getConnector(connectorName)!=null) {
			throw new IllegalArgumentException("A connector with the name " + connectorName + " already exists!");
		} else {
			this.getAvailableConnectors().put(connectorName, connector);
			PropertyChangeEvent eventAdded = new PropertyChangeEvent(this, CONNECTOR_ADDED, null, connectorName);
			this.notifyListeners(eventAdded);
		}
	}
	
	/**
	 * Removes the specified connector.
	 * @param connectorName the connector name
	 * @return true, if successful
	 */
	public boolean removeConnector(String connectorName) {
		AbstractConnector connector = this.getConnector(connectorName);
		
		// --- Connector not found ------------------------
		if (connector==null) return false;
		
		// --- Can't delete active connectors -------------
		if (connector.isConnected()==true) return false;
		
		// --- Remove the connector -----------------------
		this.getAvailableConnectors().remove(connectorName);
		
		// --- Notify registered listeners ----------------
		PropertyChangeEvent removedEvent = new PropertyChangeEvent(this, CONNECTOR_REMOVED, connectorName, null);
		this.notifyListeners(removedEvent);
		
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
		for (PropertyChangeListener listener : listeners) {
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
					connectorInstance.setConnectorProperties(properties);
					this.addNewConnector(connectorName, connectorInstance);
				} else {
					System.err.println("[" + this.getClass().getSimpleName() + "] Could not find a ConnectorService implementation for " + serviceClassName);
				}
			}
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
	
	
}
