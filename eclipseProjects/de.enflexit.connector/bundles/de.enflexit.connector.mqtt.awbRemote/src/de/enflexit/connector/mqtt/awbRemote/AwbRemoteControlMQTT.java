package de.enflexit.connector.mqtt.awbRemote;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import agentgui.core.application.Application;
import agentgui.core.application.ApplicationListener;
import agentgui.core.jade.Platform.SystemAgent;
import agentgui.core.jade.PlatformStateInformation.PlatformState;
import agentgui.simulationService.agents.LoadExecutionAgent;
import de.enflexit.awb.remoteControl.AwbRemoteControl;
import de.enflexit.awb.remoteControl.AwbSimulationSettings;
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
public class AwbRemoteControlMQTT implements AwbRemoteControl, MQTTSubscriber, ApplicationListener, PropertyChangeListener {
	
	private static final String MQTT_TOPIC_REMOTE_COMMANDS = "awbControl";
	private static final String MQTT_TOPIC_STATUS_UPDATES = "awbStatus";
	
	private static final String COMMAND_START_MAS = "StartMAS";
	private static final String COMMAND_STOP_MAS = "StopMAS";
	
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
	

	/* (non-Javadoc)
	 * @see de.enflexit.awb.remoteControl.AwbRemoteControl#loadProject(java.lang.String)
	 */
	@Override
	public boolean loadProject(String projectName) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see de.enflexit.awb.remoteControl.AwbRemoteControl#selectSetup(java.lang.String)
	 */
	@Override
	public boolean selectSetup(String setupName) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see de.enflexit.awb.remoteControl.AwbRemoteControl#configureSimulation(de.enflexit.awb.remoteControl.AwbSimulationSettings)
	 */
	@Override
	public boolean configureSimulation(AwbSimulationSettings simulationSettings) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see de.enflexit.awb.remoteControl.AwbRemoteControl#startMultiAgentSystem()
	 */
	@Override
	public boolean startMultiAgentSystem() {
		Object[] startWith = new Object[1];
		startWith[0] = LoadExecutionAgent.BASE_ACTION_Start;
		Application.getJadePlatform().startSystemAgent(SystemAgent.SimStarter, null, startWith, true);
		Application.getMainWindow().setEnableSimStart(false);
		return true;
	}

	/* (non-Javadoc)
	 * @see de.enflexit.awb.remoteControl.AwbRemoteControl#stopMultiAgentSystem()
	 */
	@Override
	public boolean stopMultiAgentSystem() {
		Application.getJadePlatform().stop(true);
		return true;
	}

	/* (non-Javadoc)
	 * @see de.enflexit.awb.remoteControl.AwbRemoteControl#discreteSimulationNextStep()
	 */
	@Override
	public void discreteSimulationNextStep() {
		// TODO Auto-generated method stub

	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.awb.remoteControl.AwbRemoteControl#sendStatusUpdate(de.enflexit.awb.remoteControl.AwbStatusUpdate)
	 */
	@Override
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
		String command = messageWrapper.getPayloadString();
		if (command.equals(COMMAND_START_MAS)) {
			this.startMultiAgentSystem();
		} else if (command.equals(COMMAND_STOP_MAS)) {
			this.stopMultiAgentSystem();
		} else {
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
	 * @see agentgui.core.application.ApplicationListener#onApplicationEvent(agentgui.core.application.ApplicationListener.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(ApplicationEvent ae) {
//		System.out.println("[" + this.getClass().getSimpleName() + "] Received application event " + ae.getApplicationEvent());
		if (ae.getApplicationEvent()==ApplicationEvent.PROJECT_LOADED) {
			AwbStatusUpdate statusUpdate = new AwbStatusUpdate();
			statusUpdate.setAwbState(AwbState.PROJECT_READY);
			statusUpdate.setStateDetails(Application.getProjectFocused().getProjectName());
			this.sendStatusUpdate(statusUpdate);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent pce) {
//		System.out.println("[" + this.getClass().getSimpleName() + "] Received property change event " + pce.getPropertyName());
		if (pce.getPropertyName().equals("PlatformState")) {
			PlatformState newState = (PlatformState) pce.getNewValue();
			if (newState==PlatformState.RunningMAS) {
				AwbStatusUpdate statusUpdate = new AwbStatusUpdate();
				statusUpdate.setAwbState(AwbState.SIMULATION_READY);
				this.sendStatusUpdate(statusUpdate);
			}
		}
	}


}
