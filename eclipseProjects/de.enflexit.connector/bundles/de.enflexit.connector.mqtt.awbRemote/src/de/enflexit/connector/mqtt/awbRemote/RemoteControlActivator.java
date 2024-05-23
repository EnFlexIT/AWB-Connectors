package de.enflexit.connector.mqtt.awbRemote;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * This activator class starts and stops the MQTT remote control.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class RemoteControlActivator implements BundleActivator {
	
	AwbRemoteControlMQTT remoteControl;

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		
		System.out.println("MQTT remote control bundle started!");
		
		if (this.getRemoteControl().doConnectorCheck()==true) {
			this.getRemoteControl().subscribeForCommands();
			System.out.println("[" + this.getClass().getSimpleName() + "] MQTT connector available, listenning for commands.");
			this.getRemoteControl().sendReadyStatus();
			
		} else {
			System.out.println("[" + this.getClass().getSimpleName() + "] MQTT connector not available!");
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
