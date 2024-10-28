package de.enflexit.connector.opcua.ui;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import de.enflexit.connector.opcua.OpcUaConnector;

/**
 * The Class OpcUaConnectorPanel.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaConnectorPanel extends JTabbedPane {

	private static final long serialVersionUID = 8381100221948055055L;

	private OpcUaConnector opcUaConnector;
	
	private JPanel baseConfigPanel;
	private OpcUaViews uaViews;
	
	/**
	 * Instantiates a new OPC/UA connector / configuration panel.
	 *
	 * @param opcUaConnector the current OpcUaConnector
	 * @param baseConfigPanel the base configuration panel
	 */
	public OpcUaConnectorPanel(OpcUaConnector opcUaConnector, JPanel baseConfigPanel) {
		this.opcUaConnector = opcUaConnector;
		this.baseConfigPanel = baseConfigPanel;
		this.initialize();
	}
	
	/**
	 * Initializes the tabs of the configuration panel.
	 */
	private void initialize() {
		this.addTab(" OPC UA - Views ", this.getOpcUaViews());
		this.addTab(" Connection Settings ", this.baseConfigPanel);
	}
	
	/**
	 * Adds the base configuration panel.
	 * @param  baseConfigPanel the base configuration panel
	 */
	public void addBaseConfigurationPanel(JPanel baseConfigPanel) {

		if (baseConfigPanel==null) return;

		// --- Remove all tabs --------
		while (this.getTabCount()>0) {
			this.removeTabAt(0);
		}
		
		this.baseConfigPanel = baseConfigPanel;
		this.initialize();
	}
	
	/**
	 * Returns a OPC/UA data view.
	 * @return the opc ua data view
	 */
	public OpcUaViews getOpcUaViews() {
		if (uaViews==null) {
			uaViews = new OpcUaViews(this.opcUaConnector);
		}
		return uaViews;
	}
	
}
