package de.enflexit.connector.nymea.ui;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Font;
import java.awt.Insets;
import de.enflexit.connector.nymea.rpcClient.JsonRpcResponse;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class MethodResultsPanel extends JPanel{
	
	public enum ExecutionState{
		NotStarted, Running, Successful, Failed
	}
	
	private static final long serialVersionUID = -2936982409463991843L;
	private static final String INITIAL_METHOD_NAME = "None";
	private static final String INITIAL_EXECUTION_RESULT = "Not yet executed";
	
	private JLabel jLabelLastMethodCaption;
	private JLabel jLabelExecutionResultCaption;
	private JLabel jLabelExecutionResult;
	private JLabel jLabelResponse;
	private JScrollPane jScrollPaneServerResponse;
	private JLabel jLabelLastMethodCall;
	private ObjectBrowserTree responseTreeView;
	
	public MethodResultsPanel() {
		initialize();
	}
	
	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		GridBagConstraints gbc_jLabelLastMethodCaption = new GridBagConstraints();
		gbc_jLabelLastMethodCaption.anchor = GridBagConstraints.EAST;
		gbc_jLabelLastMethodCaption.insets = new Insets(10, 10, 5, 5);
		gbc_jLabelLastMethodCaption.gridx = 0;
		gbc_jLabelLastMethodCaption.gridy = 0;
		add(getJLabelLastMethodCaption(), gbc_jLabelLastMethodCaption);
		GridBagConstraints gbc_jLabelLastMethodCall = new GridBagConstraints();
		gbc_jLabelLastMethodCall.anchor = GridBagConstraints.WEST;
		gbc_jLabelLastMethodCall.insets = new Insets(10, 5, 5, 0);
		gbc_jLabelLastMethodCall.gridx = 1;
		gbc_jLabelLastMethodCall.gridy = 0;
		add(getJLabelLastMethodCall(), gbc_jLabelLastMethodCall);
		GridBagConstraints gbc_jLabelExecutionStateCaption = new GridBagConstraints();
		gbc_jLabelExecutionStateCaption.anchor = GridBagConstraints.EAST;
		gbc_jLabelExecutionStateCaption.insets = new Insets(5, 10, 5, 5);
		gbc_jLabelExecutionStateCaption.gridx = 0;
		gbc_jLabelExecutionStateCaption.gridy = 1;
		add(getJLabelExecutionResulsCaption(), gbc_jLabelExecutionStateCaption);
		GridBagConstraints gbc_jLabelExecutionState = new GridBagConstraints();
		gbc_jLabelExecutionState.anchor = GridBagConstraints.WEST;
		gbc_jLabelExecutionState.insets = new Insets(5, 5, 5, 0);
		gbc_jLabelExecutionState.gridx = 1;
		gbc_jLabelExecutionState.gridy = 1;
		add(getJLabelExecutionResult(), gbc_jLabelExecutionState);
		GridBagConstraints gbc_jLabelResponse = new GridBagConstraints();
		gbc_jLabelResponse.anchor = GridBagConstraints.EAST;
		gbc_jLabelResponse.insets = new Insets(5, 10, 5, 5);
		gbc_jLabelResponse.gridx = 0;
		gbc_jLabelResponse.gridy = 2;
		add(getJLabelResponse(), gbc_jLabelResponse);
		GridBagConstraints gbc_jScrollPaneServerResponse = new GridBagConstraints();
		gbc_jScrollPaneServerResponse.gridwidth = 2;
		gbc_jScrollPaneServerResponse.fill = GridBagConstraints.BOTH;
		gbc_jScrollPaneServerResponse.gridx = 0;
		gbc_jScrollPaneServerResponse.gridy = 3;
		add(getJScrollPaneServerResponse(), gbc_jScrollPaneServerResponse);
	}

	private JLabel getJLabelLastMethodCaption() {
		if (jLabelLastMethodCaption == null) {
			jLabelLastMethodCaption = new JLabel("Last executed method:");
			jLabelLastMethodCaption.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelLastMethodCaption;
	}
	private JLabel getJLabelExecutionResulsCaption() {
		if (jLabelExecutionResultCaption == null) {
			jLabelExecutionResultCaption = new JLabel("Execution state:");
			jLabelExecutionResultCaption.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelExecutionResultCaption;
	}
	private JLabel getJLabelExecutionResult() {
		if (jLabelExecutionResult == null) {
			jLabelExecutionResult = new JLabel(INITIAL_EXECUTION_RESULT);
			jLabelExecutionResult.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelExecutionResult;
	}
	private JLabel getJLabelResponse() {
		if (jLabelResponse == null) {
			jLabelResponse = new JLabel("Server response:");
			jLabelResponse.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelResponse;
	}
	
	public void setExecutionState(ExecutionState executionState) {
		this.getJLabelExecutionResult().setText(executionState.toString());
	}
	
	public void setResults(String method, JsonRpcResponse response) {
		this.getJLabelLastMethodCall().setText(method);
		if (response!=null) {
			this.setExecutionState((response.isSuccess()) ? ExecutionState.Successful : ExecutionState.Failed);
		} else {
			this.getJLabelExecutionResult().setText("Failed");
		}
		this.updateResultsTreeView(response);
	}
	
	private JScrollPane getJScrollPaneServerResponse() {
		if (jScrollPaneServerResponse == null) {
			jScrollPaneServerResponse = new JScrollPane();
			jScrollPaneServerResponse.setViewportView(getResponseTreeView());
		}
		return jScrollPaneServerResponse;
	}
	private JLabel getJLabelLastMethodCall() {
		if (jLabelLastMethodCall == null) {
			jLabelLastMethodCall = new JLabel(INITIAL_METHOD_NAME);
			jLabelLastMethodCall.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelLastMethodCall;
	}
	private ObjectBrowserTree getResponseTreeView() {
		if (responseTreeView == null) {
			responseTreeView = new ObjectBrowserTree();
			this.updateResultsTreeView(null);
		}
		return responseTreeView;
	}
	
	private void updateResultsTreeView(JsonRpcResponse response) {
		DefaultMutableTreeNode rootNode;
		if (response==null) {
			rootNode = new DefaultMutableTreeNode("Execute a method to show the results here!");
		} else if (response.isError()==true){
			rootNode = new DefaultMutableTreeNode("Method execution failed!");
		} else {
			rootNode = new DefaultMutableTreeNode("Method results");
			ObjectBrowserTree.addMapContentChildNodes(response.getParams(), rootNode);
		}
		this.getResponseTreeView().setModel(new DefaultTreeModel(rootNode));
	}
}
