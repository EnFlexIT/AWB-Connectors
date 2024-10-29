package de.enflexit.connector.opcua.ui.endpoint;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;

import de.enflexit.common.properties.Properties;
import de.enflexit.connector.opcua.OpcUaConnector;

/**
 * The Class OpcUaSubscriptionPanel.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaSubscriptionPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 3456689990982277842L;

	private OpcUaConnector opcUaConnector;
	
	private Dimension spinnerSize = new Dimension(100, 26);
	
	private JLabel jLabelSubscriptionHeader;
	private JLabel jLabelPublishingInterval;
	private JSpinner jSpinnerPublishingInterval;
	private JLabel jLabelLifetimeCount;
	private JSpinner jSpinnerLifetimeCount;
	private JLabel jLabelMaxKeepAliveCount;
	private JSpinner jSpinnerMaxKeepAliveCount;
	private JLabel jLabelMaxNotificationsPerPublish;
	private JSpinner jSpinnerMaxNotificationsPerPublish;
	private JLabel jLabelPriority;
	private JSpinner jSpinnerPriority;
	
	private JSeparator jSeparatorSubscription;
	private JLabel jLabelMonitoringHeader;
	private JLabel jLabelSamplingInterval;
	private JSpinner jSpinnerSamplingInterval;
	private JLabel jLabelQueueSize;
	private JSpinner jSpinnerQueueSize;
	private JLabel jLabelDiscardOldest;
	private JCheckBox jCheckboxDiscardOldest;
	private JLabel jLabelMonitoringMode;
	private JComboBox<MonitoringMode> jComboBoxMonitoringModel;

	
	public OpcUaSubscriptionPanel(OpcUaConnector opcUaConnector) {
		this.opcUaConnector = opcUaConnector;
		this.initialize();
		this.setPropertiesToPanel();
	}
	/**
	 * Initializes this panel.
	 */
	private void initialize() {
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		this.setLayout(gridBagLayout);
		
		GridBagConstraints gbc_jLabelSubscriptionHeader = new GridBagConstraints();
		gbc_jLabelSubscriptionHeader.insets = new Insets(0, 0, 5, 0);
		gbc_jLabelSubscriptionHeader.anchor = GridBagConstraints.WEST;
		gbc_jLabelSubscriptionHeader.gridx = 0;
		gbc_jLabelSubscriptionHeader.gridy = 0;
		add(getJLabelSubscriptionHeader(), gbc_jLabelSubscriptionHeader);
		GridBagConstraints gbc_jLabelPublishingInterval = new GridBagConstraints();
		gbc_jLabelPublishingInterval.anchor = GridBagConstraints.WEST;
		gbc_jLabelPublishingInterval.gridx = 0;
		gbc_jLabelPublishingInterval.gridy = 1;
		add(getJLabelPublishingInterval(), gbc_jLabelPublishingInterval);
		GridBagConstraints gbc_jSpinnerPublishingInterval = new GridBagConstraints();
		gbc_jSpinnerPublishingInterval.anchor = GridBagConstraints.WEST;
		gbc_jSpinnerPublishingInterval.insets = new Insets(0, 5, 0, 0);
		gbc_jSpinnerPublishingInterval.gridx = 1;
		gbc_jSpinnerPublishingInterval.gridy = 1;
		add(getJSpinnerPublishingInterval(), gbc_jSpinnerPublishingInterval);
		GridBagConstraints gbc_jLabelLifetimeCount = new GridBagConstraints();
		gbc_jLabelLifetimeCount.anchor = GridBagConstraints.WEST;
		gbc_jLabelLifetimeCount.gridx = 0;
		gbc_jLabelLifetimeCount.gridy = 2;
		add(getJLabelLifetimeCount(), gbc_jLabelLifetimeCount);
		GridBagConstraints gbc_jSpinnerLifetimeCount = new GridBagConstraints();
		gbc_jSpinnerLifetimeCount.anchor = GridBagConstraints.WEST;
		gbc_jSpinnerLifetimeCount.insets = new Insets(0, 5, 0, 0);
		gbc_jSpinnerLifetimeCount.gridx = 1;
		gbc_jSpinnerLifetimeCount.gridy = 2;
		add(getJSpinnerLifetimeCount(), gbc_jSpinnerLifetimeCount);
		GridBagConstraints gbc_jLabelMaxKeepAliveCount = new GridBagConstraints();
		gbc_jLabelMaxKeepAliveCount.anchor = GridBagConstraints.WEST;
		gbc_jLabelMaxKeepAliveCount.gridx = 0;
		gbc_jLabelMaxKeepAliveCount.gridy = 3;
		add(getJLabelMaxKeepAliveCount(), gbc_jLabelMaxKeepAliveCount);
		GridBagConstraints gbc_jSpinnerMaxKeepAliveCount = new GridBagConstraints();
		gbc_jSpinnerMaxKeepAliveCount.anchor = GridBagConstraints.WEST;
		gbc_jSpinnerMaxKeepAliveCount.insets = new Insets(0, 5, 0, 0);
		gbc_jSpinnerMaxKeepAliveCount.gridx = 1;
		gbc_jSpinnerMaxKeepAliveCount.gridy = 3;
		add(getJSpinnerMaxKeepAliveCount(), gbc_jSpinnerMaxKeepAliveCount);
		GridBagConstraints gbc_jLabelMaxNotificationsPerPublish = new GridBagConstraints();
		gbc_jLabelMaxNotificationsPerPublish.anchor = GridBagConstraints.WEST;
		gbc_jLabelMaxNotificationsPerPublish.gridx = 0;
		gbc_jLabelMaxNotificationsPerPublish.gridy = 4;
		add(getJLabelMaxNotificationsPerPublish(), gbc_jLabelMaxNotificationsPerPublish);
		GridBagConstraints gbc_jSpinnerMaxNotificationsPerPublish = new GridBagConstraints();
		gbc_jSpinnerMaxNotificationsPerPublish.insets = new Insets(0, 5, 0, 0);
		gbc_jSpinnerMaxNotificationsPerPublish.anchor = GridBagConstraints.WEST;
		gbc_jSpinnerMaxNotificationsPerPublish.gridx = 1;
		gbc_jSpinnerMaxNotificationsPerPublish.gridy = 4;
		add(getJSpinnerMaxNotificationsPerPublish(), gbc_jSpinnerMaxNotificationsPerPublish);
		GridBagConstraints gbc_jLabelPriority = new GridBagConstraints();
		gbc_jLabelPriority.anchor = GridBagConstraints.WEST;
		gbc_jLabelPriority.gridx = 0;
		gbc_jLabelPriority.gridy = 5;
		add(getJLabelPriority(), gbc_jLabelPriority);
		GridBagConstraints gbc_jSpinnerPriority = new GridBagConstraints();
		gbc_jSpinnerPriority.insets = new Insets(0, 5, 0, 0);
		gbc_jSpinnerPriority.anchor = GridBagConstraints.WEST;
		gbc_jSpinnerPriority.gridx = 1;
		gbc_jSpinnerPriority.gridy = 5;
		add(getJSpinnerPriority(), gbc_jSpinnerPriority);
		GridBagConstraints gbc_jSeparatorSubscription = new GridBagConstraints();
		gbc_jSeparatorSubscription.insets = new Insets(10, 0, 5, 0);
		gbc_jSeparatorSubscription.gridwidth = 2;
		gbc_jSeparatorSubscription.fill = GridBagConstraints.HORIZONTAL;
		gbc_jSeparatorSubscription.gridx = 0;
		gbc_jSeparatorSubscription.gridy = 6;
		add(getJSeparatorSubscription(), gbc_jSeparatorSubscription);
		GridBagConstraints gbc_jLabelMonitoringHeader = new GridBagConstraints();
		gbc_jLabelMonitoringHeader.anchor = GridBagConstraints.WEST;
		gbc_jLabelMonitoringHeader.gridx = 0;
		gbc_jLabelMonitoringHeader.gridy = 7;
		add(getJLabelMonitoringHeader(), gbc_jLabelMonitoringHeader);
		GridBagConstraints gbc_jLabelSamplingInterval = new GridBagConstraints();
		gbc_jLabelSamplingInterval.anchor = GridBagConstraints.WEST;
		gbc_jLabelSamplingInterval.gridx = 0;
		gbc_jLabelSamplingInterval.gridy = 8;
		add(getJLabelSamplingInterval(), gbc_jLabelSamplingInterval);
		GridBagConstraints gbc_jSpinnerSamplingInterval = new GridBagConstraints();
		gbc_jSpinnerSamplingInterval.insets = new Insets(0, 5, 0, 0);
		gbc_jSpinnerSamplingInterval.anchor = GridBagConstraints.WEST;
		gbc_jSpinnerSamplingInterval.gridx = 1;
		gbc_jSpinnerSamplingInterval.gridy = 8;
		add(getJSpinnerSamplingInterval(), gbc_jSpinnerSamplingInterval);
		GridBagConstraints gbc_jLabelQueueSize = new GridBagConstraints();
		gbc_jLabelQueueSize.anchor = GridBagConstraints.WEST;
		gbc_jLabelQueueSize.gridx = 0;
		gbc_jLabelQueueSize.gridy = 9;
		add(getJLabelQueueSize(), gbc_jLabelQueueSize);
		GridBagConstraints gbc_jSpinnerQueueSize = new GridBagConstraints();
		gbc_jSpinnerQueueSize.insets = new Insets(0, 5, 0, 0);
		gbc_jSpinnerQueueSize.anchor = GridBagConstraints.WEST;
		gbc_jSpinnerQueueSize.gridx = 1;
		gbc_jSpinnerQueueSize.gridy = 9;
		add(getJSpinnerQueueSize(), gbc_jSpinnerQueueSize);
		GridBagConstraints gbc_jLabelDiscardOldest = new GridBagConstraints();
		gbc_jLabelDiscardOldest.anchor = GridBagConstraints.WEST;
		gbc_jLabelDiscardOldest.gridx = 0;
		gbc_jLabelDiscardOldest.gridy = 10;
		add(getJLabelDiscardOldest(), gbc_jLabelDiscardOldest);
		GridBagConstraints gbc_jCheckboxDiscardOldest = new GridBagConstraints();
		gbc_jCheckboxDiscardOldest.insets = new Insets(0, 5, 0, 0);
		gbc_jCheckboxDiscardOldest.anchor = GridBagConstraints.WEST;
		gbc_jCheckboxDiscardOldest.gridx = 1;
		gbc_jCheckboxDiscardOldest.gridy = 10;
		add(getJCheckboxDiscardOldest(), gbc_jCheckboxDiscardOldest);
		GridBagConstraints gbc_jLabelMonitoringMode = new GridBagConstraints();
		gbc_jLabelMonitoringMode.anchor = GridBagConstraints.WEST;
		gbc_jLabelMonitoringMode.gridx = 0;
		gbc_jLabelMonitoringMode.gridy = 11;
		add(getJLabelMonitoringMode(), gbc_jLabelMonitoringMode);
		GridBagConstraints gbc_jComboBoxMonitoringModeä = new GridBagConstraints();
		gbc_jComboBoxMonitoringModeä.insets = new Insets(0, 5, 0, 0);
		gbc_jComboBoxMonitoringModeä.anchor = GridBagConstraints.WEST;
		gbc_jComboBoxMonitoringModeä.gridx = 1;
		gbc_jComboBoxMonitoringModeä.gridy = 11;
		add(getJComboBoxMonitoringModel(), gbc_jComboBoxMonitoringModeä);
	}

	private JLabel getJLabelSubscriptionHeader() {
		if (jLabelSubscriptionHeader == null) {
			jLabelSubscriptionHeader = new JLabel("Subscription Settings:");
			jLabelSubscriptionHeader.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelSubscriptionHeader;
	}
	
	private JLabel getJLabelPublishingInterval() {
		if (jLabelPublishingInterval == null) {
			jLabelPublishingInterval = new JLabel("Publishing Interval:");
			jLabelPublishingInterval.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelPublishingInterval;
	}
	private JSpinner getJSpinnerPublishingInterval() {
		if (jSpinnerPublishingInterval == null) {
			jSpinnerPublishingInterval = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
			jSpinnerPublishingInterval.setFont(new Font("Dialog", Font.PLAIN, 12));
			jSpinnerPublishingInterval.setPreferredSize(spinnerSize);
		}
		return jSpinnerPublishingInterval;
	}
	
	private JLabel getJLabelLifetimeCount() {
		if (jLabelLifetimeCount == null) {
			jLabelLifetimeCount = new JLabel("Lifetime Count:");
			jLabelLifetimeCount.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelLifetimeCount;
	}
	private JSpinner getJSpinnerLifetimeCount() {
		if (jSpinnerLifetimeCount == null) {
			jSpinnerLifetimeCount = new JSpinner(new SpinnerNumberModel(0, 0, 6000, 1));
			jSpinnerLifetimeCount.setFont(new Font("Dialog", Font.PLAIN, 12));
			jSpinnerLifetimeCount.setPreferredSize(spinnerSize);
		}
		return jSpinnerLifetimeCount;
	}
	
	private JLabel getJLabelMaxKeepAliveCount() {
		if (jLabelMaxKeepAliveCount == null) {
			jLabelMaxKeepAliveCount = new JLabel("Max Keep Alive Count:");
			jLabelMaxKeepAliveCount.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelMaxKeepAliveCount;
	}
	private JSpinner getJSpinnerMaxKeepAliveCount() {
		if (jSpinnerMaxKeepAliveCount == null) {
			jSpinnerMaxKeepAliveCount = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
			jSpinnerMaxKeepAliveCount.setFont(new Font("Dialog", Font.PLAIN, 12));
			jSpinnerMaxKeepAliveCount.setPreferredSize(spinnerSize);
		}
		return jSpinnerMaxKeepAliveCount;
	}
	
	private JLabel getJLabelMaxNotificationsPerPublish() {
		if (jLabelMaxNotificationsPerPublish == null) {
			jLabelMaxNotificationsPerPublish = new JLabel("Max Notifications Per Publish:");
			jLabelMaxNotificationsPerPublish.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelMaxNotificationsPerPublish;
	}
	private JSpinner getJSpinnerMaxNotificationsPerPublish() {
		if (jSpinnerMaxNotificationsPerPublish == null) {
			jSpinnerMaxNotificationsPerPublish = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
			jSpinnerMaxNotificationsPerPublish.setFont(new Font("Dialog", Font.PLAIN, 12));
			jSpinnerMaxNotificationsPerPublish.setPreferredSize(spinnerSize);
		}
		return jSpinnerMaxNotificationsPerPublish;
	}
	
	private JLabel getJLabelPriority() {
		if (jLabelPriority == null) {
			jLabelPriority = new JLabel("Priority:");
			jLabelPriority.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelPriority;
	}
	private JSpinner getJSpinnerPriority() {
		if (jSpinnerPriority == null) {
			jSpinnerPriority = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
			jSpinnerPriority.setFont(new Font("Dialog", Font.PLAIN, 12));
			jSpinnerPriority.setPreferredSize(spinnerSize);
		}
		return jSpinnerPriority;
	}
	
	private JSeparator getJSeparatorSubscription() {
		if (jSeparatorSubscription == null) {
			jSeparatorSubscription = new JSeparator();
		}
		return jSeparatorSubscription;
	}
	
	
	private JLabel getJLabelMonitoringHeader() {
		if (jLabelMonitoringHeader == null) {
			jLabelMonitoringHeader = new JLabel("Monitoring Settings:");
			jLabelMonitoringHeader.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelMonitoringHeader;
	}
	
	
	
	private JLabel getJLabelSamplingInterval() {
		if (jLabelSamplingInterval == null) {
			jLabelSamplingInterval = new JLabel("Sampling Interval:");
			jLabelSamplingInterval.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelSamplingInterval;
	}
	private JSpinner getJSpinnerSamplingInterval() {
		if (jSpinnerSamplingInterval == null) {
			jSpinnerSamplingInterval = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
			jSpinnerSamplingInterval.setPreferredSize(new Dimension(100, 26));
			jSpinnerSamplingInterval.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jSpinnerSamplingInterval;
	}
	private JLabel getJLabelQueueSize() {
		if (jLabelQueueSize == null) {
			jLabelQueueSize = new JLabel("Queue Size:");
			jLabelQueueSize.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelQueueSize;
	}
	private JSpinner getJSpinnerQueueSize() {
		if (jSpinnerQueueSize == null) {
			jSpinnerQueueSize = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
			jSpinnerQueueSize.setPreferredSize(new Dimension(100, 26));
			jSpinnerQueueSize.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jSpinnerQueueSize;
	}
	private JLabel getJLabelDiscardOldest() {
		if (jLabelDiscardOldest == null) {
			jLabelDiscardOldest = new JLabel("Discard Oldest:");
			jLabelDiscardOldest.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelDiscardOldest;
	}
	private JCheckBox getJCheckboxDiscardOldest() {
		if (jCheckboxDiscardOldest == null) {
			jCheckboxDiscardOldest = new JCheckBox("");
			jCheckboxDiscardOldest.setFont(new Font("Dialog", Font.PLAIN, 12));
			jCheckboxDiscardOldest.setPreferredSize(new Dimension(26, 26));
			jCheckboxDiscardOldest.addActionListener(this);
		}
		return jCheckboxDiscardOldest;
	}
	
	private JLabel getJLabelMonitoringMode() {
		if (jLabelMonitoringMode == null) {
			jLabelMonitoringMode = new JLabel("Monitoring Mode:");
			jLabelMonitoringMode.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelMonitoringMode;
	}
	private JComboBox<MonitoringMode> getJComboBoxMonitoringModel() {
		if (jComboBoxMonitoringModel == null) {
			Vector<MonitoringMode> monitoringModes = new Vector<>(Arrays.asList(MonitoringMode.values()));
			monitoringModes.remove(MonitoringMode.Disabled);
			jComboBoxMonitoringModel = new JComboBox<>(monitoringModes);
			jComboBoxMonitoringModel.setFont(new Font("Dialog", Font.PLAIN, 12));
			jComboBoxMonitoringModel.setPreferredSize(spinnerSize);
			jComboBoxMonitoringModel.addActionListener(this);
		}
		return jComboBoxMonitoringModel;
	}
	
	
	public void setPropertiesToPanel() {
		
		// --- Exit in case of missing connector --------------------
		if (this.opcUaConnector==null) return;
		Properties properties = this.opcUaConnector.getConnectorProperties();
		
		// --- SubscriptionSettings: Publishing Interval and so on -- 
		this.getJSpinnerPublishingInterval().setValue(properties.getIntegerValue(OpcUaConnector.PROP_DATA_SUBSCRIPTION_PUBLISHING_INTERVAL));
		this.getJSpinnerLifetimeCount().setValue(properties.getIntegerValue(OpcUaConnector.PROP_DATA_SUBSCRIPTION_LIFE_TIME_COUNT));
		this.getJSpinnerMaxKeepAliveCount().setValue(properties.getIntegerValue(OpcUaConnector.PROP_DATA_SUBSCRIPTION_MAX_KEEP_ALIVE_COUNT));
		this.getJSpinnerMaxNotificationsPerPublish().setValue(properties.getIntegerValue(OpcUaConnector.PROP_DATA_SUBSCRIPTION_MAX_NOTIFICATIONS_PER_PUBLISH));
		this.getJSpinnerPriority().setValue(properties.getIntegerValue(OpcUaConnector.PROP_DATA_SUBSCRIPTION_PRIORITY));
		
		// --- MonitoringSettings -----------------------------------
		this.getJSpinnerSamplingInterval().setValue(properties.getIntegerValue(OpcUaConnector.PROP_DATA_MONITORING_SAMPLING_INTERVAL));
		this.getJSpinnerQueueSize().setValue(properties.getIntegerValue(OpcUaConnector.PROP_DATA_MONITORING_QUEUE_SIZE));
		this.getJCheckboxDiscardOldest().setSelected(properties.getBooleanValue(OpcUaConnector.PROP_DATA_MONITORING_DISCARD_OLDEST));
		this.getJComboBoxMonitoringModel().setSelectedItem(MonitoringMode.valueOf(properties.getStringValue(OpcUaConnector.PROP_DATA_MONITORING_MODE)));
		
	}

	/**
	 * Checks for configuration errors in the current setting.
	 * @return true, if successful
	 */
	public String getConfigurationError() {

		// ----------------------------------------------------------
		// --- Check for configuration name -------------------------
		// ----------------------------------------------------------

		// --- No error, return null --------------------------------
		return null;
	}
	
	/**
	 * Sets the panel to properties.
	 * @return true, if successful
	 */
	public boolean setPanelToProperties() {
		
		// --- Exit in case of missing connector --------------------
		if (this.opcUaConnector==null) return false;
		Properties properties = this.opcUaConnector.getConnectorProperties();
		
		// --- Avoid saving mis-configuration -----------------------
		boolean isErrorFree = (this.getConfigurationError()==null);
		if (isErrorFree==false) return false;
		
		// --- SubscriptionSettings: Publishing Interval and so on --
		properties.setIntegerValue(OpcUaConnector.PROP_DATA_SUBSCRIPTION_PUBLISHING_INTERVAL, this.toInteger(this.getJSpinnerPublishingInterval()));
		properties.setIntegerValue(OpcUaConnector.PROP_DATA_SUBSCRIPTION_LIFE_TIME_COUNT, this.toInteger(this.getJSpinnerLifetimeCount()));
		properties.setIntegerValue(OpcUaConnector.PROP_DATA_SUBSCRIPTION_MAX_KEEP_ALIVE_COUNT, this.toInteger(this.getJSpinnerMaxKeepAliveCount()));
		properties.setIntegerValue(OpcUaConnector.PROP_DATA_SUBSCRIPTION_MAX_NOTIFICATIONS_PER_PUBLISH, this.toInteger(this.getJSpinnerMaxNotificationsPerPublish()));
		properties.setIntegerValue(OpcUaConnector.PROP_DATA_SUBSCRIPTION_PRIORITY, this.toInteger(this.getJSpinnerPriority()));
		
		// --- MonitoringSettings -----------------------------------
		properties.setIntegerValue(OpcUaConnector.PROP_DATA_MONITORING_SAMPLING_INTERVAL, this.toInteger(this.getJSpinnerSamplingInterval()));
		properties.setIntegerValue(OpcUaConnector.PROP_DATA_MONITORING_QUEUE_SIZE, this.toInteger(this.getJSpinnerQueueSize()));
		properties.setBooleanValue(OpcUaConnector.PROP_DATA_MONITORING_DISCARD_OLDEST, this.getJCheckboxDiscardOldest().isSelected());
		properties.setStringValue(OpcUaConnector.PROP_DATA_MONITORING_MODE, this.getJComboBoxMonitoringModel().getSelectedItem().toString());
		
		// --- Ensure that the changes are saved --------------------
		this.opcUaConnector.saveSettings();
		
		return true;
	}
	
	/**
	 * Evaluates the integer value out of the specified {@link JSpinner}.
	 *
	 * @param spinner the JSpinner to get the value from
	 * @return the integer
	 */
	private Integer toInteger(JSpinner spinner) {
		
		Integer intValue = null;
		if (spinner!=null) {
			try {
				if (spinner.getValue()==null) {
					intValue = 0;
				} else {
					intValue = (Integer)spinner.getValue();
				}
			} catch (Exception ex) {
				//ex.printStackTrace();
			}
		}
		return intValue;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		if (ae.getSource()==this.getJComboBoxMonitoringModel()) {

			
			
		}
		
	}
	
	
}
