package de.enflexit.connector.core.ui;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.enflexit.common.properties.Properties;
import de.enflexit.common.properties.PropertiesEvent;
import de.enflexit.common.properties.PropertiesListener;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.ConnectorService;
import de.enflexit.connector.core.manager.ConnectorManager;

import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JComponent;

/**
 * The main panel for the {@link ConnectorManager}'s configuration UI.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorManagerMainPanel extends JPanel implements ActionListener, ListSelectionListener, PropertyChangeListener, PropertiesListener {
	
	private static final long serialVersionUID = 3162788243111915591L;
	
	private static final String ICON_ADD = "ListPlus.png";
	private static final String ICON_REMOVE = "ListMinus.png";
	private static final String ICON_APPLY = "Apply.png";
	private static final String ICON_RESET = "Reset.png";
	private static final String ICON_START = "Start.png";
	private static final String ICON_STOP = "Stop.png";
	private static final String ICON_RESTART = "Restart.png";
	
	private JToolBar mainToolBar;
	private JButton jButtonAdd;
	private JButton jButtonRemove;
	private JButton jButtonStart;
	private JButton jButtonStop;
	private JButton jButtonRestart;
	
	private JPopupMenu jPopupMenuNewConnection;
	
	private JSplitPane mainSplitPane;
	private JScrollPane jScrollPane;
	private JList<String> connectorsList;
	private DefaultListModel<String> connectorsListModel;
	
	private ConnectorConfigurationPanel manageConnectionPanel;
	
	private String selectedConnectorName;
	
	private boolean skipPendingChangesQuestion = false;
	private JButton jButtonApply;
	private JButton jButtonDiscard;
	
	private boolean configChanged;
	
	/**
	 * Instantiates a new connector manager main panel.
	 */
	public ConnectorManagerMainPanel() {
		this.initialize();
	}
	
	/**
	 * Initializes the UI components.
	 */
	private void initialize() {
		this.setLayout(new BorderLayout(0, 0));
		this.add(this.getMainSplitPane(), BorderLayout.CENTER);
		this.add(this.getMainToolBar(), BorderLayout.NORTH);
		ConnectorManager.getInstance().addListener(this);
	}
	
	private JToolBar getMainToolBar() {
		if (mainToolBar == null) {
			mainToolBar = new JToolBar();
			mainToolBar.setFloatable(false);
			mainToolBar.add(getJButtonAdd());
			mainToolBar.add(getJButtonRemove());
			mainToolBar.addSeparator();
			mainToolBar.add(getJButtonApply());
			mainToolBar.add(getJButtonDiscard());
			mainToolBar.addSeparator();
			mainToolBar.add(getJButtonStart());
			mainToolBar.add(getJButtonStop());
			mainToolBar.add(getJButtonRestart());
		}
		return mainToolBar;
	}

	private JButton getJButtonAdd() {
		if (jButtonAdd == null) {
			jButtonAdd = new JButton(BundleHelper.getImageIcon(ICON_ADD));
			jButtonAdd.setToolTipText("Add a new connector");
			jButtonAdd.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					ConnectorManagerMainPanel.this.showJPopupMenuAddNewConnection();
				}
				@Override
				public void mousePressed(MouseEvent e) {
					ConnectorManagerMainPanel.this.showJPopupMenuAddNewConnection();
				}
			});
		}
		return jButtonAdd;
	}

	private JButton getJButtonRemove() {
		if (jButtonRemove == null) {
			jButtonRemove = new JButton(BundleHelper.getImageIcon(ICON_REMOVE));
			jButtonRemove.setToolTipText("Remove the selected connector");
			jButtonRemove.setEnabled(false);
			jButtonRemove.addActionListener(this);
		}
		return jButtonRemove;
	}

	private JButton getJButtonApply() {
		if (jButtonApply == null) {
			jButtonApply = new JButton(BundleHelper.getImageIcon(ICON_APPLY));
			jButtonApply.setToolTipText("Apply changes to the selected connector");
			jButtonApply.setEnabled(false);
			jButtonApply.addActionListener(this);
		}
		return jButtonApply;
	}

	private JButton getJButtonDiscard() {
		if (jButtonDiscard == null) {
			jButtonDiscard = new JButton(BundleHelper.getImageIcon(ICON_RESET));
			jButtonDiscard.setToolTipText("Discard changes to the selected connection");
			jButtonDiscard.setEnabled(false);
			jButtonDiscard.addActionListener(this);
		}
		return jButtonDiscard;
	}

	private JButton getJButtonStart() {
		if (jButtonStart == null) {
			jButtonStart = new JButton(BundleHelper.getImageIcon(ICON_START));
			jButtonStart.setToolTipText("Start the selected connector");
			jButtonStart.setEnabled(false);
			jButtonStart.addActionListener(this);
		}
		return jButtonStart;
	}

	private JButton getJButtonStop() {
		if (jButtonStop == null) {
			jButtonStop = new JButton(BundleHelper.getImageIcon(ICON_STOP));
			jButtonStop.setToolTipText("Stop the selected connector");
			jButtonStop.setEnabled(false);
			jButtonStop.addActionListener(this);
		}
		return jButtonStop;
	}

	private JButton getJButtonRestart() {
		if (jButtonRestart == null) {
			jButtonRestart = new JButton(BundleHelper.getImageIcon(ICON_RESTART));
			jButtonRestart.setToolTipText("Restart the selected connector");
			jButtonRestart.setEnabled(false);
			jButtonRestart.addActionListener(this);
		}
		return jButtonRestart;
	}
	
	
	private void showJPopupMenuAddNewConnection() {
		this.getjPopupMenuNewConnection().show(this.getJButtonAdd(), 0, this.getJButtonAdd().getHeight());
	}
	
	private JPopupMenu getjPopupMenuNewConnection() {
		if (jPopupMenuNewConnection==null) {
			jPopupMenuNewConnection = new JPopupMenu();
			
			HashMap<String, ConnectorService> availableServices = ConnectorManager.getInstance().getAvailableConnectorServices();
			for (ConnectorService connectorService : availableServices.values()) {
				CreateConnectorAction action = new CreateConnectorAction(connectorService);
				jPopupMenuNewConnection.add(action);
			}
			
		}
		return jPopupMenuNewConnection;
	}

	/**
	 * Gets the j split pane.
	 * @return the j split pane
	 */
	private JSplitPane getMainSplitPane() {
		if (mainSplitPane == null) {
			mainSplitPane = new JSplitPane();
			mainSplitPane.setLeftComponent(getJScrollPane());
			mainSplitPane.setRightComponent(getConfigurationPanel());
			mainSplitPane.setDividerLocation(250);
			mainSplitPane.setDividerSize(5);
			mainSplitPane.setResizeWeight(0);
		}
		return mainSplitPane;
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
			connectorsList.setCellRenderer(new ConnectorsListCellRenderer());
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
			for (String connectorName : ConnectorManager.getInstance().getConfiguredConnectorNames()) {
				connectorsListModel.addElement(connectorName);
			}
		}
		return connectorsListModel;
	}
	
	protected ConnectorConfigurationPanel getConfigurationPanel() {
		if (manageConnectionPanel == null) {
			manageConnectionPanel = new ConnectorConfigurationPanel();
		}
		return manageConnectionPanel;
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource()==ConnectorManager.getInstance()) {
			
			if (evt.getPropertyName().equals(ConnectorManager.CONNECTOR_ADDED)) {
				String newConnectorName = (String) evt.getNewValue();
				this.getConnectorsListModel().addElement(newConnectorName);
				int newElementIndex = this.getIndexOfListElement(newConnectorName);
				if (newElementIndex>=0) {
					this.getConnectorsList().setSelectedIndex(newElementIndex);
				}
			} else if (evt.getPropertyName().equals(ConnectorManager.CONNECTOR_REMOVED)) {
				String removedConnectorName = (String) evt.getOldValue();
				int elementIndex = this.getIndexOfListElement(removedConnectorName);
				if (elementIndex>-1) {
					this.getConnectorsList().clearSelection();
					this.getConnectorsListModel().remove(elementIndex);
				}
				
			} else if (evt.getPropertyName().equals(ConnectorManager.CONNECTOR_RENAMED)) {
				this.skipPendingChangesQuestion = true;
				String oldName = (String) evt.getOldValue();
				String newName = (String) evt.getNewValue();
				int elementIndex = this.getIndexOfListElement(oldName);
				if (elementIndex>-1) {
					this.getConnectorsList().clearSelection();
					this.getConnectorsListModel().remove(elementIndex);
					if (elementIndex<this.getConnectorsListModel().getSize()) {
						this.getConnectorsListModel().add(elementIndex, newName);
					} else {
						this.getConnectorsListModel().addElement(newName);
					}
					this.getConnectorsList().setSelectedIndex(elementIndex);
				}
				this.skipPendingChangesQuestion = false;
			}
		}
	}
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent lse) {
		
		if (lse.getSource()==this.getConnectorsList() && lse.getValueIsAdjusting()==false) {
			if (this.selectedConnectorName!=null) {
				if (this.isConfigChanged()==true && this.skipPendingChangesQuestion==false) {
					String userMessage = "Your current configuration has pending changes! Apply before switching?";
					int userReply = JOptionPane.showConfirmDialog(this, userMessage, "Apply changes?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (userReply==JOptionPane.YES_OPTION) {
						this.applyChanges();
					}
				}
			}
			
			String connectorName = this.getConnectorsList().getSelectedValue();
			this.setSelectedConnector(connectorName);
		}
	}
	/**
	 * Sets the selected connector.
	 * @param connectorName the new selected connector
	 */
	private void setSelectedConnector(String connectorName) {
		this.selectedConnectorName = connectorName;
		if (connectorName!=null) {
			// --- Get the properties for the selected connector and set them to the panel --------
			Properties propertiesToSet = ConnectorManager.getInstance().getConnectorProperties(connectorName);
			this.setPropertiesToEdit(propertiesToSet);
		} else {
			// --- No connector selected, clear the panel -----------------------------------------
			this.setPropertiesToEdit(null);
		}
	}
	/**
	 * Sets the current properties to edit.
	 * @param connectorProperties the new properties to edit
	 */
	private void setPropertiesToEdit(Properties connectorProperties) {
		
		int dividerPos = this.getMainSplitPane().getDividerLocation();
		
		// --- Unsubscribe from the property events of the previously selected connection ---------
		if (this.getConfigurationPanel().getConnectorProperties()!=null) {
			this.getConfigurationPanel().getConnectorProperties().removePropertiesListener(this);
		}

		if (connectorProperties!=null) {
			// --- Create a working copy for editing ? --------------------------------------------
			//Properties workingInstance = SerialClone.clone(connectorProperties);
			//workingInstance.addPropertiesListener(this);
			
			this.getConfigurationPanel().setConnectorProperties(connectorProperties);
			connectorProperties.addPropertiesListener(this);
			
			// --- If the connector instance is available, use its UI component -------------------
			AbstractConnector connectorInstance = ConnectorManager.getInstance().getConnectorByName(connectorProperties.getStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_NAME));
			if (connectorInstance!=null) {
				JComponent connectorUI = connectorInstance.getConfigurationUIComponent(this.getConfigurationPanel());
				if (connectorUI==null) {
					connectorUI = this.getConfigurationPanel();
				} 
				this.getMainSplitPane().setRightComponent(connectorUI);
				
			} else {
				// --- If not, e.g. because the corresponding service is not available, use the default panel.
				this.getMainSplitPane().setRightComponent(this.getConfigurationPanel());
			}
			
		} else {
			this.getConfigurationPanel().setConnectorProperties(null);
		}
		
		this.getMainSplitPane().setDividerLocation(dividerPos);
		this.updateButtonState();
	}
	
	protected void dispose() {
		
		ConnectorManager.getInstance().removeListener(this);
		
		// --- If the connectors provided a custom configuration UI, dispose those too
		for (String connectorName : ConnectorManager.getInstance().getConfiguredConnectorNames()) {
			AbstractConnector connector = ConnectorManager.getInstance().getConnectorByName(connectorName);
			if (connector!=null) {
				connector.disposeUI();
			}
		}
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==this.getJButtonAdd()) {
			this.getjPopupMenuNewConnection().show(this.getJButtonAdd(), 0, this.getJButtonAdd().getHeight());
		} else if (ae.getSource()==this.getJButtonRemove()) {
			this.deleteConnection();
		} else if (ae.getSource()==this.getJButtonApply()) {
			this.applyChanges();
		} else if (ae.getSource()==this.getJButtonDiscard()) {
			this.discardChanges();
		} else if (ae.getSource()==this.getJButtonStart()) {
			this.startConnection();
		} else if (ae.getSource()==this.getJButtonStop()) {
			this.stopConnection();
		} else if (ae.getSource()==this.getJButtonRestart()) {
			this.stopConnection();
			this.startConnection();
		}
	}
	
	/**
	 * Gets the index of list element.
	 * @param element the element
	 * @return the index of list element
	 */
	private int getIndexOfListElement(String element) {
		for (int i=0; i<this.getConnectorsListModel().getSize(); i++) {
			if (this.getConnectorsListModel().elementAt(i).equals(element)) {
				return i;
			}
		}
		return -1;
	}

	private void updateButtonState() {
		if (this.selectedConnectorName==null) {
			// --- No connector selected ----------------------------
			this.getJButtonRemove().setEnabled(false);
			this.getJButtonStart().setEnabled(false);
			this.getJButtonStop().setEnabled(false);
			this.getJButtonRestart().setEnabled(false);
			
		} else if (this.getSelectedConnector()==null) {
			// --- The selected connector is not available ----------
			this.getJButtonRemove().setEnabled(true);
			this.getJButtonStart().setEnabled(false);
			this.getJButtonStop().setEnabled(false);
			this.getJButtonRestart().setEnabled(false);
			
		} else if (this.getSelectedConnector().isConnected()==true) {
			// --- The selected connector is connected --------------
			this.getJButtonRemove().setEnabled(false);
			this.getJButtonStart().setEnabled(false);
			this.getJButtonStop().setEnabled(true);
			this.getJButtonRestart().setEnabled(true);
			
		} else {
			// --- The selected connector is not connected ----------
			this.getJButtonRemove().setEnabled(true);
			this.getJButtonStart().setEnabled(true);
			this.getJButtonStop().setEnabled(false);
			this.getJButtonRestart().setEnabled(false);
		}
	}
	
	private void startConnection() {
		if (this.getSelectedConnector()!=null) {
			boolean success = this.getSelectedConnector().connect();
			if (success==false) {
				JOptionPane.showMessageDialog(this, "Failed to open the connection! Please check your settings.", "Connection failed", JOptionPane.ERROR_MESSAGE);
			} else {
				this.updateButtonState();
			}
		} else {
			JOptionPane.showMessageDialog(this, "Failed to instantiate the connector! Is there a connector service for the cnfigured protocol?", "Not available", JOptionPane.ERROR_MESSAGE);
		}
		
		ArrayList<AbstractConnector> connectors = ConnectorManager.getInstance().getConnectorsByProtocol("MQTT");
		for (AbstractConnector connector : connectors) {
			System.out.println(connector.getConnectorProperties().getStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_NAME) + " - connected: " + connector.isConnected());
		}
		
	}
	private void stopConnection() {
		if (this.getSelectedConnector()!=null) {
			this.getSelectedConnector().disconnect();
			this.updateButtonState();
		}
	}
	
	private void deleteConnection() {
		
		if (this.isCurrentlyConnected()==true) {
			JOptionPane.showMessageDialog(this, "The selected connection is currently active! Please disconnect before deleting.", "Currently connected!", JOptionPane.WARNING_MESSAGE);
		} else {
			int answer = JOptionPane.showConfirmDialog(this, "This will delete the selected connection - are you sure?", "Confirm delete", JOptionPane.YES_NO_OPTION);
			if (answer==JOptionPane.YES_OPTION) {
				String connectorName = this.selectedConnectorName;
				ConnectorManager.getInstance().removeConnector(connectorName);
			}
		}
	}
	
	private AbstractConnector getSelectedConnector() {
		if (this.selectedConnectorName!=null) {
			return ConnectorManager.getInstance().getConnectorByName(this.selectedConnectorName);
		} else {
			return null;
		}
	}
	
	private boolean isCurrentlyConnected() {
		if (this.getSelectedConnector()!=null) {
			return this.getSelectedConnector().isConnected();
		} else {
			return false;
		}
	}
	
	/**
	 * This action class handles the creation of new connectors.
	 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
	 */
	private class CreateConnectorAction extends AbstractAction {
		
		private static final long serialVersionUID = 1953290624795078546L;
		
		private ConnectorService connectorService;

		public CreateConnectorAction(ConnectorService connectorService) {
			super("New " + connectorService.getProtocolName() + " connection");
			this.connectorService = connectorService;
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {

			String connectorName = this.askForConnectorName();
			
			if (connectorName!=null) {
				this.createConnector(connectorName);
			} else {
				System.out.println("User abort");
			}
			
		}
		
		/**
		 * Asks the user for a connector name, checks if the provided name is valid.
		 * @return the name provided by the user, null if the user canceled the dialog
		 */
		private String askForConnectorName() {
			String dialogMessage = "Please enter a unique name for the new connection";
			String connectorName = null;
			
			boolean nameValid = false;
			while (nameValid==false) {
				connectorName = JOptionPane.showInputDialog(ConnectorManagerMainPanel.this, dialogMessage, "New " + this.connectorService.getProtocolName()  + " connector", JOptionPane.QUESTION_MESSAGE);
				
				// --- User abort -------------------------
				if (connectorName==null) return null;
				
				if (connectorName.isBlank()==true) {
					// --- No name was entered ------------
					dialogMessage = "The connector  name cannot be empty, please enter a unique name!";
					
				} else if (ConnectorManager.getInstance().getConnectorProperties(connectorName)!=null) { 
					// --- The name is already in use -----
					dialogMessage = "The name is already in use, please choose a different name!";
					
				} else {
					// --- Name accepted ------------------
					nameValid=true;
				}
			}
			
			return connectorName;
		}
		
		/**
		 * Creates a new connector with the provided  name, and adds it to the {@link ConnectorManager}.
		 * @param connectorName the connector name
		 */
		private void createConnector(String connectorName) {
			AbstractConnector newConnector = connectorService.getNewConnectorInstance();
			Properties connectorProperties = newConnector.getInitialProperties();
			connectorProperties.setStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_NAME, connectorName);
			connectorProperties.setStringValue(AbstractConnector.PROPERTY_KEY_CONNECTOR_PROTOCOL, this.connectorService.getProtocolName());
			newConnector.setConnectorProperties(connectorProperties);
			ConnectorManager.getInstance().addNewConnector(connectorName, newConnector);
		}
		
	}
	
	
	/**
	 * Apples all changes to the current connector configuration.
	 */
	protected void applyChanges() {

		//--- Update the properties in the connector manager --------
		Properties editedProperties = this.getConfigurationPanel().getConnectorProperties();
		ConnectorManager.getInstance().updateConnectorProperties(this.selectedConnectorName, editedProperties);
		this.setConfigChanged(false);
		
		// --- If the connector has been instantiated already, update that instance, too ----------
		AbstractConnector connector = ConnectorManager.getInstance().getConnectorByName(this.selectedConnectorName);
		if (connector!=null) {
			connector.setConnectorProperties(editedProperties);
			
			// --- Check if currently connected. Is so, ask for reconnect to apply changes --------
			if (connector.isConnected()) {
				String message = "The connection you modified is currently active! Reconnect now to apply the changes immediately?";
				int userResponse = JOptionPane.showConfirmDialog(this, message, "Reconnect now?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (userResponse==JOptionPane.YES_OPTION) {
					connector.disconnect();
					connector.connect();
				}
			}
		}
	}
	
	/**
	 * Discards all changes to the current connector configuration.
	 */
	private void discardChanges() {
		int userResponse = JOptionPane.showConfirmDialog(this, "This will discard your changes! Are you sure?", "Discard changes?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (userResponse==JOptionPane.YES_OPTION) {
			// --- Replace with the original properties from the connector ----
			Properties originalProperties = ConnectorManager.getInstance().getConnectorProperties(this.selectedConnectorName);
			this.setPropertiesToEdit(originalProperties);
			this.setConfigChanged(false);
		}
	}

	/**
	 * Checks if the selected configuration is changed.
	 * @return true, if is config changed
	 */
	public boolean isConfigChanged() {
		return configChanged;
	}
	/**
	 * Sets the configuration changed state, also enables/disables related buttons.
	 * @param configChanged the new config changed
	 */
	private void setConfigChanged(boolean configChanged) {
		this.configChanged = configChanged;
		
		// --- These buttons only make sense when the configuration is changed
		this.getJButtonApply().setEnabled(configChanged);
		this.getJButtonDiscard().setEnabled(configChanged);
	}
	/* (non-Javadoc)
	 * @see de.enflexit.common.properties.PropertiesListener#onPropertiesEvent(de.enflexit.common.properties.PropertiesEvent)
	 */
	@Override
	public void onPropertiesEvent(PropertiesEvent propertiesEvent) {
		this.setConfigChanged(true);
	}
}
