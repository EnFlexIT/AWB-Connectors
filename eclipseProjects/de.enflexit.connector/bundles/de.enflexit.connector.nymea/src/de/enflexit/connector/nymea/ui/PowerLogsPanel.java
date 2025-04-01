package de.enflexit.connector.nymea.ui;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import de.enflexit.connector.nymea.NymeaConnector;
import de.enflexit.connector.nymea.dataModel.PowerLogEntry;
import de.enflexit.connector.nymea.dataModel.SampleRate;
import de.enflexit.connector.nymea.dataModel.Thing;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JComboBox;

/**
 * This panel shows the power log entries for one specific "thing". 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class PowerLogsPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 1311526084535219572L;
	
	private JLabel jLabelTimeFrom;
	private JTextField jTextFieldFrom;
	private JLabel jLabelTill;
	private JTextField jTextFieldTo;
	private JButton jButtonReload;
	private JScrollPane jScrollPaneTableData;
	
	private JTable jTableData;
	private DefaultTableModel tableModel;
	
	private NymeaConnector connector;
	private ArrayList<PowerLogEntry> logEntries;
	
	private DateTimeFormatter dateTimeFormatter;
	private JLabel jLabelSampleRate;
	private JComboBox<SampleRate> jComboBoxSampleRate;
	
	private Thing thing;
	
	/**
	 * Instantiates a new power logs panel.
	 * @deprecated Added for window builder compatibility only, use the other constructor for actual instantiation.
	 */
	@Deprecated
	public PowerLogsPanel() {
		initialize();
	}
	
	/**
	 * Instantiates a new power logs panel.
	 * @param connector the connector to use for requesting new data-
	 * @param thingID the thing ID
	 * @param logEntries the log entries to be initially displayed
	 */
	public PowerLogsPanel(NymeaConnector connector, Thing thing, ArrayList<PowerLogEntry> logEntries) {
		this.connector = connector;
		this.thing = thing;
		this.logEntries = logEntries;
		this.initialize();
	}
	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		GridBagConstraints gbc_jLabelTimeFrom = new GridBagConstraints();
		gbc_jLabelTimeFrom.insets = new Insets(10, 10, 5, 5);
		gbc_jLabelTimeFrom.gridx = 0;
		gbc_jLabelTimeFrom.gridy = 0;
		add(getJLabelTimeFrom(), gbc_jLabelTimeFrom);
		GridBagConstraints gbc_jTextFieldFrom = new GridBagConstraints();
		gbc_jTextFieldFrom.insets = new Insets(10, 5, 5, 5);
		gbc_jTextFieldFrom.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldFrom.gridx = 1;
		gbc_jTextFieldFrom.gridy = 0;
		add(getJTextFieldFrom(), gbc_jTextFieldFrom);
		GridBagConstraints gbc_jLabelTill = new GridBagConstraints();
		gbc_jLabelTill.anchor = GridBagConstraints.EAST;
		gbc_jLabelTill.insets = new Insets(10, 5, 5, 5);
		gbc_jLabelTill.gridx = 2;
		gbc_jLabelTill.gridy = 0;
		add(getJLabelTill(), gbc_jLabelTill);
		GridBagConstraints gbc_jTextFieldTill = new GridBagConstraints();
		gbc_jTextFieldTill.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldTill.insets = new Insets(10, 5, 5, 5);
		gbc_jTextFieldTill.gridx = 3;
		gbc_jTextFieldTill.gridy = 0;
		add(getJTextFieldTo(), gbc_jTextFieldTill);
		GridBagConstraints gbc_jLabelSampleRate = new GridBagConstraints();
		gbc_jLabelSampleRate.anchor = GridBagConstraints.EAST;
		gbc_jLabelSampleRate.insets = new Insets(10, 5, 5, 5);
		gbc_jLabelSampleRate.gridx = 4;
		gbc_jLabelSampleRate.gridy = 0;
		add(getJLabelSampleRate(), gbc_jLabelSampleRate);
		GridBagConstraints gbc_jComboBoxSampleRate = new GridBagConstraints();
		gbc_jComboBoxSampleRate.insets = new Insets(10, 5, 5, 5);
		gbc_jComboBoxSampleRate.fill = GridBagConstraints.HORIZONTAL;
		gbc_jComboBoxSampleRate.gridx = 5;
		gbc_jComboBoxSampleRate.gridy = 0;
		add(getJComboBoxSampleRate(), gbc_jComboBoxSampleRate);
		GridBagConstraints gbc_jButtonReload = new GridBagConstraints();
		gbc_jButtonReload.anchor = GridBagConstraints.EAST;
		gbc_jButtonReload.insets = new Insets(10, 5, 5, 10);
		gbc_jButtonReload.gridx = 6;
		gbc_jButtonReload.gridy = 0;
		add(getJButtonReload(), gbc_jButtonReload);
		GridBagConstraints gbc_jScrollPaneTableData = new GridBagConstraints();
		gbc_jScrollPaneTableData.gridwidth = 7;
		gbc_jScrollPaneTableData.fill = GridBagConstraints.BOTH;
		gbc_jScrollPaneTableData.gridx = 0;
		gbc_jScrollPaneTableData.gridy = 1;
		add(getJScrollPaneTableData(), gbc_jScrollPaneTableData);
	}

	private JLabel getJLabelTimeFrom() {
		if (jLabelTimeFrom == null) {
			jLabelTimeFrom = new JLabel("From");
			jLabelTimeFrom.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelTimeFrom;
	}
	private JTextField getJTextFieldFrom() {
		if (jTextFieldFrom == null) {
			jTextFieldFrom = new JTextField();
			jTextFieldFrom.setColumns(10);
			
			long timeStamp = this.getFirstTimestamp();
			if (timeStamp>-1) {
				jTextFieldFrom.setText(this.getFormattedDateTime(timeStamp));
			}
		}
		return jTextFieldFrom;
	}
	private JLabel getJLabelTill() {
		if (jLabelTill == null) {
			jLabelTill = new JLabel("till");
			jLabelTill.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelTill;
	}
	private JTextField getJTextFieldTo() {
		if (jTextFieldTo == null) {
			jTextFieldTo = new JTextField();
			jTextFieldTo.setColumns(10);
			
			long timeStamp = this.getLastTimestamp();
			if (timeStamp>-1) {
				jTextFieldTo.setText(this.getFormattedDateTime(timeStamp));
			}
		}
		return jTextFieldTo;
	}
	private JButton getJButtonReload() {
		if (jButtonReload == null) {
			jButtonReload = new JButton(new ImageIcon(this.getClass().getResource("/icons/Refresh.png")));
			jButtonReload.setToolTipText("Reload the table contents, according to the settings made.");
			jButtonReload.addActionListener(this);
		}
		return jButtonReload;
	}
	private JScrollPane getJScrollPaneTableData() {
		if (jScrollPaneTableData == null) {
			jScrollPaneTableData = new JScrollPane();
			jScrollPaneTableData.setViewportView(getJTableData());
		}
		return jScrollPaneTableData;
	}
	private JTable getJTableData() {
		if (jTableData == null) {
			jTableData = new JTable();
			jTableData.setModel(this.getTableModel());
		}
		return jTableData;
	}
	
	private ArrayList<PowerLogEntry> getLogEntries() {
		if (logEntries==null) {
			logEntries = new ArrayList<PowerLogEntry>();
		}
		return logEntries;
	}
	
	private DefaultTableModel getTableModel() {
		if (tableModel==null) {
			tableModel = new DefaultTableModel();
			
			tableModel.addColumn("Date/Time");
			tableModel.addColumn("Current power");
			tableModel.addColumn("Total consumption");
			tableModel.addColumn("Total production");
			
			for (PowerLogEntry logEntry : this.getLogEntries()) {
				tableModel.addRow(this.getDataRow(logEntry));
			}
		}
		return tableModel;
	}
	
	private DateTimeFormatter getDateTimeFormatter() {
		if (dateTimeFormatter==null) {
			dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(ZoneId.systemDefault());
		}
		return dateTimeFormatter;
	}
	
	private Vector<String> getDataRow(PowerLogEntry powerLogEntry){
		Vector<String> dataRow = new Vector<String>();
		
		long timestampSeconds = powerLogEntry.getTimestamp();
		dataRow.add(this.getFormattedDateTime(timestampSeconds));

		dataRow.add(String.valueOf(powerLogEntry.getCurrentPower()));
		dataRow.add(String.valueOf(powerLogEntry.getTotalConsumption()));
		dataRow.add(String.valueOf(powerLogEntry.getTotalProduction()));
		
		return dataRow;
	}
	
	private String getFormattedDateTime(long timestampSeconds) {
		Instant insant = Instant.ofEpochSecond(timestampSeconds);
		ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(insant, ZoneId.systemDefault());
		return this.getDateTimeFormatter().format(zonedDateTime);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==this.getJButtonReload()) {
		
			// --- Extract relevant infos from the inputs -----------
			Instant from = Instant.from(this.getDateTimeFormatter().parse(this.getJTextFieldFrom().getText()));
			Instant to = Instant.from(this.getDateTimeFormatter().parse(this.getJTextFieldTo().getText()));
			SampleRate sampleRate = this.getJComboBoxSampleRate().getItemAt(this.getJComboBoxSampleRate().getSelectedIndex());
			
			// --- Request power logs as specified --------
			ArrayList<PowerLogEntry> logEntries = this.connector.getNymeaClient().getThingPowerLogs(this.thing.getId(), from.toEpochMilli(), to.toEpochMilli(), sampleRate);
			
			// --- Reset the table model ------------------
			if (logEntries!=null) {
				this.logEntries = logEntries;
				this.tableModel=null;
				this.getJTableData().setModel(this.getTableModel());
			}
		}
	}
	
	/**
	 * Gets the timestamp from the first log entry in the list.
	 * @return the first timestamp
	 */
	private long getFirstTimestamp() {
		if (this.logEntries!=null && this.logEntries.size()>0) {
			return this.logEntries.get(0).getTimestamp();
		} else {
			return -1;
		}
	}
	
	/**
	 * Gets the timestamp from the last log entry in the list.
	 * @return the last timestamp
	 */
	private long getLastTimestamp() {
		if (this.logEntries!=null && this.logEntries.size()>0) {
			return this.logEntries.get(this.logEntries.size()-1).getTimestamp();
		} else {
			return -1;
		}
	}
	
	private JLabel getJLabelSampleRate() {
		if (jLabelSampleRate == null) {
			jLabelSampleRate = new JLabel("Sample Rate");
			jLabelSampleRate.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelSampleRate;
	}
	
	private JComboBox<SampleRate> getJComboBoxSampleRate() {
		if (jComboBoxSampleRate == null) {
			jComboBoxSampleRate = new JComboBox<SampleRate>();
			jComboBoxSampleRate.setModel(new DefaultComboBoxModel<SampleRate>(SampleRate.values()));
			jComboBoxSampleRate.setSelectedItem(SampleRate.SAMPLE_RATE_15_MINS);
		}
		return jComboBoxSampleRate;
	}
}
