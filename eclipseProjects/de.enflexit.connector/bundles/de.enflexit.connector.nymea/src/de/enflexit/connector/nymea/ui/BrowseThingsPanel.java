package de.enflexit.connector.nymea.ui;

import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import de.enflexit.connector.nymea.dataModel.SampleRate;
import de.enflexit.connector.nymea.dataModel.State;
import de.enflexit.connector.nymea.dataModel.StateType;
import de.enflexit.connector.nymea.dataModel.Thing;

/**
 * This panels allows to browse the servers API information, as provided by nymea's JSONRPC.Introspect method. 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class BrowseThingsPanel extends JPanel implements ConnectorListener, ActionListener, TreeSelectionListener, TreeWillExpandListener {
	
	private static final long serialVersionUID = -6158151344814720644L;
	
	private static final String NODE_TEXT_EMPTY_TREE = "Start the connection to request the available things from the HEMS";

	private JToolBar thingsBrowserToolBar;
	private JButton toolbarButtonLoadThings;
	private JButton toolbarButtonPowerLogs;
	
	private JScrollPane thingsTreeScrollPane;
	private ObjectBrowserTree thingsTree;
	
	private NymeaConnector connector;
	
	private CompletableFuture<HashMap<String, String>> thingsClassesFuture;
	
	private Thing selectedThing;
	private ArrayList<String> skipChildNodesList;

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
			toolbarButtonLoadThings.setEnabled(false);
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
				@Override
				public void mouseClicked(MouseEvent me) {
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
	 * Reloads tree model.
	 */
	private void reloadTreeModel() {
		
		DefaultMutableTreeNode rootNode = null;
		
		List<?> thingsList = this.connector.getNymeaClient().getAvailableThings();
		if (thingsList!=null) {
			rootNode = new DefaultMutableTreeNode("Available things for the connected HEMS system");
			
			for (int i=0; i<thingsList.size(); i++) {
				Map<?,?> thingDetails = (Map<?, ?>) thingsList.get(i);
				
				Thing thing = new Thing();
				thing.setName((String) thingDetails.get("name"));
				thing.setId((String) thingDetails.get("id"));
				thing.setThingClassID((String) thingDetails.get("thingClassId"));

				DefaultMutableTreeNode thingNode = new DefaultMutableTreeNode(thing);
				ObjectBrowserTree.addMapContentChildNodes((Map<?, ?>) thingsList.get(i), thingNode, this.getSkipChildNodesList());
				
				if (thing.getThingClassID()!=null) {
					String thingClassID = thing.getThingClassID();
					this.addThingClassNameNode(thingNode, thingClassID);
				}
				
				if (thingDetails.get("states")!=null) {
					@SuppressWarnings("unchecked")
					ArrayList<Map<?,?>> states = (ArrayList<Map<?,?>>) thingDetails.get("states");
					for (Map<?,?> stateMap : states) {
						State state = new State();
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
	}
	
	/**
	 * Add a child node with the thing class name.
	 * @param thingNode the thing node
	 * @param thingClassID the thing class ID
	 */
	private void addThingClassNameNode(DefaultMutableTreeNode thingNode, String thingClassID) {
		
		// --- Add a temporary node while requesting the things classes from the server
		DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode("thingClassName: Pending...");
		thingNode.add(tempNode);
		
		// --- Request the thing classes, replace the temporary node when done
		Thread waitingThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					HashMap<String, String> thingsClassesHashMap = BrowseThingsPanel.this.getThingsClassesFuture().get();
					String thingClassName = thingsClassesHashMap.get(thingClassID);
					
					if (thingClassName!=null) {
						thingNode.remove(tempNode);
						thingNode.add(new DefaultMutableTreeNode("thingClassName: " + thingClassName));
					}
					
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		waitingThread.start();
	}
	
	@Override
	public void onConnectorEvent(ConnectorEvent connectorEvent) {
		if (connectorEvent.getSource()==this.connector) {
			if (connectorEvent.getEvent()==Event.CONNECTED) {
				this.getToolbarButtonLoadThings().setEnabled(true);
				
			} else if (connectorEvent.getEvent()==Event.DISCONNECTED) {
				this.getThingsTree().setModel(new DefaultTreeModel(new DefaultMutableTreeNode(NODE_TEXT_EMPTY_TREE)));
			}
		}
	}
	
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
			System.out.println("[" + this.getClass().getSimpleName() + "]Obtaining things classes failed!");
		}
		return thingsClassNames;
	}
	
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
	
	public synchronized CompletableFuture<HashMap<String, StateType>> getStateTypesFuture(String thingClassID){
		CompletableFuture<HashMap<String, StateType>> stateTypesFuture = new CompletableFuture<HashMap<String,StateType>>();
		Runnable fetchTask = new Runnable() {
			
			@Override
			public void run() {
				HashMap<String, StateType> stateTypes = BrowseThingsPanel.this.loadStateTypes(thingClassID);
				stateTypesFuture.complete(stateTypes);
			}
		};
		
		Executors.newSingleThreadExecutor().submit(fetchTask);
		return stateTypesFuture;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==this.getToolbarButtonLoadThings()) {
			this.reloadTreeModel();
		} else if (ae.getSource()==this.getToolbarButtonPowerLogs()) {
			if (this.selectedThing!=null) {
				String thingID = selectedThing.getId();
				SampleRate sampleRate = SampleRate.SAMPLE_RATE_15_MINS;
				long timeTo = Instant.now().toEpochMilli();
				long timeFrom = Instant.now().minus(Duration.ofDays(7)).toEpochMilli();
				
				this.connector.getNymeaClient().getThingPowerLogs(thingID, timeFrom, timeTo, sampleRate);
			}
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent tse) {
		TreePath selectionPath = tse.getPath();
		if (selectionPath.getPathCount()>1) {
			DefaultMutableTreeNode thingNode = (DefaultMutableTreeNode) selectionPath.getPathComponent(1);
			Thing thing = (Thing) thingNode.getUserObject();
			this.setSelectedThing(thing);
		}
		System.out.println("TreeSelectionEvent - Selected path: " + selectionPath);
	}

	private void setSelectedThing(Thing selectedThing) {
		this.selectedThing = selectedThing;
		this.getToolbarButtonPowerLogs().setEnabled(selectedThing!=null);
	}

	private ArrayList<String> getSkipChildNodesList() {
		if (skipChildNodesList==null) {
			skipChildNodesList = new ArrayList<String>();
			skipChildNodesList.add("states");
		}
		return skipChildNodesList;
	}

	@Override
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
		DefaultMutableTreeNode nodeToExpand = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
		if (nodeToExpand.getUserObject() instanceof Thing) {
			Thing thing = (Thing) nodeToExpand.getUserObject();
			HashMap<String, StateType> stateTypes = this.loadStateTypes(thing.getThingClassID());
			
			DefaultMutableTreeNode stateVarsNode = new DefaultMutableTreeNode("State variables");
			
			for (State state : thing.getStatesList()) {
				state.setDisplayName(stateTypes.get(state.getStateTypeID()).getDisplayName());
				stateVarsNode.add(new DefaultMutableTreeNode(state));
			}
			
			nodeToExpand.add(stateVarsNode);
		}
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
		// --- Not required ----------------------------
	}
	
}
