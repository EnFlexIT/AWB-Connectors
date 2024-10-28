package de.enflexit.connector.opcua.ui.endpoint;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.X509IdentityProvider;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;

import de.enflexit.common.properties.Properties;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.manager.ConnectorManager;
import de.enflexit.connector.opcua.BundleHelper;
import de.enflexit.connector.opcua.OpcUaConnector;
import de.enflexit.connector.opcua.OpcUaHelper;

/**
 * The Class OpcUaEndpointDialg.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaEndpointPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 5469489269113194690L;

	private static final Dimension BUTTON_SIZE = new Dimension(26, 26);
	
	private OpcUaConnector opcUaConnector;
	
	private JLabel jLabelConfigName;
	private JTextField jTextFieldConfigurationName;
	private JSeparator jSeparatorConfigName;
	
	private JLabel jLabelEndpointURL;
	private JTextField jTextFieldEndpointUrl;
	private JButton jButtonDiscoverEndpointUrl;
	private JSeparator jSeparatorEndpointURL;
	
	private JLabel jLabelSecurityPolicy;
	private JComboBox<SecurityPolicy> jComboBoxSecurityPolicy;
	private JLabel jLabelMsgMode;
	private JComboBox<MessageSecurityMode> jComboBoxMsgMode;
	private JSeparator jSeparatorSecurity;

	private JRadioButton jRadioButtonAuthAnonymous;
	private JRadioButton jRadioButtonUsernamePassword;
	private JRadioButton jRadioButtonCertificate;
	
	private JLabel jLabelUsername;
	private JTextField jTextFieldUsername;
	private JLabel jLabelPassword;
	private JPasswordField jTextFieldPassword;

	private JLabel jLabelCertificate;
	private JTextField jTextFieldCertificate;
	private JButton jButtonCertificate;
	private JLabel jLabelPrivateKey;
	private JTextField jTextFieldPrivateKey;
	private JButton jButtonPrivateKey;

	private JSeparator jSeparatorAnonymous;
	private JSeparator jSeparatorUsernamePassword;

	
	/**
	 * Instantiates a new OpcUaEndpointPanel.
	 * @param opcUaConnector the opc ua connector
	 */
	public OpcUaEndpointPanel(OpcUaConnector opcUaConnector) {
		this.opcUaConnector = opcUaConnector;
		this.initialize();
		this.setPropertiesToPanel();
	}
	/**
	 * Initialize.
	 */
	private void initialize() {
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		this.setLayout(gridBagLayout);
		GridBagConstraints gbc_jLabelConfigName = new GridBagConstraints();
		gbc_jLabelConfigName.insets = new Insets(0, 0, 0, 5);
		gbc_jLabelConfigName.anchor = GridBagConstraints.WEST;
		gbc_jLabelConfigName.gridx = 0;
		gbc_jLabelConfigName.gridy = 0;
		this.add(getJLabelConfigName(), gbc_jLabelConfigName);
		GridBagConstraints gbc_jTextFieldConfigurationName = new GridBagConstraints();
		gbc_jTextFieldConfigurationName.gridwidth = 2;
		gbc_jTextFieldConfigurationName.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldConfigurationName.gridx = 1;
		gbc_jTextFieldConfigurationName.gridy = 0;
		this.add(getJTextFieldConfigurationName(), gbc_jTextFieldConfigurationName);
		GridBagConstraints gbc_jSeparatorConfigName = new GridBagConstraints();
		gbc_jSeparatorConfigName.insets = new Insets(10, 0, 5, 0);
		gbc_jSeparatorConfigName.fill = GridBagConstraints.HORIZONTAL;
		gbc_jSeparatorConfigName.gridwidth = 3;
		gbc_jSeparatorConfigName.gridx = 0;
		gbc_jSeparatorConfigName.gridy = 1;
		this.add(getJSeparatorConfigName(), gbc_jSeparatorConfigName);
		GridBagConstraints gbc_jLabelEndpointURL = new GridBagConstraints();
		gbc_jLabelEndpointURL.insets = new Insets(0, 0, 0, 5);
		gbc_jLabelEndpointURL.anchor = GridBagConstraints.WEST;
		gbc_jLabelEndpointURL.gridx = 0;
		gbc_jLabelEndpointURL.gridy = 2;
		this.add(getJLabelEndpointURL(), gbc_jLabelEndpointURL);
		GridBagConstraints gbc_jTextFieldEndpointUrl = new GridBagConstraints();
		gbc_jTextFieldEndpointUrl.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldEndpointUrl.gridx = 1;
		gbc_jTextFieldEndpointUrl.gridy = 2;
		this.add(getJTextFieldEndpointUrl(), gbc_jTextFieldEndpointUrl);
		GridBagConstraints gbc_jButtonDiscoverEndpointUrl = new GridBagConstraints();
		gbc_jButtonDiscoverEndpointUrl.gridx = 2;
		gbc_jButtonDiscoverEndpointUrl.gridy = 2;
		this.add(getJButtonDiscoverEndpointUrl(), gbc_jButtonDiscoverEndpointUrl);
		GridBagConstraints gbc_jSeparatorEndpointURL = new GridBagConstraints();
		gbc_jSeparatorEndpointURL.insets = new Insets(10, 0, 5, 0);
		gbc_jSeparatorEndpointURL.fill = GridBagConstraints.HORIZONTAL;
		gbc_jSeparatorEndpointURL.gridwidth = 3;
		gbc_jSeparatorEndpointURL.gridx = 0;
		gbc_jSeparatorEndpointURL.gridy = 3;
		this.add(getJSeparatorEndpointURL(), gbc_jSeparatorEndpointURL);
		GridBagConstraints gbc_jLabelSecurityPolicy = new GridBagConstraints();
		gbc_jLabelSecurityPolicy.insets = new Insets(0, 0, 0, 5);
		gbc_jLabelSecurityPolicy.anchor = GridBagConstraints.WEST;
		gbc_jLabelSecurityPolicy.gridx = 0;
		gbc_jLabelSecurityPolicy.gridy = 4;
		this.add(getJLabelSecurityPolicy(), gbc_jLabelSecurityPolicy);
		GridBagConstraints gbc_jComboBoxSecurityPolicy = new GridBagConstraints();
		gbc_jComboBoxSecurityPolicy.gridwidth = 2;
		gbc_jComboBoxSecurityPolicy.fill = GridBagConstraints.HORIZONTAL;
		gbc_jComboBoxSecurityPolicy.gridx = 1;
		gbc_jComboBoxSecurityPolicy.gridy = 4;
		this.add(getJComboBoxSecurityPolicy(), gbc_jComboBoxSecurityPolicy);
		GridBagConstraints gbc_jLabelMsgMode = new GridBagConstraints();
		gbc_jLabelMsgMode.insets = new Insets(0, 0, 0, 5);
		gbc_jLabelMsgMode.anchor = GridBagConstraints.WEST;
		gbc_jLabelMsgMode.gridx = 0;
		gbc_jLabelMsgMode.gridy = 5;
		this.add(getJLabelMsgMode(), gbc_jLabelMsgMode);
		GridBagConstraints gbc_jComboBoxMsgMode = new GridBagConstraints();
		gbc_jComboBoxMsgMode.gridwidth = 2;
		gbc_jComboBoxMsgMode.fill = GridBagConstraints.HORIZONTAL;
		gbc_jComboBoxMsgMode.gridx = 1;
		gbc_jComboBoxMsgMode.gridy = 5;
		this.add(getJComboBoxMsgMode(), gbc_jComboBoxMsgMode);
		GridBagConstraints gbc_jSeparatorSecurity = new GridBagConstraints();
		gbc_jSeparatorSecurity.fill = GridBagConstraints.HORIZONTAL;
		gbc_jSeparatorSecurity.insets = new Insets(10, 0, 5, 0);
		gbc_jSeparatorSecurity.gridwidth = 3;
		gbc_jSeparatorSecurity.gridx = 0;
		gbc_jSeparatorSecurity.gridy = 6;
		this.add(getJSeparatorSecurity(), gbc_jSeparatorSecurity);
		GridBagConstraints gbc_jRadioButtonAuthAnonymous = new GridBagConstraints();
		gbc_jRadioButtonAuthAnonymous.anchor = GridBagConstraints.WEST;
		gbc_jRadioButtonAuthAnonymous.gridwidth = 2;
		gbc_jRadioButtonAuthAnonymous.gridx = 0;
		gbc_jRadioButtonAuthAnonymous.gridy = 7;
		this.add(getJRadioButtonAuthAnonymous(), gbc_jRadioButtonAuthAnonymous);
		GridBagConstraints gbc_jSeparatorAnonymous = new GridBagConstraints();
		gbc_jSeparatorAnonymous.insets = new Insets(5, 0, 5, 0);
		gbc_jSeparatorAnonymous.fill = GridBagConstraints.HORIZONTAL;
		gbc_jSeparatorAnonymous.gridwidth = 3;
		gbc_jSeparatorAnonymous.gridx = 0;
		gbc_jSeparatorAnonymous.gridy = 8;
		this.add(getJSeparatorAnonymous(), gbc_jSeparatorAnonymous);
		GridBagConstraints gbc_jRadioButtonUsernamePassword = new GridBagConstraints();
		gbc_jRadioButtonUsernamePassword.anchor = GridBagConstraints.WEST;
		gbc_jRadioButtonUsernamePassword.gridwidth = 2;
		gbc_jRadioButtonUsernamePassword.gridx = 0;
		gbc_jRadioButtonUsernamePassword.gridy = 9;
		this.add(getJRadioButtonUsernamePassword(), gbc_jRadioButtonUsernamePassword);
		GridBagConstraints gbc_jLabelUsername = new GridBagConstraints();
		gbc_jLabelUsername.insets = new Insets(0, 0, 0, 5);
		gbc_jLabelUsername.anchor = GridBagConstraints.WEST;
		gbc_jLabelUsername.gridx = 0;
		gbc_jLabelUsername.gridy = 10;
		this.add(getJLabelUsername(), gbc_jLabelUsername);
		GridBagConstraints gbc_jTextFieldUsername = new GridBagConstraints();
		gbc_jTextFieldUsername.gridwidth = 2;
		gbc_jTextFieldUsername.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldUsername.gridx = 1;
		gbc_jTextFieldUsername.gridy = 10;
		this.add(getJTextFieldUsername(), gbc_jTextFieldUsername);
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.insets = new Insets(0, 0, 0, 5);
		gbc_lblPassword.anchor = GridBagConstraints.WEST;
		gbc_lblPassword.gridx = 0;
		gbc_lblPassword.gridy = 11;
		this.add(getJLabelPassword(), gbc_lblPassword);
		GridBagConstraints gbc_jTextFieldPassword = new GridBagConstraints();
		gbc_jTextFieldPassword.gridwidth = 2;
		gbc_jTextFieldPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldPassword.gridx = 1;
		gbc_jTextFieldPassword.gridy = 11;
		this.add(getJTextFieldPassword(), gbc_jTextFieldPassword);
		GridBagConstraints gbc_jSeparatorUsernamePassword = new GridBagConstraints();
		gbc_jSeparatorUsernamePassword.insets = new Insets(5, 0, 5, 0);
		gbc_jSeparatorUsernamePassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_jSeparatorUsernamePassword.gridwidth = 3;
		gbc_jSeparatorUsernamePassword.gridx = 0;
		gbc_jSeparatorUsernamePassword.gridy = 12;
		this.add(getJSeparatorUsernamePassword(), gbc_jSeparatorUsernamePassword);
		GridBagConstraints gbc_jRadioButtonCertificate = new GridBagConstraints();
		gbc_jRadioButtonCertificate.anchor = GridBagConstraints.WEST;
		gbc_jRadioButtonCertificate.gridwidth = 2;
		gbc_jRadioButtonCertificate.gridx = 0;
		gbc_jRadioButtonCertificate.gridy = 13;
		this.add(getJRadioButtonCertificate(), gbc_jRadioButtonCertificate);
		GridBagConstraints gbc_jLabelCertificate = new GridBagConstraints();
		gbc_jLabelCertificate.anchor = GridBagConstraints.WEST;
		gbc_jLabelCertificate.gridx = 0;
		gbc_jLabelCertificate.gridy = 14;
		this.add(getJLabelCertificate(), gbc_jLabelCertificate);
		GridBagConstraints gbc_jTextFieldCertificate = new GridBagConstraints();
		gbc_jTextFieldCertificate.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldCertificate.gridx = 1;
		gbc_jTextFieldCertificate.gridy = 14;
		this.add(getJTextFieldCertificate(), gbc_jTextFieldCertificate);
		GridBagConstraints gbc_jButtonCertificate = new GridBagConstraints();
		gbc_jButtonCertificate.gridx = 2;
		gbc_jButtonCertificate.gridy = 14;
		this.add(getJButtonCertificate(), gbc_jButtonCertificate);
		GridBagConstraints gbc_jLabelPrivateKey = new GridBagConstraints();
		gbc_jLabelPrivateKey.anchor = GridBagConstraints.WEST;
		gbc_jLabelPrivateKey.gridx = 0;
		gbc_jLabelPrivateKey.gridy = 15;
		this.add(getJLabelPrivateKey(), gbc_jLabelPrivateKey);
		GridBagConstraints gbc_jTextFieldPrivateKey = new GridBagConstraints();
		gbc_jTextFieldPrivateKey.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldPrivateKey.gridx = 1;
		gbc_jTextFieldPrivateKey.gridy = 15;
		this.add(getJTextFieldPrivateKey(), gbc_jTextFieldPrivateKey);
		GridBagConstraints gbc_jButtonPrivateKey = new GridBagConstraints();
		gbc_jButtonPrivateKey.gridx = 2;
		gbc_jButtonPrivateKey.gridy = 15;
		this.add(getJButtonPrivateKey(), gbc_jButtonPrivateKey);
		
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(this.getJRadioButtonAuthAnonymous());
		bg.add(this.getJRadioButtonUsernamePassword());
		bg.add(this.getJRadioButtonCertificate());
		
	}

	private JLabel getJLabelConfigName() {
		if (jLabelConfigName == null) {
			jLabelConfigName = new JLabel("Configuration Name:");
			jLabelConfigName.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelConfigName;
	}
	private JTextField getJTextFieldConfigurationName() {
		if (jTextFieldConfigurationName == null) {
			jTextFieldConfigurationName = new JTextField();
			jTextFieldConfigurationName.setFont(new Font("Dialog", Font.PLAIN, 12));
			jTextFieldConfigurationName.setColumns(10);
		}
		return jTextFieldConfigurationName;
	}
	private JLabel getJLabelEndpointURL() {
		if (jLabelEndpointURL == null) {
			jLabelEndpointURL = new JLabel("Endpoint Url:");
			jLabelEndpointURL.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelEndpointURL;
	}
	private JTextField getJTextFieldEndpointUrl() {
		if (jTextFieldEndpointUrl == null) {
			jTextFieldEndpointUrl = new JTextField();
			jTextFieldEndpointUrl.setFont(new Font("Dialog", Font.PLAIN, 12));
			jTextFieldEndpointUrl.setColumns(10);
		}
		return jTextFieldEndpointUrl;
	}
	private JButton getJButtonDiscoverEndpointUrl() {
		if (jButtonDiscoverEndpointUrl == null) {
			jButtonDiscoverEndpointUrl = new JButton();
			jButtonDiscoverEndpointUrl.setToolTipText("Discover endpoint ...");
			jButtonDiscoverEndpointUrl.setIcon(BundleHelper.getImageIcon("Search.png"));
			jButtonDiscoverEndpointUrl.setPreferredSize(BUTTON_SIZE);
			jButtonDiscoverEndpointUrl.setFont(new Font("Dialog", Font.PLAIN, 12));
			jButtonDiscoverEndpointUrl.addActionListener(this);
		}
		return jButtonDiscoverEndpointUrl;
	}
	
	
	private JLabel getJLabelSecurityPolicy() {
		if (jLabelSecurityPolicy == null) {
			jLabelSecurityPolicy = new JLabel("Security Policy:");
			jLabelSecurityPolicy.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelSecurityPolicy;
	}
	private JComboBox<SecurityPolicy> getJComboBoxSecurityPolicy() {
		if (jComboBoxSecurityPolicy == null) {
			jComboBoxSecurityPolicy = new JComboBox<>(SecurityPolicy.values());
			jComboBoxSecurityPolicy.setFont(new Font("Dialog", Font.PLAIN, 12));
			jComboBoxSecurityPolicy.addActionListener(this);
		}
		return jComboBoxSecurityPolicy;
	}
	
	private JLabel getJLabelMsgMode() {
		if (jLabelMsgMode == null) {
			jLabelMsgMode = new JLabel("Message Security Mode:");
			jLabelMsgMode.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelMsgMode;
	}
	private JComboBox<MessageSecurityMode> getJComboBoxMsgMode() {
		if (jComboBoxMsgMode == null) {
			// --- Define item list ------------- 
			Vector<MessageSecurityMode> msmItems = new Vector<>(Arrays.asList(MessageSecurityMode.values()));
			msmItems.remove(MessageSecurityMode.Invalid);
			// --- Define component -------------
			jComboBoxMsgMode = new JComboBox<>(msmItems);
			jComboBoxMsgMode.setFont(new Font("Dialog", Font.PLAIN, 12));
			jComboBoxMsgMode.addActionListener(this);
		}
		return jComboBoxMsgMode;
	}
	
	
	private JRadioButton getJRadioButtonAuthAnonymous() {
		if (jRadioButtonAuthAnonymous == null) {
			jRadioButtonAuthAnonymous = new JRadioButton("Anonymous");
			jRadioButtonAuthAnonymous.setFont(new Font("Dialog", Font.PLAIN, 12));
			jRadioButtonAuthAnonymous.addActionListener(this);
		}
		return jRadioButtonAuthAnonymous;
	}
	private JRadioButton getJRadioButtonUsernamePassword() {
		if (jRadioButtonUsernamePassword == null) {
			jRadioButtonUsernamePassword = new JRadioButton("Username + Password");
			jRadioButtonUsernamePassword.setFont(new Font("Dialog", Font.PLAIN, 12));
			jRadioButtonUsernamePassword.addActionListener(this);
		}
		return jRadioButtonUsernamePassword;
	}
	private JRadioButton getJRadioButtonCertificate() {
		if (jRadioButtonCertificate == null) {
			jRadioButtonCertificate = new JRadioButton("Certificate");
			jRadioButtonCertificate.setFont(new Font("Dialog", Font.PLAIN, 12));
			jRadioButtonCertificate.addActionListener(this);
		}
		return jRadioButtonCertificate;
	}
	private JLabel getJLabelUsername() {
		if (jLabelUsername == null) {
			jLabelUsername = new JLabel("Username");
			jLabelUsername.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelUsername;
	}
	private JLabel getJLabelPassword() {
		if (jLabelPassword == null) {
			jLabelPassword = new JLabel("Password");
			jLabelPassword.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelPassword;
	}
	private JTextField getJTextFieldUsername() {
		if (jTextFieldUsername == null) {
			jTextFieldUsername = new JTextField();
			jTextFieldUsername.setFont(new Font("Dialog", Font.PLAIN, 12));
			jTextFieldUsername.setColumns(10);
		}
		return jTextFieldUsername;
	}
	private JPasswordField getJTextFieldPassword() {
		if (jTextFieldPassword == null) {
			jTextFieldPassword = new JPasswordField();
			jTextFieldPassword.setFont(new Font("Dialog", Font.PLAIN, 12));
			jTextFieldPassword.setColumns(10);
		}
		return jTextFieldPassword;
	}
	private JLabel getJLabelCertificate() {
		if (jLabelCertificate == null) {
			jLabelCertificate = new JLabel("Certificate");
			jLabelCertificate.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelCertificate;
	}
	private JLabel getJLabelPrivateKey() {
		if (jLabelPrivateKey == null) {
			jLabelPrivateKey = new JLabel("Private Key");
			jLabelPrivateKey.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelPrivateKey;
	}
	private JTextField getJTextFieldCertificate() {
		if (jTextFieldCertificate == null) {
			jTextFieldCertificate = new JTextField();
			jTextFieldCertificate.setFont(new Font("Dialog", Font.PLAIN, 12));
			jTextFieldCertificate.setColumns(10);
		}
		return jTextFieldCertificate;
	}
	private JTextField getJTextFieldPrivateKey() {
		if (jTextFieldPrivateKey == null) {
			jTextFieldPrivateKey = new JTextField();
			jTextFieldPrivateKey.setFont(new Font("Dialog", Font.PLAIN, 12));
			jTextFieldPrivateKey.setColumns(10);
		}
		return jTextFieldPrivateKey;
	}
	private JButton getJButtonCertificate() {
		if (jButtonCertificate == null) {
			jButtonCertificate = new JButton();
			jButtonCertificate.setToolTipText("Open Certificate File");
			jButtonCertificate.setFont(new Font("Dialog", Font.PLAIN, 12));
			jButtonCertificate.setIcon(BundleHelper.getImageIcon("MBOpen.png"));
			jButtonCertificate.setPreferredSize(BUTTON_SIZE);
			jButtonCertificate.addActionListener(this);
		}
		return jButtonCertificate;
	}
	private JButton getJButtonPrivateKey() {
		if (jButtonPrivateKey == null) {
			jButtonPrivateKey = new JButton();
			jButtonPrivateKey.setToolTipText("Open File of Private Key");
			jButtonPrivateKey.setFont(new Font("Dialog", Font.PLAIN, 12));
			jButtonPrivateKey.setIcon(BundleHelper.getImageIcon("MBOpen.png"));
			jButtonPrivateKey.setPreferredSize(BUTTON_SIZE);
			jButtonPrivateKey.addActionListener(this);
		}
		return jButtonPrivateKey;
	}
	private JSeparator getJSeparatorConfigName() {
		if (jSeparatorConfigName == null) {
			jSeparatorConfigName = new JSeparator();
		}
		return jSeparatorConfigName;
	}
	private JSeparator getJSeparatorEndpointURL() {
		if (jSeparatorEndpointURL == null) {
			jSeparatorEndpointURL = new JSeparator();
		}
		return jSeparatorEndpointURL;
	}
	private JSeparator getJSeparatorSecurity() {
		if (jSeparatorSecurity == null) {
			jSeparatorSecurity = new JSeparator();
		}
		return jSeparatorSecurity;
	}
	private JSeparator getJSeparatorAnonymous() {
		if (jSeparatorAnonymous == null) {
			jSeparatorAnonymous = new JSeparator();
		}
		return jSeparatorAnonymous;
	}
	private JSeparator getJSeparatorUsernamePassword() {
		if (jSeparatorUsernamePassword == null) {
			jSeparatorUsernamePassword = new JSeparator();
		}
		return jSeparatorUsernamePassword;
	}
	
	public void setPropertiesToPanel() {
		
		// --- Exit in case of missing connector --------------------
		if (this.opcUaConnector==null) return;
		Properties properties = this.opcUaConnector.getConnectorProperties();
		
		// --- Connector name and end point URL ---------------------
		String connectorName = properties.getStringValue(OpcUaConnector.PROPERTY_KEY_CONNECTOR_NAME);
		this.getJTextFieldConfigurationName().setText(connectorName);
		
		String endpointURL = properties.getStringValue(OpcUaConnector.PROP_ENDPOINT_URL);
		this.getJTextFieldEndpointUrl().setText(endpointURL);
		
		// --- SecurityPolicy & MessageSecurityMode -----------------
		String securityPolicyProp = properties.getStringValue(OpcUaConnector.PROP_SECURITY_POLICY);
		SecurityPolicy securityPolicy = securityPolicyProp !=null ? SecurityPolicy.valueOf(securityPolicyProp) : SecurityPolicy.None;
		this.getJComboBoxSecurityPolicy().setSelectedItem(securityPolicy);
		
		String msgSecModeProp = properties.getStringValue(OpcUaConnector.PROP_SECURITY_MESSAGE_MODE);
		MessageSecurityMode msgSecMode = msgSecModeProp!=null ? MessageSecurityMode.valueOf(msgSecModeProp) : MessageSecurityMode.None;
		this.getJComboBoxMsgMode().setSelectedItem(msgSecMode);
		
		// --- Authentication method -------------------------------- 
		String idProviderName = properties.getStringValue(OpcUaConnector.PROP_AUTH_TYPE);
		switch (idProviderName) {
		case "Anonymous": 
			this.getJRadioButtonAuthAnonymous().setSelected(true);
			this.getJTextFieldUsername().setText(null);
			this.getJTextFieldPassword().setText(null);
			this.getJTextFieldCertificate().setText(null);
			this.getJTextFieldPrivateKey().setText(null);
			break;
			
		case "Username":
			this.getJRadioButtonUsernamePassword().setSelected(true);
			this.getJTextFieldUsername().setText(properties.getStringValue(OpcUaConnector.PROP_AUTH_USERNAME));
			this.getJTextFieldPassword().setText(properties.getStringValue(OpcUaConnector.PROP_AUTH_PASSWORD));
			this.getJTextFieldCertificate().setText(null);
			this.getJTextFieldPrivateKey().setText(null);
			break;
			
		case "X509Identity":
			this.getJRadioButtonCertificate().setSelected(true);
			this.getJTextFieldUsername().setText(null);
			this.getJTextFieldPassword().setText(null);
			this.getJTextFieldCertificate().setText(properties.getStringValue(OpcUaConnector.PROP_AUTH_CERTIFICATE));
			this.getJTextFieldPrivateKey().setText(properties.getStringValue(OpcUaConnector.PROP_AUTH_PRIVATE_KEY));
			break;
		
		default:
			throw new IllegalArgumentException("Unexpected value for identity provider: " + idProviderName + ". Use 'Anonymous', 'Username' or 'X509Identity'");
		}
		this.onRadioButtonSelection();
		
	}

	/**
	 * Checks for configuration errors in the current setting.
	 * @return true, if successful
	 */
	public String getConfigurationError() {

		// ----------------------------------------------------------
		// --- Check for configuration name -------------------------
		// ----------------------------------------------------------
		String newConnectorName = this.getJTextFieldConfigurationName().getText();
		if (newConnectorName==null || newConnectorName.isBlank()==true) {
			return "The connector name is not allowed to be null or empty!";
		}
		// --- Check for double name ---------------------------------		
		AbstractConnector conn = ConnectorManager.getInstance().getConnectorByName(newConnectorName);
		if (conn!=null) {
			// --- There is a connector with the new name ------------
			if (conn!=this.opcUaConnector) {
				// --- It's not me -----------------------------------
				return "The new configuration name '" + newConnectorName + "' is already used by another connector!";
			}
		}
		
		// ----------------------------------------------------------
		// --- Check for end point URL ------------------------------
		// ----------------------------------------------------------
		String newEndpointUrl = this.getJTextFieldEndpointUrl().getText();
		if (newEndpointUrl==null || newEndpointUrl.isBlank()==true) {
			return "The endpoint URL is not allowed to be null or empty!";
		}
		if ( ! (newEndpointUrl.startsWith("opc.tcp") || newEndpointUrl.startsWith("http") || newEndpointUrl.startsWith("https"))) {
			return "The endpoint URL should start with a prefix like 'opc.tcp', 'http' or 'https'!";
		}
		
		// ----------------------------------------------------------
		// --- Check authentication settings ------------------------
		// ----------------------------------------------------------
		if (this.getJRadioButtonUsernamePassword().isSelected()==true) {
			// --- user name & password authentication -------------- 
			String newUsername = this.getJTextFieldUsername().getText();
			if (newUsername==null || newUsername.isBlank()==true) {
				return "The user name is not allowed to be null or empty!";
			}
			String newPassword = new String(this.getJTextFieldPassword().getPassword());
			if (newPassword==null || newPassword.isBlank()==true) {
				return "The password is not allowed to be null or empty!";
			}
			
		} else if (this.getJRadioButtonCertificate().isSelected()==true) {
			// --- certificate authentication -----------------------
			String newCertificate = this.getJTextFieldCertificate().getText();
			if (newCertificate==null || newCertificate.isBlank()==true) {
				return "The certificate path is not allowed to be null or empty!";
			}
			String newPrivateKey = this.getJTextFieldPrivateKey().getText();
			if (newPrivateKey==null || newPrivateKey.isBlank()==true) {
				return "The private key path is not allowed to be null or empty!";
			}
			
		}

		// --- No error, return null --------------------------------
		return null;
	}
	
	/**
	 * Sets the panel to properties.
	 * @return true, if successful
	 */
	public boolean setPanelToProperties() {
		
		// --- Exit in case of missing connector --------------------
		if (this.opcUaConnector==null) return false;
		Properties properties = this.opcUaConnector.getConnectorProperties();
		
		// --- Avoid saving mis-configuration -----------------------
		boolean isErrorFree = (this.getConfigurationError()==null);
		if (isErrorFree==false) return false;
		
		// --- Connector name and end point URL ---------------------
		String oldConfigName = properties.getStringValue(OpcUaConnector.PROPERTY_KEY_CONNECTOR_NAME);
		String newConfigName = this.getJTextFieldConfigurationName().getText();
		properties.setStringValue(OpcUaConnector.PROPERTY_KEY_CONNECTOR_NAME, newConfigName);
		properties.setStringValue(OpcUaConnector.PROP_ENDPOINT_URL, this.getJTextFieldEndpointUrl().getText());
		
		// --- SecurityPolicy & MessageSecurityMode -----------------
		properties.setStringValue(OpcUaConnector.PROP_SECURITY_POLICY, this.getJComboBoxSecurityPolicy().getSelectedItem().toString());
		properties.setStringValue(OpcUaConnector.PROP_SECURITY_MESSAGE_MODE, this.getJComboBoxMsgMode().getSelectedItem().toString());

		// --- Authentication method --------------------------------
		if (this.getJRadioButtonAuthAnonymous().isSelected()==true) {
			properties.setStringValue(OpcUaConnector.PROP_AUTH_TYPE, OpcUaHelper.getIdentityProviderName(AnonymousProvider.class));
			properties.remove(OpcUaConnector.PROP_AUTH_USERNAME);
			properties.remove(OpcUaConnector.PROP_AUTH_PASSWORD);
			properties.remove(OpcUaConnector.PROP_AUTH_CERTIFICATE);
			properties.remove(OpcUaConnector.PROP_AUTH_PRIVATE_KEY);
			
		} else if (this.getJRadioButtonUsernamePassword().isSelected()==true) {
			properties.setStringValue(OpcUaConnector.PROP_AUTH_TYPE, OpcUaHelper.getIdentityProviderName(UsernameProvider.class));
			properties.setStringValue(OpcUaConnector.PROP_AUTH_USERNAME, this.getJTextFieldUsername().getText());
			properties.setStringValue(OpcUaConnector.PROP_AUTH_PASSWORD, new String(this.getJTextFieldPassword().getPassword()));
			properties.remove(OpcUaConnector.PROP_AUTH_CERTIFICATE);
			properties.remove(OpcUaConnector.PROP_AUTH_PRIVATE_KEY);
			
		} else if (this.getJRadioButtonCertificate().isSelected()==true) {
			properties.setStringValue(OpcUaConnector.PROP_AUTH_TYPE, OpcUaHelper.getIdentityProviderName(X509IdentityProvider.class));
			properties.remove(OpcUaConnector.PROP_AUTH_USERNAME);
			properties.remove(OpcUaConnector.PROP_AUTH_PASSWORD);
			properties.setStringValue(OpcUaConnector.PROP_AUTH_CERTIFICATE, this.getJTextFieldCertificate().getText());
			properties.setStringValue(OpcUaConnector.PROP_AUTH_PRIVATE_KEY, this.getJTextFieldPrivateKey().getText());
			
		}
		
		// --- Ensure that the changes are saved --------------------
		ConnectorManager.getInstance().updateConnectorProperties(oldConfigName, properties);
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {

		if (ae.getSource()==this.getJButtonDiscoverEndpointUrl()) {
			
		} else if (ae.getSource()==this.getJButtonCertificate()) {
			
		} else if (ae.getSource()==this.getJButtonPrivateKey()) {
			
			
		} else if (ae.getSource()==this.getJComboBoxSecurityPolicy()) {
			// --- Nothing to do here yet -----------------
		} else if (ae.getSource()==this.getJComboBoxMsgMode()) {
			// --- Nothing to do here yet -----------------			
		
		} else if (ae.getSource()==this.getJRadioButtonAuthAnonymous() || ae.getSource()==this.getJRadioButtonUsernamePassword() || ae.getSource()==this.getJRadioButtonCertificate()) {
			this.onRadioButtonSelection();
		}
		
	}
	
	/**
	 * On radio button selection.
	 */
	private void onRadioButtonSelection() {
		
		//boolean isEnabledAnnonymous = this.getJRadioButtonAuthAnonymous().isSelected();
		boolean isEnabledUsrPswd = this.getJRadioButtonUsernamePassword().isSelected();
		boolean isEnabledCertificate = this.getJRadioButtonCertificate().isSelected();
		
		this.getJLabelUsername().setEnabled(isEnabledUsrPswd);
		this.getJTextFieldUsername().setEnabled(isEnabledUsrPswd);
		this.getJLabelPassword().setEnabled(isEnabledUsrPswd);
		this.getJTextFieldPassword().setEnabled(isEnabledUsrPswd);
		
		this.getJLabelCertificate().setEnabled(isEnabledCertificate);
		this.getJTextFieldCertificate().setEnabled(isEnabledCertificate);
		this.getJButtonCertificate().setEnabled(isEnabledCertificate);
		this.getJLabelPrivateKey().setEnabled(isEnabledCertificate);
		this.getJTextFieldPrivateKey().setEnabled(isEnabledCertificate);
		this.getJButtonPrivateKey().setEnabled(isEnabledCertificate);
		
	}
	
	
}
