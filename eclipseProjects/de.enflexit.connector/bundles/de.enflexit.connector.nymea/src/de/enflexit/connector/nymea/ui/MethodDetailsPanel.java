package de.enflexit.connector.nymea.ui;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Font;
import javax.swing.table.DefaultTableModel;

import de.enflexit.connector.nymea.dataModel.JsonRpcMethod;
import de.enflexit.connector.nymea.dataModel.JsonRpcMethod.Parameter;

import java.awt.Insets;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;

public class MethodDetailsPanel extends JPanel {
	
	private static final long serialVersionUID = -1279695264980838689L;
	
	private static final int COLUMN_INDEX_PARAM_NAME = 0;
	private static final int COLUMN_INDEX_PARAM_VALUE = 3;

	private JLabel jLabelDesriptionCaption;
	private JLabel jLabelPermissionScopeCaption;
	private JLabel jLabelPermissionScope;
	private JScrollPane jScrollPaneParams;
	private JLabel jLabelParameters;
	private JTable jTableParameters;
	private DefaultTableModel parametersTableModel;
	private JLabel jLabelDescription;
	
	public MethodDetailsPanel() {
		initialize();
	}
	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		GridBagConstraints gbc_jLabelDesriptionCaption = new GridBagConstraints();
		gbc_jLabelDesriptionCaption.anchor = GridBagConstraints.EAST;
		gbc_jLabelDesriptionCaption.insets = new Insets(10, 10, 5, 5);
		gbc_jLabelDesriptionCaption.gridx = 0;
		gbc_jLabelDesriptionCaption.gridy = 0;
		add(getJLabelDesriptionCaption(), gbc_jLabelDesriptionCaption);
		GridBagConstraints gbc_jLabelDescription = new GridBagConstraints();
		gbc_jLabelDescription.anchor = GridBagConstraints.WEST;
		gbc_jLabelDescription.insets = new Insets(10, 5, 5, 0);
		gbc_jLabelDescription.gridx = 1;
		gbc_jLabelDescription.gridy = 0;
		add(getJLabelDescription(), gbc_jLabelDescription);
		GridBagConstraints gbc_jLabelPermissionScopeCaption = new GridBagConstraints();
		gbc_jLabelPermissionScopeCaption.insets = new Insets(5, 10, 5, 5);
		gbc_jLabelPermissionScopeCaption.anchor = GridBagConstraints.WEST;
		gbc_jLabelPermissionScopeCaption.gridx = 0;
		gbc_jLabelPermissionScopeCaption.gridy = 1;
		add(getJLabelPermissionScopeCaption(), gbc_jLabelPermissionScopeCaption);
		GridBagConstraints gbc_jLabelPermissionScope = new GridBagConstraints();
		gbc_jLabelPermissionScope.anchor = GridBagConstraints.WEST;
		gbc_jLabelPermissionScope.insets = new Insets(0, 0, 5, 0);
		gbc_jLabelPermissionScope.gridx = 1;
		gbc_jLabelPermissionScope.gridy = 1;
		add(getJLabelPermissionScope(), gbc_jLabelPermissionScope);
		GridBagConstraints gbc_jLabelParameters = new GridBagConstraints();
		gbc_jLabelParameters.anchor = GridBagConstraints.EAST;
		gbc_jLabelParameters.insets = new Insets(5, 10, 5, 5);
		gbc_jLabelParameters.gridx = 0;
		gbc_jLabelParameters.gridy = 2;
		add(getJLabelParameters(), gbc_jLabelParameters);
		GridBagConstraints gbc_jScrollPaneParams = new GridBagConstraints();
		gbc_jScrollPaneParams.gridwidth = 2;
		gbc_jScrollPaneParams.fill = GridBagConstraints.BOTH;
		gbc_jScrollPaneParams.gridx = 0;
		gbc_jScrollPaneParams.gridy = 3;
		add(getJScrollPaneParams(), gbc_jScrollPaneParams);
	}


	private JLabel getJLabelDesriptionCaption() {
		if (jLabelDesriptionCaption == null) {
			jLabelDesriptionCaption = new JLabel("Method Description:");
			jLabelDesriptionCaption.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelDesriptionCaption;
	}
	private JLabel getJLabelPermissionScopeCaption() {
		if (jLabelPermissionScopeCaption == null) {
			jLabelPermissionScopeCaption = new JLabel("Resuired Permission Scope:");
			jLabelPermissionScopeCaption.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelPermissionScopeCaption;
	}
	private JLabel getJLabelPermissionScope() {
		if (jLabelPermissionScope == null) {
			jLabelPermissionScope = new JLabel("");
		}
		return jLabelPermissionScope;
	}
	private JScrollPane getJScrollPaneParams() {
		if (jScrollPaneParams == null) {
			jScrollPaneParams = new JScrollPane();
			jScrollPaneParams.setViewportView(getJTableParameters());
		}
		return jScrollPaneParams;
	}
	private JLabel getJLabelParameters() {
		if (jLabelParameters == null) {
			jLabelParameters = new JLabel("Method Parameters:");
			jLabelParameters.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelParameters;
	}
	private JTable getJTableParameters() {
		if (jTableParameters == null) {
			jTableParameters = new JTable();
			jTableParameters.setModel(this.getParametersTableModel());
			
		}
		return jTableParameters;
	}
	private DefaultTableModel getParametersTableModel() {
		if (parametersTableModel==null) {
			parametersTableModel = new DefaultTableModel() {

				private static final long serialVersionUID = -8761026646956607760L;

				/* (non-Javadoc)
				 * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
				 */
				@Override
				public boolean isCellEditable(int row, int column) {
					// --- Only the value column can be edited
					return column==COLUMN_INDEX_PARAM_VALUE;
				}
				
			};
			parametersTableModel.addColumn("Name");
			parametersTableModel.addColumn("Type");
			parametersTableModel.addColumn("Required");
			parametersTableModel.addColumn("Value");
		}
		return parametersTableModel;
	}
	
	public void setCurrentMethod(JsonRpcMethod method) {
		if (method!=null) {
			this.getJLabelPermissionScope().setText(method.getPermissionScope().toString());
			this.getJLabelDescription().setText(method.getDescription());
			this.getJLabelDescription().setToolTipText(method.getDescription());
		} else {
			this.getJLabelPermissionScope().setText(null);
			this.getJLabelDescription().setText(null);
			this.getJLabelDescription().setToolTipText(null);
		}
		this.setParamTableRows(method);
	}
	
	private void setParamTableRows(JsonRpcMethod method) {
		// --- Discard old contents -----------------------
		this.getParametersTableModel().setRowCount(0);
		if (method!=null) {
			for (Parameter param: method.getParameters()) {
				Vector<String> rowData = new Vector<>();
				rowData.add(param.getName());
				rowData.add(param.getType());
				rowData.add((param.isRequired() ? "Required" : "Optional"));
				rowData.add("");
				this.getParametersTableModel().addRow(rowData);
			}
		} else {
			Vector<String> rowData = new Vector<>();
			rowData.add("This method has no parameters");
			rowData.add(null);
			rowData.add(null);
			rowData.add(null);
			this.getParametersTableModel().addRow(rowData);
		}
	}
	
	/**
	 * Gets the configured parameter values from the table.
	 * @return the param values
	 */
	public HashMap<String, String> getParamValues(){
		HashMap<String, String> paramValues = new HashMap<>();
		for (int i=0; i<this.getParametersTableModel().getRowCount(); i++) {
			String paramName = (String) this.getJTableParameters().getValueAt(i, COLUMN_INDEX_PARAM_NAME);
			String paramValue = (String) this.getJTableParameters().getValueAt(i, COLUMN_INDEX_PARAM_VALUE);
			if (paramValue!=null && paramValue.isBlank()==false) {
				paramValues.put(paramName, paramValue);
			}
		}
		return paramValues;
	}
	private JLabel getJLabelDescription() {
		if (jLabelDescription == null) {
			jLabelDescription = new JLabel("");
			jLabelDescription.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelDescription;
	}
}
