package de.enflexit.connector.core.ui;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;

import org.agentgui.gui.swing.MainWindowExtension;
import agentgui.core.application.Application;
import agentgui.core.gui.MainWindow.WorkbenchMenu;
import de.enflexit.connector.core.manager.ConnectorManager;

/**
 * This class implements the UI integration for the {@link ConnectorManager} into the AWB:
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorUiIntegration extends MainWindowExtension implements ActionListener {
	
	private enum IntegrationType {
		APPLICATION, TRAY_ICON, NONE
	}
	
	private static final String ICON_PATH = "/icons/Connection.png";
	
	private JButton toolbarButton;
	private JMenuItem menuItem;
	private MenuItem trayIconMenuItem;
	
	private ImageIcon imageIcon;
	
	/**
	 * Initialize.
	 */
	@Override
	public void initialize() {
		switch (this.getIntegrationType()) {
		case APPLICATION:
			// --- Tool bar and tray icon menu ------------
			this.addToolbarComponent(this.getToolbarButton(), 8, SeparatorPosition.NoSeparator);
			this.addTrayIconMenuItem(this.getTrayIconMenuItem(), 4, SeparatorPosition.SeparatorInFrontOf);
			this.addJMenuItem(WorkbenchMenu.MenuExtra, this.getMenuItem(), 6, SeparatorPosition.NoSeparator);
			break;
		case TRAY_ICON:
			// --- Tray icon menu only --------------------
			this.addTrayIconMenuItem(this.getTrayIconMenuItem(), 4, SeparatorPosition.SeparatorInFrontOf);
			break;
		case NONE:
			// --- Nothing to do here ---------------------
			break;
		}
	}
	
	/**
	 * Gets the toolbar button.
	 * @return the toolbar button
	 */
	private JButton getToolbarButton() {
		if (toolbarButton==null) {
			toolbarButton = new JButton(this.getImageIcon());
			toolbarButton.setToolTipText("Configure connector settings");
			toolbarButton.addActionListener(this);
		}
		return toolbarButton;
	}
	
	/**
	 * Gets the menu item.
	 * @return the menu item
	 */
	private JMenuItem getMenuItem() {
		if (menuItem==null) {
			menuItem = new JMenuItem("Connector Manager", this.getImageIcon());
			menuItem.addActionListener(this);
		}
		return menuItem;
	}

	/**
	 * Gets the tray icon menu item.
	 * @return the tray icon menu item
	 */
	private MenuItem getTrayIconMenuItem() {
		if (trayIconMenuItem==null) {
			trayIconMenuItem = new MenuItem("Connector Manager");
			trayIconMenuItem.addActionListener(this);
		}
		return trayIconMenuItem;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==this.getToolbarButton() || ae.getSource()==this.getMenuItem() || ae.getSource()==this.getTrayIconMenuItem()) {
			ConnectorManagerDialog.showDialog();
		}
	}
	
	/**
	 * Gets the integration type.
	 * @return the integration type
	 */
	private IntegrationType getIntegrationType() {
		IntegrationType integrationType = null;
		
		if (Application.isOperatingHeadless()==true) {
			integrationType = IntegrationType.NONE;
		} else {
			
			switch (Application.getGlobalInfo().getExecutionMode()) {
			case APPLICATION:
				integrationType = IntegrationType.APPLICATION;
				break;
				
			case DEVICE_SYSTEM:
				switch (Application.getGlobalInfo().getDeviceServiceExecutionMode()) {
				case AGENT:
					integrationType = IntegrationType.TRAY_ICON;
					break;
				case SETUP:
					integrationType = IntegrationType.APPLICATION;
					break;
				}
				break;
			case SERVER:
			case SERVER_MASTER:
			case SERVER_SLAVE:
				integrationType = IntegrationType.TRAY_ICON;
				break;
			}
		}
		
		return integrationType;
	}
	
	/**
	 * Gets the image icon.
	 * @return the image icon
	 */
	private ImageIcon getImageIcon() {
		if (imageIcon==null) {
			imageIcon = new ImageIcon(this.getClass().getResource(ICON_PATH));
		}
		return imageIcon;
	}
	
}
