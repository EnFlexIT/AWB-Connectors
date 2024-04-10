package de.enflexit.connector.core.ui;

import java.awt.Component;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.enflexit.common.ServiceFinder;
import de.enflexit.connector.core.ConnectorService;

/**
 * A {@link JComboBox} that provides a selection of available {@link ConnectorService} implementations.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorServiceComboBox extends JComboBox<ConnectorService> {

	private static final long serialVersionUID = 3233062077462831687L;
	
	private DefaultComboBoxModel<ConnectorService> comboBoxModel;

	/**
	 * Instantiates a new connector service combo box.
	 */
	public ConnectorServiceComboBox() {
		super();
		this.setModel(this.getComboBoxModel());
		this.setRenderer(new ListCellRenderer<ConnectorService>() {

			/* (non-Javadoc)
			 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
			 */
			@Override
			public Component getListCellRendererComponent(JList<? extends ConnectorService> servicesList, ConnectorService selectedService, int arg2, boolean arg3, boolean arg4) {
				JLabel rendererComponent = new JLabel();
				rendererComponent.setText(selectedService.getProtocolName());
				return rendererComponent;
			}
		});
	}
	
	/**
	 * Gets the combo box model.
	 * @return the combo box model
	 */
	private DefaultComboBoxModel<ConnectorService> getComboBoxModel() {
		if (comboBoxModel==null) {
			Vector<ConnectorService> entries = new Vector<>();
			List<ConnectorService> services = ServiceFinder.findServices(ConnectorService.class);
			for (ConnectorService service : services) {
				entries.add(service);
			}
			comboBoxModel = new DefaultComboBoxModel<>(entries);
		}
		return comboBoxModel;
	}

}
