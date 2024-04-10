package de.enflexit.connector.core.ui;

import javax.swing.JDialog;
import javax.swing.JPanel;

import agentgui.core.application.Application;
import de.enflexit.common.swing.JDialogSizeAndPostionController;
import de.enflexit.common.swing.JDialogSizeAndPostionController.JDialogPosition;

/**
 * A dialog to configure connectors.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorManagerDialog extends JDialog {
	
	private static final long serialVersionUID = 7253191266415274699L;
	private ConnectorManagerMainPanel mainPanel;
	
	/**
	 * Instantiates a new connector manager dialog.
	 */
	public ConnectorManagerDialog() {
		super(Application.getMainWindow());
		initialize();
	}
	
	/**
	 * Initialize the dialog.
	 */
	private void initialize() {
		this.setContentPane(this.getMainPanel());
		this.setTitle("Connector configuration");
		this.setSize(800, 600);
		JDialogSizeAndPostionController.setJDialogPositionOnScreen(this, JDialogPosition.ParentCenter);
	}

	/**
	 * Gets the main panel.
	 * @return the main panel
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new ConnectorManagerMainPanel();
		}
		return mainPanel;
	}
}
