package de.enflexit.connector.core.ui;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.JTextField;
import javax.swing.JWindow;

import de.enflexit.common.properties.Properties;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.ConnectorService;
import de.enflexit.connector.core.manager.ConnectorManager;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.Color;
import javax.swing.JSeparator;

/**
 * This class provides a UI for adding new connectors.
 *
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorCreationPanel extends JWindow implements ActionListener {

	private static final long serialVersionUID = 4105793443627183974L;
	
	private static final String ICON_APPLY = "Apply.png";
	private static final String ICON_CANCEL = "Cancel.png";
	
	private JLabel jLabelManageConnections;
	private JTextField jTextFieldName;
	private JLabel jLabelName;
	private JLabel jLabelProtocol;
	private JComboBox<String> connectorServiceComboBox;
	private JLabel jLabelErrorMessage;
	private JButton jButtonCancel;
	private JButton jButtonApply;
	private JSeparator separator;
	
	ConnectorManagerMainPanel parentPanel;
	
	/**
	 * Instantiates a new creates the connection panel.
	 * @deprecated For window builder compatibility only! Please use the other constructor.
	 */
	@Deprecated
	public ConnectorCreationPanel() {
		this.initialize();
	}
	
	/**
	 * Instantiates a new creates the connection panel.
	 * @param parent the parent panel
	 */
	public ConnectorCreationPanel(Frame owner, ConnectorManagerMainPanel parentPanel) {
		super(owner);
		this.parentPanel = parentPanel;
		this.initialize();
	}
	
	
	
	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		GridBagConstraints gbc_jLabelManageConnections = new GridBagConstraints();
		gbc_jLabelManageConnections.anchor = GridBagConstraints.WEST;
		gbc_jLabelManageConnections.gridwidth = 2;
		gbc_jLabelManageConnections.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelManageConnections.gridx = 0;
		gbc_jLabelManageConnections.gridy = 0;
		getContentPane().add(getJLabelManageConnections(), gbc_jLabelManageConnections);
		GridBagConstraints gbc_jLabelName = new GridBagConstraints();
		gbc_jLabelName.anchor = GridBagConstraints.EAST;
		gbc_jLabelName.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelName.gridx = 0;
		gbc_jLabelName.gridy = 1;
		getContentPane().add(getJLabelName(), gbc_jLabelName);
		GridBagConstraints gbc_jTextFieldName = new GridBagConstraints();
		gbc_jTextFieldName.insets = new Insets(5, 0, 5, 5);
		gbc_jTextFieldName.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldName.gridx = 1;
		gbc_jTextFieldName.gridy = 1;
		getContentPane().add(getJTextFieldName(), gbc_jTextFieldName);
		GridBagConstraints gbc_jLabelProtocol = new GridBagConstraints();
		gbc_jLabelProtocol.anchor = GridBagConstraints.EAST;
		gbc_jLabelProtocol.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelProtocol.gridx = 2;
		gbc_jLabelProtocol.gridy = 1;
		getContentPane().add(getJLabelProtocol(), gbc_jLabelProtocol);
		GridBagConstraints gbc_connectorServiceComboBox = new GridBagConstraints();
		gbc_connectorServiceComboBox.anchor = GridBagConstraints.WEST;
		gbc_connectorServiceComboBox.insets = new Insets(5, 0, 5, 5);
		gbc_connectorServiceComboBox.gridx = 3;
		gbc_connectorServiceComboBox.gridy = 1;
		getContentPane().add(getConnectorServiceComboBox(), gbc_connectorServiceComboBox);
		GridBagConstraints gbc_jLabelErrorMessage = new GridBagConstraints();
		gbc_jLabelErrorMessage.insets = new Insets(0, 0, 5, 5);
		gbc_jLabelErrorMessage.gridwidth = 4;
		gbc_jLabelErrorMessage.gridx = 0;
		gbc_jLabelErrorMessage.gridy = 2;
		getContentPane().add(getJLabelErrorMessage(), gbc_jLabelErrorMessage);
		GridBagConstraints gbc_jButtonApply = new GridBagConstraints();
		gbc_jButtonApply.anchor = GridBagConstraints.EAST;
		gbc_jButtonApply.insets = new Insets(0, 0, 5, 5);
		gbc_jButtonApply.gridx = 4;
		gbc_jButtonApply.gridy = 2;
		getContentPane().add(getJButtonApply(), gbc_jButtonApply);
		GridBagConstraints gbc_jButtonCancel = new GridBagConstraints();
		gbc_jButtonCancel.insets = new Insets(0, 0, 5, 0);
		gbc_jButtonCancel.gridx = 5;
		gbc_jButtonCancel.gridy = 2;
		getContentPane().add(getJButtonCancel(), gbc_jButtonCancel);
		
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.gridwidth = 6;
		gbc_separator.insets = new Insets(0, 0, 0, 5);
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 3;
		getContentPane().add(getSeparator(), gbc_separator);
		
		parentPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				System.out.println("Parent panel hidden");
				ConnectorCreationPanel.this.hidePanel();
			}
		});
		
		this.requestFocus();
		this.setVisible(true);
	}


	private JLabel getJLabelManageConnections() {
		if (jLabelManageConnections == null) {
			jLabelManageConnections = new JLabel("New Connection:");
			jLabelManageConnections.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelManageConnections;
	}
	private JTextField getJTextFieldName() {
		if (jTextFieldName == null) {
			jTextFieldName = new JTextField();
			jTextFieldName.setColumns(10);
		}
		return jTextFieldName;
	}
	private JLabel getJLabelName() {
		if (jLabelName == null) {
			jLabelName = new JLabel("Name:");
			jLabelName.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelName;
	}
	private JLabel getJLabelProtocol() {
		if (jLabelProtocol == null) {
			jLabelProtocol = new JLabel("Protocol:");
			jLabelProtocol.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelProtocol;
	}
	private JComboBox<String> getConnectorServiceComboBox() {
		if (connectorServiceComboBox == null) {
			connectorServiceComboBox = new JComboBox<>();
			connectorServiceComboBox.setModel(this.getConnectorsComboBoxModel());
			connectorServiceComboBox.addActionListener(this);
		}
		return connectorServiceComboBox;
	}
	private DefaultComboBoxModel<String> getConnectorsComboBoxModel() {
		DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
		comboBoxModel.addElement("--- Please Select ---");
		comboBoxModel.addAll(ConnectorManager.getInstance().getAvailableConnectorServices().keySet());
		return comboBoxModel;
	}
	private JLabel getJLabelErrorMessage() {
		if (jLabelErrorMessage == null) {
			jLabelErrorMessage = new JLabel("");
			jLabelErrorMessage.setForeground(new Color(165, 42, 42));
			jLabelErrorMessage.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelErrorMessage;
	}
	private JButton getJButtonApply() {
		if (jButtonApply == null) {
			jButtonApply = new JButton(BundleHelper.getImageIcon(ICON_APPLY));
			jButtonApply.addActionListener(this);
			jButtonApply.setToolTipText("Create a new connector with this name and protocol");
			jButtonApply.setEnabled(false);
		}
		return jButtonApply;
	}
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton(BundleHelper.getImageIcon(ICON_CANCEL));
			jButtonCancel.setToolTipText("Discard without creating");
			jButtonCancel.addActionListener(this);
		}
		return jButtonCancel;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==this.getJButtonApply()) {
			this.createNewConnector();
		} else if (ae.getSource()==this.getJButtonCancel()) {
			this.hidePanel();
		} else if (ae.getSource()==this.getConnectorServiceComboBox()) {
			int selectedIndex = this.getConnectorServiceComboBox().getSelectedIndex();
			this.getJButtonApply().setEnabled(selectedIndex>0);
		}
	}
	
	private void createNewConnector() {
		String connectorName = this.getJTextFieldName().getText();
		
		// --- Check if a unique name is specified ------------------
		if (connectorName.isBlank()==true) {
			String errorMessage = "The connector name must not be empty! Please specify a unique name.";
			this.getJLabelErrorMessage().setText(errorMessage);
		} else if (ConnectorManager.getInstance().getConnectorProperies(connectorName)!=null) {
			String errorMessage = "A connector with the name " + connectorName + "already exists! Please choose a different name.";
			this.getJLabelErrorMessage().setText(errorMessage);
		} else {
			
			// --- Create the connector -----------------------------
			String protocolName = (String) this.getConnectorServiceComboBox().getSelectedItem();
			ConnectorService connectorService = ConnectorManager.getInstance().getConnectorServiceForProtocol(protocolName);
			if (connectorService!=null) {
				AbstractConnector newConnector = connectorService.getNewConnectorInstance();
				Properties connectorProperties = newConnector.getInitialProperties();
				connectorProperties.setStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_NAME, connectorName);
				newConnector.setConnectorProperties(connectorProperties);
				ConnectorManager.getInstance().addNewConnector(connectorName, newConnector);
			} else {
				System.err.println("[" + this.getClass().getSimpleName() + "] No connector service found for the protocol " + protocolName);
			}
			
			this.hidePanel();
		}
	}
	
	private void hidePanel() {
		this.getJTextFieldName().setText("");
		this.getJLabelErrorMessage().setText("");
		this.setVisible(false);
	}
	
	private JSeparator getSeparator() {
		if (separator == null) {
			separator = new JSeparator();
		}
		return separator;
	}
}
