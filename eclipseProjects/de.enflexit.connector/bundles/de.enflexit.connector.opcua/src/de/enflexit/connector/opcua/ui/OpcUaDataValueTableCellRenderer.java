package de.enflexit.connector.opcua.ui;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

import de.enflexit.connector.opcua.OpcUaHelper;

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
		JComponent displayComponent = (JComponent) super.getTableCellRendererComponent(table, tableValue, isSelected, hasFocus, row, column);
		
		// --- Check if value is of type DataValue --------
		DataValue dataValue = null;
		if (tableValue instanceof UaVariableNode) {
			UaVariableNode uaVarNode = (UaVariableNode) tableValue;
			dataValue = uaVarNode.getValue();
			
		} else if (tableValue instanceof DataValue) {
			dataValue = (DataValue) tableValue;
		}
		
		if (dataValue!=null) {
			// --- Check the data type --------------------
			Class<?> valueType = OpcUaHelper.getDataType(dataValue);
			if (valueType.equals(Boolean.class)==true) {
				// --- Exchange by check box --------------
				JCheckBox displayCheckBox = new JCheckBox();
				displayCheckBox.setBackground(displayComponent.getBackground());
				displayCheckBox.setForeground(displayComponent.getForeground());
				displayCheckBox.setOpaque(displayComponent.isOpaque());
				displayComponent = displayCheckBox;
			}
			OpcUaDataValueTableCellRenderer.setDataValueToDisplayComponent(dataValue, displayComponent);
		}
	
		return displayComponent;
	}
	

	/**
	 * Sets the data value to display component.
	 *
	 * @param dataValue the data value
	 * @param jComponent the j component
	 * @return the string
	 */
	public static String setDataValueToDisplayComponent(DataValue dataValue, JComponent jComponent) {
		return setDataValueToDisplayComponent((dataValue==null ? null : dataValue.getValue()), jComponent);
	}
	/**
	 * Sets the specified Variant to a display component.
	 *
	 * @param dataValue the data value
	 * @param jComponent the j component
	 * @return the string value that was set 
	 */
	public static String setDataValueToDisplayComponent(Variant variant, JComponent jComponent) {
		
		String valueSet = null; 
		
		// --- Get the Variant value of the DataValue ---------------
		Object variantValue = variant.getValue();
		if (variantValue==null) {
			valueSet = "Null";
			
		} else if (variantValue.getClass().isArray()==true) {
			// --- For arrays ---------------------------------------
			String displayText = "";
			Object[] valueArr = (Object[]) variantValue;
			for (int i = 0; i < valueArr.length; i++) {
				displayText = displayText + (displayText.isBlank() ? valueArr[i] : "," + valueArr[i]);
			}
			displayText = "[" + displayText +  "]";
			valueSet = displayText;
			
		} else {
			// --- For primitive values -----------------------------
			valueSet = variantValue.toString();
		}
		
		if (jComponent instanceof JLabel) {
			((JLabel) jComponent).setText(valueSet);
		} else if (jComponent instanceof JTextField) {
			((JTextField) jComponent).setText(valueSet);
		} else if (jComponent instanceof JCheckBox) {
			((JCheckBox)jComponent).setSelected((Boolean)variantValue);
		}
		
		return valueSet;
	}
	
}
