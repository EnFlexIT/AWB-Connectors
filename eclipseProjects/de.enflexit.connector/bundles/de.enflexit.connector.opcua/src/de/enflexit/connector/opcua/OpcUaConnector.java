package de.enflexit.connector.opcua;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.SessionActivityListener;
import org.eclipse.milo.opcua.sdk.client.api.UaSession;
import org.eclipse.milo.opcua.stack.core.Stack;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;

import de.enflexit.common.properties.Properties;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.opcua.OpcUaConnectorListener.Event;
import de.enflexit.connector.opcua.ui.OpcUAConnectorPanel;


/**
 * The Class OpcUaConnector.
 * 
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaConnector extends AbstractConnector {

	private OpcUaClient opcUaClient;
	private boolean opcUaClientActive;
	
	private OpcUAConnectorPanel configPanel;
	
	private List<OpcUaConnectorListener> connectorListener;
	
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#getProtocolName()
	 */
	@Override
	public String getProtocolName() {
		return OpcUAConnectorService.CONNECTOR_NAME;
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#getInitialProperties()
	 */
	@Override
	public Properties getInitialProperties() {
		
		Properties initProps = new Properties();
		initProps.setStringValue(PROPERTY_KEY__CONNECTOR_PROTOCOL, OpcUAConnectorService.CONNECTOR_NAME);
		initProps.setStringValue(PROPERTY_KEY_CONNECTOR_START_ON, StartOn.ManualStart.toString());

		
		
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
			}
		});
	}
	// --------------------------------------------------------------
	
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#getConfigurationUIComponent(javax.swing.JPanel)
	 */
	@Override
	public JComponent getConfigurationUIComponent(JPanel baseConfigPanel) {
		if (configPanel==null) {
			configPanel = new OpcUAConnectorPanel(this, baseConfigPanel);
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
			// --- TODO Check the available settings --------------------------
			
			
			
			// --- Try to establish connection --------------------------------
			try {
				
				this.opcUaClient = OpcUaClient.create(
						"opc.tcp://SIDEWALK:62541/milo",
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
			this.opcUaClient.disconnect();
			Stack.releaseSharedResources();
			this.opcUaClient = null;
			this.opcUaClientActive = false;
		}
	}
	
	/**
	 * Returns the current OpcUaClient, if the connection was activated successfully.
	 * @return the OpcUaClient or <code>null</code>
	 */
	public OpcUaClient getOpcUaClient() {
		return opcUaClient;
	}
	
}
