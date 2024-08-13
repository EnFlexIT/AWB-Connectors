package de.enflexit.connector.mqtt.awbRemote;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import agentgui.core.application.Application;
import agentgui.core.config.GlobalInfo;
import agentgui.core.project.Project;
import agentgui.simulationService.transaction.AbstractDiscreteSimulationStepController;
import de.enflexit.awb.remoteControl.AwbRemoteControl;
import de.enflexit.awb.remoteControl.AwbSimulationSettings;
import de.enflexit.awb.remoteControl.AwbState;
import de.enflexit.awbRemote.jsonCommand.AwbCommand;
import de.enflexit.awbRemote.jsonCommand.AwbNotification;
import de.enflexit.awbRemote.jsonCommand.Parameter;
import de.enflexit.awbRemote.jsonCommand.Parameter.ParamName;
import de.enflexit.common.properties.Properties;
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
	
	public static final String PROPERTY_KEY_BROKER_HOST = "mqtt.remoteControl.brokerHost";
	public static final String PROPERTY_KEY_COMMAND_TOPIC = "mqtt.remoteControl.commandTopic";
	public static final String PROPERTY_KEY_STATUS_TOPIC = "mqtt.remoteControl.statusTopic";
	public static final String PROPERTY_KEY_CONTROL_STEPS = "mqtt.remoteControl.controlSteps";
	
	private static final String DEFAULT_TOPIC_REMOTE_COMMANDS = "awbControl";
	private static final String DEFAULT_TOPIC_STATUS_UPDATES = "awbStatus";
	private static final String DEFAULT_BROKER_HOST = "localhost";
	private static final boolean DEFAULT_CONTROL_STEPS = true;

	private MQTTConnector mqttConnector;
	
	private DateTimeFormatter dateTimeFormatter;
	
	private Gson gson;
	
	private DiscreteSimulationStepConcroller stepController;
	
	private String statusTopic;
	private String commandTopic;
	private String brokerHost;
	private Boolean controlSteps;
	
	/**
	 * Instantiates a new AWB remote control MQTT.
	 */
	public AwbRemoteControlMQTT() {
		Application.addApplicationListener(this);
		if (this.isControlSteps()==true) {
			this.getStepController();	// Initialize the step controller
		}
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
		this.getMqttConnector().subscribe(this.getCommandTopic(), this);
	}

	/**
	 * Gets the mqtt connector.
	 * @return the mqtt connector
	 */
	private MQTTConnector getMqttConnector() {
		if (mqttConnector==null) {
			mqttConnector = (MQTTConnector) ConnectorManager.getInstance().getConnectorByHostAndProtocol(this.getBrokerHost(), MQTTConnectorConfiguration.PROTOCOL_NAME);
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
			this.getMqttConnector().publish(this.getStatusTopic(), messageContent);
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
				
				this.sendAwbStateNotification(AwbNotification.AwbState.CONFIGURATION_SET);
				
				break;
			case START_MAS:
				this.startMultiAgentSystem();
				break;
			case NEXT_STEP:
				this.getStepController().stepSimulation();
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
		this.getMqttConnector().unsubscribe(this.getCommandTopic(), this);
	}
	
	protected void sendAwbStateNotification(AwbNotification.AwbState awbState) {
		AwbNotification notification = new AwbNotification();
		notification.setAwbState(awbState);
		if (awbState==AwbNotification.AwbState.PROJECT_LOADED) {
			notification.setStateDetails(Application.getProjectFocused().getProjectName());
		} else if (awbState == AwbNotification.AwbState.SETUP_LOADED) {
			notification.setStateDetails(Application.getProjectFocused().getSimulationSetupCurrent());
		}
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
			System.err.println("[" + this.getClass().getSimpleName() + "] Could not get AwbCommand from Json string: " + jsonString);
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
	public void setAwbState(AwbState awbState) {
		super.setAwbState(awbState);
		this.sendAwbStateNotification(this.getJsonAwbState(awbState));
	}
	
	/**
	 * Translates an {@link AwbState} from the remote control base bundle to an 
	 * {@link de.enflexit.awbRemote.jsonCommand.AwbNotification.AwbState} from 
	 * the generated JSON classes.
	 * @param awbState the awb state
	 * @return the json awb state
	 */
	private AwbNotification.AwbState getJsonAwbState(AwbState awbState){
		switch (awbState) {
		case AWB_READY:
			return AwbNotification.AwbState.AWB_READY;
		case PROJECT_LOADED:
			return AwbNotification.AwbState.PROJECT_LOADED;
		case SETUP_READY:
			return AwbNotification.AwbState.SETUP_LOADED;
		case MAS_STARTED:
			return AwbNotification.AwbState.JADE_STARTED;
		case SIMULATION_STEP_DONE:
			return AwbNotification.AwbState.READY_FOR_NEXT_STEP;
		case SIMULATION_FINISHED:
			return AwbNotification.AwbState.SIMULATION_FINISHED;
		case MAS_STOPPED:
			return AwbNotification.AwbState.JADE_STOPPED;
		default:
			return null;
		}
	}
	
	/**
	 * Gets the {@link DiscreteSimulationStepConcroller} for this remote control instance.
	 * @return the step controller
	 */
	private DiscreteSimulationStepConcroller getStepController() {
		if (stepController==null) {
			stepController = new DiscreteSimulationStepConcroller();
		}
		return stepController;
	}
	
	private String getCommandTopic() {
		if (commandTopic==null) {
			String topicFromProperties = null;
			Project projectFocused = Application.getProjectFocused();
			if (projectFocused!=null) {
				Properties projectProperties = Application.getProjectFocused().getProperties();
				topicFromProperties = projectProperties.getStringValue(PROPERTY_KEY_COMMAND_TOPIC);
			}
			commandTopic = (topicFromProperties!=null) ? topicFromProperties : DEFAULT_TOPIC_REMOTE_COMMANDS;
		}
		return commandTopic;
	}
	
	private String getStatusTopic() {
		if (statusTopic==null) {
			String topicFromProperties = null;
			Project projectFocused = Application.getProjectFocused();
			if (projectFocused!=null) {
				Properties projectProperties = Application.getProjectFocused().getProperties();
				topicFromProperties = projectProperties.getStringValue(PROPERTY_KEY_STATUS_TOPIC);
			}
			statusTopic = (topicFromProperties!=null) ? topicFromProperties : DEFAULT_TOPIC_STATUS_UPDATES;
		}
		return statusTopic;
	}

	private boolean isControlSteps() {
		if (controlSteps==null) {
			Boolean boolFromProperties = null;
			Project projectFocused = Application.getProjectFocused();
			if (projectFocused!=null) {
				Properties projectProperties = Application.getProjectFocused().getProperties();
				boolFromProperties = projectProperties.getBooleanValue(PROPERTY_KEY_CONTROL_STEPS);
			}
			controlSteps = (boolFromProperties!=null) ? boolFromProperties : DEFAULT_CONTROL_STEPS; 
		}
		return controlSteps;
	}


	private String getBrokerHost() {
		if (brokerHost==null) {
			String hostFromProperties  = null;
			Project projectFocused = Application.getProjectFocused();
			if (projectFocused!=null) {
				Properties projectProperties = Application.getProjectFocused().getProperties();
				hostFromProperties = projectProperties.getStringValue(PROPERTY_KEY_BROKER_HOST);
			}
			brokerHost = (hostFromProperties!=null) ? hostFromProperties : DEFAULT_BROKER_HOST;
		}
		return brokerHost;
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.awb.remoteControl.AwbRemoteControl#onApplicationEvent(agentgui.core.application.ApplicationListener.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(ApplicationEvent ae) {
		if (ae.getApplicationEvent()==ApplicationEvent.PROJECT_FOCUSED) {
			this.applyRemoteControlSettingsFromProject();
		}
		super.onApplicationEvent(ae);
	}
	
	private void applyRemoteControlSettingsFromProject() {
		boolean changed = false;
		
		// --- Check if remote control settings are specified in the project properties -----------
		Project projectFocused = Application.getProjectFocused();
		if (projectFocused!=null) {
			Properties projectProperties = Application.getProjectFocused().getProperties();
			String brokerHostFromProperties = projectProperties.getStringValue(PROPERTY_KEY_BROKER_HOST);
			if (brokerHostFromProperties!=null) {
				this.brokerHost = brokerHostFromProperties;
				changed = true;
			}
			String commandsTopicFromProperties = projectProperties.getStringValue(PROPERTY_KEY_COMMAND_TOPIC);
			if (commandsTopicFromProperties!=null) {
				this.commandTopic = commandsTopicFromProperties;
				changed = true;
			}
			String statusTopicFromProperties = projectProperties.getStringValue(PROPERTY_KEY_STATUS_TOPIC);
			if (statusTopicFromProperties!=null) {
				this.statusTopic = statusTopicFromProperties;
				changed = true;
			}
			Boolean controlStepsFromProperties = projectProperties.getBooleanValue(PROPERTY_KEY_CONTROL_STEPS);
			if (controlStepsFromProperties!=null) {
				this.controlSteps = controlStepsFromProperties;
				changed = true;
			}
			
			// --- If project settings have been applied, dispose the old connector instance
			if (changed==true) {
				if (this.getMqttConnector().isConnected()==true) {
					this.getMqttConnector().disconnect();
				}
				this.mqttConnector = null;
			}
		}
	}

	/**
	 * Inner class for handling discrete simulation steps.
	 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
	 */
	private class DiscreteSimulationStepConcroller extends AbstractDiscreteSimulationStepController{
	
		/* (non-Javadoc)
		 * @see agentgui.simulationService.transaction.AbstractDiscreteSimulationStepController#onSimulationStepDone()
		 */
		@Override
		public void onSimulationStepDone() {
			AwbRemoteControlMQTT.this.sendAwbStateNotification(AwbNotification.AwbState.READY_FOR_NEXT_STEP);
		}
	
		/* (non-Javadoc)
		 * @see agentgui.simulationService.transaction.AbstractDiscreteSimulationStepController#waitForNextSimulationStepInvocation()
		 */
		@Override
		public boolean waitForNextSimulationStepInvocation() {
			return true;
		}
		
	}
	
		
}
