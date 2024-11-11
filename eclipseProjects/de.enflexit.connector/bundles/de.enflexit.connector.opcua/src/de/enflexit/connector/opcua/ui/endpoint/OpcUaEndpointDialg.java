package de.enflexit.connector.opcua.ui.endpoint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import de.enflexit.connector.opcua.BundleHelper;
import de.enflexit.connector.opcua.OpcUaConnector;
import de.enflexit.connector.opcua.ui.OpcUaConnectorToolbar;

/**
 * The Class OpcUaEndpointDialg.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaEndpointDialg extends JDialog implements ActionListener {

	private static final long serialVersionUID = 5469489269113194690L;

	private Dimension BUTTON_SIZE = new Dimension(100, 26);
	
	private OpcUaConnector opcUaConnector;
	
	private JPanel jPanelContent;
	
	private JTabbedPane jTabbedPaneSettings;
	
	private JScrollPane jScrollPaneEndpoint;
	private JPanel jPanelWrapperForEndpoint;
	private OpcUaEndpointPanel opcUaEndpointPanel;
	
	private JScrollPane jScrollPaneSubscription;
	private JPanel jPanelWrapperForSubscription;
	private OpcUaSubscriptionPanel opcUaSubscriptionPanel;
	
	
	private JPanel jPanelButtons;
	private JButton jButtonCancel;
	private JButton jButtonApply;
	
	/**
	 * Instantiates a new OpcUaEndpointDialg.
	 * @param owner the owner
	 */
	public OpcUaEndpointDialg(Window owner, OpcUaConnector opcUaConnector) {
		super(owner);
		this.opcUaConnector = opcUaConnector;
		this.initialize();
	}
	/**
	 * Initialize.
	 */
	private void initialize() {
		
		this.setTitle( "OPC-UA Connection Properties" );
		this.setIconImage(BundleHelper.getImage(OpcUaConnectorToolbar.IMAGE_FILE_NAME_CONNECTOR));
		this.setModal(true);
		
		this.setContentPane(this.getJPanelContent());
		this.setLocationRelativeTo(null);
		
		this.registerEscapeKeyStroke();
		this.setSize(520, 530);
		this.setLocationRelativeTo(null);
	}
	
	/**
     * Registers the escape key stroke in order to close this dialog.
     */
    private void registerEscapeKeyStroke() {
    	final ActionListener listener = new ActionListener() {
            public final void actionPerformed(final ActionEvent e) {
    			setVisible(false);
            }
        };
        final KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true);
        this.getRootPane().registerKeyboardAction(listener, keyStroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }
 
    public JPanel getJPanelContent() {
    	if (jPanelContent==null) {
    		jPanelContent = new JPanel();
    		
    		GridBagLayout gridBagLayout = new GridBagLayout();
    		gridBagLayout.columnWidths = new int[]{0, 0};
    		gridBagLayout.rowHeights = new int[]{0, 0, 0};
    		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
    		gridBagLayout.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
    		jPanelContent.setLayout(gridBagLayout);
    		
    		GridBagConstraints gbc_opcuaEndpointPanel = new GridBagConstraints();
    		gbc_opcuaEndpointPanel.fill = GridBagConstraints.BOTH;
    		gbc_opcuaEndpointPanel.insets = new Insets(10, 10, 0, 10);
    		gbc_opcuaEndpointPanel.gridx = 0;
    		gbc_opcuaEndpointPanel.gridy = 0;
    		jPanelContent.add(this.getJTabbedPaneSettings(), gbc_opcuaEndpointPanel);
    		
    		GridBagConstraints gbc_jPanelButtons = new GridBagConstraints();
    		gbc_jPanelButtons.insets = new Insets(10, 10, 10, 10);
    		gbc_jPanelButtons.fill = GridBagConstraints.VERTICAL;
    		gbc_jPanelButtons.gridx = 0;
    		gbc_jPanelButtons.gridy = 1;
    		jPanelContent.add(this.getJPanelButtons(), gbc_jPanelButtons);
    		
    	}
		return jPanelContent;
	}
    
    
    private JTabbedPane getJTabbedPaneSettings() {
    	if (jTabbedPaneSettings==null) {
    		jTabbedPaneSettings = new JTabbedPane();
    		jTabbedPaneSettings.addTab(" Connection Settings ",   this.getJScrollPaneEndpoint());
    		jTabbedPaneSettings.addTab(" Subscription Settings ", this.getJScrollPaneSubscription());
    	}
		return jTabbedPaneSettings;
	}

    private JScrollPane getJScrollPaneEndpoint() {
    	if (jScrollPaneEndpoint==null) {
    		jScrollPaneEndpoint = new JScrollPane();
    		jScrollPaneEndpoint.setViewportView(this.getJPanelWrapperForEndpoint());
    		jScrollPaneEndpoint.setBorder(BorderFactory.createEmptyBorder());
    	}
		return jScrollPaneEndpoint;
	}
    private JPanel getJPanelWrapperForEndpoint() {
    	if (jPanelWrapperForEndpoint==null) {
    		jPanelWrapperForEndpoint = new JPanel();
    		jPanelWrapperForEndpoint.setBorder(BorderFactory.createEmptyBorder());
    		
    		GridBagLayout gbl_jPanelWrapperForEndPointPanel = new GridBagLayout();
    		gbl_jPanelWrapperForEndPointPanel.columnWidths = new int[]{0, 0};
    		gbl_jPanelWrapperForEndPointPanel.rowHeights = new int[]{0, 0};
    		gbl_jPanelWrapperForEndPointPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
    		gbl_jPanelWrapperForEndPointPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
    		jPanelWrapperForEndpoint.setLayout(gbl_jPanelWrapperForEndPointPanel);
    		
    		GridBagConstraints gbc_opcUaEndpointPanel = new GridBagConstraints();
    		gbc_opcUaEndpointPanel.fill = GridBagConstraints.BOTH;
    		gbc_opcUaEndpointPanel.insets = new Insets(10, 10, 10, 10);
    		gbc_opcUaEndpointPanel.gridx = 0;
    		gbc_opcUaEndpointPanel.gridy = 0;
    		jPanelWrapperForEndpoint.add(this.getOpcUaEndpointPanel(), gbc_opcUaEndpointPanel);
    	}
    	return jPanelWrapperForEndpoint;
    }
    private OpcUaEndpointPanel getOpcUaEndpointPanel() {
    	if (opcUaEndpointPanel==null) {
    		opcUaEndpointPanel = new OpcUaEndpointPanel(this.opcUaConnector);
    	}
    	return opcUaEndpointPanel;
    }
    
    private JScrollPane getJScrollPaneSubscription() {
    	if (jScrollPaneSubscription==null) {
    		jScrollPaneSubscription = new JScrollPane();
    		jScrollPaneSubscription.setBorder(BorderFactory.createEmptyBorder());
    		jScrollPaneSubscription.setViewportView(this.getJPanelWrapperForSubscription());
    	}
		return jScrollPaneSubscription;
	}
    private JPanel getJPanelWrapperForSubscription() {
    	if (jPanelWrapperForSubscription==null) {
    		jPanelWrapperForSubscription = new JPanel();
    		jPanelWrapperForSubscription.setBorder(BorderFactory.createEmptyBorder());
    		
    		GridBagLayout gbl_jPanelWrapperForEndPointPanel = new GridBagLayout();
    		gbl_jPanelWrapperForEndPointPanel.columnWidths = new int[]{0, 0};
    		gbl_jPanelWrapperForEndPointPanel.rowHeights = new int[]{0, 0};
    		gbl_jPanelWrapperForEndPointPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
    		gbl_jPanelWrapperForEndPointPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
    		jPanelWrapperForSubscription.setLayout(gbl_jPanelWrapperForEndPointPanel);
    		
    		GridBagConstraints gbc_opcUaEndpointPanel = new GridBagConstraints();
    		gbc_opcUaEndpointPanel.fill = GridBagConstraints.HORIZONTAL;
    		gbc_opcUaEndpointPanel.anchor = GridBagConstraints.NORTH;
    		gbc_opcUaEndpointPanel.insets = new Insets(10, 10, 10, 10);
    		gbc_opcUaEndpointPanel.gridx = 0;
    		gbc_opcUaEndpointPanel.gridy = 0;
    		jPanelWrapperForSubscription.add(this.getOpcUaSubscriptionPanel(), gbc_opcUaEndpointPanel);
    	}
    	return jPanelWrapperForSubscription;
    }    
    private OpcUaSubscriptionPanel getOpcUaSubscriptionPanel() {
    	if (opcUaSubscriptionPanel==null) {
    		opcUaSubscriptionPanel = new OpcUaSubscriptionPanel(this.opcUaConnector);
    	}
    	return opcUaSubscriptionPanel;
    }
    
    
	private JPanel getJPanelButtons() {
		if (jPanelButtons == null) {
			jPanelButtons = new JPanel();
			GridBagLayout gbl_jPanelButtons = new GridBagLayout();
			gbl_jPanelButtons.columnWidths = new int[]{0, 0, 0};
			gbl_jPanelButtons.rowHeights = new int[]{0, 0};
			gbl_jPanelButtons.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			gbl_jPanelButtons.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			jPanelButtons.setLayout(gbl_jPanelButtons);
			GridBagConstraints gbc_jButtonApply = new GridBagConstraints();
			gbc_jButtonApply.insets = new Insets(10, 0, 10, 30);
			gbc_jButtonApply.gridx = 0;
			gbc_jButtonApply.gridy = 0;
			jPanelButtons.add(getJButtonApply(), gbc_jButtonApply);
			GridBagConstraints gbc_jButtonCancel = new GridBagConstraints();
			gbc_jButtonCancel.insets = new Insets(0, 30, 0, 0);
			gbc_jButtonCancel.gridx = 1;
			gbc_jButtonCancel.gridy = 0;
			jPanelButtons.add(getJButtonCancel(), gbc_jButtonCancel);
		}
		return jPanelButtons;
	}
	private JButton getJButtonApply() {
		if (jButtonApply == null) {
			jButtonApply = new JButton("Apply");
			jButtonApply.setFont(new Font("Dialog", Font.BOLD, 12));
			jButtonApply.setForeground(new Color(0, 152, 0));
			jButtonApply.setPreferredSize(BUTTON_SIZE);
			jButtonApply.setEnabled(this.opcUaConnector.isConnected()==false);
			jButtonApply.addActionListener(this);
		}
		return jButtonApply;
	}
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton("Cancel");
			jButtonCancel.setFont(new Font("Dialog", Font.BOLD, 12));
			jButtonCancel.setForeground(new Color(152, 0, 0));
			jButtonCancel.setPreferredSize(BUTTON_SIZE);
			jButtonCancel.addActionListener(this);
		}
		return jButtonCancel;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		if (ae.getSource()==this.getJButtonApply()) {
			// --- Apply edited connector settings ------------------
			
			// --- Try to find configuration errors -----------------
			String errMsg = this.getOpcUaEndpointPanel().getConfigurationError();
			if (errMsg==null) {
				errMsg = this.getOpcUaSubscriptionPanel().getConfigurationError();
			}
			if (errMsg!=null) {
				JOptionPane.showMessageDialog(this, errMsg, "Configuration Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (this.getOpcUaEndpointPanel().setPanelToProperties()==true && this.getOpcUaSubscriptionPanel().setPanelToProperties()==true) {
				this.setVisible(false);
				this.dispose();
			}
			
		} else if (ae.getSource()==this.getJButtonCancel()) {
			// --- Cancel editing -----------------------------------
			this.setVisible(false);
			this.dispose();
			
		}
		
	}
}
