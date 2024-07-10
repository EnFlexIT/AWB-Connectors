package de.enflexit.connector.mqtt.awbRemote;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import agentgui.core.application.Application;
import agentgui.core.config.GlobalInfo;
import de.enflexit.awb.remoteControl.AwbRemoteControl;
import de.enflexit.awb.remoteControl.AwbSimulationSettings;
import de.enflexit.awbRemote.jsonCommand.AwbCommand;
import de.enflexit.awbRemote.jsonCommand.AwbNotification;
import de.enflexit.awbRemote.jsonCommand.AwbNotification.AwbState;
import de.enflexit.awbRemote.jsonCommand.Parameter;
import de.enflexit.awbRemote.jsonCommand.Parameter.ParamName;
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
	
	private String brokerHost = "localhost";

	private MQTTConnector mqttConnector;
	
	private DateTimeFormatter dateTimeFormatter;
	
	private Gson gson;
	
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
	public void sendStatusUpdate(AwbNotification notification) {
		if (this.getMqttConnector()!=null && this.getMqttConnector().isConnected()==true) {
			String messageContent = this.getGson().toJson(notification);
			this.getMqttConnector().publish(MQTT_TOPIC_STATUS_UPDATES, messageContent);
		}
	}

	/* (non-Javadoc)
	 * @see de.enflexit.connector.mqtt.MQTTSubscriber#handleMessage(de.enflexit.connector.mqtt.MQTTMessageWrapper)
	 */
	@Override
	public void handleMessage(MQTTMessageWrapper messageWrapper) {
		String messageString = messageWrapper.getPayloadString();
		
		AwbCommand command = this.getCommandFromJsonString(messageString);
		
		if (command!=null) {
			switch (command.getCommand()) {
			case LOAD_PROJECT:
				String projectFolder = command.getParameter();
				if (projectFolder!=null) {
					this.loadProject(projectFolder);
				} else {
					System.err.println("[" + this.getClass().getSimpleName() + "] Unable to load project, no project folder passed!");
				}
				break;
			case SELECT_SETUP:
				String setupName = command.getParameter();
				if (setupName!=null) {
					this.selectSetup(setupName);
				} else {
					System.err.println("[" + this.getClass().getSimpleName() + "] Unable to select setup, no setup name passed!");
				}
				break;
			case CONFIGURE_SIMULATION:
				
				AwbSimulationSettings simSettings = new AwbSimulationSettings();
				
				String paramSimStart = this.getParameterByName(command, ParamName.SIMULATION_START_TIME);
				ZonedDateTime simStart = ZonedDateTime.parse(paramSimStart, this.getDateTimeFormatter());
				simSettings.setSimulationStartTime(simStart.toInstant().toEpochMilli());
				
				String paramSimEnd = this.getParameterByName(command, ParamName.SIMULATION_END_TIME);
				ZonedDateTime simEnd = ZonedDateTime.parse(paramSimEnd, this.getDateTimeFormatter());
				simSettings.setSimulationEndTime(simEnd.toInstant().toEpochMilli());
				
				String paramSimStep = this.getParameterByName(command, ParamName.SIMULATION_STEP_LENGTH);
				int simStepSeconds = Integer.parseInt(paramSimStep);
				simSettings.setSimulationStepSeconds(simStepSeconds);
				
				this.configureSimulation(simSettings);
				
				this.sendConfigurationSet();
				
				break;
			case START_MAS:
				this.startMultiAgentSystem();
				break;
			case NEXT_STEP:
				// --- Not implemented yet!!!
				break;
			case STOP_MAS:
				this.stopMultiAgentSystem();
				break;
			}
		} else {
			System.err.println("[" + this.getClass().getSimpleName() + "] The received string is no valid Json representaiton AwbCommand: " + messageString);
		}
		
	}

	/**
	 * Unsubscribes from the remote commands topic.
	 */
	protected void unsubscribeFromCommands() {
		this.getMqttConnector().unsubscribe(MQTT_TOPIC_REMOTE_COMMANDS, this);
	}
	
	/**
	 * Sends a status update that AWB is ready to receive MQTT commands.
	 */
	protected void sendAwbReady() {
		AwbNotification notification = new AwbNotification();
		notification.setAwbState(AwbState.AWB_READY);
		this.sendStatusUpdate(notification);
	}

	/**
	 * Sends a status update that a project was loaded.
	 * @param projectName the project name
	 */
	private void sendProjectLoaded(String projectName) {
		AwbNotification notification = new AwbNotification();
		notification.setAwbState(AwbState.PROJECT_LOADED);
		notification.setStateDetails(projectName);
		this.sendStatusUpdate(notification);
	}

	/**
	 * Sends a status update that a setup was selected.
	 *
	 * @param setupName the setup name
	 */
	private void sendSetupReady(String setupName) {
		AwbNotification notification = new AwbNotification();
		notification.setAwbState(AwbState.SETUP_LOADED);
		notification.setStateDetails(setupName);
		this.sendStatusUpdate(notification);
	}
	
	/**
	 * Sends a status update that the simulation is ready.
	 */
	private void sendConfigurationSet() {
		AwbNotification notification = new AwbNotification();
		notification.setAwbState(AwbState.CONFIGURATION_SET);
		this.sendStatusUpdate(notification);
	}

	/**
	 * Sends a status update that the simulation is ready.
	 */
	private void sendMasStarted() {
		AwbNotification notification = new AwbNotification();
		notification.setAwbState(AwbState.MAS_STARTED);
		this.sendStatusUpdate(notification);
	}
	
	/**
	 * Sends a status update that the simulation is ready.
	 */
	private void sendMasStopped() {
		AwbNotification notification = new AwbNotification();
		notification.setAwbState(AwbState.MAS_STOPPED);
		this.sendStatusUpdate(notification);
	}

	/**
	 * Extracts an {@link AwbCommand} from the provided json string.
	 * @param jsonString the json string
	 * @return the awb command
	 */
	private AwbCommand getCommandFromJsonString(String jsonString) {
		AwbCommand command = null;
		try {
			command = this.getGson().fromJson(jsonString, AwbCommand.class);
		} catch (JsonSyntaxException jse) {
			System.err.println("[" + this.getClass().getSimpleName() + "] Could not get AwbCommand from Json string!");
//			jse.printStackTrace();
		}
		return command;
	}
	
	/**
	 * Gets the {@link Parameter} with the specified name from the provided {@link AwbCommand}.
	 * @param command the command
	 * @param paramName the parameter name
	 * @return the parameter by name
	 */
	private String getParameterByName(AwbCommand command, ParamName paramName) {
		
		if (command==null || command.getParameterList()==null) {
			return null;
		}
		
		for (Parameter parameter : command.getParameterList()) {
			if (parameter.getParamName().equals(paramName)) {
				return (String) parameter.getParamValue();
			}
		}
		
		return null;
	}
	
	private DateTimeFormatter getDateTimeFormatter() {
		if (dateTimeFormatter==null) {
			Application.getGlobalInfo();
			dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(GlobalInfo.getCurrentZoneId());
		}
		return dateTimeFormatter;
	}
	
	private Gson getGson() {
		if (gson==null) {
			gson = new Gson();
		}
		return gson;
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.awb.remoteControl.AwbRemoteControl#setAwbState(de.enflexit.awb.remoteControl.AwbState)
	 */
	@Override
	public void setAwbState(de.enflexit.awb.remoteControl.AwbState awbState) {
		super.setAwbState(awbState);
		switch(awbState) {
		case AWB_READY:
			this.sendAwbReady();
			break;
		case PROJECT_LOADED:
			this.sendProjectLoaded(Application.getProjectFocused().getProjectName());
			break;
		case SETUP_READY:
			this.sendSetupReady(Application.getProjectFocused().getSimulationSetupCurrent());
			break;
		case MAS_STARTED:
			this.sendMasStarted();
			break;
		case SIMULATION_STEP_DONE:
			// --- Not implemented yet --------------------
			break;
		case SIMULATION_FINISHED:
			// --- Not implemented yet --------------------
			break;
		case MAS_STOPPED:
			this.sendMasStopped();
			break;
		}
	}
		
}
