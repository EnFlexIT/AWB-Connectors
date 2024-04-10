package de.enflexit.connector.core.ui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.enflexit.connector.core.ConnectorConfiguration;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.ConnectorManager;
import de.enflexit.connector.core.ConnectorService;

import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JLabel;

/**
 * The main panel for the {@link ConnectorManager}'s configuration UI.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorManagerMainPanel extends JPanel implements ActionListener, ListSelectionListener, PropertyChangeListener {
	
	private static final long serialVersionUID = 3162788243111915591L;
	
	private static final String ICON_PATH_ADD = "/icons/ListPlus.png";
	private static final String ICON_PATH_REMOVE = "/icons/ListMinus.png";
	
	private JSplitPane jSplitPane;
	private JScrollPane jScrollPane;
	private JList<String> connectorsList;
	private DefaultListModel<String> connectorsListModel;
	private JToolBar jToolBarManageConnections;
	private JButton jButtonAddNew;
	private ConnectorServiceComboBox jComboBoxSelectConnector;
//	private JComboBox<String> jComboBoxSelectConnector;
	private JButton jButtonRemoveSelected;
	private ConnectorConfigurationPanel connectorConfogurationPanel;
	private JLabel jLabelAddConnector;
	
	/**
	 * Instantiates a new connector manager main panel.
	 */
	public ConnectorManagerMainPanel() {
		initialize();
	}
	
	/**
	 * Initializes the UI components.
	 */
	private void initialize() {
		this.setLayout(new BorderLayout(0, 0));
		this.add(getJToolBarManageConnections(), BorderLayout.NORTH);
		this.add(getJSplitPane(), BorderLayout.CENTER);
		ConnectorManager.getInstance().addListener(this);
	}
	
	/**
	 * Gets the j split pane.
	 * @return the j split pane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setLeftComponent(getJScrollPane());
			jSplitPane.setRightComponent(getConnectorConfogurationPanel());
		}
		return jSplitPane;
	}
	
	/**
	 * Gets the j scroll pane.
	 * @return the j scroll pane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getConnectorsList());
		}
		return jScrollPane;
	}
	
	/**
	 * Gets the connectors list.
	 * @return the connectors list
	 */
	private JList<String> getConnectorsList() {
		if (connectorsList == null) {
			connectorsList = new JList<String>();
			connectorsList.setModel(this.getConnectorsListModel());
			connectorsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			connectorsList.addListSelectionListener(this);
		}
		return connectorsList;
	}
	
	/**
	 * Gets the connectors list model.
	 * @return the connectors list model
	 */
	private DefaultListModel<String> getConnectorsListModel() {
		if (connectorsListModel==null) {
			connectorsListModel = new DefaultListModel<String>();
			for (String connectorName : ConnectorManager.getInstance().getConnectorNames()) {
				connectorsListModel.addElement(connectorName);
			}
		}
		return connectorsListModel;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==this.getJButtonAddNew()) {
			this.addNewConnection();
		} else if (ae.getSource()==this.getJButtonRemoveSelected()) {
			//TODO implement removing the selected connectors
		}
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource()==ConnectorManager.getInstance() && evt.getPropertyName().equals(ConnectorManager.CONNECTOR_ADDED)) {
			String newConnectorName = (String) evt.getNewValue();
			this.getConnectorsListModel().addElement(newConnectorName);
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent lse) {
		if (lse.getSource()==this.getConnectorsList()) {
			String selectedConnectorName = this.getConnectorsList().getSelectedValue();
			AbstractConnector selectedConnector = ConnectorManager.getInstance().getConnector(selectedConnectorName);
			ConnectorConfiguration connectorConfig = selectedConnector.getConnectorConfiguration();
			this.getConnectorConfogurationPanel().setConfigurationToUI(connectorConfig);
		}
	}
	
	private JToolBar getJToolBarManageConnections() {
		if (jToolBarManageConnections == null) {
			jToolBarManageConnections = new JToolBar();
			jToolBarManageConnections.setFloatable(false);
			jToolBarManageConnections.add(getJLabelAddConnector());
			jToolBarManageConnections.add(getJComboBoxSelectConnector());
			jToolBarManageConnections.add(getJButtonAddNew());
			jToolBarManageConnections.addSeparator();
			jToolBarManageConnections.add(getJButtonRemoveSelected());
		}
		return jToolBarManageConnections;
	}
	private JButton getJButtonAddNew() {
		if (jButtonAddNew == null) {
			jButtonAddNew = new JButton(new ImageIcon(this.getClass().getResource(ICON_PATH_ADD)));
			jButtonAddNew.setToolTipText("Add a new connection with the selected protocol");
			jButtonAddNew.addActionListener(this);
		}
		return jButtonAddNew;
	}
	private ConnectorServiceComboBox getJComboBoxSelectConnector() {
		if (jComboBoxSelectConnector == null) {
			jComboBoxSelectConnector = new ConnectorServiceComboBox();
		}
		return jComboBoxSelectConnector;
	}
//	private JComboBox<String> getJComboBoxSelectConnector() {
//		if (jComboBoxSelectConnector == null) {
//			jComboBoxSelectConnector = new JComboBox<String>();
//		}
//		return jComboBoxSelectConnector;
//	}
	private JButton getJButtonRemoveSelected() {
		if (jButtonRemoveSelected == null) {
			jButtonRemoveSelected = new JButton(new ImageIcon(this.getClass().getResource(ICON_PATH_REMOVE)));
			jButtonRemoveSelected.setToolTipText("Remove the currently selected connection");
			jButtonRemoveSelected.addActionListener(this);
		}
		return jButtonRemoveSelected;
	}
	private ConnectorConfigurationPanel getConnectorConfogurationPanel() {
		if (connectorConfogurationPanel == null) {
			connectorConfogurationPanel = new ConnectorConfigurationPanel();
		}
		return connectorConfogurationPanel;
	}
	
	/**
	 * Adds a new connection of the selected type.
	 */
	private void addNewConnection() {
		ConnectorService connectorService = (ConnectorService) this.getJComboBoxSelectConnector().getSelectedItem();
		String connectorName = this.createInitialName(connectorService.getProtocolName());
		AbstractConnector newConnector = connectorService.getNewConnectorInstance();
		ConnectorConfiguration config = connectorService.getNewConfigurationInstance();
		config.setName(connectorName);
		config.setProtocol(connectorService.getProtocolName());
		config.setConnectorProperties(config.getInitialProperties());
		newConnector.setConnectorConfiguration(config);
		ConnectorManager.getInstance().addNewConnector(connectorName, newConnector);
				
	}
	
	/**
	 * Creates an initial name for a new connection with the specified protocol.
	 * @param protocol the protocol
	 * @return the string
	 */
	private String createInitialName(String protocol) {
		String baseName = "New " + protocol + " connection";
		String connectorName = baseName;
		int suffixCounter = 0;
		while (ConnectorManager.getInstance().getConnector(connectorName)!=null) {
			suffixCounter++;
			String suffixString =  " (" + suffixCounter + ")";
			connectorName = baseName + suffixString;
		}
		return connectorName;
	}
	private JLabel getJLabelAddConnector() {
		if (jLabelAddConnector == null) {
			jLabelAddConnector = new JLabel("New connection");
			jLabelAddConnector.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelAddConnector;
	}
}
