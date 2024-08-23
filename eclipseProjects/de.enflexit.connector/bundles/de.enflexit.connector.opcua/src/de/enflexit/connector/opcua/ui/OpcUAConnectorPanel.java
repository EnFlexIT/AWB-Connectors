package de.enflexit.connector.opcua.ui;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import de.enflexit.connector.opcua.OpcUaConnector;

/**
 * The Class OpcUAConnectorPanel.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUAConnectorPanel extends JTabbedPane {

	private static final long serialVersionUID = 8381100221948055055L;

	private OpcUaConnector opcUaConnector;
	
	private JPanel baseConfigPanel;
	private OpcUaDataView dataView;
	
	/**
	 * Instantiates a new opc UA connector panel.
	 *
	 * @param opcUaConnector the current OpcUaConnector
	 * @param baseConfigPanel the base config panel
	 */
	public OpcUAConnectorPanel(OpcUaConnector opcUaConnector, JPanel baseConfigPanel) {
		this.opcUaConnector = opcUaConnector;
		this.baseConfigPanel = baseConfigPanel;
		this.initialize();
	}
	private void initialize() {
		
		this.addTab(" Connection Settings ", this.baseConfigPanel);
		this.addTab(" Data View ", this.getOpcUaDataView());
		
	}
	
	public OpcUaDataView getOpcUaDataView() {
		if (dataView==null) {
			dataView = new OpcUaDataView(this.opcUaConnector);
		}
		return dataView;
	}
	
	
	
}
