package de.enflexit.connector.opcua.ui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;

/**
 * The Class OpcUaDataValueTableCellRenderEditor.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaDataValueTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -6439873155983036500L;

	/* (non-Javadoc)
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object tableValue, boolean isSelected, boolean hasFocus, int row, int column) {
		
		// --- Get display component as usual -------------
		JLabel displayComponent = (JLabel) super.getTableCellRendererComponent(table, tableValue, isSelected, hasFocus, row, column);
		
		// --- Check if value is of type DataValue --------
		if (tableValue instanceof UaVariableNode) {
			
			UaVariableNode uaVarNode = (UaVariableNode) tableValue;
			DataValue dataValue = uaVarNode.getValue(); 
			Object variantValue = dataValue.getValue().getValue();
			if (variantValue==null) {
				displayComponent.setText("Null");
				
			} else if (variantValue.getClass().isArray()==true) {
				String displayText = "";
				Object[] valueArr = (Object[]) variantValue;
				for (int i = 0; i < valueArr.length; i++) {
					displayText = displayText + (displayText.isBlank() ? valueArr[i] : "," + valueArr[i]);
				}
				displayText = "[" + displayText +  "]";
				displayComponent.setText(displayText);
				
			} else {
				displayComponent.setText(variantValue.toString());
				
			}
		}
		
		return displayComponent;
	}
	

}
