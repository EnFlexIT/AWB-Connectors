package de.enflexit.connector.opcua.ui;

import javax.swing.JPanel;

import de.enflexit.connector.opcua.OpcUaConnector;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;

public class OpcUaDataView extends JPanel {

	private static final long serialVersionUID = -9117876381830606802L;

	private OpcUaConnector opcUaConnector;
	private JSplitPane jSplitPane;
	private OpcUaBrowserWidget opcUaBrowserWidget;
	
	/**
	 * Instantiates a new opc ua data view.
	 *
	 * @param opcUaConnector the opc ua connector
	 */
	public OpcUaDataView(OpcUaConnector opcUaConnector) {
		this.opcUaConnector = opcUaConnector;
		initialize();
	}
	private void initialize() {
		this.setLayout(new BorderLayout(0, 0));
		this.add(getJSplitPane());
	}
	
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setLeftComponent(getOpcUaBrowserWidget());
		}
		return jSplitPane;
	}
	private OpcUaBrowserWidget getOpcUaBrowserWidget() {
		if (opcUaBrowserWidget == null) {
			opcUaBrowserWidget = new OpcUaBrowserWidget(this.opcUaConnector);
		}
		return opcUaBrowserWidget;
	}
}
