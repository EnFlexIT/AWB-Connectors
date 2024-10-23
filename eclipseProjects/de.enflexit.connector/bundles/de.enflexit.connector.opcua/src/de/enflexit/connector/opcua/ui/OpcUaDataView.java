package de.enflexit.connector.opcua.ui;

import java.awt.Font;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;

import javax.swing.DropMode;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;

import de.enflexit.connector.opcua.OpcUaConnector;
import de.enflexit.connector.opcua.OpcUaConnectorListener;

/**
 * The Class OpcUaDataView.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaDataView extends JScrollPane implements OpcUaConnectorListener {

	private static final long serialVersionUID = -9117876381830606802L;

	private OpcUaConnector opcUaConnector;
	
	private DefaultTableModel tableModel;
	private JTable jTableDataView;
	
	/**
	 * Instantiates a new OpcUaDataView.
	 * @param opcUaConnector the OpcUaDataView
	 */
	public OpcUaDataView(OpcUaConnector opcUaConnector) {
		this.opcUaConnector = opcUaConnector;
		this.opcUaConnector.addConnectionListener(this);
		this.initialize();
	}
	
	private void initialize() {
		this.setViewportView(this.getJTableDataView());
		this.reFillTableModel();
	}

	private DefaultTableModel getTableModel() {
		if (tableModel==null) {
			Vector<String> header = new Vector<>();
			header.add("UaNode");
			header.add("#");
			header.add("Node ID");
			header.add("DisplayName");
			header.add("Value");
			header.add("DataType");
			header.add("SourceTimestamp");
			header.add("ServerTimestamp");
			header.add("StatusCode");
			
			tableModel = new DefaultTableModel(null, header) {
				private static final long serialVersionUID = -8648404786784718747L;
				@Override
				public boolean isCellEditable(int row, int column) {
					if (column!=5) return false;
					return true;
				}
			};
		}
		return tableModel;
	}
	private JTable getJTableDataView() {
		if (jTableDataView == null) {
			jTableDataView = new JTable(this.getTableModel());
			jTableDataView.setFont(new Font("Dialog", Font.PLAIN, 12));
			jTableDataView.setFillsViewportHeight(true);
			jTableDataView.getTableHeader().setReorderingAllowed(false);

			jTableDataView.setDragEnabled(true);
			jTableDataView.setTransferHandler(new OpcUaBrowserTransferHandler(this.opcUaConnector));
			
			jTableDataView.setDropMode(DropMode.INSERT_ROWS);
			
			TableColumnModel tcm =  jTableDataView.getColumnModel();

			// --- Column UaNode ------------------------------------ 
			tcm.getColumn(0).setWidth(0);
			tcm.getColumn(0).setMinWidth(0);
			tcm.getColumn(0).setMaxWidth(0);
			tcm.getColumn(0).setResizable(false);
			
			// --- Column '#' - No. ---------------------------------
			tcm.getColumn(1).setMaxWidth(35);
			
			// --- Column 'StatusCode -------------------------------
			tcm.getColumn(8).setMaxWidth(100);
			
		}
		return jTableDataView;
	}
	
	/**
	 * Re-Fills the local TableModel.
	 */
	private void reFillTableModel() {
		
		// --- Empty table model ------------------------------------
		this.getTableModel().setRowCount(0);
		
		if (this.opcUaConnector.isConnected()==false) return;
		
		// --- Refill table model -----------------------------------
		List<String> nodeIDList = this.opcUaConnector.getOpcUaDataAccess().getNodeIdListOrdered();
		nodeIDList.forEach(nodeIdString -> this.addDataValue(NodeId.parse(nodeIdString), this.opcUaConnector.getOpcUaDataAccess().getValueHashMap().get(nodeIdString)));
		
	}

	/**
	 * Adds the data value.
	 *
	 * @param nodeID the node ID
	 * @param dataValue the data value
	 */
	private void addDataValue(NodeId nodeID, DataValue dataValue) {
		
		UaNode uaNode = null;
		try {
			uaNode = this.opcUaConnector.getOpcUaClient().getAddressSpace().getNode(nodeID);
			this.addDataRow(uaNode, dataValue);
			
		} catch (UaException uaEx) {
			uaEx.printStackTrace();
		}
	}
	
	/**
	 * Adds the specified {@link UaNode} and its data value as table row.
	 *
	 * @param uaNode the ua node
	 * @param dataValue the data value
	 */
	private void addDataRow(UaNode uaNode, DataValue dataValue) {
		
		Vector<Object> dataRow = new Vector<>();
		// --- UaNode -----------------
		dataRow.add(uaNode);
		
		// --- # ----------------------
		dataRow.add(this.getTableModel().getRowCount() + 1);
		
		// --- Node ID ----------------
		dataRow.add(uaNode.getNodeId().toParseableString());
		
		// --- DisplayName ------------
		LocalizedText lText = uaNode.getDisplayName();
		String valueLocal = "\"" + (lText.getLocale()==null ? "" : lText.getLocale()) + "\"" ; 
		String valueText  = "\"" + (lText.getText()==null ? "" : lText.getText()) + "\"" ;
		dataRow.add(valueLocal + ", " + valueText);
		
		// --- Value ------------------
		Object value = null;
		if (dataValue!=null) {
			// --- From DataValue -----
			value = dataValue.getValue().getValue();
			if (value==null) {
				value = "Null";
			} else if (value.getClass().isArray()==true) {
				Object[] valueArr = (Object[]) value;
				String valueString = "";
				
				for (int i = 0; i < valueArr.length; i++) {
					if (valueString.isBlank()==true) {
						valueString += valueArr[i].toString();
					} else {
						valueString += ", " + valueArr[i].toString();
					}
				}
				value = valueString;
			} 
		}
		dataRow.add(value);
		
		// --- DataType ---------------
		dataRow.add(this.getDataTypeDescription(uaNode));
		
		// --- SourceTimestamp, ServerTimestamp & StatusCode --------
		String sourceTime = "";
		String serverTime = "";
		String status = "?";
		if (dataValue!=null) {
			sourceTime = this.getDateTimeAsString(dataValue.getSourceTime());
			serverTime = this.getDateTimeAsString(dataValue.getServerTime());
			status = this.getQuality(dataValue.getStatusCode()) + " (" + dataValue.getStatusCode().getValue() + ")";
		}
		dataRow.add(sourceTime);
		dataRow.add(serverTime);
		dataRow.add(status);
		
		// --- Add the row to the table model ----------------------- 
		this.getTableModel().addRow(dataRow);
	}
	
	/**
	 * Returns a Milo DateTime instance as string.
	 *
	 * @param dateTime the date time
	 * @return the date time as string
	 */
	private String getDateTimeAsString(DateTime dateTime) {
		return ZonedDateTime.ofInstant(dateTime.getJavaInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(OpcUaConnector.DATE_TIME_PATTERN_FORMAT));
	}
	
	/**
	 * Returns the data type description for the specified UaNode.
	 *
	 * @param uaNodeRow the ua node row
	 * @return the data type description
	 */
	private String getDataTypeDescription(UaNode uaNodeRow) {
		
		String dataTypeDescription = "?";
		if (uaNodeRow instanceof UaVariableNode) {
			
			// --- Get NodeId of DataType -----------------
			NodeId nodeID = ((UaVariableNode) uaNodeRow).getDataType();

			// --- Define a default description ----------- 
			dataTypeDescription = "ns=" + nodeID.getNamespaceIndex().toString() + ", id=" + nodeID.getIdentifier() + ", " + nodeID.getType().name(); 
			
			UaNode uaNodeDataType = null;
			try {
				uaNodeDataType = this.opcUaConnector.getOpcUaClient().getAddressSpace().getNode(nodeID);
				dataTypeDescription = uaNodeDataType.getDisplayName().getText() + " (" + dataTypeDescription + ")";
				
			} catch (UaException uaEx) {
				uaEx.printStackTrace();
			} 
		}
		return dataTypeDescription;
	}
	
	/**
	 * Returns the 'quality' of a StatusCode as String.
	 *
	 * @param statusCode the status code
	 * @return the quality
	 */
	private String getQuality(StatusCode statusCode) {
		if (statusCode.isGood()) {
			return "Good";
		} else if (statusCode.isBad()) {
			return "Bad";
		} else if (statusCode.isUncertain()) {
			return "Uncertain";
		} else {
			return "Unknown";
		}
	}
	
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.opcua.OpcUaConnectorListener#onConnection()
	 */
	@Override
	public void onConnection() { 
		this.reFillTableModel();
	}

	@Override
	public void onDisconnection() {	}

	@Override
	public void onSessionActive() { }

	@Override
	public void onSessionInactive() { }

	@Override
	public void onBrowserUaNodeSelection() { }
	
	
}
