package de.enflexit.connector.mqtt.awbRemote;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
import de.enflexit.connector.core.AbstractConnector;
import de.enflexit.connector.core.manager.ConnectorManager;
import de.enflexit.connector.mqtt.MQTTConnector;
import de.enflexit.connector.mqtt.MQTTConnectorConfiguration;
import de.enflexit.connector.mqtt.MQTTMessageWrapper;
import de.enflexit.connector.mqtt.MQTTSubscriber;

/**
 * This implementation of {@link AwbRemoteControl} allows to control an AWB instance via MQTT. 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class AwbRemoteControlMQTT extends AwbRemoteControl implements MQTTSubscriber, PropertyChangeListener {
	
	public static final String PROPERTY_KEY_CONNECTOR_NAME = "mqtt.remoteControl.connectorName";
	public static final String PROPERTY_KEY_BROKER_HOST = "mqtt.remoteControl.brokerHost";
	public static final String PROPERTY_KEY_COMMAND_TOPIC = "mqtt.remoteControl.commandTopic";
	public static final String PROPERTY_KEY_STATUS_TOPIC = "mqtt.remoteControl.statusTopic";
	public static final String PROPERTY_KEY_CONTROL_STEPS = "mqtt.remoteControl.controlSteps";
	
	private static final String DEFAULT_TOPIC_REMOTE_COMMANDS = "awbControl";
	private static final String DEFAULT_TOPIC_STATUS_UPDATES = "awbStatus";
	private static final boolean DEFAULT_CONTROL_STEPS = true;

	private MQTTConnector mqttConnector;
	
	private DateTimeFormatter dateTimeFormatter;
	
	private Gson gson;
	
	private DiscreteSimulationStepConcroller stepController;
	
	private String statusTopic;
	private String commandTopic;
	private Boolean controlSteps;
	
	private boolean connectorCheckFailed;
	
	/**
	 * Instantiates a new AWB remote control MQTT.
	 */
	public AwbRemoteControlMQTT() {
		Application.addApplicationListener(this);
		ConnectorManager.getInstance().addListener(this);
		if (this.isControlSteps()==true) {
			this.getStepController().enable();	// Initialize the step controller
		}
	}

	/**
	 * Checks if the MQTT connector is configured and available
	 * @return true, if successful
	 */
	public boolean isConnectorAvailable() {
		if (this.getMqttConnector()==null) {
			this.connectorCheckFailed = true;
			System.err.println("[" + this.getClass().getSimpleName() + "] No MQTT connector available!");
			return false;
		} else {
			this.connectorCheckFailed = false;
			return true;
		}
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
			
			// --- If a specific connector is configured in the project properties, use that one --
			AbstractConnector connectorFromProperties = this.getConnectorFromProjectProperties(Application.getProjectFocused());
			if (connectorFromProperties!=null && connectorFromProperties instanceof MQTTConnector) {
				mqttConnector = (MQTTConnector) connectorFromProperties;
				
				
			// --- If not, use the first configured MQTT connector by default ---------------------
			} else {
				ArrayList<AbstractConnector> mqttConnectors = ConnectorManager.getInstance().getConnectorsByProtocol(MQTTConnectorConfiguration.PROTOCOL_NAME);
				if (mqttConnectors.size()>0 && mqttConnectors.get(0) instanceof MQTTConnector) {
					mqttConnector = (MQTTConnector) mqttConnectors.get(0);
				}
			}
			
		}
		
		if (mqttConnector != null && mqttConnector.isConnected()==false) {
			mqttConnector.connect();
		}
		
		return mqttConnector;
	}
	
	/**
	 * Gets the connector that is configured in the properties of the provided project. May return null if nothing is configured there.
	 * @param project the project
	 * @return the connector, null if not configured in the project properties.
	 */
	private MQTTConnector getConnectorFromProjectProperties(Project project) {
		if (project==null) return null;
		return this.getConnectorFromProperties(project.getProperties());
	}
	
	/**
	 * Gets the MQTT connector that is configured in the provided properties. May return null if nothing is configured there.
	 * @param properties the properties
	 * @return the connector, null if not configured in the properties.
	 */
	private MQTTConnector getConnectorFromProperties(Properties properties) {
		
		// --- If a connector name is configured in the properties, use the corresponding connector
		String connectorNameFromProperties = properties.getStringValue(PROPERTY_KEY_CONNECTOR_NAME);
		if (connectorNameFromProperties!=null && connectorNameFromProperties.isEmpty()==false) {
			AbstractConnector connector = ConnectorManager.getInstance().getConnectorByName(connectorNameFromProperties);
			if (connector!=null && connector instanceof MQTTConnector) {
				return (MQTTConnector) connector;
			}
		}
		
		// --- If a broker host is configured in the properties, use an MQTT connector from that host
		String brokerHostFromProperties = properties.getStringValue(PROPERTY_KEY_BROKER_HOST);
		if (brokerHostFromProperties!=null && brokerHostFromProperties.isEmpty()==false) {
			AbstractConnector connector = ConnectorManager.getInstance().getConnectorByHostAndProtocol(brokerHostFromProperties, MQTTConnectorConfiguration.PROTOCOL_NAME);
			if (connector!=null && connector instanceof MQTTConnector) {
				return (MQTTConnector) connector;
			}
		}
		
		// --- Nothing configured -------------------------
		return null;
	}
	
	/**
	 * Publishes a status update to the corresponding MQTT topic.
	 * @param statusUpdate the status update
	 */
	public void sendStatusUpdate(AwbNotification notification) {
		if (this.getMqttConnector()!=null && this.getMqttConnector().isConnected()==true) {
			String messageContent = this.getGson().toJson(notification);
			boolean retain = (notification.getAwbState()==AwbNotification.AwbState.AWB_READY || notification.getAwbState()==AwbNotification.AwbState.AWB_TERMINATED);
			this.getMqttConnector().publish(this.getStatusTopic(), messageContent, retain);
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
					boolean success = this.loadProject(projectFolder);
					
					if (success==false) {
						this.sendFailureUpdate("Failed to load project from " + projectFolder);
						System.err.println("[" + this.getClass().getSimpleName() + "] Failed to load project from " + projectFolder);
					}
					
				} else {
					this.sendFailureUpdate("Missing parameter - LoadProject needs a project folder to load from!");
					System.err.println("[" + this.getClass().getSimpleName() + "] Missing parameter - LoadProject needs a project folder to load from!");
				}
				break;
			case SELECT_SETUP:
				String setupName = command.getParameter();
				if (setupName!=null) {
					boolean success = this.selectSetup(setupName);
					if (success==false) {
						this.sendFailureUpdate("Failed to select simultation setup " + setupName);
						System.err.println("[" + this.getClass().getSimpleName() + "] Failed to select simulaiton setup " + setupName);
					}
				} else {
					this.sendFailureUpdate("Unable to select setup, no setup name passed!");
					System.err.println("[" + this.getClass().getSimpleName() + "] Unable to select setup, no setup name passed!");
				}
				break;
			case SET_TIME_CONFIGURATION:
				
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
				
				this.sendAwbStateNotification(AwbNotification.AwbState.TIME_CONFIGURATION_SET);
				
				break;
			case START_SIMULATION:
				this.startMultiAgentSystem();
				break;
			case NEXT_STEP:
				this.getStepController().stepSimulation();
				break;
			case STOP_SIMULATION:
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
	
	private void sendFailureUpdate(String failureMessage) {
		AwbNotification failureNotification = new AwbNotification();
		failureNotification.setAwbState(AwbNotification.AwbState.COMMAND_FAILED);
		failureNotification.setStateDetails(failureMessage);
		this.sendStatusUpdate(failureNotification);
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
			return AwbNotification.AwbState.SIMULATION_STARTED;
		case SIMULATION_STEP_DONE:
			return AwbNotification.AwbState.READY_FOR_NEXT_STEP;
		case SIMULATION_FINISHED:
			return AwbNotification.AwbState.SIMULATION_FINISHED;
		case MAS_STOPPED:
			return AwbNotification.AwbState.SIMULATION_STOPPED;
		case AWB_TERMINATED:
			return AwbNotification.AwbState.AWB_TERMINATED;
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
	
	/* (non-Javadoc)
	 * @see de.enflexit.awb.remoteControl.AwbRemoteControl#onApplicationEvent(agentgui.core.application.ApplicationListener.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(ApplicationEvent ae) {
		if (ae.getApplicationEvent()==ApplicationEvent.PROJECT_FOCUSED) {
			
			if (Application.getProjectFocused()!=null) {
				this.applyConfigurationFromProjectProperties(Application.getProjectFocused().getProperties());
			}
		}
		super.onApplicationEvent(ae);
	}
	
	private void applyConfigurationFromProjectProperties(Properties properties) {
		
		if (properties==null) return;
		
		String commandTopicFromProperties = properties.getStringValue(PROPERTY_KEY_COMMAND_TOPIC);
		if (commandTopicFromProperties!=null && commandTopicFromProperties.isBlank()==false) {
			if (commandTopicFromProperties.equals(this.commandTopic)==false) {
				this.getMqttConnector().unsubscribe(this.getCommandTopic(), this);
				this.commandTopic = commandTopicFromProperties;
				this.getMqttConnector().subscribe(this.getCommandTopic(), this);
			}
		}
		
		String statusTopicFromProperties = properties.getStringValue(PROPERTY_KEY_STATUS_TOPIC);
		if (statusTopicFromProperties!=null && statusTopicFromProperties.isBlank()==false) {
			this.statusTopic = statusTopicFromProperties;
		}
		
		Boolean controlStepsFromProperties = properties.getBooleanValue(PROPERTY_KEY_CONTROL_STEPS);
		if (controlStepsFromProperties!=null && controlStepsFromProperties!=this.controlSteps) {
			this.controlSteps = controlStepsFromProperties;
			if (this.controlSteps==true) {
				this.getStepController().enable();
			} else {
				this.getStepController().disable();
			}
		}
		
		MQTTConnector connectorFromProject = this.getConnectorFromProperties(properties);
		
		if (connectorFromProject!=null && connectorFromProject!=this.mqttConnector) {
			this.getMqttConnector().unsubscribe(this.getCommandTopic(), this);
			this.mqttConnector = connectorFromProject;
			this.getMqttConnector().subscribe(this.getCommandTopic(), this);
			this.setAwbState(AwbState.AWB_READY);
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

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent pce) {
		if (pce.getSource()==ConnectorManager.getInstance() && pce.getPropertyName().equals(ConnectorManager.CONNECTOR_ADDED)) {
			// --- If previously not connected and a new connector is added, try to subscribe -----
			if (this.connectorCheckFailed==true && this.isConnectorAvailable()==true) {
				this.subscribeForCommands();
				
				// --- Send status updates to the new connection --------------
				this.setAwbState(AwbState.AWB_READY);
				if (Application.getProjectFocused()!=null) {
					this.setAwbState(AwbState.PROJECT_LOADED);
					this.setAwbState(AwbState.SETUP_READY);
				}
			}
		}
	}
	
	

}
