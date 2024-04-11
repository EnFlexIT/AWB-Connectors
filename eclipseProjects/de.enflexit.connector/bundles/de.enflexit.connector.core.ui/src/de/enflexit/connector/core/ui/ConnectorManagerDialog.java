package de.enflexit.connector.core.ui;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

import agentgui.core.application.Application;
import de.enflexit.common.swing.JDialogSizeAndPostionController;
import de.enflexit.common.swing.JDialogSizeAndPostionController.JDialogPosition;

/**
 * A dialog to configure connectors.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorManagerDialog extends JDialog {
	
	private static final long serialVersionUID = 7253191266415274699L;
	private ConnectorManagerMainPanel connectorMainPanel;
	
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
		this.setIconImage(new ImageIcon(this.getClass().getResource("/icons/Connection.png")).getImage());
		this.setSize(900, 600);
		
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				ConnectorManagerDialog.this.setVisible(false);
				ConnectorManagerDialog.this.dispose();
			}
		});
		
		JDialogSizeAndPostionController.setJDialogPositionOnScreen(this, JDialogPosition.ParentCenter);
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				System.out.println("Connector manager dialog resized: " + ConnectorManagerDialog.this.getSize());
			}
		});
	}

	/**
	 * Gets the main panel.
	 * @return the main panel
	 */
	private ConnectorManagerMainPanel getMainPanel() {
		if (connectorMainPanel == null) {
			connectorMainPanel = new ConnectorManagerMainPanel();
		}
		return connectorMainPanel;
	}
	
	@Override
	public void dispose() {
		this.getMainPanel().dispose();
		super.dispose();
	}
}
