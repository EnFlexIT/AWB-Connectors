package de.enflexit.connector.mqtt.awbRemote;

import agentgui.core.application.Application;
import de.enflexit.awb.remoteControl.AwbRemoteControl;
import de.enflexit.awb.remoteControl.AwbStatusUpdate;
import de.enflexit.awb.remoteControl.AwbStatusUpdate.AwbState;
import de.enflexit.connector.core.manager.ConnectorManager;
import de.enflexit.connector.mqtt.MQTTConnector;
import de.enflexit.connector.mqtt.MQTTConnectorConfiguration;
import de.enflexit.connector.mqtt.MQTTMessageWrapper;
import de.enflexit.connector.mqtt.MQTTSubscriber;

/**
 * This implementation of {@link AwbRemoteControl} allows to control an AWB instance via MQTT. 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class AwbRemoteControlMQTT extends AwbRemoteControl implements MQTTSubscriber {
	
	private static final String MQTT_TOPIC_REMOTE_COMMANDS = "awbControl";
	private static final String MQTT_TOPIC_STATUS_UPDATES = "awbStatus";
	
	private static final String COMMAND_START_MAS = "StartMAS";
	private static final String COMMAND_STOP_MAS = "StopMAS";
	private static final String COMMAND_LOAD_PROJECT = "LoadProject";
	private static final String COMMAND_SELECT_SETUP = "SelectSetup";
	
	private String brokerHost = "localhost";

	private MQTTConnector mqttConnector;
	
	/**
	 * Instantiates a new AWB remote control MQTT.
	 */
	public AwbRemoteControlMQTT() {
		Application.addApplicationListener(this);
		Application.getJadePlatform().addPropertyChangeListener(this);
	}

	/**
	 * Checks if the MQTT connector is configured and available
	 * @return true, if successful
	 */
	public boolean doConnectorCheck() {
		if (this.getMqttConnector()==null) {
			System.err.println("[" + this.getClass().getSimpleName() + "] No configured connector found for protocol " + MQTTConnectorConfiguration.PROTOCOL_NAME + " and host " + this.brokerHost);
			return false;
		} else if (this.getMqttConnector().isConnected()==false) {
			System.err.println("[" + this.getClass().getSimpleName() + "] A connector for protocol " + MQTTConnectorConfiguration.PROTOCOL_NAME + " and host " + this.brokerHost + " was found, but is not connected. Please check your settings!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Subscribes for the MQTT channel for AWB remote commands.
	 */
	public void subscribeForCommands() {
		this.getMqttConnector().subscribe(MQTT_TOPIC_REMOTE_COMMANDS, this);
	}

	private MQTTConnector getMqttConnector() {
		if (mqttConnector==null) {
			mqttConnector = (MQTTConnector) ConnectorManager.getInstance().getConnectorByHostAndProtocol(brokerHost, MQTTConnectorConfiguration.PROTOCOL_NAME);
		}
		return mqttConnector;
	}
	
	/**
	 * Publishes a status update to the corresponding MQTT topic.
	 * @param statusUpdate the status update
	 */
	public void sendStatusUpdate(AwbStatusUpdate statusUpdate) {
		if (this.getMqttConnector()!=null && this.getMqttConnector().isConnected()==true) {
			String messageContent = statusUpdate.toJsonString();
			this.getMqttConnector().publish(MQTT_TOPIC_STATUS_UPDATES, messageContent);
		}
	}

	/* (non-Javadoc)
	 * @see de.enflexit.connector.mqtt.MQTTSubscriber#handleMessage(de.enflexit.connector.mqtt.MQTTMessageWrapper)
	 */
	@Override
	public void handleMessage(MQTTMessageWrapper messageWrapper) {
		String messageString = messageWrapper.getPayloadString();
		String[] stringParts = messageString.split(";");
		String command = stringParts[0];
		String param = (stringParts.length>1) ? stringParts[1] : null;
		if (command.equals(COMMAND_START_MAS)) {
			this.startMultiAgentSystem();
		} else if (command.equals(COMMAND_STOP_MAS)) {
			this.stopMultiAgentSystem();
		} else if (command.equals(COMMAND_LOAD_PROJECT)) {
			if (param!=null) {
				this.loadProject(param);
			} else {
				System.err.println("[" + this.getClass().getSimpleName() + "] Unable to load project, no project name passed!");
			}
		} else if (command.equals(COMMAND_SELECT_SETUP)) {
			if (param!=null) {
				this.selectSetup(param);
			} else {
				System.err.println("[" + this.getClass().getSimpleName() + "] Unable to select setup, no project name passed!");
			}
		}
		
		else {
			System.out.println("[" + this.getClass().getSimpleName() + "] Received command " + command + " - unknown or not implemented yet");
		}
	}

	/**
	 * Sends a status update that AWB is ready to receive MQTT commands.
	 */
	protected void sendReadyStatus() {
		AwbStatusUpdate statusUpdate = new AwbStatusUpdate();
		statusUpdate.setAwbState(AwbState.AWB_READY);
		this.sendStatusUpdate(statusUpdate);
	}
	
	/**
	 * Unsubscribes from the remote commands topic.
	 */
	protected void unsubscribeFromCommands() {
		this.getMqttConnector().unsubscribe(MQTT_TOPIC_REMOTE_COMMANDS, this);
	}

	

	/* (non-Javadoc)
	 * @see de.enflexit.awb.remoteControl.AwbRemoteControl#projectLoaded(java.lang.String)
	 */
	@Override
	public void projectLoaded(String projectName) {
		AwbStatusUpdate statusUpdate = new AwbStatusUpdate();
		statusUpdate.setAwbState(AwbState.PROJECT_LOADED);
		statusUpdate.setStateDetails(projectName);
		this.sendStatusUpdate(statusUpdate);
	}

	/* (non-Javadoc)
	 * @see de.enflexit.awb.remoteControl.AwbRemoteControl#setupReady(java.lang.String)
	 */
	@Override
	public void setupReady(String setupName) {
		AwbStatusUpdate statusUpdate = new AwbStatusUpdate();
		statusUpdate.setAwbState(AwbState.SETUP_READY);
		statusUpdate.setStateDetails(setupName);
		this.sendStatusUpdate(statusUpdate);
	}

	/* (non-Javadoc)
	 * @see de.enflexit.awb.remoteControl.AwbRemoteControl#simulationReady()
	 */
	@Override
	public void simulationReady() {
		AwbStatusUpdate statusUpdate = new AwbStatusUpdate();
		statusUpdate.setAwbState(AwbState.SIMULATION_READY);
		this.sendStatusUpdate(statusUpdate);
	}
		
}
