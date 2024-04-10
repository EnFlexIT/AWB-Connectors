package de.enflexit.connector.core.ui;

import javax.swing.JPanel;

import de.enflexit.common.properties.PropertiesEvent;
import de.enflexit.common.properties.PropertiesListener;
import de.enflexit.common.properties.PropertiesPanel;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.AbstractConnectorProperties;
import de.enflexit.connector.core.AbstractConnectorProperties.StartOn;

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


/**
 * THis class provides the UI to manage an existing connection.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ManageConnectionPanel extends JPanel implements ActionListener, PropertiesListener{
	
	private static final long serialVersionUID = -1435935216371131219L;
	
	private JLabel jLabelManageConnection;
	private JButton jButtonTestConnection;
	private JButton jButtonDeleteConneciton;
	private PropertiesPanel connectionPropertiesPanel;
	
	private AbstractConnector connector;
	private JLabel jLabelStartOn;
	private JComboBox<StartOn> jComboBoxStartOn;
	private JLabel jLabelConnectionDetails;
	
	public ManageConnectionPanel() {
		initialize();
	}
	
	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		GridBagConstraints gbc_jLabelManageConnection = new GridBagConstraints();
		gbc_jLabelManageConnection.anchor = GridBagConstraints.WEST;
		gbc_jLabelManageConnection.gridwidth = 4;
		gbc_jLabelManageConnection.insets = new Insets(5, 5, 5, 0);
		gbc_jLabelManageConnection.gridx = 0;
		gbc_jLabelManageConnection.gridy = 0;
		add(getJLabelManageConnection(), gbc_jLabelManageConnection);
		GridBagConstraints gbc_jLabelStartOn = new GridBagConstraints();
		gbc_jLabelStartOn.anchor = GridBagConstraints.EAST;
		gbc_jLabelStartOn.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelStartOn.gridx = 0;
		gbc_jLabelStartOn.gridy = 1;
		add(getJLabelStartOn(), gbc_jLabelStartOn);
		GridBagConstraints gbc_jComboBoxStartOn = new GridBagConstraints();
		gbc_jComboBoxStartOn.insets = new Insets(5, 0, 5, 5);
		gbc_jComboBoxStartOn.fill = GridBagConstraints.HORIZONTAL;
		gbc_jComboBoxStartOn.gridx = 1;
		gbc_jComboBoxStartOn.gridy = 1;
		add(getJComboBoxStartOn(), gbc_jComboBoxStartOn);
		GridBagConstraints gbc_jButtonTestConnection = new GridBagConstraints();
		gbc_jButtonTestConnection.anchor = GridBagConstraints.EAST;
		gbc_jButtonTestConnection.insets = new Insets(5, 10, 10, 10);
		gbc_jButtonTestConnection.gridx = 2;
		gbc_jButtonTestConnection.gridy = 1;
		add(getJButtonTestConnection(), gbc_jButtonTestConnection);
		GridBagConstraints gbc_jButtonDeleteConneciton = new GridBagConstraints();
		gbc_jButtonDeleteConneciton.anchor = GridBagConstraints.WEST;
		gbc_jButtonDeleteConneciton.insets = new Insets(5, 10, 10, 10);
		gbc_jButtonDeleteConneciton.gridx = 3;
		gbc_jButtonDeleteConneciton.gridy = 1;
		add(getJButtonDeleteConneciton(), gbc_jButtonDeleteConneciton);
		GridBagConstraints gbc_jLabelConnectionDetails = new GridBagConstraints();
		gbc_jLabelConnectionDetails.anchor = GridBagConstraints.WEST;
		gbc_jLabelConnectionDetails.gridwidth = 4;
		gbc_jLabelConnectionDetails.insets = new Insets(10, 5, 5, 5);
		gbc_jLabelConnectionDetails.gridx = 0;
		gbc_jLabelConnectionDetails.gridy = 2;
		add(getJLabelConnectionDetails(), gbc_jLabelConnectionDetails);
		GridBagConstraints gbc_connectionPropertiesPanel = new GridBagConstraints();
		gbc_connectionPropertiesPanel.gridwidth = 4;
		gbc_connectionPropertiesPanel.insets = new Insets(0, 10, 0, 0);
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
	private JButton getJButtonTestConnection() {
		if (jButtonTestConnection == null) {
			jButtonTestConnection = new JButton("Test");
			jButtonTestConnection.setFont(new Font("Dialog", Font.BOLD, 12));
			jButtonTestConnection.addActionListener(this);
			jButtonTestConnection.setEnabled(false);
		}
		return jButtonTestConnection;
	}
	private JButton getJButtonDeleteConneciton() {
		if (jButtonDeleteConneciton == null) {
			jButtonDeleteConneciton = new JButton("Delete");
			jButtonDeleteConneciton.setFont(new Font("Dialog", Font.BOLD, 12));
			jButtonDeleteConneciton.addActionListener(this);
			jButtonDeleteConneciton.setEnabled(false);
		}
		return jButtonDeleteConneciton;
	}
	private PropertiesPanel getConnectionPropertiesPanel() {
		if (connectionPropertiesPanel == null) {
			connectionPropertiesPanel = new PropertiesPanel();
		}
		return connectionPropertiesPanel;
	}

	/* (non-Javadoc)
	 * @see de.enflexit.common.properties.PropertiesListener#onPropertiesEvent(de.enflexit.common.properties.PropertiesEvent)
	 */
	@Override
	public void onPropertiesEvent(PropertiesEvent propertiesEvent) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==this.getJButtonTestConnection()) {
			if (this.connector.connect()==true) {
				JOptionPane.showMessageDialog(this, "Connection successful!", "Connection successful!", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "Connection failed", "Connection failed!", JOptionPane.ERROR_MESSAGE);
			}
		} else if (ae.getSource()==this.getJButtonDeleteConneciton()) {
			JOptionPane.showMessageDialog(this, "Deleting the current connection - not implemented yet!");
		} else if (ae.getSource()==this.getJComboBoxStartOn()) {
			this.connector.getConnectorProperties().setStringValue(AbstractConnectorProperties.PROPERTY_KEY_CONNECTOR_START_ON, this.getJComboBoxStartOn().getSelectedItem().toString());
		}
	}

	/**
	 * Sets the connector to be configured.
	 * @param connector the new connector
	 */
	public void setConnector(AbstractConnector connector) {
		this.connector = connector;
		if (connector!=null) {
			this.getConnectionPropertiesPanel().setProperties(connector.getConnectorProperties());
			StartOn startOn = StartOn.valueOf(connector.getConnectorProperties().getStringValue(AbstractConnectorProperties.PROPERTY_KEY_CONNECTOR_START_ON));
			this.getJComboBoxStartOn().setSelectedItem(startOn);
			this.getJButtonTestConnection().setEnabled(true);
			this.getJButtonDeleteConneciton().setEnabled(true);
		} else {
			this.getConnectionPropertiesPanel().setProperties(null);
			this.getJButtonTestConnection().setEnabled(false);
			this.getJButtonDeleteConneciton().setEnabled(false);
		}
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
	private JLabel getJLabelConnectionDetails() {
		if (jLabelConnectionDetails == null) {
			jLabelConnectionDetails = new JLabel("Connection Details");
			jLabelConnectionDetails.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelConnectionDetails;
	}
}
