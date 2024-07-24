package de.enflexit.connector.mqtt.awbRemote;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import agentgui.core.application.Application;
import de.enflexit.awbRemote.jsonCommand.AwbNotification.AwbState;

/**
 * This activator class starts and stops the MQTT remote control.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class MqttRemoteControlActivator implements BundleActivator {
	
	AwbRemoteControlMQTT remoteControl;

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {

		// --- Only activate the remote control when the current application is hosting the main container of a JADE platform.
		if (Application.isRemoteContainerApplication()==false) { 
			if (this.getRemoteControl().doConnectorCheck()==true) {
				this.getRemoteControl().subscribeForCommands();
				System.out.println("[" + this.getClass().getSimpleName() + "] MQTT connector available, listenning for commands.");
				this.getRemoteControl().sendAwbStateNotification(AwbState.AWB_READY);
				
			} else {
				System.out.println("[" + this.getClass().getSimpleName() + "] MQTT connector not available!");
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		this.getRemoteControl().unsubscribeFromCommands();
	}
	
	/**
	 * Gets the remote control.
	 * @return the remote control
	 */
	private AwbRemoteControlMQTT getRemoteControl() {
		if (remoteControl==null) {
			remoteControl = new AwbRemoteControlMQTT();
		}
		return remoteControl;
	}

}
