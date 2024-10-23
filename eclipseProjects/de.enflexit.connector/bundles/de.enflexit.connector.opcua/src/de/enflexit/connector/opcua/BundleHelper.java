package de.enflexit.connector.opcua;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JComponent;


/**
 * The Class BundleHelper provides some static help methods to be used within the bundle.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class BundleHelper {

	private static final String imagePackage = "/icons/";
	
	/**
	 * Gets the image package location as String.
	 * @return the image package
	 */
	public static String getImagePackage() {
		return imagePackage;
	}
	/**
	 * Gets the image icon for the specified image.
	 *
	 * @param fileName the file name
	 * @return the image icon
	 */
	public static ImageIcon getImageIcon(String fileName) {
		String imagePackage = getImagePackage();
		ImageIcon imageIcon=null;
		try {
			imageIcon = new ImageIcon(BundleHelper.class.getResource((imagePackage + fileName)));
		} catch (Exception err) {
			System.err.println("Error while searching for image file '" + fileName + "' in " + imagePackage);
			err.printStackTrace();
		}	
		return imageIcon;
	}
	
	
	/**
	 * Searches the first instance of the component on a parent component, specified by it's class.
	 *
	 * @param <Type> the generic type
	 * @param parentComponent the parent component
	 * @param typeClass the type class
	 * @return the component by class
	 */
	@SuppressWarnings("unchecked")
	public static <Type extends JComponent> Type getSubComponentByClass(JComponent parentComponent, Class<Type> typeClass) {
		
		// --- Fast exit? ---------------------------------
		if (typeClass == null) return null;
		if (parentComponent.getClass().equals(typeClass)==true) {
			return (Type) parentComponent; 
		}
		
		// --- Search sub components ----------------------
		for (int i = 0; i < parentComponent.getComponentCount(); i++) {
			
			Component subComp = parentComponent.getComponent(i);
			if (subComp.getClass().equals(typeClass)==true) {
				return (Type) subComp; 
			}

			if (subComp instanceof JComponent) {
				JComponent subJComp = (JComponent) subComp;
				if (subJComp.getComponentCount()>0) {
					Type compFound = getSubComponentByClass(subJComp, typeClass);
					if (compFound!=null) {
						return compFound;
					}
				}
			}
			
		}
		return null;
	}
	
	/**
	 * Searches for the instance of the specified class in the parent elements of the specified component.
	 *
	 * @param <Type> the generic type
	 * @param component the component
	 * @param typeClass the type class
	 * @return the component by class
	 */
	@SuppressWarnings("unchecked")
	public static <Type extends JComponent> Type getParentComponentByClass(JComponent component, Class<Type> typeClass) {

		if (typeClass == null) return null;
		
		Component parentComponent = component;
		while (parentComponent!=null) {
			if (parentComponent.getClass().equals(typeClass)==true) {
				return (Type) parentComponent;
			}
			parentComponent = parentComponent.getParent();
		}
		return null;
	}
	
}
