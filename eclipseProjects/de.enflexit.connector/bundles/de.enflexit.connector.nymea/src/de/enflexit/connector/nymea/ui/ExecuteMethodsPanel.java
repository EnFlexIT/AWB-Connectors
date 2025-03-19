package de.enflexit.connector.nymea.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JComboBox;
import java.awt.Insets;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import de.enflexit.common.swing.JComboBoxWide;
import de.enflexit.connector.core.ConnectorEvent;
import de.enflexit.connector.core.ConnectorEvent.Event;
import de.enflexit.connector.core.ConnectorListener;
import de.enflexit.connector.nymea.NymeaConnector;
import de.enflexit.connector.nymea.dataModel.JsonRpcMethod;
import de.enflexit.connector.nymea.rpcClient.JsonRpcResponse;
import de.enflexit.connector.nymea.ui.MethodResultsPanel.ExecutionState;

public class ExecuteMethodsPanel extends JPanel implements ActionListener, ConnectorListener {
	
	private static final long serialVersionUID = 376310821307402532L;
	private static final String MAP_KEY_METHODS = "methods";
	
	private JLabel jLabelMethodSelection;
	private JComboBoxWide<String> jComboBoxMethodSelection;
	private JLabel jLabelNamespaceFilter;
	private JComboBox<String> jComboBoxNamespaceFilter;
	private JButton jButtonExecute;
	
	private MethodDetailsPanel methodDetailsPanel;
	private MethodResultsPanel methodResultsPanel;
	
	private NymeaConnector nymeaConnector;
	private HashMap<String, JsonRpcMethod> methodsHashMap;
	
	/**
	 * Instantiates a new methods panel.
	 * @deprecated Added for window builder compatibility only. Please use the other constructor for actual instantiation.
	 */
	@Deprecated  
	public ExecuteMethodsPanel() {
		this.initialize();
	}
	
	/**
	 * Instantiates a new methods panel.
	 * @param connector the connector
	 */
	public ExecuteMethodsPanel(NymeaConnector connector) {
		this.nymeaConnector = connector;
		this.nymeaConnector.addConnectorListener(this);
		this.initialize();
	}

	/**
	 * Initialize the UI components.
	 */
	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		GridBagConstraints gbc_jLabelMethodSelection = new GridBagConstraints();
		gbc_jLabelMethodSelection.weighty = 0.1;
		gbc_jLabelMethodSelection.insets = new Insets(10, 10, 5, 5);
		gbc_jLabelMethodSelection.anchor = GridBagConstraints.EAST;
		gbc_jLabelMethodSelection.gridx = 0;
		gbc_jLabelMethodSelection.gridy = 0;
		add(getJLabelMethodSelection(), gbc_jLabelMethodSelection);
		GridBagConstraints gbc_jComboBoxMethodSelection = new GridBagConstraints();
		gbc_jComboBoxMethodSelection.insets = new Insets(10, 5, 5, 5);
		gbc_jComboBoxMethodSelection.fill = GridBagConstraints.HORIZONTAL;
		gbc_jComboBoxMethodSelection.gridx = 1;
		gbc_jComboBoxMethodSelection.gridy = 0;
		add(getJComboBoxMethodSelection(), gbc_jComboBoxMethodSelection);
		GridBagConstraints gbc_jLabelNamespaceFilter = new GridBagConstraints();
		gbc_jLabelNamespaceFilter.insets = new Insets(10, 5, 5, 5);
		gbc_jLabelNamespaceFilter.anchor = GridBagConstraints.EAST;
		gbc_jLabelNamespaceFilter.gridx = 2;
		gbc_jLabelNamespaceFilter.gridy = 0;
		add(getJLabelNamespaceFilter(), gbc_jLabelNamespaceFilter);
		GridBagConstraints gbc_jCompoBoxNamespaceFilter = new GridBagConstraints();
		gbc_jCompoBoxNamespaceFilter.insets = new Insets(10, 5, 5, 5);
		gbc_jCompoBoxNamespaceFilter.fill = GridBagConstraints.HORIZONTAL;
		gbc_jCompoBoxNamespaceFilter.gridx = 3;
		gbc_jCompoBoxNamespaceFilter.gridy = 0;
		add(getJComboBoxNamespaceFilter(), gbc_jCompoBoxNamespaceFilter);
		GridBagConstraints gbc_jButtonExecute = new GridBagConstraints();
		gbc_jButtonExecute.insets = new Insets(10, 5, 5, 0);
		gbc_jButtonExecute.anchor = GridBagConstraints.WEST;
		gbc_jButtonExecute.gridx = 4;
		gbc_jButtonExecute.gridy = 0;
		add(getJButtonExecute(), gbc_jButtonExecute);
		GridBagConstraints gbc_jPanelMethodDetails = new GridBagConstraints();
		gbc_jPanelMethodDetails.weighty = 0.3;
		gbc_jPanelMethodDetails.insets = new Insets(0, 0, 5, 0);
		gbc_jPanelMethodDetails.gridwidth = 5;
		gbc_jPanelMethodDetails.fill = GridBagConstraints.BOTH;
		gbc_jPanelMethodDetails.gridx = 0;
		gbc_jPanelMethodDetails.gridy = 1;
		add(getMethodDetailsPanel(), gbc_jPanelMethodDetails);
		GridBagConstraints gbc_methodResultsPanel = new GridBagConstraints();
		gbc_methodResultsPanel.weighty = 0.6;
		gbc_methodResultsPanel.gridwidth = 5;
		gbc_methodResultsPanel.insets = new Insets(0, 0, 0, 5);
		gbc_methodResultsPanel.fill = GridBagConstraints.BOTH;
		gbc_methodResultsPanel.gridx = 0;
		gbc_methodResultsPanel.gridy = 2;
		add(getMethodResultsPanel(), gbc_methodResultsPanel);
	}

	private JLabel getJLabelMethodSelection() {
		if (jLabelMethodSelection == null) {
			jLabelMethodSelection = new JLabel("Select Method");
			jLabelMethodSelection.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelMethodSelection;
	}
	private JComboBoxWide<String> getJComboBoxMethodSelection() {
		if (jComboBoxMethodSelection == null) {
			jComboBoxMethodSelection = new JComboBoxWide<String>();
			jComboBoxMethodSelection.setPreferredSize(new Dimension(120, 26));
			jComboBoxMethodSelection.setSize(new Dimension(120, 26));
			jComboBoxMethodSelection.setMaximumSize(new Dimension(120, 26));
			jComboBoxMethodSelection.addActionListener(this);
		}
		return jComboBoxMethodSelection;
	}
	private JLabel getJLabelNamespaceFilter() {
		if (jLabelNamespaceFilter == null) {
			jLabelNamespaceFilter = new JLabel("Filter by namespace");
			jLabelNamespaceFilter.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelNamespaceFilter;
	}
	private JComboBox<String> getJComboBoxNamespaceFilter() {
		if (jComboBoxNamespaceFilter == null) {
			jComboBoxNamespaceFilter = new JComboBox<String>();
			jComboBoxNamespaceFilter.setPreferredSize(new Dimension(120, 26));
			jComboBoxNamespaceFilter.setSize(new Dimension(120, 26));
			jComboBoxNamespaceFilter.setMaximumSize(new Dimension(120, 26));
			jComboBoxNamespaceFilter.addActionListener(this);
		}
		return jComboBoxNamespaceFilter;
	}
	private JButton getJButtonExecute() {
		if (jButtonExecute == null) {
			jButtonExecute = new JButton();
			jButtonExecute.setSize(new Dimension(26, 26));
			jButtonExecute.setIcon(new ImageIcon(this.getClass().getResource("/icons/Execute.png")));
			jButtonExecute.setEnabled(false);
			jButtonExecute.addActionListener(this);
		}
		return jButtonExecute;
	}
	
	/**
	 * Builds the combo box models for method selection and namespace filtering.
	 */
	private void buildNamespaceFilterComboBoxModels() {
		
		Vector<String> namespaces = new Vector<>();
		
		if (this.getMethodsHashMap()!=null) {
			for (String methodName : this.getMethodsHashMap().keySet()) {
				if (this.isNamespaceInList(methodName, namespaces)==false) {
					String[] nameParts = methodName.split("\\.");
					
					if (nameParts.length>0) {
						String namespace = nameParts[0];
						namespaces.add(namespace);
					} else {
						System.out.println("This method has no namespace: " + methodName);
					}
				}
			}
		}
		
		Collections.sort(namespaces);
		namespaces.add(0, "--- Show all ---");
		
		this.getJComboBoxNamespaceFilter().setModel(new DefaultComboBoxModel<>(namespaces));
	}
	
	private void buildMethodsComboBoxModel(String namespacefilter) {
		Vector<String> methods = new Vector<>();
		if (this.getMethodsHashMap()!=null) {
			for (String methodName : this.getMethodsHashMap().keySet()) {
				if (namespacefilter== null || methodName.startsWith(namespacefilter)) {
					methods.add(methodName);
				}
			}
		}
		
		Collections.sort(methods);
		methods.add(0, "--- Please select ---");
		
		this.getJComboBoxMethodSelection().setModel(new DefaultComboBoxModel<>(methods));
	}
	
	/**
	 * Checks if the provided list of namespaces contains the namespace of the provided method
	 * @param methodName the method name
	 * @param namespaces the namespaces
	 * @return true, if is namespace in list
	 */
	private boolean isNamespaceInList(String methodName, Vector<String> namespaces) {
		for (String namespace : namespaces) {
			if (methodName.startsWith(namespace)) {
				return true;
			}
		}
		return false;
	}
	
	private NymeaConnector getNymeaConnector() {
		return nymeaConnector;
	}
	private HashMap<String, JsonRpcMethod> getMethodsHashMap() {
		if (methodsHashMap==null) {
			
			if (this.nymeaConnector.getIntrospectionData()!=null) {
				
				methodsHashMap = new HashMap<>();
				
				Map<?,?> methodsMap = (Map<?, ?>) this.getNymeaConnector().getIntrospectionData().get(MAP_KEY_METHODS);
				
				for (Object key : methodsMap.keySet()) {
					String methodName = (String) key;
					Map<?,?> methodData = (Map<?, ?>) methodsMap.get(key);
					
					methodsHashMap.put(methodName, new JsonRpcMethod(methodName, methodData));
				}
			}
			
		}
		return methodsHashMap;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==this.getJComboBoxMethodSelection()) {
			// --- Show the details for the selected method ---------
			JsonRpcMethod method = null;
			if (this.getJComboBoxMethodSelection().getSelectedIndex()>0) {
				String methodName = (String) this.getJComboBoxMethodSelection().getSelectedItem();
				method = this.getMethodsHashMap().get(methodName);
			}
			this.showMethodDetails(method);
			this.getJButtonExecute().setEnabled(method!=null);
		} else if (ae.getSource()==this.getJComboBoxNamespaceFilter()) {
			// --- Filter the methods list --------------------------
			String namespaceFilter = null;
			if (this.getJComboBoxNamespaceFilter().getSelectedIndex()>0) {
				namespaceFilter = (String) this.getJComboBoxNamespaceFilter().getSelectedItem();
			}
			this.applyNamespaceFilter(namespaceFilter);
		} else if (ae.getSource()==this.getJButtonExecute()) {
			this.executeSelectedMethod();
		}
	}
	
	private void applyNamespaceFilter(String namespaceFilter) {
		String previouslySelected = (String) this.getJComboBoxMethodSelection().getSelectedItem();
		this.buildMethodsComboBoxModel(namespaceFilter);

		DefaultComboBoxModel<String> methodsModel = (DefaultComboBoxModel<String>) this.getJComboBoxMethodSelection().getModel();
		int selectionIndex = methodsModel.getIndexOf(previouslySelected);
		
		if (selectionIndex>0) {
			this.getJComboBoxMethodSelection().setSelectedIndex(selectionIndex);
		} else {
			this.getJComboBoxMethodSelection().setSelectedIndex(0);
		}
		
	}

	private void showMethodDetails(JsonRpcMethod method) {
		this.getMethodDetailsPanel().setCurrentMethod(method);
	}
	
	private void executeSelectedMethod() {
		String selectedMethod = (String) this.getJComboBoxMethodSelection().getSelectedItem();
		HashMap<String, String> parameters = this.getMethodDetailsPanel().getParamValues();
		
		this.getMethodResultsPanel().setExecutionState(ExecutionState.Running);
		this.getJButtonExecute().setEnabled(false);
		
		Thread waitingThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				JsonRpcResponse response = ExecuteMethodsPanel.this.getNymeaConnector().getNymeaClient().executeRpcMethod(selectedMethod, parameters);
				ExecuteMethodsPanel.this.getMethodResultsPanel().setResults(selectedMethod, response);
				ExecuteMethodsPanel.this.getJButtonExecute().setEnabled(true);
			}
		});
		
		waitingThread.start();
		
	}

	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.ConnectorListener#onConnectorEvent(de.enflexit.connector.core.ConnectorEvent)
	 */
	@Override
	public void onConnectorEvent(ConnectorEvent connectorEvent) {
		if (connectorEvent.getSource()==this.getNymeaConnector() && connectorEvent.getEvent()==Event.CONNECTED) {
			this.buildMethodsComboBoxModel(null);
			this.buildNamespaceFilterComboBoxModels();
		}
	}
	private MethodDetailsPanel getMethodDetailsPanel() {
		if (methodDetailsPanel == null) {
			methodDetailsPanel = new MethodDetailsPanel();
		}
		return methodDetailsPanel;
	}
	private MethodResultsPanel getMethodResultsPanel() {
		if (methodResultsPanel == null) {
			methodResultsPanel = new MethodResultsPanel();
		}
		return methodResultsPanel;
	}
}
