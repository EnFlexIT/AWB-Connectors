package de.enflexit.connector.nymea;

import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import de.enflexit.common.properties.Properties;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.nymea.rpcClient.NymeaRpcClient;
import de.enflexit.connector.nymea.ui.IntrospectionPanel;
import de.enflexit.connector.nymea.ui.BrowseThingsPanel;
import de.enflexit.connector.nymea.ui.ExecuteMethodsPanel;

/**
 * This class implements a nymea connector, as used by Consolinno's Leaflet HEMS.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class NymeaConnector extends AbstractConnector {
	
	public static final String PROTOCOL_NAME = "Nymea";
	
	public static final String PROPERTY_KEY_NYMEA_USERNAME = "nymea.auth.username";
	public static final String PROPERTY_KEY_NYMEA_PASSWORD = "nymea.auth.password";
	public static final String PROPERTY_KEY_NYMEA_AUTH_TOKEN = "nymea.auth.token";
	
	public static final String PROPERTY_KEY_NYMEA_CLIENT_UUID = "nymea.clientUUID";
	public static final String PROPERTY_KEY_NYMEA_CLIENT_NAME = "nymea.clientName";
	public static final String PROPERTY_KEY_NYMEA_SERVER_UUID = "nymea.serverUUID";
	
	private NymeaConnectorSettings connectorSettings;
	private NymeaRpcClient nymeaClient;
	
	private HashMap<String, Object> introspectionData;
	
	private IntrospectionPanel introspectionPanel;
	private BrowseThingsPanel browseThingsPanel;
	private ExecuteMethodsPanel executeMethodsPanel;
	
	private boolean connected;

	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#getProtocolName()
	 */
	@Override
	public String getProtocolName() {
		return PROTOCOL_NAME;
	}

	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#getInitialProperties()
	 */
	@Override
	public Properties getInitialProperties() {
		Properties properties = new Properties();
		properties.setStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_PROTOCOL, PROTOCOL_NAME);
		properties.setStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_START_ON, AbstractConnector.StartOn.ManualStart.toString());
		
		// --- No useful defaults available, but adding empty entries  to show what can/should be configured
		properties.setStringValue(AbstractConnector.PROPERTY_KEY_SERVER_HOST, "hems-remoteproxy.services.consolinno.de");
		properties.setIntegerValue(AbstractConnector.PROPERTY_KEY_SERVER_PORT, 2213);
		
		properties.setStringValue(NymeaConnector.PROPERTY_KEY_NYMEA_USERNAME, "");
		properties.setStringValue(NymeaConnector.PROPERTY_KEY_NYMEA_PASSWORD, "");
		properties.setStringValue(NymeaConnector.PROPERTY_KEY_NYMEA_CLIENT_UUID, "4739d245-3768-4cab-b4a5-b48af19d70da");
		properties.setStringValue(NymeaConnector.PROPERTY_KEY_NYMEA_CLIENT_NAME, "Java-Client UDE");
		properties.setStringValue(NymeaConnector.PROPERTY_KEY_NYMEA_SERVER_UUID, "");
		
		return properties;
	}

	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#connect()
	 */
	@Override
	public boolean connect() {
		// --- Establish the connection -----------------------------
		this.connected = this.getNymeaClient().openConnection();
		if (this.connected) {
			
			// --- Authenticate -------------------------------------
			if (this.getNymeaClient().isAuthenticated()==false) {
				this.getNymeaClient().authenticateUser();
			}
			
			// --- CLose connection if authentication failed -------- 
			if (this.getNymeaClient().isAuthenticated()==false) {
				this.disconnect();
			}
		}
		return this.connected;
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#isConnected()
	 */
	@Override
	public boolean isConnected() {
		return this.connected;
	}

	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#disconnect()
	 */
	@Override
	public void disconnect() {
		this.getNymeaClient().closeConnection();
		this.connected = false;
	}
	
	/**
	 * Gets the connector settings.
	 * @return the connector settings
	 */
	public NymeaConnectorSettings getConnectorSettings() {
		if (connectorSettings==null) {
			// --- Initialize with the connector properties ---------
			connectorSettings = new NymeaConnectorSettings(this.getConnectorProperties());
		}
		return connectorSettings;
	}
	
	/**
	 * Gets the introspection data.
	 * @return the introspection data
	 */
	public HashMap<String, Object> getIntrospectionData() {
		if (introspectionData==null && this.isConnected()) {
			introspectionData = this.getNymeaClient().sendIntrospectionRequest();
		}
		return introspectionData;
	}
	
	/**
	 * Gets the nymea client.
	 * @return the nymea client
	 */
	public NymeaRpcClient getNymeaClient() {
		if (nymeaClient==null) {
			nymeaClient = new NymeaRpcClient(this.getConnectorSettings());
		}
		return nymeaClient;
	}

	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#getConfigurationUIComponent(javax.swing.JPanel)
	 */
	@Override
	public JComponent getConfigurationUIComponent(JPanel baseConfigPanel) {

		// --- To solve the problem of the "lost" properties panel after switching connectors, always create a new tabbed pane, but remember the sub panels   
		JTabbedPane configurationUIComponent = new JTabbedPane();
		configurationUIComponent.addTab(" Properties ", baseConfigPanel);
		configurationUIComponent.addTab(" API Introspection  ", this.getIntrospectionPanel());
		configurationUIComponent.addTab(" Browse Things  ", this.getBrowseThingsPanel());
		configurationUIComponent.addTab(" Methods Execution ", this.getExecuteMethodsPanel());
		
		return configurationUIComponent;
	}

	/**
	 * Gets the introspection panel.
	 * @return the introspection panel
	 */
	private IntrospectionPanel getIntrospectionPanel() {
		if (introspectionPanel==null) {
			introspectionPanel = new IntrospectionPanel(this);
		}
		return introspectionPanel;
	}

	/**
	 * Gets the browse things panel.
	 * @return the browse things panel
	 */
	private BrowseThingsPanel getBrowseThingsPanel() {
		if (browseThingsPanel==null) {
			browseThingsPanel = new BrowseThingsPanel(this);
		}
		return browseThingsPanel;
	}

	/**
	 * Gets the execute methods panel.
	 * @return the execute methods panel
	 */
	private ExecuteMethodsPanel getExecuteMethodsPanel() {
		if (executeMethodsPanel == null) {
			executeMethodsPanel = new ExecuteMethodsPanel(this);
		}
		return executeMethodsPanel;
	}

	
	
	
}
