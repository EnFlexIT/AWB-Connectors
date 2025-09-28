package de.enflexit.connector.nymea.ui;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JToolTip;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import de.enflexit.connector.core.ConnectorEvent;
import de.enflexit.connector.core.ConnectorEvent.Event;
import de.enflexit.connector.core.ConnectorListener;
import de.enflexit.connector.nymea.NymeaConnector;
import de.enflexit.connector.nymea.dataModel.PowerLogEntry;
import de.enflexit.connector.nymea.dataModel.SampleRate;
import de.enflexit.connector.nymea.dataModel.StateVariable;
import de.enflexit.connector.nymea.dataModel.StateType;
import de.enflexit.connector.nymea.dataModel.Thing;

/**
 * This panels allows to browse the servers API information, as provided by nymea's JSONRPC.Introspect method. 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class BrowseThingsPanel extends JPanel implements ConnectorListener, ActionListener, TreeSelectionListener, TreeWillExpandListener {
	
	private static final long serialVersionUID = -6158151344814720644L;
	
	private static final String NODE_TEXT_EMPTY_TREE = "Refresh to load the available things from the HEMS";

	private JToolBar thingsBrowserToolBar;
	private JButton toolbarButtonLoadThings;
	private JButton toolbarButtonPowerLogs;
	
	private JScrollPane thingsTreeScrollPane;
	private ObjectBrowserTree thingsTree;
	
	private NymeaConnector connector;
	
	private CompletableFuture<HashMap<String, String>> thingsClassesFuture;
	
	private Thing selectedThing;
	private ArrayList<String> skipChildNodesList;
	
	private HashMap<String, String> thingsClassNames;

	/**
	 * Added for window builder compatibility only. Use the other constructor for actual instantiation.
	 */
	@Deprecated
	public BrowseThingsPanel() {}
	
	/**
	 * Instantiates a new introspection panel.
	 * @param connector the nymea connector to perform the introspection on.
	 */
	public BrowseThingsPanel(NymeaConnector connector) {
		this.connector = connector;
		this.connector.addConnectorListener(this);
		initialize();
	}
	private void initialize() {
		this.setLayout(new BorderLayout(0, 0));
		this.add(this.getThingsBrowserToolBar(), BorderLayout.NORTH);
		this.add(getThingsTreeScrollPane(), BorderLayout.CENTER);
	}
	
	private JToolBar getThingsBrowserToolBar() {
		if (thingsBrowserToolBar==null) {
			thingsBrowserToolBar = new JToolBar();
			thingsBrowserToolBar.setFloatable(false);
			thingsBrowserToolBar.add(this.getToolbarButtonLoadThings());
			thingsBrowserToolBar.add(this.getToolbarButtonPowerLogs());
		}
		return thingsBrowserToolBar;
	}
	
	private JButton getToolbarButtonLoadThings() {
		if (toolbarButtonLoadThings==null) {
			toolbarButtonLoadThings = new JButton(new ImageIcon(this.getClass().getResource("/icons/Refresh.png")));
			toolbarButtonLoadThings.setToolTipText("Get the available \"things\" from the HEMS system.");
			toolbarButtonLoadThings.addActionListener(this);
//			toolbarButtonLoadThings.setEnabled(false);
		}
		return toolbarButtonLoadThings;
	}
	
	private JButton getToolbarButtonPowerLogs() {
		if (toolbarButtonPowerLogs==null) {
			toolbarButtonPowerLogs = new JButton(new ImageIcon(this.getClass().getResource("/icons/CheckChart.png")));
			toolbarButtonPowerLogs.setToolTipText("Get the bower balance logs for the selected thing.");
			toolbarButtonPowerLogs.addActionListener(this);
			toolbarButtonPowerLogs.setEnabled(false);
		}
		return toolbarButtonPowerLogs;
	}
	
	private JScrollPane getThingsTreeScrollPane() {
		if (thingsTreeScrollPane == null) {
			thingsTreeScrollPane = new JScrollPane();
			thingsTreeScrollPane.setViewportView(getThingsTree());
		}
		return thingsTreeScrollPane;
	}
	private ObjectBrowserTree getThingsTree() {
		if (thingsTree == null) {
			thingsTree = new ObjectBrowserTree();
			thingsTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(NODE_TEXT_EMPTY_TREE)));
			thingsTree.setToolTipText("Right click on a node to copy its value to the clipboard");
			thingsTree.addMouseListener(new MouseAdapter() {
				
				/* (non-Javadoc)
				 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
				 */
				@Override
				public void mouseClicked(MouseEvent me) {
					
					// --- Copy the node value on right click -------
					if (SwingUtilities.isRightMouseButton(me)) {
						DefaultMutableTreeNode clickedNode = BrowseThingsPanel.this.getClickedNode(me.getX(), me.getY());
						if (clickedNode!=null && clickedNode.isLeaf()==true) {
							String nodeContent = (String) clickedNode.getUserObject();
							String[] parts = nodeContent.split(": ");
							if (parts.length==2) {
								String nodeValue = parts[1];
								StringSelection stringSelection = new StringSelection(nodeValue);
								Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
								clipboard.setContents(stringSelection, null);
								BrowseThingsPanel.this.showSuccessMessage(nodeValue, me.getXOnScreen(), me.getYOnScreen());
							}
							
						} else {
							System.err.println("Couldn't identify node!");
						}
					}
				}
			});
			
			thingsTree.addTreeSelectionListener(this);
			thingsTree.addTreeWillExpandListener(this);
		}
		return thingsTree;
	}
	
	/**
	 * Reloads the tree model.
	 */
	private void reloadTreeModel() {
		
		DefaultMutableTreeNode rootNode = null;
		
		// --- Request the list of things from the HEMS system.
		List<?> thingsList = this.connector.getNymeaClient().getAvailableThings();
		if (thingsList!=null) {
			rootNode = new DefaultMutableTreeNode("Available \"things\" for the connected HEMS system");
			
			for (int i=0; i<thingsList.size(); i++) {
				Map<?,?> thingDetails = (Map<?, ?>) thingsList.get(i);
				
				// --- Create an object containing the relevant information
				Thing thing = new Thing();
				thing.setName((String) thingDetails.get("name"));
				thing.setId((String) thingDetails.get("id"));
				thing.setThingClassID((String) thingDetails.get("thingClassId"));

				// --- Create a tree node for the thing -----------------------
				DefaultMutableTreeNode thingNode = new DefaultMutableTreeNode(thing);
				// --- Automatically create child nodes for the thing properties, except those excluded by the skip list 
				ObjectBrowserTree.addMapContentChildNodes((Map<?, ?>) thingsList.get(i), thingNode, this.getSkipChildNodesList());
				
				String thingsClassName = this.getThingsClassNames().get(thing.getThingClassID());
				if (thingsClassName==null) {
					thingsClassName = "Pending...";
				}
				thingNode.add(new DefaultMutableTreeNode("thingClassName: " + thingsClassName));
				
				// --- Handle the thing's "states" (actually state-describing variables)
				if (thingDetails.get("states")!=null) {
					@SuppressWarnings("unchecked")
					ArrayList<Map<?,?>> states = (ArrayList<Map<?,?>>) thingDetails.get("states");
					for (Map<?,?> stateMap : states) {
						StateVariable state = new StateVariable();
						state.setStateTypeID((String) stateMap.get("stateTypeId"));
						state.setValue(stateMap.get("value"));
						thing.getStatesList().add(state);
					}
				}
				
				rootNode.add(thingNode);
			}
			
			this.getThingsTree().setModel(new DefaultTreeModel(rootNode));
		} else {
			JOptionPane.showMessageDialog(this, "Requesting things from the HEMS failed! Please check your conenciton settings!", "Unable to get things details!", JOptionPane.ERROR_MESSAGE);
		}
		
		this.getThingsTree().setModel(new DefaultTreeModel(rootNode));
		this.getThingsTree().repaint();
		
		if (this.getThingsClassNames().size()==0) {
			this.getThingsClassNamesFromHEMS();
		}
	}
	
	/**
	 * Gets the things class names from the HEMS system. Adds them to the tree when done.
	 */
	private void getThingsClassNamesFromHEMS() {
		// --- Request the thing classes, replace in the temporary nodes when done
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					BrowseThingsPanel.this.thingsClassNames = BrowseThingsPanel.this.getThingsClassesFuture().get();
					BrowseThingsPanel.this.addThingsClassNames();
					
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
	}
	
	private void addThingsClassNames() {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) this.getThingsTree().getModel().getRoot();
		for (int i=0; i<rootNode.getChildCount(); i++) {
			DefaultMutableTreeNode thingNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
			Thing thing = (Thing) thingNode.getUserObject();
			DefaultMutableTreeNode temporaryNode = (DefaultMutableTreeNode) thingNode.getLastChild();
			if (temporaryNode.getUserObject().equals("thingClassName: Pending...")) {
				temporaryNode.setUserObject("thingClassName: " + this.getThingsClassNames().get(thing.getThingClassID()));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.connector.core.ConnectorListener#onConnectorEvent(de.enflexit.connector.core.ConnectorEvent)
	 */
	@Override
	public void onConnectorEvent(ConnectorEvent connectorEvent) {
		if (connectorEvent.getSource()==this.connector) {
			if (connectorEvent.getEvent()==Event.CONNECTED) {
//				this.reloadTreeModel();
				this.getToolbarButtonLoadThings().setEnabled(true);
			} else if (connectorEvent.getEvent()==Event.DISCONNECTED) {
				this.getThingsTree().setModel(new DefaultTreeModel(new DefaultMutableTreeNode(NODE_TEXT_EMPTY_TREE)));
			}
		}
	}
	
	/**
	 * Loads the things classes from the HEMS, returns a HashMap mapping class IDs to display names..
	 * @return the hash map
	 */
	private HashMap<String, String> loadThingsClasses() {
		HashMap<String, String> thingsClassNames = new HashMap<>();
		List<?> thingsClasses = this.connector.getNymeaClient().getThingClasses();
		if (thingsClasses!=null) {
			System.out.println("Received " + thingsClasses.size() + " things classes");
			for (Object thingClass : thingsClasses) {
				Map<?,?> thingClassMap = (Map<?, ?>) thingClass;
				String id = (String) thingClassMap.get("id");
				String name = (String) thingClassMap.get("displayName");
				thingsClassNames.put(id, name);
			}
		} else {
			System.out.println("[" + this.getClass().getSimpleName() + "] Obtaining things classes failed!");
		}
		return thingsClassNames;
	}
	
	/**
	 * Loads the StateType instances for the specified ThingClass from the HEMS system.
	 * @param thingClassID the thing class ID
	 * @return a mapping from the stateTypeIDs to the corresponding StateType objects. 
	 */
	private HashMap<String, StateType> loadStateTypes(String thingClassID){
		HashMap<String, StateType> stateTypes = new HashMap<String, StateType>();
		List<StateType> stateTypesList = this.connector.getNymeaClient().getStateTypes(thingClassID);
		if (stateTypesList!=null) {
			for (StateType stateType : stateTypesList) {
				stateTypes.put(stateType.getId(), stateType);
			}
		} else {
			System.out.println("[" + this.getClass().getSimpleName() + "] Obtaining state types failed!");
		}
		return stateTypes;
	}
	
	/**
	 * Gets the tree node for a clicked position. May be null if there is no node at that position.
	 * @param clickX the click X
	 * @param clickY the click Y
	 * @return the clicked node
	 */
	private DefaultMutableTreeNode getClickedNode(int clickX, int clickY) {
        TreePath path = this.getThingsTree().getPathForLocation(clickX, clickY);
        if (path!=null) {
        	Object node = path.getLastPathComponent();
        	if (node instanceof DefaultMutableTreeNode) {
        		return (DefaultMutableTreeNode) node;
        	}
        }
        return null;
	}
	
	/**
	 * Shows a message to indicate something was copied.
	 * @param copiedText the copied text
	 * @param posX the pos X
	 * @param posY the pos Y
	 */
	private void showSuccessMessage(String copiedText, int posX, int posY) {
		
		JToolTip toolTip = this.getThingsTree().createToolTip();
		toolTip.setTipText("Copied " + copiedText + " to the clipboard.");
		
		Popup popup = PopupFactory.getSharedInstance().getPopup(this.getThingsTree(), toolTip, posX, posY);
		popup.show();
	    // create a timer to hide the popup later
	    Timer timer = new Timer(1500, new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	popup.hide();

	        }
	    });
	    timer.setRepeats(false);
	    timer.start();
	}
	
	/**
	 * This {@link CompletableFuture} requests the things classes from the HEMS, and returns the result 
	 * @return the things classes future
	 */
	public synchronized CompletableFuture<HashMap<String, String>> getThingsClassesFuture() {
		if (thingsClassesFuture==null) {
			thingsClassesFuture = new CompletableFuture<HashMap<String,String>>();
			
			Runnable fetchTask = new Runnable() {
				
				@Override
				public void run() {
					HashMap<String, String> thingsClasses = BrowseThingsPanel.this.loadThingsClasses();
					thingsClassesFuture.complete(thingsClasses);
				}
			};

			Executors.newSingleThreadExecutor().submit(fetchTask);
		}
		return thingsClassesFuture;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==this.getToolbarButtonLoadThings()) {
			this.reloadTreeModel();
		} else if (ae.getSource()==this.getToolbarButtonPowerLogs()) {
			if (this.selectedThing!=null) {
				SampleRate sampleRate = SampleRate.SAMPLE_RATE_15_MINS;
				long timeTo = Instant.now().toEpochMilli();
				long timeFrom = Instant.now().minus(Duration.ofDays(7)).toEpochMilli();
				
				ArrayList<PowerLogEntry> logEntries = this.connector.getNymeaClient().getThingPowerLogs(selectedThing.getId(), timeFrom, timeTo, sampleRate);
				this.showPowerLogs(this.connector, selectedThing, logEntries);
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	@Override
	public void valueChanged(TreeSelectionEvent tse) {
		TreePath selectionPath = tse.getPath();
		if (selectionPath.getPathCount()>1) {
			DefaultMutableTreeNode thingNode = (DefaultMutableTreeNode) selectionPath.getPathComponent(1);
			Thing thing = (Thing) thingNode.getUserObject();
			this.setSelectedThing(thing);
		} else {
			this.setSelectedThing(null);
		}
	}

	/**
	 * Sets the currently selected thing.
	 * @param selectedThing the new selected thing
	 */
	private void setSelectedThing(Thing selectedThing) {
		this.selectedThing = selectedThing;
		this.getToolbarButtonPowerLogs().setEnabled(selectedThing!=null);
	}

	/**
	 * Specifies a list of response parameters that are excluded from the automatic tree generation.
	 * @return the skip child nodes list
	 */
	private ArrayList<String> getSkipChildNodesList() {
		if (skipChildNodesList==null) {
			skipChildNodesList = new ArrayList<String>();
			skipChildNodesList.add("states");
		}
		return skipChildNodesList;
	}
	
	/**
	 * Gets the things class names.
	 * @return the things class names
	 */
	private HashMap<String, String> getThingsClassNames() {
		if (thingsClassNames==null) {
			thingsClassNames = new HashMap<String, String>();
		}
		return thingsClassNames;
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TreeWillExpandListener#treeWillExpand(javax.swing.event.TreeExpansionEvent)
	 */
	@Override
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
		
		// --- Do some pre-processing when handling a thing node
		DefaultMutableTreeNode nodeToExpand = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
		if (nodeToExpand.getUserObject() instanceof Thing) {
			
			// --- Request the corresponding StateType objects, which contain descriptive information for the states (actually system variables)
			Thing thing = (Thing) nodeToExpand.getUserObject();
			HashMap<String, StateType> stateTypes = this.loadStateTypes(thing.getThingClassID());
			
			DefaultMutableTreeNode stateVarsNode = new DefaultMutableTreeNode("Current State");

			// --- Add child nodes for the States, enriched with information from the StateTypes
			for (StateVariable stateVariable : thing.getStatesList()) {
				StateType stateType =stateTypes.get(stateVariable.getStateTypeID());
				stateVariable.setDisplayName(stateType.getDisplayName());
				stateVariable.setUnit(stateType.getUnit());
				
				stateVarsNode.add(new DefaultMutableTreeNode(stateVariable));
			}
			
			nodeToExpand.add(stateVarsNode);
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TreeWillExpandListener#treeWillCollapse(javax.swing.event.TreeExpansionEvent)
	 */
	@Override
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
		// --- Not required ----------------------------
	}
	
	/**
	 * Opens a dialog to view the power logs for the selected thing
	 * @param connector the connector
	 * @param thingID the thing ID
	 * @param logEntries the log entries
	 */
	private void showPowerLogs(NymeaConnector connector, Thing thing, ArrayList<PowerLogEntry> logEntries) {
		PowerLogsPanel powerLogsPanel = new PowerLogsPanel(connector, thing, logEntries);
		JDialog powerLogsDialog = new JDialog(SwingUtilities.getWindowAncestor(this));
		powerLogsDialog.setContentPane(powerLogsPanel);
		powerLogsDialog.setTitle("Power logs for " + thing.getName());
		powerLogsDialog.setSize(750, 480);
		powerLogsDialog.setVisible(true);
	}
	
}
