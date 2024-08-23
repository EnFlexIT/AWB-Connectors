package de.enflexit.connector.core.ui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.enflexit.common.properties.Properties;
import de.enflexit.common.swing.OwnerDetection;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.manager.ConnectorManager;

import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JComponent;

/**
 * The main panel for the {@link ConnectorManager}'s configuration UI.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorManagerMainPanel extends JPanel implements ActionListener, ListSelectionListener, PropertyChangeListener {
	
	private static final long serialVersionUID = 3162788243111915591L;
	
	private static final String ICON_ADD = "ListPlus.png";
	private static final String ICON_REMOVE = "ListMinus.png";
	private static final String ICON_START = "Start.png";
	private static final String ICON_STOP = "Stop.png";
	private static final String ICON_RESTART = "Restart.png";
	
	private JToolBar mainToolBar;
	private JButton jButtonAdd;
	private JButton jButtonRemove;
	private JButton jButtonStart;
	private JButton jButtonStop;
	private JButton jButtonRestart;
	
	private JSplitPane mainSplitPane;
	private JScrollPane jScrollPane;
	private JList<String> connectorsList;
	private DefaultListModel<String> connectorsListModel;
	
	private ConnectorCreationPanel createConnectionPanel;
	private ConnectorConfigurationPanel manageConnectionPanel;
	
	private String selectedConnectorName;
	
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
		this.add(getMainSplitPane(), BorderLayout.CENTER);
		ConnectorManager.getInstance().addListener(this);
		add(getMainToolBar(), BorderLayout.NORTH);
	}
	
	private JToolBar getMainToolBar() {
		if (mainToolBar == null) {
			mainToolBar = new JToolBar();
			mainToolBar.setFloatable(false);
			mainToolBar.add(getJButtonAdd());
			mainToolBar.add(getJButtonRemove());
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
			jButtonAdd.addActionListener(this);
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
			mainSplitPane.setResizeWeight(0.5);
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
	
	private ConnectorCreationPanel getCreateConnectionPanel() {
		if (createConnectionPanel == null) {
			Frame owner = OwnerDetection.getOwnerFrameForComponent(this);
			createConnectionPanel = new ConnectorCreationPanel(owner, this);
		}
		return createConnectionPanel;
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
				
			}
		}
	}
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent lse) {
		if (lse.getSource()==this.getConnectorsList()) {
			
			if (this.selectedConnectorName!=null) {
				if (this.getConfigurationPanel().hasPendingChanges()==true) {
					String userMessage = "Your current configuration has pending changes! Apply before switching?";
					int userReply = JOptionPane.showConfirmDialog(this, userMessage, "Apply changes?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (userReply==JOptionPane.YES_OPTION) {
						this.getConfigurationPanel().applyChanges();
					}
				}
			}
			
			String connectorName = this.getConnectorsList().getSelectedValue();
			this.setSelectedConnector(connectorName);
		}
	}
	
	private void setSelectedConnector(String connectorName) {
		this.selectedConnectorName = connectorName;
		if (connectorName!=null) {
			Properties connectorProperties = ConnectorManager.getInstance().getConnectorProperies(connectorName);
			this.getConfigurationPanel().setConnectorProperties(connectorProperties);
			
			AbstractConnector connectorInstance = ConnectorManager.getInstance().getConnectorByName(connectorName);
			if (connectorInstance!=null) {
				// --- If the connector instance is available, use the UI component defined there for configuration
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
		this.updateButtonState();
	}
	
	protected void dispose() {
		ConnectorManager.getInstance().removeListener(this);
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==this.getJButtonAdd()) {
			this.showCreatePanel();
		} else if (ae.getSource()==this.getJButtonRemove()) {
			this.deleteConnection();
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

	private void showCreatePanel() {
		this.getCreateConnectionPanel();
		int posX = Double.valueOf(this.getConfigurationPanel().getLocationOnScreen().getX()).intValue();
		int posY = Double.valueOf(this.getConfigurationPanel().getLocationOnScreen().getY()).intValue();
		int width = this.getConfigurationPanel().getWidth();
		this.getCreateConnectionPanel().setLocation(posX, posY);
		this.getCreateConnectionPanel().setSize(width, 135);
		this.getCreateConnectionPanel().requestFocus();
		this.getCreateConnectionPanel().setVisible(true);
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
	
}
