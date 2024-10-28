package de.enflexit.connector.opcua;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JPanel;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.SessionActivityListener;
import org.eclipse.milo.opcua.sdk.client.api.UaSession;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.X509IdentityProvider;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.Stack;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

import de.enflexit.common.properties.Properties;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.opcua.OpcUaConnectorListener.Event;
import de.enflexit.connector.opcua.ui.OpcUaAttributeWidget;
import de.enflexit.connector.opcua.ui.OpcUaBrowserTreeModel;
import de.enflexit.connector.opcua.ui.OpcUaBrowserWidget;
import de.enflexit.connector.opcua.ui.OpcUaConnectorPanel;
import de.enflexit.connector.opcua.ui.OpcUaConnectorToolbar;
import de.enflexit.connector.opcua.ui.OpcUaDataView;

/**
 * The Class OpcUaConnector.
 * 
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaConnector extends AbstractConnector {

	public static final String DATE_TIME_PATTERN_FORMAT = "dd.MM.yy HH:mm:ss.SSS";
	
	private static final String PROP_CONNECTION = "Connector.Settings";
	private static final String PROP_SECURITY = "SecuritySettings";
	private static final String PROP_AUTHENTIFICATION = "Authentification";
	
	
	public static final String PROP_ENDPOINT_URL = PROP_CONNECTION + ".EndpointUrl";
	
	public static final String PROP_SECURITY_POLICY = PROP_CONNECTION + "." + PROP_SECURITY + ".SecurityPolicy";
	public static final String PROP_SECURITY_MESSAGE_MODE = PROP_CONNECTION + "." + PROP_SECURITY + ".MessageSecurityMode";
	
	public static final String PROP_AUTH_TYPE = PROP_CONNECTION + "." + PROP_AUTHENTIFICATION;
	public static final String PROP_AUTH_USERNAME = PROP_CONNECTION + "." + PROP_AUTHENTIFICATION + ".Username";
	public static final String PROP_AUTH_PASSWORD = PROP_CONNECTION + "." + PROP_AUTHENTIFICATION + ".Password";
	
	public static final String PROP_AUTH_CERTIFICATE = PROP_CONNECTION + "." + PROP_AUTHENTIFICATION + ".Certificate";
	public static final String PROP_AUTH_PRIVATE_KEY  = PROP_CONNECTION + "." + PROP_AUTHENTIFICATION + ".PrivateKey";
	
	
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

		initProps.setStringValue(PROP_ENDPOINT_URL, "opc.tcp://localhost:62541/milo");
		
		initProps.setStringValue(PROP_SECURITY_POLICY, SecurityPolicy.None.toString());
		initProps.setStringValue(PROP_SECURITY_MESSAGE_MODE, MessageSecurityMode.None.toString());
		
		initProps.setStringValue(PROP_AUTH_TYPE, OpcUaHelper.getIdentityProviderName(AnonymousProvider.class));
		
		return initProps;
	}
	
	
	// --------------------------------------------------------------
	// --- From here listener handling ------------------------------
	// --------------------------------------------------------------
	/**
	 * Returns the local connector listener.
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
		if (listener!=null) this.getConnectorListener().remove(listener);
	}
	/**
	 * Removes all listener of the specified type from the current list of {@link OpcUaConnectorListener}.
	 * @param listener the listener type to remove
	 */
	public void removeConnectionListener(Class<?> listenerClass) {
		if (listenerClass!=null) {
			// --- Find listener to remove ------
			List<OpcUaConnectorListener> listToRemove = new ArrayList<>();
			for (OpcUaConnectorListener listener : this.getConnectorListener()) {
				if (listener.getClass().equals(listenerClass)==true) {
					listToRemove.add(listener);
				}
			}
			// --- Remove all listeners found --- 
			listToRemove.forEach(listenerToRemove -> this.getConnectorListener().remove(listenerToRemove));
		}
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
		if (this.configPanel!=null) {
			this.getConnectorProperties().removePropertiesListener(OpcUaConnectorToolbar.class);
			this.removeConnectionListener(OpcUaConnectorToolbar.class);
			this.removeConnectionListener(OpcUaBrowserWidget.class);
			this.removeConnectionListener(OpcUaAttributeWidget.class);
			this.removeConnectionListener(OpcUaDataView.class);
			this.configPanel = null;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.AbstractConnector#connect()
	 */
	@Override
	public boolean connect() {
		
		if (this.opcUaClient==null) {

			// --- Try to establish connection --------------------------------
			try {

				// --- Get required information -------------------------------
				String endpointURL  = this.getConnectorProperties().getStringValue(PROP_ENDPOINT_URL);
				EndpointDescription endpointDescription = this.getEndPointDescription(endpointURL);
				
				// --- Build client configuration -----------------------------
				OpcUaClientConfig clientConfig = OpcUaClientConfig.builder()
						.setApplicationName(LocalizedText.english(this.getConnectorProperties().getStringValue(PROPERTY_KEY_CONNECTOR_NAME)))
						.setApplicationUri("urn:awb:connctor:opcua:" + this.hashCode())
						.setCertificate(null)
						.setKeyPair(null)
						.setEndpoint(endpointDescription)
						.setIdentityProvider(this.getIdentityProvider())
						.setRequestTimeout(UInteger.valueOf(5000))
						.build();
				
				this.opcUaClient = OpcUaClient.create(clientConfig);
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
				
				// --- Inform listener ----------------------------------------
				this.informListener(Event.Connect);
				
				// --- Start the data acquisition -----------------------------
				this.getOpcUaDataAccess().startDataAcquisition();
						
			} catch (UaException | InterruptedException | ExecutionException uaEx) {
				this.opcUaClient = null;
				this.opcUaClientActive = false;
				uaEx.printStackTrace();
			}
		}
		
		
		return this.opcUaClientActive;
	}
	
	/**
	 * Based on the specified end point URL, returns the currently configured {@link EndpointDescription}
	 * with respect to the {@link SecurityPolicy} and the {@link MessageSecurityMode}.
	 *
	 * @param endpointURL the end point URL
	 * @return the end point description
	 */
	private EndpointDescription getEndPointDescription(String endpointURL) {
		
		if (endpointURL==null || endpointURL.isBlank()==true) return null;
		
		String securityPolicyProperty  = this.getConnectorProperties().getStringValue(PROP_SECURITY_POLICY);
		String msgSecurityModeProperty = this.getConnectorProperties().getStringValue(PROP_SECURITY_MESSAGE_MODE);
		
		SecurityPolicy securityPolicy       = securityPolicyProperty==null  ? SecurityPolicy.None      : SecurityPolicy.valueOf(securityPolicyProperty);
		MessageSecurityMode msgSecurityMode = msgSecurityModeProperty==null ? MessageSecurityMode.None : MessageSecurityMode.valueOf(msgSecurityModeProperty);
		
		EndpointDescription epDec = null;
		try {
			
			// --- Call discover for end point URL --------------------------------------
			List<EndpointDescription> epDescList = OpcUaHelper.discoverEndPointDescription(endpointURL);
			// --- Filter according to SecurityPolicy and MessageSecurityMode -----------
			epDec = epDescList.stream()
			.filter(e -> e.getSecurityPolicyUri().equals(securityPolicy.getUri()))
			.filter(e -> e.getSecurityMode()==msgSecurityMode)
			.findFirst()
			.orElseThrow(() -> new Exception("No desired endpoint could be found returned"));
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return epDec;
	}
	
	/**
	 * Returns the current identity provider.
	 * @return the identity provider
	 */
	private IdentityProvider getIdentityProvider() {
		
		IdentityProvider idProvider = null;
		
		String idProviderName = this.getConnectorProperties().getStringValue(PROP_AUTH_TYPE);
		switch (idProviderName) {
		case "Anonymous": 
			idProvider = new AnonymousProvider();
			break;
			
		case "Username":
			String user= this.getConnectorProperties().getStringValue(PROP_AUTH_USERNAME);
			String pswd = this.getConnectorProperties().getStringValue(PROP_AUTH_PASSWORD);
			idProvider = new UsernameProvider(user, pswd);
			break;
			
		case "X509Identity":
			// TODO Load certificate and PrivateKey
			X509Certificate cert = null;
			PrivateKey pk = null;
			idProvider = new X509IdentityProvider(cert, pk);
			break;
		
		default:
			throw new IllegalArgumentException("Unexpected value for identity provider: " + idProviderName + ". Use 'Anonymous', 'Username' or 'X509Identity'");
		}
		return idProvider;
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
