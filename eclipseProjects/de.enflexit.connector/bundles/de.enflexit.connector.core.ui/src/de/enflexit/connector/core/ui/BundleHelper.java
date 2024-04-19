package de.enflexit.connector.core.ui;

import javax.swing.ImageIcon;

/**
 * This class contains some static helper methods.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class BundleHelper {
	
	private static final String ICON_PATH = "/icons/";
	
	public static ImageIcon getImageIcon(String iconFileName) {
		return new ImageIcon(BundleHelper.class.getResource(ICON_PATH + iconFileName));
	}
}
