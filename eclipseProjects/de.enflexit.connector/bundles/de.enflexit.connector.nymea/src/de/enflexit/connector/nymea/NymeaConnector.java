package de.enflexit.connector.nymea;

import java.time.Instant;
import java.time.Period;
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
	
	private JTabbedPane configurationUIComponent;
	
	private HashMap<String, Object> introspectionData;
	
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
		return properties;
	}

	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#connect()
	 */
	@Override
	public boolean connect() {
		this.connected = this.getNymeaClient().openConnection();
		if (this.connected) {
			if (this.getNymeaClient().isAuthenticated()==false) {
				this.getNymeaClient().authenticateUser();
			}
			if (this.getNymeaClient().isAuthenticated()) {
//				this.sendTestCalls();
//				this.getNymeaClient().printMethodsOverview();
//				this.notifyListeners(new ConnectorEvent(this, Event.CONNECTED));
//				this.sendTestCalls();
			}
		}
		return this.connected;
	}
	
	private void sendTestCalls() {
		
		// --- Current power balance ----------------------
		this.getNymeaClient().getPowerBalance();
		
		// --- Power logs of the last week ----------------
		Instant now = Instant.now();
		Instant oneWeekAgo = now.minus(Period.ofDays(7));
		this.getNymeaClient().getPowerBalanceLogs(oneWeekAgo.toEpochMilli(), now.toEpochMilli(), "SampleRate15Mins");
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
	
	public NymeaConnectorSettings getConnectorSettings() {
		if (connectorSettings==null) {
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

		if (configurationUIComponent==null) {
			configurationUIComponent = new JTabbedPane();
			configurationUIComponent.addTab(" Properties ", baseConfigPanel);
			configurationUIComponent.addTab(" API Introspection  ", new IntrospectionPanel(this));
			configurationUIComponent.addTab(" Browse Things  ", new BrowseThingsPanel(this));
			configurationUIComponent.addTab(" Methods Execution ", new ExecuteMethodsPanel(this));
		}
		
		return configurationUIComponent;
	}
	
	
}
