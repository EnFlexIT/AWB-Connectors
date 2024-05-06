package de.enflexit.connector.core.ui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import agentgui.core.application.Application;
import agentgui.core.application.ApplicationListener;
import de.enflexit.common.swing.JDialogSizeAndPostionController;
import de.enflexit.common.swing.JDialogSizeAndPostionController.JDialogPosition;
import de.enflexit.connector.core.manager.ConnectorManager;

/**
 * A dialog to configure connectors.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorManagerDialog extends JDialog implements ApplicationListener {
	
	private static final long serialVersionUID = 7253191266415274699L;
	
	private static final String PREF_CONNECTOR_DIALOG_X = "Connector-Dialog-X";
	private static final String PREF_CONNECTOR_DIALOG_Y = "Connector-Dialog-Y";
	private static final String PREF_CONNECTOR_DIALOG_WIDTH  = "Connector-Dialog-WIDTH";
	private static final String PREF_CONNECTOR_DIALOG_HEIGHT = "Connector-Dialog-HEIGHT";
	
	private ConnectorManagerMainPanel connectorMainPanel;
	
	private Timer sizePositionWaitTimer;
	
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
		
		this.loadAndApplyDialogSizeAndPosition();
		this.registerEscapeKeyStroke();
		this.addSizeAndPositionsListener();
		
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		Application.addApplicationListener(this);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				if (ConnectorManager.getInstance().isConfigChanged()==true
						) {
					String userMessage = "Your configuration has unsaved changes! Store before closing?";
					int userReply = JOptionPane.showConfirmDialog(ConnectorManagerDialog.this, userMessage, "Save changes?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					
					if (userReply==JOptionPane.YES_OPTION) {
						ConnectorManager.getInstance().saveConfigurationsToDefaultFile();
					}
				}
				
				ConnectorManagerDialog.this.setVisible(false);
				ConnectorManagerDialog.this.dispose();
			}
		});
		
		JDialogSizeAndPostionController.setJDialogPositionOnScreen(this, JDialogPosition.ParentCenter);
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
	
	/**
     * Registers the escape key stroke in order to close this dialog.
     */
    private void registerEscapeKeyStroke() {
    	final ActionListener listener = new ActionListener() {
            public final void actionPerformed(final ActionEvent e) {
            	ConnectorManagerDialog.this.setVisible(false);
            	ConnectorManagerDialog.this.dispose();
            }
        };
        final KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true);
        this.getRootPane().registerKeyboardAction(listener, keyStroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }
    
    /**
     * Adds the size and positions listener.
     */
    private void addSizeAndPositionsListener() {
    	this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				ConnectorManagerDialog.this.startSizePositionWaitTimer();
			}
			@Override
			public void componentMoved(ComponentEvent e) {
				ConnectorManagerDialog.this.startSizePositionWaitTimer();
			}
		});
    }
    /**
     * Starts (or restarts) the size position wait timer.
     */
    private void startSizePositionWaitTimer() {
    	if (this.getSizePositionWaitTimer().isRunning()==true) {
    		this.getSizePositionWaitTimer().restart();
		} else {
			this.getSizePositionWaitTimer().start();
		}
    }
    /**
     * Returns the size position wait timer.
     * @return the size position wait timer
     */
	private Timer getSizePositionWaitTimer() {
		if (sizePositionWaitTimer==null) {
			sizePositionWaitTimer = new Timer(500, new ActionListener() {
				/* (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				@Override
				public void actionPerformed(ActionEvent ae) {
					ConnectorManagerDialog.this.saveDialogSizeAndPosition();
				}
			});
			sizePositionWaitTimer.setRepeats(false);
		}
		return sizePositionWaitTimer;
	}
    
    /**
     * Save dialog size and position.
     */
    private void saveDialogSizeAndPosition() {
    
    	if (this.isVisible()==false) return;
    	
    	Point dialogPos = this.getLocationOnScreen();
    	BundleHelper.getEclipsePreferences().putInt(PREF_CONNECTOR_DIALOG_X, dialogPos.x);
    	BundleHelper.getEclipsePreferences().putInt(PREF_CONNECTOR_DIALOG_Y, dialogPos.y);
    	BundleHelper.getEclipsePreferences().putInt(PREF_CONNECTOR_DIALOG_WIDTH,  this.getWidth());
    	BundleHelper.getEclipsePreferences().putInt(PREF_CONNECTOR_DIALOG_HEIGHT, this.getHeight());
    	BundleHelper.saveEclipsePreferences();
    }
    /**
     * Load and apply dialog size and position.
     */
    private void loadAndApplyDialogSizeAndPosition() {

    	int width  = BundleHelper.getEclipsePreferences().getInt(PREF_CONNECTOR_DIALOG_WIDTH, 0);
    	int height = BundleHelper.getEclipsePreferences().getInt(PREF_CONNECTOR_DIALOG_HEIGHT, 0);
    	if (height==0 || width==0) {
    		this.setSize(900, 600);	
    	} else {
    		this.setSize(width, height);
    	}
    	
    	int posX = BundleHelper.getEclipsePreferences().getInt(PREF_CONNECTOR_DIALOG_X, 0);
    	int posY = BundleHelper.getEclipsePreferences().getInt(PREF_CONNECTOR_DIALOG_Y, 0);
    	if (posX==0 || posY==0) {
    		this.setLocationRelativeTo(null);
    	} else {
    		this.setLocation(posX, posY);
    	}
    }
	
	/* (non-Javadoc)
	 * @see java.awt.Window#dispose()
	 */
	@Override
	public void dispose() {
		Application.removeApplicationListener(this);
		this.getMainPanel().dispose();
		super.dispose();
	}

	/* (non-Javadoc)
	 * @see agentgui.core.application.ApplicationListener#onApplicationEvent(agentgui.core.application.ApplicationListener.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(ApplicationEvent awbEvent) {
		if (awbEvent.getApplicationEvent()==ApplicationEvent.PROJECT_CLOSED) {
			this.dispose();
		}
	}
}
