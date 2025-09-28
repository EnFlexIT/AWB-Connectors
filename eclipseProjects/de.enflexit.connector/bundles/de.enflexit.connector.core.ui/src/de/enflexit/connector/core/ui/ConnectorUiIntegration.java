package de.enflexit.connector.core.ui;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import de.enflexit.awb.baseUI.SeparatorPosition;
import de.enflexit.awb.baseUI.mainWindow.MainWindowExtension;
import de.enflexit.awb.core.Application;
import de.enflexit.awb.core.ui.AwbMainWindowMenu;
import de.enflexit.common.swing.AwbThemeImageIcon;
import de.enflexit.connector.core.manager.ConnectorManager;

/**
 * This class implements the UI integration for the {@link ConnectorManager} into the AWB:
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorUiIntegration extends MainWindowExtension implements ActionListener {
	
	private enum IntegrationType {
		APPLICATION, TRAY_ICON, NONE
	}
	
	private static final String ICON_PATH_LIGHT_MODE = "/icons/Connection_LightMode.png";
	private static final String ICON_PATH_DARK_MODE = "/icons/Connection_DarkMode.png";
	
	private AwbThemeImageIcon themedIcon;
	private JButton toolbarButton;
	private JMenuItem menuItem;
	private MenuItem trayIconMenuItem;

	private static ConnectorManagerDialog cmDialog;
	
	
	/* (non-Javadoc)
	 * @see org.agentgui.gui.swing.MainWindowExtension#initialize()
	 */
	@Override
	public void initialize() {
		switch (this.getIntegrationType()) {
		case APPLICATION:
			// --- Tool bar and tray icon menu ------------
			this.addTrayIconMenuItem(this.getTrayIconMenuItem(), 5, SeparatorPosition.SeparatorAfter);
			this.addJMenuItem(AwbMainWindowMenu.MenuExtra, this.getJMenuItem(), 6, SeparatorPosition.NoSeparator);
			this.addToolbarComponent(this.getToolbarButton(), 7, SeparatorPosition.SeparatorAfter);
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
	 * Gets the image icon.
	 * @return the image icon
	 */
	private AwbThemeImageIcon getThemedIcon() {
		if (themedIcon==null) {
			ImageIcon imageIconLightMode = new ImageIcon(this.getClass().getResource(ICON_PATH_LIGHT_MODE));
			ImageIcon imageIconDarkMode = new ImageIcon(this.getClass().getResource(ICON_PATH_DARK_MODE));
			themedIcon = new AwbThemeImageIcon(imageIconLightMode, imageIconDarkMode);
		}
		return themedIcon;
	}
	/**
	 * Gets the toolbar button.
	 * @return the toolbar button
	 */
	private JButton getToolbarButton() {
		if (toolbarButton==null) {
			toolbarButton = new JButton(this.getThemedIcon());
			toolbarButton.setToolTipText("Configure connections");
			toolbarButton.addActionListener(this);
		}
		return toolbarButton;
	}
	/**
	 * Gets the menu item.
	 * @return the menu item
	 */
	private JMenuItem getJMenuItem() {
		if (menuItem==null) {
			menuItem = new JMenuItem(ConnectorManagerDialog.TITLE, this.getThemedIcon());
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
		if (ae.getSource()==this.getToolbarButton() || ae.getSource()==this.getTrayIconMenuItem() || ae.getSource()==this.getJMenuItem()) {
			ConnectorUiIntegration.openOrFocusConnectorManagerDialog();
		}
	}
	/**
	 * Open or focus connector manager dialog.
	 */
	private static void openOrFocusConnectorManagerDialog() {
		if (cmDialog==null) {
			cmDialog = new ConnectorManagerDialog();
			cmDialog.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					ConnectorUiIntegration.cmDialog = null;
				}
			});
			cmDialog.setVisible(true);
		} else {
			cmDialog.toFront();
			cmDialog.requestFocus();
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
	
	
}
