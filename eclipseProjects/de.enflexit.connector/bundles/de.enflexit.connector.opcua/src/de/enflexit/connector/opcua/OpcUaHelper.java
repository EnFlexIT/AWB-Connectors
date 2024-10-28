package de.enflexit.connector.opcua;

import java.util.List;

import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

/**
 * The Class OpcUaHelper.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class OpcUaHelper {

	private static final String DISCOVER_END_PATH = "discovery";
	
	/**
	 * Discover end point description.
	 *
	 * @param url the url to search for OPCUA end points
	 * @return the list of en points found or null
	 */
	public static List<EndpointDescription> discoverEndPointDescription(String url) {
		
		boolean isDebug = false;
		List<EndpointDescription> endpoints = null;
		try {
			endpoints = DiscoveryClient.getEndpoints(url).get();

		} catch (Throwable ex) {
			// --- Check for discovery URL ----------------
			if (url.endsWith(DISCOVER_END_PATH)==false) {
				// --- Create discovery end point URL -----
				String discoveryUrl = url;
				if (discoveryUrl.endsWith("/")==false) {
					discoveryUrl += "/";
				}
				discoveryUrl += "discovery";
				// --- Check the discovery URL ------------
				return discoverEndPointDescription(discoveryUrl);
			}
			if (isDebug==true) ex.printStackTrace();
		}
		return endpoints;
	}

	/**
	 * Return the identity provider name, derived from the specified class' name.
	 *
	 * @param identityProviderClass the identity provider class
	 * @return the identity provider name
	 */
	public static String getIdentityProviderName(Class<? extends IdentityProvider> identityProviderClass) {
		if (identityProviderClass==null) return null;
		return identityProviderClass.getSimpleName().replace("Provider", "");
	}
	
}
