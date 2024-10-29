package de.enflexit.connector.opcua.ui.endpoint;

import javax.swing.JPanel;

import de.enflexit.connector.opcua.OpcUaConnector;
import java.awt.GridBagLayout;

/**
 * The Class OpcUaSubscriptionPanel.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaSubscriptionPanel extends JPanel {

	private static final long serialVersionUID = 3456689990982277842L;

	private OpcUaConnector opcUaConnector;
	
	public OpcUaSubscriptionPanel(OpcUaConnector opcUaConnector) {
		this.opcUaConnector = opcUaConnector;
		this.initialize();
	}
	/**
	 * Initializes this panel.
	 */
	private void initialize() {
		
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0};
		gridBagLayout.rowHeights = new int[]{0};
		gridBagLayout.columnWeights = new double[]{Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{Double.MIN_VALUE};
		this.setLayout(gridBagLayout);
		
	}

}
