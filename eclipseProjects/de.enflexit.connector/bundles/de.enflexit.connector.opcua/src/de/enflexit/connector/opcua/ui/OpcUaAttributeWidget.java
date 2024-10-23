package de.enflexit.connector.opcua.ui;

import java.awt.Font;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.eclipse.milo.opcua.sdk.client.model.nodes.objects.FolderTypeNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;

import de.enflexit.connector.opcua.OpcUaConnector;
import de.enflexit.connector.opcua.OpcUaConnectorListener;

/**
 * The Class OpcUaAttributeWidget.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaAttributeWidget extends JScrollPane implements OpcUaConnectorListener {

	private static final long serialVersionUID = -4837654776526023691L;

	private OpcUaConnector opcUaConnector;
	
	private DefaultTableModel tableModel;
	private JTable jTableAttributes;
	
			
	/**
	 * Instantiates a new OpcUaAttributeWidget.
	 * @param opcUaConnector the OpcUaConnector
	 */
	public OpcUaAttributeWidget(OpcUaConnector opcUaConnector) {
		this.opcUaConnector = opcUaConnector;
		this.opcUaConnector.addConnectionListener(this);
		this.initialize();
	}
	/**
	 * Initialize the current widget.
	 */
	private void initialize() {
		this.setViewportView(this.getJTableAttributes());
	}
	
	private DefaultTableModel getTableModel() {
		if (tableModel==null) {
			Vector<String> header = new Vector<>();
			header.add("Attribute");
			header.add("Value");
			tableModel = new DefaultTableModel(null, header) {
				private static final long serialVersionUID = -8648404786784718747L;
				@Override
				public boolean isCellEditable(int row, int column) {

					if (column==0) return false;
					
					String keyValue = (String) OpcUaAttributeWidget.this.getTableModel().getValueAt(row, 0);
					if (keyValue.equalsIgnoreCase("Value")==true) {
						return true;
					}
					return false;
				}
				
			};
		}
		return tableModel;
	}
	private JTable getJTableAttributes() {
		if (jTableAttributes == null) {
			jTableAttributes = new JTable(this.getTableModel());
			jTableAttributes.setFont(new Font("Dialog", Font.PLAIN, 12));
			jTableAttributes.setFillsViewportHeight(true);
			jTableAttributes.getTableHeader().setReorderingAllowed(false);

		}
		return jTableAttributes;
	}
	
	private void clearTable() {
		this.getTableModel().setRowCount(0);
	}
	
	
	private void addRow(String key, Object value) {
		Vector<Object> dataRow = new Vector<>();
		dataRow.add(key);
		dataRow.add(value);
		this.getTableModel().addRow(dataRow);
	}
	
	private void addRow(String key, LocalizedText lText) {
		String valueLocal = "\"" + (lText.getLocale()==null ? "" : lText.getLocale()) + "\"" ; 
		String valueText  = "\"" + (lText.getText()==null ? "" : lText.getText()) + "\"" ;
		this.addRow(key, valueLocal + ", " + valueText);
	}
	
	private void addRow(String key, DateTime dateTime) {
		this.addRow(key, ZonedDateTime.ofInstant(dateTime.getJavaInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(OpcUaConnector.DATE_TIME_PATTERN_FORMAT)));
	}
	
	private void addRow(String key, UShort uShort) {
		this.addRow(key, uShort==null ? 0 : uShort.longValue());
	}
	
	private void addRow(String key, StatusCode statusCode) {
		this.addRow(key, this.getQuality(statusCode) + " (" + statusCode.getValue() + ")");
	}
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
	
	private void addRow(String key, UInteger[] uIntegerArray) {
		
		String description = "Null";
		if (uIntegerArray!=null) {
			description = "[";
			for (int i = 0; i < uIntegerArray.length; i++) {
				UInteger uInt = uIntegerArray[i];
				description += uInt.intValue();
				if (i < uIntegerArray.length-1) {
					description += ",";	
				}
			}
			description += "]";
			
		}
		this.addRow(key, description);
	}
	
	private void addRow(String key, NodeId nodeID) {
		
		// --- Define a default description ----- 
		String nodeIDDescription = "ns=" + nodeID.getNamespaceIndex().toString() + ", id=" + nodeID.getIdentifier() + ", " + nodeID.getType().name(); 

		UaNode uaNode = null;
		try {
			uaNode = this.opcUaConnector.getOpcUaClient().getAddressSpace().getNode(nodeID);
			nodeIDDescription = uaNode.getDisplayName().getText() + " (" + nodeIDDescription + ")";
			
		} catch (UaException uaEx) {
			uaEx.printStackTrace();
		} 
		this.addRow(key, nodeIDDescription);
	}
	
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.opcua.OpcUaConnectorListener#onBrowserUaNodeSelection()
	 */
	@Override
	public void onBrowserUaNodeSelection() {

		// --- Stop editing & clear table model ---------------------
		if (this.getJTableAttributes().getCellEditor()!=null) {
			this.getJTableAttributes().getCellEditor().stopCellEditing();
		}
		this.clearTable();
		
		UaNode uaNode = this.opcUaConnector.getBrowserUaNode();
		
		this.addRow("NodeID", uaNode.getNodeId().toParseableString());
		this.addRow("NodeClass", uaNode.getNodeClass().name());
		this.addRow("BrowseName", uaNode.getBrowseName().toParseableString());

		this.addRow("DisplayName", uaNode.getDisplayName());
		this.addRow("Description", uaNode.getDescription());
		
		if (uaNode instanceof FolderTypeNode) {
			this.addRow("EventNotifier", ((FolderTypeNode)uaNode).getEventNotifier());
		}
		
		// --- For nodes/variable with data type and value ----------
		if (uaNode instanceof UaVariableNode) {
			
			UaVariableNode uaVarNode = (UaVariableNode)uaNode;
			try {
				uaVarNode.readValue();
			} catch (UaException uaEx) {
				uaEx.printStackTrace();
			}
			this.addRows(uaVarNode.getValue());
			
			this.addRow("DataType", uaVarNode.getDataType());
			
			this.addRow("ValueRank", uaVarNode.getValueRank());
			this.addRow("ArrayDimensions", uaVarNode.getArrayDimensions());
			this.addRow("AccessLevel", uaVarNode.getAccessLevel());
			this.addRow("UserAccessLevel", uaVarNode.getUserAccessLevel());
			
			this.addRow("MinimumSamplingInterval", uaVarNode.getMinimumSamplingInterval());
			this.addRow("Historizing", uaVarNode.getHistorizing());
		}

		this.addRow("WriteMask", uaNode.getWriteMask().intValue());
		this.addRow("UserWriteMask", uaNode.getUserWriteMask().intValue());
		
	}
	/**
	 * Adds table rows for the specified DataValue.
	 * @param dataValue the data value
	 */
	private void addRows(DataValue dataValue) {

		this.addRow("SourceTimeStamp", dataValue.getSourceTime());
		this.addRow("SourcePicoseconds", dataValue.getSourcePicoseconds());
		
		this.addRow("ServerTimeStamp", dataValue.getServerTime());
		this.addRow("ServerPicoseconds", dataValue.getServerPicoseconds());
		this.addRow("StatusCode", dataValue.getStatusCode());
		
		// --- Add the value --------------------
		Object value = dataValue.getValue().getValue();
		if (value==null) {
			this.addRow("Value", "Null");
		} else if (value.getClass().isArray()==true) {
			Object[] valueArr = (Object[]) value;
			for (int i = 0; i < valueArr.length; i++) {
				this.addRow("Value [" + i + "]", valueArr[i]);
			}
		} else {
			this.addRow("Value", value);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.opcua.OpcUaConnectorListener#onConnection()
	 */
	@Override
	public void onConnection() { }
	/* (non-Javadoc)
	 * @see de.enflexit.connector.opcua.OpcUaConnectorListener#onDisconnection()
	 */
	@Override
	public void onDisconnection() { }
	/* (non-Javadoc)
	 * @see de.enflexit.connector.opcua.OpcUaConnectorListener#onSessionActive()
	 */
	@Override
	public void onSessionActive() { }
	/* (non-Javadoc)
	 * @see de.enflexit.connector.opcua.OpcUaConnectorListener#onSessionInactive()
	 */
	@Override
	public void onSessionInactive() { }
	
}
