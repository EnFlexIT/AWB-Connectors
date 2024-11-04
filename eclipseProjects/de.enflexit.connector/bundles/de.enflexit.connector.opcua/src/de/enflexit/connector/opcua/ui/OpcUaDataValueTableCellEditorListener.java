package de.enflexit.connector.opcua.ui;

import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;

/**
 * The Class OpcUaDataValueTableCellRenderEditorListener can be registered at 
 * an {@link OpcUaDataValueTableCellEditor} to react on value changes.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public interface OpcUaDataValueTableCellEditorListener {

	/**
	 * React on an OpcUa data value change here. Even the cell value was changed, it is not
	 * implemented to transfer the new value to the server connected.
	 *
	 * @param row the edited table row
	 * @param col the edited table column
	 * @param oldValue the old DataValue
	 * @param newValue the new DataValue
	 */
	public void onOpcUaDataValueChange(int row, int col, DataValue oldDataValue, DataValue newDataValue);

}
