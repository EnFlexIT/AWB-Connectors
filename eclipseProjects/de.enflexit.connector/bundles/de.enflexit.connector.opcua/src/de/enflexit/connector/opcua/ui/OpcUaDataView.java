package de.enflexit.connector.opcua.ui;

import java.awt.Font;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

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
	}

	private DefaultTableModel getTableModel() {
		if (tableModel==null) {
			Vector<String> header = new Vector<>();
			header.add("UaNode");
			header.add("#");
			header.add("Server");
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
					if (column!=4) return false;
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

			TableColumnModel tcm =  jTableDataView.getColumnModel();

			// --- Column UaNode ------------------------------------ 
			tcm.getColumn(0).setWidth(0);
			tcm.getColumn(0).setMinWidth(0);
			tcm.getColumn(0).setMaxWidth(0);
			tcm.getColumn(0).setResizable(false);
			
			// --- Column '#' - No. ---------------------------------
			tcm.getColumn(1).setMaxWidth(40);
			
		}
		return jTableDataView;
	}
	
	
	
	@Override
	public void onConnection() { }

	@Override
	public void onDisconnection() {	}

	@Override
	public void onSessionActive() { }

	@Override
	public void onSessionInactive() { }

	@Override
	public void onBrowserUaNodeSelection() { }
	
	
}
