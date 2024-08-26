package de.enflexit.connector.core.ui;

import javax.swing.JPanel;

import de.enflexit.common.SerialClone;
import de.enflexit.common.properties.Properties;
import de.enflexit.common.properties.PropertiesEvent;
import de.enflexit.common.properties.PropertiesListener;
import de.enflexit.common.properties.PropertiesPanel;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.AbstractConnector.StartOn;
import de.enflexit.connector.core.ConnectorService;
import de.enflexit.connector.core.manager.ConnectorManager;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import java.awt.Color;


/**
 * THis class provides the UI to manage an existing connection.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorConfigurationPanel extends JPanel implements ActionListener, PropertiesListener{
	
	private static final long serialVersionUID = -1435935216371131219L;
	
	private JLabel jLabelManageConnection;
	private JButton jButtonTest;
	private PropertiesPanel connectionPropertiesPanel;
	
	private JLabel jLabelStartOn;
	private JComboBox<StartOn> jComboBoxStartOn;
	private JLabel jLabelConnectionDetails;
	
	private boolean pauseListener;
	private JSeparator separator;
	private JButton jButtonDiscard;
	private JButton jButtonApply;
	
	private boolean isChanged;
	private JLabel jLabelProtocol;
	private JLabel jLabelProtocolName;
	
	private Properties originalProperties;
	
	/**
	 * Instantiates a new manage connection panel.
	 */
	public ConnectorConfigurationPanel() {
		initialize();
	}
	
	/**
	 * Initializes the UI components.
	 */
	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		GridBagConstraints gbc_jLabelManageConnection = new GridBagConstraints();
		gbc_jLabelManageConnection.anchor = GridBagConstraints.WEST;
		gbc_jLabelManageConnection.gridwidth = 4;
		gbc_jLabelManageConnection.insets = new Insets(5, 13, 5, 0);
		gbc_jLabelManageConnection.gridx = 0;
		gbc_jLabelManageConnection.gridy = 0;
		add(getJLabelManageConnection(), gbc_jLabelManageConnection);
		GridBagConstraints gbc_jLabelStartOn = new GridBagConstraints();
		gbc_jLabelStartOn.anchor = GridBagConstraints.EAST;
		gbc_jLabelStartOn.insets = new Insets(5, 13, 5, 5);
		gbc_jLabelStartOn.gridx = 0;
		gbc_jLabelStartOn.gridy = 1;
		add(getJLabelStartOn(), gbc_jLabelStartOn);
		GridBagConstraints gbc_jComboBoxStartOn = new GridBagConstraints();
		gbc_jComboBoxStartOn.anchor = GridBagConstraints.WEST;
		gbc_jComboBoxStartOn.insets = new Insets(5, 0, 5, 5);
		gbc_jComboBoxStartOn.gridx = 1;
		gbc_jComboBoxStartOn.gridy = 1;
		add(getJComboBoxStartOn(), gbc_jComboBoxStartOn);
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.insets = new Insets(0, 0, 5, 0);
		gbc_separator.gridx = 3;
		gbc_separator.gridy = 1;
		add(getSeparator(), gbc_separator);
		GridBagConstraints gbc_jLabelConnectionDetails = new GridBagConstraints();
		gbc_jLabelConnectionDetails.anchor = GridBagConstraints.WEST;
		gbc_jLabelConnectionDetails.gridwidth = 2;
		gbc_jLabelConnectionDetails.insets = new Insets(10, 13, 5, 5);
		gbc_jLabelConnectionDetails.gridx = 0;
		gbc_jLabelConnectionDetails.gridy = 2;
		add(getJLabelConnectionDetails(), gbc_jLabelConnectionDetails);
		GridBagConstraints gbc_jLabelProtocol = new GridBagConstraints();
		gbc_jLabelProtocol.insets = new Insets(10, 5, 5, 5);
		gbc_jLabelProtocol.gridx = 2;
		gbc_jLabelProtocol.gridy = 2;
		add(getJLabelProtocol(), gbc_jLabelProtocol);
		GridBagConstraints gbc_jLabelProtocolName = new GridBagConstraints();
		gbc_jLabelProtocolName.anchor = GridBagConstraints.WEST;
		gbc_jLabelProtocolName.insets = new Insets(10, 5, 5, 0);
		gbc_jLabelProtocolName.gridx = 3;
		gbc_jLabelProtocolName.gridy = 2;
		add(getJLabelProtocolName(), gbc_jLabelProtocolName);
		GridBagConstraints gbc_connectionPropertiesPanel = new GridBagConstraints();
		gbc_connectionPropertiesPanel.insets = new Insets(5, 0, 5, 0);
		gbc_connectionPropertiesPanel.gridwidth = 5;
		gbc_connectionPropertiesPanel.fill = GridBagConstraints.BOTH;
		gbc_connectionPropertiesPanel.gridx = 0;
		gbc_connectionPropertiesPanel.gridy = 3;
		add(getConnectionPropertiesPanel(), gbc_connectionPropertiesPanel);
		GridBagConstraints gbc_jButtonTest = new GridBagConstraints();
		gbc_jButtonTest.gridwidth = 2;
		gbc_jButtonTest.anchor = GridBagConstraints.WEST;
		gbc_jButtonTest.insets = new Insets(5, 10, 10, 10);
		gbc_jButtonTest.gridx = 0;
		gbc_jButtonTest.gridy = 4;
		add(getJButtonTest(), gbc_jButtonTest);
		GridBagConstraints gbc_jButtonApply = new GridBagConstraints();
		gbc_jButtonApply.anchor = GridBagConstraints.ABOVE_BASELINE_TRAILING;
		gbc_jButtonApply.insets = new Insets(5, 0, 10, 5);
		gbc_jButtonApply.gridx = 3;
		gbc_jButtonApply.gridy = 4;
		add(getJButtonApply(), gbc_jButtonApply);
		GridBagConstraints gbc_jButtonDiscard = new GridBagConstraints();
		gbc_jButtonDiscard.insets = new Insets(5, 5, 10, 10);
		gbc_jButtonDiscard.gridx = 4;
		gbc_jButtonDiscard.gridy = 4;
		add(getJButtonDiscard(), gbc_jButtonDiscard);
	}

	private JLabel getJLabelManageConnection() {
		if (jLabelManageConnection == null) {
			jLabelManageConnection = new JLabel("Manage connection");
			jLabelManageConnection.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelManageConnection;
	}
	private JLabel getJLabelStartOn() {
		if (jLabelStartOn == null) {
			jLabelStartOn = new JLabel("Start on");
			jLabelStartOn.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelStartOn;
	}

	private JComboBox<StartOn> getJComboBoxStartOn() {
		if (jComboBoxStartOn == null) {
			jComboBoxStartOn = new JComboBox<StartOn>();
			jComboBoxStartOn.setModel(new DefaultComboBoxModel<>(StartOn.values()));
			jComboBoxStartOn.addActionListener(this);
		}
		return jComboBoxStartOn;
	}

	private JSeparator getSeparator() {
		if (separator == null) {
			separator = new JSeparator();
		}
		return separator;
	}
	private JLabel getJLabelConnectionDetails() {
		if (jLabelConnectionDetails == null) {
			jLabelConnectionDetails = new JLabel("Connection Details");
			jLabelConnectionDetails.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelConnectionDetails;
	}

	private PropertiesPanel getConnectionPropertiesPanel() {
		if (connectionPropertiesPanel == null) {
			connectionPropertiesPanel = new PropertiesPanel();
//			connectionPropertiesPanel.addReadOnlyProperty(AbstractConnector.PROPERTY_KEY_CONNECTOR_PROTOCOL);
//			connectionPropertiesPanel.addReadOnlyProperty(AbstractConnector.PROPERTY_KEY_CONNECTOR_START_ON);
//			connectionPropertiesPanel.addRequiredProperty(AbstractConnector.PROPERTY_KEY_CONNECTOR_NAME);
		}
		return connectionPropertiesPanel;
	}

	private JButton getJButtonTest() {
		if (jButtonTest == null) {
			jButtonTest = new JButton("Test Connection");
			jButtonTest.setFont(new Font("Dialog", Font.BOLD, 12));
			jButtonTest.addActionListener(this);
			jButtonTest.setEnabled(false);
		}
		return jButtonTest;
	}

	/**
	 * Sets the connector to be configured.
	 * @param connector the new connector
	 */
	public void setConnectorProperties(Properties connectorProperties) {
		this.originalProperties = connectorProperties;
		if (connectorProperties!=null) {
			this.pauseListener = true;
			Properties propsClone = SerialClone.clone(connectorProperties);
			propsClone.addPropertiesListener(this);
			this.getJLabelProtocolName().setText(connectorProperties.getStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_PROTOCOL));
			this.getConnectionPropertiesPanel().setProperties(propsClone);
			this.setChanged(false);
			StartOn startOn = StartOn.valueOf(connectorProperties.getStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_START_ON));
			this.getJComboBoxStartOn().setSelectedItem(startOn);
			this.getJButtonTest().setEnabled(true);
			this.pauseListener = false;
		} else {
			this.getJLabelProtocolName().setText("No connection selected");
			this.getConnectionPropertiesPanel().setProperties(null);
			this.getJButtonTest().setEnabled(false);
		}
	}

	/* (non-Javadoc)
	 * @see de.enflexit.common.properties.PropertiesListener#onPropertiesEvent(de.enflexit.common.properties.PropertiesEvent)
	 */
	@Override
	public void onPropertiesEvent(PropertiesEvent propertiesEvent) {
		this.setChanged(true);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==this.getJComboBoxStartOn()) {
			if (this.pauseListener==false ) {
				this.getConnectionPropertiesPanel().getProperties().setValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_START_ON, this.getJComboBoxStartOn().getSelectedItem().toString());
			}
		} else if (ae.getSource()==this.getJButtonTest()) {
			this.testConnection();
		} else if (ae.getSource()==this.getJButtonApply()) {
			this.applyChanges();
		} else if (ae.getSource()==this.getJButtonDiscard()) {
			this.discardChanges();
		}
	}
	
	/**
	 * Tests the selected connection.
	 */
	private void testConnection() {
		String protocolName = this.getConnectionPropertiesPanel().getProperties().getStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_PROTOCOL);
		ConnectorService connectorService = ConnectorManager.getInstance().getConnectorServiceForProtocol(protocolName);
		if (connectorService!=null) {
			AbstractConnector testConnector = connectorService.getNewConnectorInstance();
			testConnector.setConnectorProperties(this.getConnectionPropertiesPanel().getProperties());
			if (testConnector.connect()==true) {
				JOptionPane.showMessageDialog(this, "Connection successful!", "Connection successful!", JOptionPane.INFORMATION_MESSAGE);
				testConnector.disconnect();
			} else {
				JOptionPane.showMessageDialog(this, "Connection test failed, please check your settings!", "Connection failed!", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			System.err.println("[" + this.getClass().getSimpleName() + "] No connector service implementation for the protocol " + protocolName + " could not be found!");
			JOptionPane.showMessageDialog(this, "No conector service found for the configured protocol " + protocolName + "!", "Not available!", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private JButton getJButtonApply() {
		if (jButtonApply == null) {
			jButtonApply = new JButton();
			jButtonApply.setForeground(new Color(0, 153, 0));
			jButtonApply.setFont(new Font("Dialog", Font.BOLD, 12));
			jButtonApply.setText("Apply");
			jButtonApply.setToolTipText("Apply changes");
			jButtonApply.setEnabled(false);
			jButtonApply.addActionListener(this);
		}
		return jButtonApply;
	}
	private JButton getJButtonDiscard() {
		if (jButtonDiscard == null) {
			jButtonDiscard = new JButton();
			jButtonDiscard.setForeground(new Color(153, 0, 0));
			jButtonDiscard.setFont(new Font("Dialog", Font.BOLD, 12));
			jButtonDiscard.setText("Discard");
			jButtonDiscard.setToolTipText("Discard changes");
			jButtonDiscard.setEnabled(false);
			jButtonDiscard.addActionListener(this);
		}
		return jButtonDiscard;
	}

	/**
	 * Checks if the current configuration has unapplied changes.
	 * @return true, if is changed
	 */
	public boolean hasPendingChanges() {
		return isChanged;
	}

	/**
	 * Sets the changed state for the current configuration.
	 * @param hasUnsavedChanges the new changed
	 */
	public void setChanged(boolean hasUnsavedChanges) {
		this.isChanged = hasUnsavedChanges;
		this.getJButtonApply().setEnabled(hasUnsavedChanges);
		this.getJButtonDiscard().setEnabled(hasUnsavedChanges);
	}
	
	/**
	 * Apples all changes to the current connector configuration.
	 */
	protected void applyChanges() {
		// --- Write the changed properties to the connector ------------------
		
		Properties editedProperties = this.getConnectionPropertiesPanel().getProperties();
		ConnectorManager.getInstance().updateConnectorProperties(this.originalProperties.getStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_NAME), editedProperties);
		this.setChanged(false);
		
		AbstractConnector connector = ConnectorManager.getInstance().getConnectorByName(this.getSelectedConnectorName());
		if (connector!=null && connector.isConnected()==true) {
			String message = "The connection you modified is currently active! Reconnect now to apply the changes immediately?";
			int userResponse = JOptionPane.showConfirmDialog(this, message, "Reconnect now?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (userResponse==JOptionPane.YES_OPTION) {
				connector.disconnect();
				connector.connect();
			}
		}
	}
	
	/**
	 * Discards all changes to the current connector configuration.
	 */
	protected void discardChanges() {
		int userResponse = JOptionPane.showConfirmDialog(this, "This will discard your changes! Are you sure?", "Discard changes?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (userResponse==JOptionPane.YES_OPTION) {
			// --- Replace with the original properties from the connector ----
			this.getConnectionPropertiesPanel().setProperties(this.originalProperties);
			this.setChanged(false);
		}
	}
	
	private JLabel getJLabelProtocol() {
		if (jLabelProtocol == null) {
			jLabelProtocol = new JLabel("Protocol:");
			jLabelProtocol.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelProtocol;
	}
	private JLabel getJLabelProtocolName() {
		if (jLabelProtocolName == null) {
			jLabelProtocolName = new JLabel("No connection selected");
			jLabelProtocolName.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelProtocolName;
	}
	
	private String getSelectedConnectorName() {
		if (this.originalProperties!=null) {
			return this.originalProperties.getStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_NAME);
		} else {
			return null;
		}
	}
}
