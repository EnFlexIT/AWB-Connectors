package de.enflexit.connector.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

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
	 * Handle connector events.
	 * @param connectorEvent the connector event
	 */
	protected void onConnectorEvent(ConnectorEvent connectorEvent) {
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
	
	private void notifyListeners(PropertyChangeEvent event) {
		for (PropertyChangeListener listener : listeners) {
			listener.propertyChange(event);
		}
	}
	
}
