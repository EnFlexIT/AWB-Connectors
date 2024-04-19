package de.enflexit.connector.core.ui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import agentgui.core.application.Application;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.AbstractConnectorProperties;
import de.enflexit.connector.core.manager.ConnectorManager;

import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JFileChooser;

/**
 * The main panel for the {@link ConnectorManager}'s configuration UI.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorManagerMainPanel extends JPanel implements ActionListener, ListSelectionListener, PropertyChangeListener {
	
	private static final long serialVersionUID = 3162788243111915591L;
	
	private static final String ICON_SAVE = "Save.png";
	private static final String ICON_LOAD = "Load.png";
	private static final String ICON_ADD = "ListPlus.png";
	private static final String ICON_REMOVE = "ListMinus.png";
	private static final String ICON_START = "Start.png";
	private static final String ICON_STOP = "Stop.png";
	private static final String ICON_RESTART = "Restart.png";
	
	private static final String FILE_SUFFIX_XML = "xml";
	private static final String FILE_SUFFIX_JSON = "json";
	
	private JToolBar mainToolBar;
	private JButton jButtonSave;
	private JButton jButtonLoad;
	private JButton jButtonAdd;
	private JButton jButtonRemove;
	private JButton jButtonStart;
	private JButton jButtonStop;
	private JButton jButtonRestart;
	
	private JSplitPane mainSplitPane;
	private JScrollPane jScrollPane;
	private JList<String> connectorsList;
	private DefaultListModel<String> connectorsListModel;
	
	private JSplitPane subSplitPane;
	private ConnectorCreationPanel createConnectionPanel;
	private ConnectorConfigurationPanel manageConnectionPanel;
	
	private JFileChooser fileChooser;
	private AbstractConnector selectedConnector;
	
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
			mainToolBar.add(getJButtonSave());
			mainToolBar.add(getJButtonLoad());
			mainToolBar.addSeparator();
			mainToolBar.add(getJButtonAdd());
			mainToolBar.add(getJButtonRemove());
			mainToolBar.addSeparator();
			mainToolBar.add(getJButtonStart());
			mainToolBar.add(getJButtonStop());
			mainToolBar.add(getJButtonRestart());
		}
		return mainToolBar;
	}

	private JButton getJButtonSave() {
		if (jButtonSave == null) {
			jButtonSave = new JButton(BundleHelper.getImageIcon(ICON_SAVE));
			jButtonSave.addActionListener(this);
		}
		return jButtonSave;
	}

	private JButton getJButtonLoad() {
		if (jButtonLoad == null) {
			jButtonLoad = new JButton(BundleHelper.getImageIcon(ICON_LOAD));
			jButtonLoad.addActionListener(this);
		}
		return jButtonLoad;
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
			mainSplitPane.setRightComponent(getSubSplitPane());
			mainSplitPane.setDividerLocation(250);
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
	
	private JSplitPane getSubSplitPane() {
		if (subSplitPane == null) {
			subSplitPane = new JSplitPane();
			subSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			subSplitPane.setDividerSize(0);
			subSplitPane.setDividerLocation(0);
			subSplitPane.setLeftComponent(getCreateConnectionPanel());
			subSplitPane.setRightComponent(getManageConnectionPanel());
		}
		return subSplitPane;
	}

	private ConnectorCreationPanel getCreateConnectionPanel() {
		if (createConnectionPanel == null) {
			createConnectionPanel = new ConnectorCreationPanel(this);
		}
		return createConnectionPanel;
	}

	private ConnectorConfigurationPanel getManageConnectionPanel() {
		if (manageConnectionPanel == null) {
			manageConnectionPanel = new ConnectorConfigurationPanel();
		}
		return manageConnectionPanel;
	}

	private JFileChooser getFileChooser() {
		if (fileChooser==null) {
			fileChooser = new JFileChooser();
			fileChooser.addChoosableFileFilter(this.getFileFilterJSON());
			fileChooser.addChoosableFileFilter(this.getFileFilterXML());
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setCurrentDirectory(Application.getGlobalInfo().getLastSelectedFolder());
		}
		return fileChooser;
	}

	/**
	 * Gets the file filter for XML files.
	 * @return the file filter XML
	 */
	private FileNameExtensionFilter getFileFilterXML() {
		FileNameExtensionFilter	fileFilterXML = new FileNameExtensionFilter("XML files", FILE_SUFFIX_XML);
		return fileFilterXML;
	}

	/**
	 * Gets the file filter for JSON files.
	 * @return the file filter JSON
	 */
	private FileNameExtensionFilter getFileFilterJSON() {
		FileNameExtensionFilter	fileFilterJSON = new FileNameExtensionFilter("JSON files", FILE_SUFFIX_JSON);
		return fileFilterJSON;
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
			
			if (this.selectedConnector!=null) {
				//TODO check for unsaved changes, ask user to save or discard 
			}
			
			String selectedConnectorName = this.getConnectorsList().getSelectedValue();
			if (selectedConnectorName!=null) {
				this.selectedConnector = ConnectorManager.getInstance().getConnector(selectedConnectorName);
			} else {
				this.selectedConnector = null;
			}
			this.getManageConnectionPanel().setConnector(this.selectedConnector);
			this.updateButtonState();
		}
	}
	protected void dispose() {
		ConnectorManager.getInstance().removeListener(this);
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==this.getJButtonSave()) {
			int result = this.getFileChooser().showSaveDialog(this);
			if (result==JFileChooser.APPROVE_OPTION) {
				File jsonFile = this.getFileChooser().getSelectedFile();
				ConnectorManager.getInstance().storeConfigurationToJSON(jsonFile);
				Application.getGlobalInfo().setLastSelectedFolder(jsonFile.getParentFile());
			}
		} else if (ae.getSource()==this.getJButtonLoad()){
			int result = this.getFileChooser().showOpenDialog(this);
			if (result==JFileChooser.APPROVE_OPTION) {
				File jsonFile = this.getFileChooser().getSelectedFile();
				ConnectorManager.getInstance().loadConfigurationFromJSON(jsonFile);
				Application.getGlobalInfo().setLastSelectedFolder(jsonFile.getParentFile());
			}
		} else if (ae.getSource()==this.getJButtonAdd()) {
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
		this.getSubSplitPane().setDividerLocation(100);
	}
	protected void hideCreatePanel() {
		this.getSubSplitPane().setDividerLocation(0);
	}
	
	private void updateButtonState() {
		if (this.selectedConnector==null) {
			// --- No connector selected ------------------
			this.getJButtonRemove().setEnabled(false);
			this.getJButtonStart().setEnabled(false);
			this.getJButtonStop().setEnabled(false);
			this.getJButtonRestart().setEnabled(false);
		} else if (this.selectedConnector.isConnected()==true) {
			// --- The selected connector is active -------
			this.getJButtonRemove().setEnabled(false);
			this.getJButtonStart().setEnabled(false);
			this.getJButtonStop().setEnabled(true);
			this.getJButtonRestart().setEnabled(true);
		} else {
			// --- The selected connector is not active ---
			this.getJButtonRemove().setEnabled(true);
			this.getJButtonStart().setEnabled(true);
			this.getJButtonStop().setEnabled(false);
			this.getJButtonRestart().setEnabled(false);
		}
	}
	
	private void startConnection() {
		boolean success = this.selectedConnector.connect();
		if (success==false) {
			JOptionPane.showMessageDialog(this, "Failed to open the connection! Please check your settings.", "Connection failed", JOptionPane.ERROR_MESSAGE);
		} else {
			this.updateButtonState();
		}
	}
	private void stopConnection() {
		this.selectedConnector.disconnect();
		this.updateButtonState();
	}
	
	private void deleteConnection() {
		if (this.selectedConnector.isConnected()==true) {
			JOptionPane.showMessageDialog(this, "The selected connection is currently active! Please disconnect before deleting.", "Currently connected!", JOptionPane.WARNING_MESSAGE);
		} else {
			int answer = JOptionPane.showConfirmDialog(this, "This will delete the selected connection - are you sure?", "Confirm delete", JOptionPane.YES_NO_OPTION);
			if (answer==JOptionPane.YES_OPTION) {
				String connectorName = this.selectedConnector.getConnectorProperties().getStringValue(AbstractConnectorProperties.PROPERTY_KEY_CONNECTOR_NAME);
				ConnectorManager.getInstance().removeConnector(connectorName);
			}
		}
	}
}
