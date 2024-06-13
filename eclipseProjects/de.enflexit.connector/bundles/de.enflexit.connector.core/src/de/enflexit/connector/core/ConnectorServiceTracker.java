package de.enflexit.connector.core;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import de.enflexit.connector.core.manager.ConnectorManager;


/**
 * {@link ServiceTracker} implementation to react on newly registered {@link ConnectorService}s.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorServiceTracker extends ServiceTracker<ConnectorService, ConnectorService>{

	/**
	 * Instantiates a new connector service tracker.
	 * @param context the context
	 * @param clazz the clazz
	 * @param customizer the customizer
	 */
	public ConnectorServiceTracker(BundleContext context, Class<ConnectorService> clazz, ServiceTrackerCustomizer<ConnectorService, ConnectorService> customizer) {
		super(context, clazz, customizer);
	}
	
	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTracker#addingService(org.osgi.framework.ServiceReference)
	 */
	@Override
	public ConnectorService addingService(ServiceReference<ConnectorService> reference) {
		ConnectorService connectorService = super.addingService(reference);
		ConnectorManager.getInstance().newConnectorServiceAdded(connectorService);
		return connectorService;
	}
	
	

}
