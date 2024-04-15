package de.enflexit.connector.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import agentgui.core.application.Application;
import agentgui.core.application.ApplicationListener;
import de.enflexit.connector.core.AbstractConnectorProperties.StartOn;
import de.enflexit.connector.core.manager.ConnectorManager;

public class ConnectorManagerActivator implements BundleActivator, ApplicationListener {

	@Override
	public void start(BundleContext context) throws Exception {
		Application.addApplicationListener(this);
	}
	@Override
	public void stop(BundleContext context) throws Exception {
		Application.removeApplicationListener(this);
	}
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
