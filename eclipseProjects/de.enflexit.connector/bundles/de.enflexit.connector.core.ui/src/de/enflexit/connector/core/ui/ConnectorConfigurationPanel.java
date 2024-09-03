package de.enflexit.connector.core.ui;

import javax.swing.JPanel;

import de.enflexit.common.properties.Properties;
import de.enflexit.common.properties.PropertiesPanel;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.AbstractConnector.StartOn;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JSeparator;


/**
 * THis class provides the UI to manage an existing connection.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorConfigurationPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = -1435935216371131219L;
	
	public static final String CONFIG_MODIFIED = "ConfigModified";
	
	private JLabel jLabelManageConnection;
	private PropertiesPanel connectionPropertiesPanel;
	
	private JLabel jLabelStartOn;
	private JComboBox<StartOn> jComboBoxStartOn;
	private JLabel jLabelConnectionDetails;
	
	private boolean pauseListener;
	private JSeparator separator;
	
	private boolean isChanged;
	private JLabel jLabelProtocol;
	private JLabel jLabelProtocolName;
	
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
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		GridBagConstraints gbc_jLabelManageConnection = new GridBagConstraints();
		gbc_jLabelManageConnection.anchor = GridBagConstraints.WEST;
		gbc_jLabelManageConnection.gridwidth = 4;
		gbc_jLabelManageConnection.insets = new Insets(5, 13, 5, 5);
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
		gbc_separator.insets = new Insets(0, 0, 5, 5);
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
		gbc_jLabelProtocolName.insets = new Insets(10, 5, 5, 5);
		gbc_jLabelProtocolName.gridx = 3;
		gbc_jLabelProtocolName.gridy = 2;
		add(getJLabelProtocolName(), gbc_jLabelProtocolName);
		GridBagConstraints gbc_connectionPropertiesPanel = new GridBagConstraints();
		gbc_connectionPropertiesPanel.insets = new Insets(5, 0, 0, 0);
		gbc_connectionPropertiesPanel.gridwidth = 5;
		gbc_connectionPropertiesPanel.fill = GridBagConstraints.BOTH;
		gbc_connectionPropertiesPanel.gridx = 0;
		gbc_connectionPropertiesPanel.gridy = 3;
		add(getConnectionPropertiesPanel(), gbc_connectionPropertiesPanel);
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
			connectionPropertiesPanel.addReadOnlyProperty(AbstractConnector.PROPERTY_KEY_CONNECTOR_PROTOCOL);
			connectionPropertiesPanel.addReadOnlyProperty(AbstractConnector.PROPERTY_KEY_CONNECTOR_START_ON);
			connectionPropertiesPanel.addRequiredProperty(AbstractConnector.PROPERTY_KEY_CONNECTOR_NAME);
		}
		return connectionPropertiesPanel;
	}

	/**
	 * Sets the connector to be configured.
	 * @param connector the new connector
	 */
	public void setConnectorProperties(Properties connectorProperties) {
		if (connectorProperties!=null) {
			this.pauseListener = true;
			this.getJLabelProtocolName().setText(connectorProperties.getStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_PROTOCOL));
			this.getConnectionPropertiesPanel().setProperties(connectorProperties);
			StartOn startOn = StartOn.valueOf(connectorProperties.getStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_START_ON));
			this.getJComboBoxStartOn().setSelectedItem(startOn);
			this.pauseListener = false;
		} else {
			this.getJLabelProtocolName().setText("No connection selected");
			this.getConnectionPropertiesPanel().setProperties(null);
		}
	}
	
	/**
	 * Gets the connector properties.
	 * @return the connector properties
	 */
	public Properties getConnectorProperties() {
		return this.getConnectionPropertiesPanel().getProperties();
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
		}
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
	
}
