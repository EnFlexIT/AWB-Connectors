package de.enflexit.connector.opcua;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JPanel;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.SessionActivityListener;
import org.eclipse.milo.opcua.sdk.client.api.UaSession;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.Stack;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;

import de.enflexit.common.properties.Properties;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.opcua.OpcUaConnectorListener.Event;
import de.enflexit.connector.opcua.ui.OpcUaBrowserTreeModel;
import de.enflexit.connector.opcua.ui.OpcUaConnectorPanel;

/**
 * The Class OpcUaConnector.
 * 
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaConnector extends AbstractConnector {

	public static final String DATE_TIME_PATTERN_FORMAT = "dd.MM.yy HH:mm:ss.SSS";
	
//	public static final String PROPERTY_OPC_UA_ = "";
//	public static final String PROPERTY_OPC_UA_ = "";
//	public static final String PROPERTY_OPC_UA_ = "";
//	public static final String PROPERTY_OPC_UA_ = "";
//	public static final String PROPERTY_OPC_UA_ = "";
	
	
	private OpcUaClient opcUaClient;
	private boolean opcUaClientActive;

	private List<OpcUaConnectorListener> connectorListener;
	
	private OpcUaBrowserTreeModel opcUaBrowserModel;
	private OpcUaDataAccess opcUaDataAccess;
	
	private OpcUaConnectorPanel configPanel;
	private UaNode browserUaNode;
	
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#getProtocolName()
	 */
	@Override
	public String getProtocolName() {
		return OpcUaConnectorService.CONNECTOR_NAME;
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#getInitialProperties()
	 */
	@Override
	public Properties getInitialProperties() {
		
		Properties initProps = new Properties();
		initProps.setStringValue(PROPERTY_KEY_CONNECTOR_PROTOCOL, OpcUaConnectorService.CONNECTOR_NAME);
		initProps.setStringValue(PROPERTY_KEY_CONNECTOR_START_ON, StartOn.ManualStart.toString());

		initProps.setStringValue(PROPERTY_KEY_SERVER_HOST, "localhost");
		initProps.setIntegerValue(PROPERTY_KEY_SERVER_PORT, 62541);
		
		return initProps;
	}
	
	
	// --------------------------------------------------------------
	// --- From here listener handling ------------------------------
	// --------------------------------------------------------------
	/**
	 * Gets the connector listener.
	 * @return the connector listener
	 */
	private List<OpcUaConnectorListener> getConnectorListener() {
		if (connectorListener==null) {
			connectorListener = new ArrayList<>();
		}
		return connectorListener;
	}
	/**
	 * Adds the connection listener.
	 * @param listener the listener to add
	 */
	public void addConnectionListener(OpcUaConnectorListener listener) {
		if (listener!=null && this.getConnectorListener().contains(listener)==false) {
			this.getConnectorListener().add(listener);
		}
	}
	/**
	 * Removes the connection listener.
	 * @param listener the listener to remove
	 */
	public void removeConnectionListener(OpcUaConnectorListener listener) {
		if (listener!=null) this.getConnectorListener().add(listener);
	}
	/**
	 * Inform listener.
	 * @param event the listener event to invoke
	 */
	private void informListener(OpcUaConnectorListener.Event event) {

		this.getConnectorListener().forEach(listener -> {
			switch (event) {
			case Connect: 
				listener.onConnection();
				break;
			case Disconnect:
				listener.onDisconnection();
				break;
			case SessionActive:
				listener.onSessionActive();
				break;
			case SessionInactive:
				listener.onSessionInactive();
				break;
			case BrowserUaNodeSelection:
				listener.onBrowserUaNodeSelection();
				break;
			}
		});
	}
	// --------------------------------------------------------------
	
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#getConfigurationUIComponent(javax.swing.JPanel)
	 */
	@Override
	public OpcUaConnectorPanel getConfigurationUIComponent(JPanel baseConfigPanel) {
		if (configPanel==null) {
			configPanel = new OpcUaConnectorPanel(this, baseConfigPanel);
		} else {
			configPanel.addBaseConfigurationPanel(baseConfigPanel);
		}
		return configPanel;
	}
	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#disposeUI()
	 */
	@Override
	public void disposeUI() {
		this.configPanel = null;
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#connect()
	 */
	@Override
	public boolean connect() {
		
		if (this.opcUaClient==null) {

			// --- Get required information -----------------------------------
			String host  = this.getConnectorProperties().getStringValue(PROPERTY_KEY_SERVER_HOST);
			Integer port = this.getConnectorProperties().getIntegerValue(PROPERTY_KEY_SERVER_PORT);
			
			
			// --- Try to establish connection --------------------------------
			try {
				
				this.opcUaClient = OpcUaClient.create(
						"opc.tcp://" + host + ":" + port + "/milo",
						endpoints ->
						endpoints.stream()
						.filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
						.findFirst(),
						configBuilder ->
						configBuilder.build()
						);
				
				this.opcUaClient.connect().get();
				this.opcUaClientActive = true;
				
				// --- Add a SessionActivityListener --------------------------
				this.opcUaClient.addSessionActivityListener(new SessionActivityListener() {
					@Override
					public void onSessionActive(UaSession session) {
						OpcUaConnector.this.opcUaClientActive = true;
						OpcUaConnector.this.informListener(Event.SessionActive);
					}
					@Override
					public void onSessionInactive(UaSession session) {
						OpcUaConnector.this.opcUaClientActive = false;
						OpcUaConnector.this.informListener(Event.SessionInactive);
					}
				});
				
				// --- Start the data acquisition -----------------------------
				this.getOpcUaDataAccess().startDataAcquisition();
				
						
			} catch (UaException | InterruptedException | ExecutionException uaEx) {
				this.opcUaClient = null;
				this.opcUaClientActive = false;
				uaEx.printStackTrace();
			}
		}
		
		// --- Inform listener ------------------------------------------------
		this.informListener(Event.Connect);
		
		return this.opcUaClientActive;
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#isConnected()
	 */
	@Override
	public boolean isConnected() {
		if (this.opcUaClient!=null) {
			return this.opcUaClientActive;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#disconnect()
	 */
	@Override
	public void disconnect() {
		if (this.opcUaClient!=null) {
			// --- Stop data acquisition --------
			this.getOpcUaDataAccess().stopDataAcquisition();
			this.opcUaDataAccess = null;
			// --- Close connection -------------
			this.opcUaClient.disconnect();
			Stack.releaseSharedResources();
			this.opcUaClient = null;
			this.opcUaClientActive = false;
			this.informListener(Event.Disconnect);
			// --- Clear browser tree -----------
			this.getOpcUaBrowserTreeModel().clearTreeModel();
		}
	}
	
	/**
	 * Returns the current OpcUaClient, if the connection was activated successfully.
	 * @return the OpcUaClient or <code>null</code>
	 */
	public OpcUaClient getOpcUaClient() {
		return opcUaClient;
	}

	
	/**
	 * Returns the OPC UA browser model of the current connector.
	 * @return the opc UA browser model
	 */
	public OpcUaBrowserTreeModel getOpcUaBrowserTreeModel() {
		if (opcUaBrowserModel==null) {
			opcUaBrowserModel = new OpcUaBrowserTreeModel(this);
		}
		return opcUaBrowserModel;
	}
	
	/**
	 * Returns the data access.
	 * @return the data access
	 */
	public OpcUaDataAccess getOpcUaDataAccess() {
		if (opcUaDataAccess==null) {
			opcUaDataAccess = new OpcUaDataAccess(this);
		}
		return opcUaDataAccess;
	}
	
	
	/**
	 * Sets the current UaNode.
	 * @param uaNode the new browser UaNode
	 */
	public void setBrowserUaNode(UaNode uaNode) {
		this.browserUaNode = uaNode;
		this.informListener(Event.BrowserUaNodeSelection);
	}
	/**
	 * Returns the currently browsed UaNode
	 * @return the browser ua node
	 */
	public UaNode getBrowserUaNode() {
		return browserUaNode;
	}
	
}
