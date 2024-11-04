package de.enflexit.connector.opcua.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.DropMode;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;

import de.enflexit.connector.opcua.BundleHelper;
import de.enflexit.connector.opcua.OpcUaConnector;
import de.enflexit.connector.opcua.OpcUaConnectorListener;
import de.enflexit.connector.opcua.OpcUaDataAccessSubscriptionListener;

/**
 * The Class OpcUaDataView.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaDataView extends JScrollPane implements OpcUaConnectorListener, OpcUaDataAccessSubscriptionListener, OpcUaDataValueTableCellEditorListener {

	private static final long serialVersionUID = -9117876381830606802L;

	private static final String HEADER_UA_NODE = "UaNode";
	private static final String HEADER_NO = "#";
	private static final String HEADER_NODE_ID = "NodeID";
	private static final String HEADER_DISPLAY_NAME = "DisplayName";
	private static final String HEADER_VALUE = "Value";
	private static final String HEADER_DATA_TYPE = "DataType";
	private static final String HEADER_SOURCE_TIMESTAMP = "SourceTimestamp";
	private static final String HEADER_SERVER_TIMESTAMP = "ServerTimestamp";
	private static final String HEADER_STATUS_CODE = "StatusCode";
	
	
	private OpcUaConnector opcUaConnector;
	
	private HashMap<String, Integer> colNameIndexHashMap;
	private HashMap<NodeId, Vector<Object>> dataRowHashMap;
	
	private DefaultTableModel tableModel;
	private JTable jTableDataView;
	private JPopupMenu jPopupMenuTable;
	private JMenuItem jMenueItemRemoveOpcUaNode;
	
	/**
	 * Instantiates a new OpcUaDataView.
	 * @param opcUaConnector the OpcUaDataView
	 */
	public OpcUaDataView(OpcUaConnector opcUaConnector) {
		this.opcUaConnector = opcUaConnector;
		this.opcUaConnector.addConnectionListener(this);
		this.opcUaConnector.getOpcUaDataAccess().addOpcUaDataAccessSubscriptionListener(this);
		this.initialize();
	}
	
	private void initialize() {
		this.setViewportView(this.getJTableDataView());
		this.reFillTableModel();
	}

	/**
	 * Returns the table model.
	 * @return the table model
	 */
	private DefaultTableModel getTableModel() {
		if (tableModel==null) {
			
			// --- Define Header vector -------------------
			Vector<String> headerVector = new Vector<>();
			headerVector.add(HEADER_UA_NODE);
			headerVector.add(HEADER_NO);
			headerVector.add(HEADER_NODE_ID);
			headerVector.add(HEADER_DISPLAY_NAME);
			headerVector.add(HEADER_VALUE);
			headerVector.add(HEADER_DATA_TYPE);
			headerVector.add(HEADER_SOURCE_TIMESTAMP);
			headerVector.add(HEADER_SERVER_TIMESTAMP);
			headerVector.add(HEADER_STATUS_CODE);
			
			// --- Remind header index --------------------
			this.colNameIndexHashMap = new HashMap<>();
			for (int i = 0; i < headerVector.size(); i++) {
				this.colNameIndexHashMap.put(headerVector.get(i), i);
			}

			// --- Define table model ---------------------
			tableModel = new DefaultTableModel(null, headerVector) {
				
				private static final long serialVersionUID = -8648404786784718747L;
				private int colIdxValue = -1; 
				
				/* (non-Javadoc)
				 * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
				 */
				@Override
				public boolean isCellEditable(int row, int column) {
					return (column==this.getIndexOfValueColumn());
				}
				/**
				 * Returns the index number of the value column.
				 * @return the index of value column
				 */
				private int getIndexOfValueColumn() {
					if (colIdxValue==-1) {
						for (int colIdx = 0; colIdx < this.getColumnCount(); colIdx++) {
							if (this.getColumnName(colIdx).equals(HEADER_VALUE)==true) {
								colIdxValue = colIdx;
								break;
							}
						}
					}
					return colIdxValue;
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
			
			jTableDataView.addMouseListener(new MouseAdapter() {
			    @Override
			    public void mouseReleased(MouseEvent me) {
			    	
			    	JTable jTableDV = OpcUaDataView.this.jTableDataView;
			    	
			    	// --- Change selection? ------------------------
			    	if (jTableDV.getSelectedRowCount()<=1) {
			    		int row = jTableDV.rowAtPoint(me.getPoint());
			    		if (row >= 0 && row < jTableDV.getRowCount()) {
			    			jTableDV.setRowSelectionInterval(row, row);
			    		} else {
			    			return;
			    		}
			    	}
			    	
			    	// --- Show popup? ------------------------------
			        if (jTableDV.getSelectedRow() >= 0 && me.isPopupTrigger() && me.getComponent() instanceof JTable ) {
			        	OpcUaDataView.this.getJPopupMenuTable().show(me.getComponent(), me.getX(), me.getY());
			        }
			    }
			});
			
			jTableDataView.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent ke) {
					if (ke.getKeyCode()==KeyEvent.VK_DELETE) {
						OpcUaDataView.this.removeSelectedOpcUaNodeFromList();
					}
				}				
			});
			
			// --- Define initial column width ----------------------
			TableColumnModel tcm =  jTableDataView.getColumnModel();

			// --- Column UaNode ------------------------------------ 
			tcm.getColumn(0).setWidth(0);
			tcm.getColumn(0).setMinWidth(0);
			tcm.getColumn(0).setMaxWidth(0);
			tcm.getColumn(0).setResizable(false);
			
			// --- Column '#' - No. ---------------------------------
			tcm.getColumn(1).setMaxWidth(40);
			
			// --- DataValue cell renderer & editor ----------------- 
			tcm.getColumn(this.colNameIndexHashMap.get(HEADER_VALUE)).setCellRenderer(new OpcUaDataValueTableCellRenderer());
			tcm.getColumn(this.colNameIndexHashMap.get(HEADER_VALUE)).setCellEditor(new OpcUaDataValueTableCellEditor(this));

			// --- Column 'StatusCode -------------------------------
			tcm.getColumn(8).setMaxWidth(100);
			
		}
		return jTableDataView;
	}

	/* (non-Javadoc)
	 * @see de.enflexit.connector.opcua.ui.OpcUaDataValueTableCellEditorListener#onOpcUaDataValueChange(int, int, org.eclipse.milo.opcua.stack.core.types.builtin.DataValue, org.eclipse.milo.opcua.stack.core.types.builtin.DataValue)
	 */
	@Override
	public void onOpcUaDataValueChange(int row, int col, DataValue oldDataValue, DataValue newDataValue) {
		
		System.out.println("[" + this.getClass().getSimpleName() + "] Value changed in row " + row + ",  column " + col); 
		
	}
	
	
	public JPopupMenu getJPopupMenuTable() {
		if (jPopupMenuTable==null) {
			jPopupMenuTable = new JPopupMenu();
			jPopupMenuTable.add(this.getJMenueItemRemoveOpcUaNode());
		}
		return jPopupMenuTable;
	}
	private JMenuItem getJMenueItemRemoveOpcUaNode() {
		if (jMenueItemRemoveOpcUaNode==null) {
			jMenueItemRemoveOpcUaNode = new JMenuItem("Remove from 'Data View' list");
			jMenueItemRemoveOpcUaNode.setIcon(BundleHelper.getImageIcon("ListMinus.png"));
			jMenueItemRemoveOpcUaNode.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					OpcUaDataView.this.removeSelectedOpcUaNodeFromList();
				}
			});
			
		}
		return jMenueItemRemoveOpcUaNode;
	}
	/**
	 * Removes the selected row of an UaNode from the list.
	 */
	private void removeSelectedOpcUaNodeFromList() {
		
		if (this.getJTableDataView().getSelectedRowCount()==-0) return;
		
		// --- Remove each UaNode ID --------------------------------
		int colUaNode = this.getIndexOfColumnName(HEADER_UA_NODE);
		for (int rowSel : this.getJTableDataView().getSelectedRows()) {
			// --- Get the UaNode to remove -------------------------
			UaNode uaNodeToRemove = (UaNode) this.getJTableDataView().getValueAt(rowSel, colUaNode);
			OpcUaDataView.this.opcUaConnector.getOpcUaDataAccess().removeOpcUaNode(uaNodeToRemove);
			this.getDataRowHashMap().remove(uaNodeToRemove.getNodeId());
		}
		// --- Refill the local table model -------------------------
		this.reFillTableModel();
	}
	
	/**
	 * Returns the index of column name.
	 *
	 * @param colName the col name
	 * @return the index of column name
	 */
	private int getIndexOfColumnName(String colName) {
		if (this.tableModel==null) return -1; 
		return this.colNameIndexHashMap.get(colName);
	}
	/**
	 * Returns the data row hash map that enables faster table row access.
	 * @return the data row hash map
	 */
	private HashMap<NodeId, Vector<Object>> getDataRowHashMap() {
		if (dataRowHashMap==null) {
			dataRowHashMap = new HashMap<>();
		}
		return dataRowHashMap;
	}
	
	/**
	 * Re-Fills the local TableModel.
	 */
	private void reFillTableModel() {
		
		// --- Empty table model ------------------------------------
		this.getTableModel().setRowCount(0);
		this.getDataRowHashMap().clear();
		
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
		dataRow.add(null);
		
		// --- DataType ---------------
		dataRow.add(this.getDataTypeDescription(uaNode));
		
		// --- SourceTimestamp, ServerTimestamp & StatusCode --------
		dataRow.add(null);
		dataRow.add(null);
		dataRow.add(null);

		// --- Call update method for table data row ---------------- 
		this.updateDataRow(dataRow, dataValue);
		// --- Add the row to the table model -----------------------
		this.getTableModel().addRow(dataRow);
		// --- Remind the data row for later updates ----------------
		this.getDataRowHashMap().put(uaNode.getNodeId(), dataRow);
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.opcua.OpcUaDataAccessSubscriptionListener#onSubscriptionValueUpdate(org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem, org.eclipse.milo.opcua.stack.core.types.builtin.DataValue)
	 */
	@Override
	public void onSubscriptionValueUpdate(UaMonitoredItem item, DataValue dataValue) {
		
		boolean isDebug = false;
		if (isDebug) System.out.println("subscription value received: item=" + item.getReadValueId().getNodeId() + ", value=" + dataValue.getValue());
		
		NodeId nodeID = item.getReadValueId().getNodeId();
		Vector<Object> dataRow = this.getDataRowHashMap().get(nodeID);
		if (dataRow!=null) {
			// --- Data row is already available ----------
			this.updateDataRow(dataRow, dataValue);
			int modelRowNumber = (int) dataRow.get(this.getIndexOfColumnName(HEADER_NO)) - 1;
			this.getTableModel().fireTableRowsUpdated(modelRowNumber, modelRowNumber);
			
		} else {
			// --- No data row found, create one ----------
			UaNode uaNode = null;
			try {
				uaNode = item.getClient().getAddressSpace().getNode(nodeID);
				this.addDataRow(uaNode, dataValue);
				
			} catch (UaException uaEx) {
				uaEx.printStackTrace();
			}
		}
	}
	/**
	 * Updates the specified table data row with the DataValue.
	 *
	 * @param dataRow the data row
	 * @param value the value
	 */
	private void updateDataRow(Vector<Object> dataRow, DataValue dataValue) {
		
		if (dataRow==null || dataValue==null) return;
		
		// --- Value ------------------
		dataRow.set(this.getIndexOfColumnName(HEADER_VALUE), dataValue);
		
		// --- SourceTimestamp, ServerTimestamp & StatusCode --------
		String sourceTime = "";
		String serverTime = "";
		String status = "?";
		if (dataValue!=null) {
			sourceTime = this.getDateTimeAsString(dataValue.getSourceTime());
			serverTime = this.getDateTimeAsString(dataValue.getServerTime());
			status = this.getQuality(dataValue.getStatusCode()) + " (" + dataValue.getStatusCode().getValue() + ")";
		}
		dataRow.set(this.getIndexOfColumnName(HEADER_SOURCE_TIMESTAMP), sourceTime);
		dataRow.set(this.getIndexOfColumnName(HEADER_SERVER_TIMESTAMP), serverTime);
		dataRow.set(this.getIndexOfColumnName(HEADER_STATUS_CODE), status);
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
		this.opcUaConnector.getOpcUaDataAccess().addOpcUaDataAccessSubscriptionListener(this);
		this.reFillTableModel();
	}

	/* (non-Javadoc)
	 * @see de.enflexit.connector.opcua.OpcUaConnectorListener#onDisconnection()
	 */
	@Override
	public void onDisconnection() {	}
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
	/* (non-Javadoc)
	 * @see de.enflexit.connector.opcua.OpcUaConnectorListener#onBrowserUaNodeSelection()
	 */
	@Override
	public void onBrowserUaNodeSelection() { }

	
}
