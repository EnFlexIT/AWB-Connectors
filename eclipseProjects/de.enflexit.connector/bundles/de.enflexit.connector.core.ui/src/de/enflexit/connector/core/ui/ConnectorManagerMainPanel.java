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
import agentgui.core.application.Application;
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.manager.ConnectorManager;

import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
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
	
	private JSplitPane mainSplitPane;
	private JScrollPane jScrollPane;
	private JList<String> connectorsList;
	private DefaultListModel<String> connectorsListModel;
	private JSplitPane subSplitPane;
	private CreateConnectionPanel createConnectionPanel;
	private ManageConnectionPanel manageConnectionPanel;
	private JToolBar mainToolBar;
	
	private JButton jButtonSave;
	private JButton jButtonLoad;
	private JFileChooser fileChooser;
	
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

	private JSplitPane getSubSplitPane() {
		if (subSplitPane == null) {
			subSplitPane = new JSplitPane();
			subSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			subSplitPane.setDividerLocation(100);
			subSplitPane.setLeftComponent(getCreateConnectionPanel());
			subSplitPane.setRightComponent(getManageConnectionPanel());
		}
		return subSplitPane;
	}

	private CreateConnectionPanel getCreateConnectionPanel() {
		if (createConnectionPanel == null) {
			createConnectionPanel = new CreateConnectionPanel();
		}
		return createConnectionPanel;
	}

	private ManageConnectionPanel getManageConnectionPanel() {
		if (manageConnectionPanel == null) {
			manageConnectionPanel = new ManageConnectionPanel();
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
			String selectedConnectorName = this.getConnectorsList().getSelectedValue();
			AbstractConnector selectedConnector = ConnectorManager.getInstance().getConnector(selectedConnectorName);
			this.getManageConnectionPanel().setConnector(selectedConnector);
		}
	}
	protected void dispose() {
		ConnectorManager.getInstance().removeListener(this);
	}
	private JToolBar getMainToolBar() {
		if (mainToolBar == null) {
			mainToolBar = new JToolBar();
			mainToolBar.add(getJButtonSave());
			mainToolBar.add(getJButtonLoad());
		}
		return mainToolBar;
	}
	private JButton getJButtonSave() {
		if (jButtonSave == null) {
			jButtonSave = new JButton("Save");
			jButtonSave.addActionListener(this);
		}
		return jButtonSave;
	}
	private JButton getJButtonLoad() {
		if (jButtonLoad == null) {
			jButtonLoad = new JButton("Load");
			jButtonLoad.addActionListener(this);
		}
		return jButtonLoad;
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
		}
	}
	
	private JFileChooser getFileChooser() {
		if (fileChooser==null) {
			fileChooser = new JFileChooser();
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setCurrentDirectory(Application.getGlobalInfo().getLastSelectedFolder());
		}
		return fileChooser;
	}
}
