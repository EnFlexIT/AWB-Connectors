package de.enflexit.connector.core.ui;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;

import de.enflexit.common.properties.PropertiesPanel;
import de.enflexit.connector.core.ConnectorConfiguration;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ConnectorConfigurationPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 5683939821664244059L;
	private static final String ICON_PATH_APPLY = "/icons/Apply.png";
	private static final String ICON_PATH_CANCEL = "/icons/Cancel.png";
	
	private JLabel jLabelConnectorName;
	private JTextField jTextFieldConnectorName;
	private JLabel jLabelConnectionProtocol;
	private PropertiesPanel propertiesPanel;
	private JButton jButtonTestConnection;
	private JButton jButtonApply;
	private JButton jButtonCancel;
	private JLabel jLabelProtocolName;
	
	/**
	 * Instantiates a new connector configuration panel.
	 */
	public ConnectorConfigurationPanel() {
		initialize();
	}
	
	/**
	 * Initializes the UI components.
	 */
	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		GridBagConstraints gbc_jLabelConnectorName = new GridBagConstraints();
		gbc_jLabelConnectorName.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelConnectorName.anchor = GridBagConstraints.EAST;
		gbc_jLabelConnectorName.gridx = 0;
		gbc_jLabelConnectorName.gridy = 0;
		add(getJLabelConnectorName(), gbc_jLabelConnectorName);
		GridBagConstraints gbc_jTextFieldConnectorName = new GridBagConstraints();
		gbc_jTextFieldConnectorName.insets = new Insets(5, 0, 5, 5);
		gbc_jTextFieldConnectorName.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldConnectorName.gridx = 1;
		gbc_jTextFieldConnectorName.gridy = 0;
		add(getJTextFieldConnectorName(), gbc_jTextFieldConnectorName);
		GridBagConstraints gbc_jLabelConnectionProtocol = new GridBagConstraints();
		gbc_jLabelConnectionProtocol.anchor = GridBagConstraints.EAST;
		gbc_jLabelConnectionProtocol.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelConnectionProtocol.gridx = 2;
		gbc_jLabelConnectionProtocol.gridy = 0;
		add(getJLabelConnectionProtocol(), gbc_jLabelConnectionProtocol);
		GridBagConstraints gbc_jLabelProtocolName = new GridBagConstraints();
		gbc_jLabelProtocolName.anchor = GridBagConstraints.WEST;
		gbc_jLabelProtocolName.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelProtocolName.gridx = 3;
		gbc_jLabelProtocolName.gridy = 0;
		add(getJLabelProtocolName(), gbc_jLabelProtocolName);
		GridBagConstraints gbc_jButtonApply = new GridBagConstraints();
		gbc_jButtonApply.anchor = GridBagConstraints.EAST;
		gbc_jButtonApply.insets = new Insets(5, 0, 5, 5);
		gbc_jButtonApply.gridx = 4;
		gbc_jButtonApply.gridy = 0;
		add(getJButtonApply(), gbc_jButtonApply);
		GridBagConstraints gbc_jButtonCancel = new GridBagConstraints();
		gbc_jButtonCancel.insets = new Insets(5, 0, 5, 0);
		gbc_jButtonCancel.gridx = 5;
		gbc_jButtonCancel.gridy = 0;
		add(getJButtonCancel(), gbc_jButtonCancel);
		GridBagConstraints gbc_propertiesPanel = new GridBagConstraints();
		gbc_propertiesPanel.insets = new Insets(0, 0, 5, 5);
		gbc_propertiesPanel.gridwidth = 6;
		gbc_propertiesPanel.fill = GridBagConstraints.BOTH;
		gbc_propertiesPanel.gridx = 0;
		gbc_propertiesPanel.gridy = 1;
		add(getPropertiesPanel(), gbc_propertiesPanel);
		GridBagConstraints gbc_jButtonTestConnection = new GridBagConstraints();
		gbc_jButtonTestConnection.anchor = GridBagConstraints.WEST;
		gbc_jButtonTestConnection.gridwidth = 2;
		gbc_jButtonTestConnection.insets = new Insets(5, 5, 0, 5);
		gbc_jButtonTestConnection.gridx = 0;
		gbc_jButtonTestConnection.gridy = 2;
		add(getJButtonTestConnection(), gbc_jButtonTestConnection);
	}

	private JLabel getJLabelConnectorName() {
		if (jLabelConnectorName == null) {
			jLabelConnectorName = new JLabel("Name:");
			jLabelConnectorName.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelConnectorName;
	}
	private JTextField getJTextFieldConnectorName() {
		if (jTextFieldConnectorName == null) {
			jTextFieldConnectorName = new JTextField();
			jTextFieldConnectorName.setColumns(10);
		}
		return jTextFieldConnectorName;
	}
	private JLabel getJLabelConnectionProtocol() {
		if (jLabelConnectionProtocol == null) {
			jLabelConnectionProtocol = new JLabel("Protocol:");
			jLabelConnectionProtocol.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelConnectionProtocol;
	}
	private PropertiesPanel getPropertiesPanel() {
		if (propertiesPanel == null) {
			propertiesPanel = new PropertiesPanel();
		}
		return propertiesPanel;
	}
	private JButton getJButtonTestConnection() {
		if (jButtonTestConnection == null) {
			jButtonTestConnection = new JButton("Test Connection");
			jButtonTestConnection.addActionListener(this);
		}
		return jButtonTestConnection;
	}
	
	private JButton getJButtonApply() {
		if (jButtonApply == null) {
			jButtonApply = new JButton(new ImageIcon(this.getClass().getResource(ICON_PATH_APPLY)));
			jButtonApply.addActionListener(this);
		}
		return jButtonApply;
	}
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton(new ImageIcon(this.getClass().getResource(ICON_PATH_CANCEL)));
			jButtonCancel.addActionListener(this);
		}
		return jButtonCancel;
	}
	/**
	 * Sets the configuration properties to the UI components.
	 * @param configuration the new configuration to UI
	 */
	public void setConfigurationToUI(ConnectorConfiguration configuration) {
		this.getJTextFieldConnectorName().setText(configuration.getName());
		this.getJLabelProtocolName().setText(configuration.getProtocol());
		
		this.getPropertiesPanel().setProperties(configuration.getConnectorProperties());
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==this.getJButtonTestConnection()) {
			//TODO test the connection
		} else if (ae.getSource()==this.getJButtonApply()) {
			//TODO check changes
			//TODO apply changes
		} else if (ae.getSource()== this.getJButtonCancel()) {
			//TODO Revert changes
		}
	}
	private JLabel getJLabelProtocolName() {
		if (jLabelProtocolName == null) {
			jLabelProtocolName = new JLabel("<Protocol>");
			jLabelProtocolName.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelProtocolName;
	}
}
