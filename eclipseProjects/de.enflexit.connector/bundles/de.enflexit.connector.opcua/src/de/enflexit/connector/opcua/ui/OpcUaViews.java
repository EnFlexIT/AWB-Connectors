package de.enflexit.connector.opcua.ui;

import javax.swing.JPanel;

import de.enflexit.connector.opcua.OpcUaConnector;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;

/**
 * The Class OpcUaViews.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaViews extends JPanel {

	private static final long serialVersionUID = -9117876381830606802L;

	private OpcUaConnector opcUaConnector;

	private JSplitPane jSplitPaneMain;
	private JTabbedPane jTabbedPaneOpcUaDetails;
	
	private JSplitPane jSplitPaneLeft;	
	
	private JPanel jPanelLeftTop;
	private OpcUaConnectorToolbar opcUaConnectorToolbar;
	private OpcUaBrowserWidget opcUaBrowserWidget;
	
	private OpcUaAttributeWidget opcUaAttributeWidget;
	private OpcUaDataView opcUaDataView;
	
	/**
	 * Instantiates a new opc ua data view.
	 * @param opcUaConnector the opc ua connector
	 */
	public OpcUaViews(OpcUaConnector opcUaConnector) {
		this.opcUaConnector = opcUaConnector;
		initialize();
	}
	private void initialize() {
		this.setLayout(new BorderLayout(0, 0));
		this.add(getJSplitPaneMain());
	}
	
	private JSplitPane getJSplitPaneMain() {
		if (jSplitPaneMain == null) {
			jSplitPaneMain = new JSplitPane();
			jSplitPaneMain.setDividerLocation(300);
			jSplitPaneMain.setDividerSize(5);
			jSplitPaneMain.setResizeWeight(0);
			jSplitPaneMain.setLeftComponent(this.getJSplitPaneLeft());
			jSplitPaneMain.setRightComponent(this.getJTabbedPaneOpcUaDetails());
		}
		return jSplitPaneMain;
	}
	
	private JSplitPane getJSplitPaneLeft() {
		if (jSplitPaneLeft == null) {
			jSplitPaneLeft = new JSplitPane();
			jSplitPaneLeft.setOrientation(JSplitPane.VERTICAL_SPLIT);
			jSplitPaneLeft.setDividerLocation(500);
			jSplitPaneLeft.setDividerSize(5);
			jSplitPaneLeft.setResizeWeight(1.0);
			jSplitPaneLeft.setTopComponent(this.getjPanelLeftTop());
			jSplitPaneLeft.setBottomComponent(this.getOpcUaAttributeWidget());
		}
		return jSplitPaneLeft;
	}

	private JPanel getjPanelLeftTop() {
		if (jPanelLeftTop==null) {
			jPanelLeftTop = new JPanel();
			jPanelLeftTop.setLayout(new BorderLayout());
			jPanelLeftTop.add(this.getOpcUaConnectorToolbar(), BorderLayout.NORTH);
			jPanelLeftTop.add(this.getOpcUaBrowserWidget(), BorderLayout.CENTER);
		}
		return jPanelLeftTop;
	}
	private OpcUaConnectorToolbar getOpcUaConnectorToolbar() {
		if (opcUaConnectorToolbar==null) {
			opcUaConnectorToolbar = new OpcUaConnectorToolbar(this.opcUaConnector);
		}
		return opcUaConnectorToolbar;
	}
	private OpcUaBrowserWidget getOpcUaBrowserWidget() {
		if (opcUaBrowserWidget == null) {
			opcUaBrowserWidget = new OpcUaBrowserWidget(this.opcUaConnector);
		}
		return opcUaBrowserWidget;
	}
	private OpcUaAttributeWidget getOpcUaAttributeWidget() {
		if (opcUaAttributeWidget==null) {
			opcUaAttributeWidget = new OpcUaAttributeWidget(this.opcUaConnector);
		}
		return opcUaAttributeWidget;
	}
	
	
	private JTabbedPane getJTabbedPaneOpcUaDetails() {
		if (jTabbedPaneOpcUaDetails==null) {
			jTabbedPaneOpcUaDetails = new JTabbedPane();
			jTabbedPaneOpcUaDetails.addTab(" Data View ", this.getOpcUaDataView());
		}
		return jTabbedPaneOpcUaDetails;
	}
	private OpcUaDataView getOpcUaDataView() {
		if (opcUaDataView==null) {
			opcUaDataView = new OpcUaDataView(this.opcUaConnector);
		}
		return opcUaDataView;
	}
	
}
