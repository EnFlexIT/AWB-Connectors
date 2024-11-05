package de.enflexit.connector.opcua.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.ULong;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;

import de.enflexit.common.swing.KeyAdapter4Numbers;
import de.enflexit.connector.opcua.OpcUaHelper;

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
	
	private int currRow;
	private int currColumn;
	
	private Object currValue;
	private DataValue currDataValue;
	private Class<?> currValueType;
	
	private DataValue newDataValue;

	private List<OpcUaDataValueTableCellEditorListener> listener;
	
	
	/**
	 * Instantiates a new OpcUaDataValueTableCellEditor.
	 * @param listener the mandatory listener to react on value changes and write such change to a server.
	 */
	public OpcUaDataValueTableCellEditor(OpcUaDataValueTableCellEditorListener listener) {
		this.addOpcUaDataValueTableCellEditorListener(listener);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object tableValue, boolean isSelected, int row, int column) {
		
		this.currRow = row;
		this.currColumn = column;
		this.currValue = tableValue;

		this.newDataValue  = null;
		this.currDataValue = null;
		
		// --- Check if value is of type DataValue --------
		if (this.currValue instanceof UaVariableNode) {
			UaVariableNode uaVarNode = (UaVariableNode) tableValue;
			this.currDataValue = uaVarNode.getValue();
			
		} else if (this.currValue instanceof DataValue) {
			this.currDataValue = (DataValue) tableValue;
		}
		
		// --- Assign value to display component ----------
		JComponent editorComponent = this.getJTextFieldEditor();
		if (currDataValue!=null) {
			// --- Check the data type --------------------
			this.currValueType = OpcUaHelper.getDataType(this.currDataValue);
			if (this.currValueType.equals(Boolean.class)==true) {
				// --- Exchange by check box --------------
				JCheckBox displayCheckBox = new JCheckBox();
				this.getJCheckBoxEdit().setBackground(editorComponent.getBackground());
				this.getJCheckBoxEdit().setForeground(editorComponent.getForeground());
				this.getJCheckBoxEdit().setOpaque(editorComponent.isOpaque());
				editorComponent = displayCheckBox;
				
			} else if (Number.class.isAssignableFrom(this.currValueType)) {
				// --- For number types -------------------
				this.getJTextFieldEditor().addKeyListener(this.keyAdapter);
				boolean isDecimalValue = this.currValueType.equals(float.class) || this.currValueType.equals(double.class);
				this.keyAdapter.setForDecimalValue(isDecimalValue);
				
			} else {
				// --- Remove local listener from editor --
				this.getJTextFieldEditor().removeKeyListener(this.keyAdapter);
				
			}
			OpcUaDataValueTableCellRenderer.setDataValueToDisplayComponent(this.currDataValue, editorComponent);
		
		} else {
			this.getJTextFieldEditor().setText("WRONG OR UNKNOWN DATA TYPE!");
		}
	
		return editorComponent;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	@Override
	public Object getCellEditorValue() {
		if (this.currValue instanceof UaVariableNode) {
			((UaVariableNode) this.currValue).setValue(this.getNewDataValue());
			return this.currValue;
		}
		return this.getNewDataValue();
	}
	/**
	 * Returns the new data value.
	 * @return the new data value
	 */
	private DataValue getNewDataValue() {
		if (newDataValue==null) {
			newDataValue = new DataValue(this.currDataValue.getValue());
		}
		return newDataValue;
	}
	/**
	 * Sets the new data value.
	 * @param newDataValue the new new data value
	 */
	private void setNewDataValue(DataValue newDataValue) {
		this.newDataValue = newDataValue;
	}
	
	
	/**
	 * Gets the JCheckBox for boolean value editing
	 * @return the j check box edit
	 */
	private JCheckBox getJCheckBoxEdit() {
		if (jCheckBoxEdit==null) {
			jCheckBoxEdit = new JCheckBox();
			jCheckBoxEdit.setOpaque(true);
			jCheckBoxEdit.setBorder(BorderFactory.createEmptyBorder());
			jCheckBoxEdit.setBackground(new Color(0,0,0,0));
			jCheckBoxEdit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					JCheckBox cb = (JCheckBox) ae.getSource();
					boolean newValue = cb.isSelected();
					stopCellEditing();
					setNewDataValue(new DataValue(new Variant(newValue)));
					informListener();
				}
			});
		}
		return jCheckBoxEdit;
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
			jTextFieldEdit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					stopCellEditing();
					setNewDataValueFromTextField();
					informListener();
				}
			});
		}
		return jTextFieldEdit;
	}
	/**
	 * Sets the new DataValue from text field.
	 */
	private void setNewDataValueFromTextField() {
		
		String newValueString = this.getJTextFieldEditor().getText();
		
		// --- Use as default value -------------------------------------------
		Variant newValueVariant = new Variant(newValueString);
		if (Number.class.isAssignableFrom(this.currValueType)) {
			// --- For numbers, do not allow Null values ----------------------
			if (newValueString==null || newValueString.isBlank()) newValueString = "0";
			
			try {
				if (this.currValueType.equals(byte.class)==true || this.currValueType.equals(Byte.class)==true) {
					newValueVariant = new Variant(Byte.valueOf(newValueString));
				} else if (this.currValueType.equals(short.class)==true || this.currValueType.equals(Short.class)==true) {
					newValueVariant = new Variant(Short.valueOf(newValueString));
				} else if (this.currValueType.equals(int.class)==true || this.currValueType.equals(Integer.class)==true) {
					newValueVariant = new Variant(Integer.parseInt(newValueString));
				} else if (this.currValueType.equals(long.class)==true || this.currValueType.equals(Long.class)==true) {
					newValueVariant = new Variant(Long.parseLong(newValueString));
				} else if (this.currValueType.equals(float.class)==true || this.currValueType.equals(Float.class)==true) {
					newValueVariant = new Variant(Float.parseFloat(newValueString));
				} else if (this.currValueType.equals(double.class)==true || this.currValueType.equals(Double.class)==true) {
					newValueVariant = new Variant(Double.parseDouble(newValueString));
					
				} else if (this.currValueType.equals(UByte.class)==true) {
					newValueVariant = new Variant(UByte.valueOf(newValueString));
				} else if (this.currValueType.equals(UInteger.class)==true) {
					newValueVariant = new Variant(UInteger.valueOf(newValueString));
				} else if (this.currValueType.equals(ULong.class)==true) {
					newValueVariant = new Variant(ULong.valueOf(newValueString));
				} else if (this.currValueType.equals(UShort.class)==true) {
					newValueVariant = new Variant(UShort.valueOf(newValueString));
					
				} else {
					System.err.println("Unknow number type: " + this.currValueType.getName());
				}
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		} else {
			// --- String is used as default ----------------------------------
			
		}
		this.setNewDataValue(new DataValue(newValueVariant));
		
	}
	
	// --------------------------------------------------------------
	// --- From here, listener handling -----------------------------
	// --------------------------------------------------------------
	/**
	 * Returns the listener.
	 * @return the listener
	 */
	private List<OpcUaDataValueTableCellEditorListener> getListener() {
		if (listener==null) {
			listener = new ArrayList<>();
		}
		return listener;
	}
	/**
	 * Adds the opc ua data value table cell editor listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean addOpcUaDataValueTableCellEditorListener(OpcUaDataValueTableCellEditorListener listener) {
		if (listener==null || this.getListener().contains(listener)==true) return false;
		return this.getListener().add(listener);
	}
	/**
	 * Removes the opc ua data value table cell editor listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean removeOpcUaDataValueTableCellEditorListener(OpcUaDataValueTableCellEditorListener listener) {
		if (listener==null) return false;
		return this.getListener().remove(listener);
	}
	/**
	 * Inform listener.
	 */
	private void informListener() {
		for (OpcUaDataValueTableCellEditorListener listener : this.getListener()) {
			try {
				listener.onOpcUaDataValueChange(this.currRow, this.currColumn, this.currDataValue, this.getNewDataValue());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
}
