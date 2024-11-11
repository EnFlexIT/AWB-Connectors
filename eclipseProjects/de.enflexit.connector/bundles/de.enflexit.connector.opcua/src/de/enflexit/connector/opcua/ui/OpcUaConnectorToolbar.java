package de.enflexit.connector.opcua.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import de.enflexit.common.properties.PropertiesEvent;
import de.enflexit.common.properties.PropertiesListener;
import de.enflexit.common.swing.OwnerDetection;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.AbstractConnector.StartOn;
import de.enflexit.connector.opcua.BundleHelper;
import de.enflexit.connector.opcua.OpcUaConnector;
import de.enflexit.connector.opcua.OpcUaConnectorListener;
import de.enflexit.connector.opcua.ui.endpoint.OpcUaEndpointDialg;

/**
 * The Class OpcUaConnectorToolbar.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaConnectorToolbar extends JToolBar implements ActionListener, OpcUaConnectorListener, PropertiesListener {

	private static final long serialVersionUID = 1334543496357971374L;

	public static String IMAGE_FILE_NAME_CONNECTOR = "Connection.png";

	private ImageIcon imageIconConnectorRed   = BundleHelper.getImageIcon("ConnectionRed.png");
	private ImageIcon imageIconConnectorGreen = BundleHelper.getImageIcon("ConnectionGreen.png");
	
	private OpcUaConnector opcUaConnector;
	
	private boolean pauseActionListener;
	private boolean pausePropertiesListener;
	
	private JButton jButtonConnectorProperties;
	
	private JLabel jLabelStartOn;
	private JComboBox<StartOn> jComboBoxStartOn;
	
	
	/**
	 * Instantiates a new opc ua connector toolbar.
	 * @param opcUaConnector the opc ua connector
	 */
	public OpcUaConnectorToolbar(OpcUaConnector opcUaConnector) {
		this.opcUaConnector = opcUaConnector;
		this.opcUaConnector.getConnectorProperties().addPropertiesListener(this);
		this.opcUaConnector.addConnectionListener(this);
		this.initialize();
	}
	/**
	 * Initialize.
	 */
	private void initialize() {
		
		// --- General toolbar settings ------------------ 
		this.setOrientation(JToolBar.HORIZONTAL);
		this.setFloatable(false);
		
		// --- Add buttons -------------------------------
		this.add(this.getJButtonConnectorProperties());
		this.addSeparator();
		this.add(this.getJLabelStartOn());
		this.add(this.getJComboBoxStartOn());
		this.addSeparator();	
		
		this.setImageIconConnector();
		
		this.pauseActionListener = true;
		this.getJComboBoxStartOn().setSelectedItem(this.opcUaConnector.getStartOn());
		this.pauseActionListener = false;
	}
	
	private JButton getJButtonConnectorProperties() {
		if (jButtonConnectorProperties==null) {
			jButtonConnectorProperties = new JButton();
			jButtonConnectorProperties.setToolTipText("Open Server Properties");
			jButtonConnectorProperties.addActionListener(this);
		}
		return jButtonConnectorProperties;
	}
	private void setImageIconConnector() {
		if (this.opcUaConnector.isConnected()==true) {
			this.getJButtonConnectorProperties().setIcon(this.imageIconConnectorGreen);
		} else {
			this.getJButtonConnectorProperties().setIcon(this.imageIconConnectorRed);
		}
	}
	
	private JLabel getJLabelStartOn() {
		if (jLabelStartOn == null) {
			jLabelStartOn = new JLabel("Start on: ");
			jLabelStartOn.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelStartOn;
	}
	private JComboBox<StartOn> getJComboBoxStartOn() {
		if (jComboBoxStartOn == null) {
			jComboBoxStartOn = new JComboBox<StartOn>(new DefaultComboBoxModel<>(StartOn.values()));
			jComboBoxStartOn.setFont(new Font("Dialog", Font.PLAIN, 12));
			jComboBoxStartOn.addActionListener(this);
		}
		return jComboBoxStartOn;
	}
	
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {

		if (this.pauseActionListener==true) return;
		
		if (ae.getSource()==this.getJButtonConnectorProperties()) {
			// --- Open dialog for server and connection properties -----------
			OpcUaEndpointDialg endpointConfig = new OpcUaEndpointDialg(OwnerDetection.getOwnerWindowForComponent(this), opcUaConnector);
			endpointConfig.setVisible(true);
			// - - - model wait - - - - 
								
		} else if (ae.getSource()==this.getJComboBoxStartOn()) {
			// --- Set new StartOn value --------------------------------------
			this.pausePropertiesListener = true;
			StartOn newStartOn = (StartOn) this.getJComboBoxStartOn().getSelectedItem();
			this.opcUaConnector.setStartOn(newStartOn);
			this.pausePropertiesListener = false;
			
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.common.properties.PropertiesListener#onPropertiesEvent(de.enflexit.common.properties.PropertiesEvent)
	 */
	@Override
	public void onPropertiesEvent(PropertiesEvent pe) {
		
		if (this.pausePropertiesListener == true) return;
		
		if (pe.getIdentifier().equals(AbstractConnector.PROPERTY_KEY_CONNECTOR_START_ON)) {
			StartOn newStartOn = StartOn.valueOf((String)pe.getPropertyValue().getValueString());
			this.pauseActionListener = true;
			this.getJComboBoxStartOn().setSelectedItem(newStartOn);
			this.pauseActionListener = false;
		}
	}
		
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.opcua.OpcUaConnectorListener#onConnection()
	 */
	@Override
	public void onConnection() { 
		this.setImageIconConnector();
	}
	/* (non-Javadoc)
	 * @see de.enflexit.connector.opcua.OpcUaConnectorListener#onDisconnection()
	 */
	@Override
	public void onDisconnection() { 
		this.setImageIconConnector();
	}

	@Override
	public void onSessionActive() { }

	@Override
	public void onSessionInactive() { }

	@Override
	public void onBrowserUaNodeSelection() { }
	

	
}
