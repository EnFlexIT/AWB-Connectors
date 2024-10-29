package de.enflexit.connector.opcua.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;

import de.enflexit.common.swing.KeyAdapter4Numbers;

/**
 * The Class OpcUaDataValueTableCellRenderEditor.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaDataValueTableCellEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = -6439873155983036500L;
	
	private JTextField jTextFieldEdit;
	private KeyAdapter4Numbers keyAdapter = new KeyAdapter4Numbers(true);
	
	private JCheckBox jCheckBoxEdit;
	
	private Object currValue;
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object tableValue, boolean isSelected, int row, int column) {
		
		this.currValue = tableValue;

		// --- Remove all key listener from editor component  -----------------
		this.getJTextFieldEditor().removeKeyListener(this.keyAdapter);
		
		// --- Analyze value to provide the correct editor component ----------
		if (tableValue instanceof UaVariableNode) {
			// TODO
			UaVariableNode uaVarNode = (UaVariableNode) tableValue;
			DataValue dataValue = uaVarNode.getValue(); 
			Object variantValue = dataValue.getValue().getValue();
			if (variantValue==null) {
				this.getJTextFieldEditor().setText("Null");
				
			} else if (variantValue.getClass().isArray()==true) {
				String displayText = "";
				Object[] valueArr = (Object[]) variantValue;
				for (int i = 0; i < valueArr.length; i++) {
					displayText = displayText + (displayText.isBlank() ? valueArr[i] : "," + valueArr[i]);
				}
				displayText = "[" + displayText +  "]";
				this.getJTextFieldEditor().setText(displayText);
				
			} else {
				this.getJTextFieldEditor().setText(variantValue.toString());
				
			}
			
		} else {
			this.getJTextFieldEditor().setText(tableValue.toString());
		}
		return this.getJTextFieldEditor();
	}
	
	
	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	@Override
	public Object getCellEditorValue() {
		return this.currValue;
	}

	/**
	 * Gets the JTextField of the editor.
	 * @return the editor JTextField  
	 */
	private JTextField getJTextFieldEditor() {
		if (jTextFieldEdit==null) {
			jTextFieldEdit = new JTextField();
			jTextFieldEdit.setOpaque(false);
			jTextFieldEdit.setBorder(BorderFactory.createEmptyBorder());
			jTextFieldEdit.setBackground(new Color(0,0,0,0));
		}
		return jTextFieldEdit;
	}
	
	private JCheckBox getJCheckBoxEdit() {
		if (jCheckBoxEdit==null) {
			jCheckBoxEdit = new JCheckBox();
			jCheckBoxEdit.setOpaque(true);
			jCheckBoxEdit.setBorder(BorderFactory.createEmptyBorder());
			jCheckBoxEdit.setBackground(new Color(0,0,0,0));
		}
		return jCheckBoxEdit;
	}
}
