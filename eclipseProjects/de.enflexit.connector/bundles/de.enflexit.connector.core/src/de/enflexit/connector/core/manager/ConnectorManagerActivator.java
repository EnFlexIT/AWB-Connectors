package de.enflexit.connector.core.manager;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import agentgui.core.application.Application;
import agentgui.core.application.ApplicationListener;
import de.enflexit.connector.core.AbstractConnectorProperties.StartOn;
import de.enflexit.connector.core.ConnectorService;
import de.enflexit.connector.core.ConnectorServiceTracker;

/**
 * This {@link BundleActivator} does some initial tasks for the {@link ConnectorManager}.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorManagerActivator implements BundleActivator, ApplicationListener {
	
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		Application.addApplicationListener(this);
		
		ConnectorServiceTracker serviceTracker = new ConnectorServiceTracker(context, ConnectorService.class, null);
		serviceTracker.open();
		
		ConnectorManager.getInstance().startConnectionsWithStartLevel(StartOn.AwbStart);
	}
	
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		Application.removeApplicationListener(this);
	}
	
	/* (non-Javadoc)
	 * @see agentgui.core.application.ApplicationListener#onApplicationEvent(agentgui.core.application.ApplicationListener.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(ApplicationEvent ae) {
		switch(ae.getApplicationEvent()) {
		case ApplicationEvent.AWB_START:
			ConnectorManager.getInstance().startConnectionsWithStartLevel(StartOn.AwbStart);
			break;
		case ApplicationEvent.AWB_STOP:
			break;
		case ApplicationEvent.JADE_START:
			ConnectorManager.getInstance().startConnectionsWithStartLevel(StartOn.JadeStartup);
			break;
		case ApplicationEvent.PROJECT_LOADED:
			ConnectorManager.getInstance().startConnectionsWithStartLevel(StartOn.ProjectLoaded);
			break;
		case ApplicationEvent.PROJECT_CLOSED:
			break;
		default:
			// --- Nothing to do here
		}
	}

}
