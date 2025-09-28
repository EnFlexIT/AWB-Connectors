package de.enflexit.connector.nymea.rpcClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.google.gson.Gson;

import de.enflexit.connector.nymea.NymeaConnectorSettings;
import de.enflexit.connector.nymea.dataModel.PowerLogEntry;
import de.enflexit.connector.nymea.dataModel.SampleRate;
import de.enflexit.connector.nymea.dataModel.StateType;

/**
 * This class implements a JSON RPC client, as required for the communication with a Nymea/Consolinno HEMS.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class NymeaRpcClient {
	
	private static final String JSON_RPC_VERISON = "2.0";
	
	private static final String RPC_METHOD_REGISTER_CLIENT = "TunnelProxy.RegisterClient";
	private static final String RPC_METHOD_HANDSHAKE = "JSONRPC.Hello";
	private static final String RPC_METHOD_AUTHENTICATE = "JSONRPC.Authenticate";
	private static final String RPC_METHOD_INTROSPECT = "JSONRPC.Introspect";
	private static final String RPC_METHOD_POWER_BALANCE = "Energy.GetPowerBalance";
	private static final String RPC_METHOD_POWER_LOGS = "Energy.GetPowerBalanceLogs";
	private static final String RPC_METHOD_POWER_LOGS_FOR_THING = "Energy.GetThingPowerLogs";
	private static final String RPC_METHOD_GET_THINGS = "Integrations.GetThings";
	private static final String RPC_METHOD_GET_THING_CLASSES = "Integrations.GetThingClasses";
	private static final String RPC_METHOD_GET_STATE_TYPES = "Integrations.GetStateTypes";
	
	private boolean debug = false;
	
	private NymeaConnectorSettings connectionSettings;
	private String authToken;
	
	private SSLSocket sslSocket;
	
	private PrintWriter outputStreamWriter;
	private BufferedReader inputStreamReader;
	
	private int nextRequestID = 0;
	
	public NymeaRpcClient(NymeaConnectorSettings connectionSettings) {
		this.connectionSettings = connectionSettings;
	}
	
	/**
	 * Open the connection to the nymea HEMS.
	 * @return true, if successful
	 */
	public boolean openConnection() {
		//TODO Check if all necessary infos are available
		
		if (this.isConnected()==false) {
			return (this.sendClientRegistration() && this.sendRpcHandshake());
		}
		
		return this.isConnected();
		
	}
	
	/**
	 * Disconnect.
	 */
	public void closeConnection() {
		try {
			this.getOutputStreamWriter().close();
			this.getInputStreamReader().close();
			this.getSslSocket().close();
			
			this.outputStreamWriter = null;
			this.inputStreamReader = null;
			this.sslSocket = null;
			
		} catch (IOException e) {
			System.err.println("[" + this.getClass().getSimpleName() + "] Error clossing the TLS connection!");
			e.printStackTrace();
		}
	}

	/**
	 * Gets the connection settings.
	 * @return the connection settings
	 */
	private NymeaConnectorSettings getConnectionSettings() {
		if (connectionSettings==null) {
			connectionSettings = new NymeaConnectorSettings(null);
		}
		return connectionSettings;
	}

	/**
	 * Gets the ssl socket.
	 * @return the ssl socket
	 * @throws UnknownHostException the unknown host exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private SSLSocket getSslSocket() throws UnknownHostException, IOException {
		if (sslSocket==null) {
			SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			sslSocket = (SSLSocket) socketFactory.createSocket(this.connectionSettings.getServerHost(), this.connectionSettings.getServerPort());
		}
		return sslSocket;
	}

	/**
	 * Gets the output stream writer.
	 * @return the output stream writer
	 * @throws UnknownHostException the unknown host exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private PrintWriter getOutputStreamWriter() throws UnknownHostException, IOException {
		if (outputStreamWriter==null) {
			outputStreamWriter = new PrintWriter(this.getSslSocket().getOutputStream());
		}
		return outputStreamWriter;
	}

	/**
	 * Gets the input stream reader.
	 * @return the input stream reader
	 * @throws UnknownHostException the unknown host exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private BufferedReader getInputStreamReader() throws UnknownHostException, IOException {
		if (inputStreamReader==null) {
			inputStreamReader = new BufferedReader(new InputStreamReader(this.getSslSocket().getInputStream()));
		}
		return inputStreamReader;
	}

	/**
	 * Sends the provided {@link JsonRpcRequest} to the server.
	 * @param request the request
	 * @return the json rpc response
	 */
	private JsonRpcResponse sendRequest(JsonRpcRequest request) {
		
		JsonRpcResponse response = null;
		try {
			String requestString = request.toJsonString();
			String responseString = this.sendRequest(requestString);
			
			if (responseString!=null && responseString.isBlank()==false) {
				response = JsonRpcResponse.fromJsonString(responseString);
			} else {
				System.err.println("[" + this.getClass().getSimpleName() + "] Received empty response!");
			}
			
		} catch (IOException e) {
			System.err.println("Communication with the server failed!");
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * Sends the actual JSON RPC String to the server, and waits for the response.
	 * @param request the request
	 * @return the string
	 * @throws UnknownHostException the unknown host exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String sendRequest(String request) throws UnknownHostException, IOException {
		
		this.debugPrint("Sending request " + request);
		
		this.getOutputStreamWriter().print(request);
		this.getOutputStreamWriter().flush();
		try {
			String response = this.getInputStreamReader().readLine();
			this.debugPrint("Received response " + response);
			return response;
		} catch (SocketTimeoutException ste) {
			System.err.println("Communication timeout!");
			return null;
		}
	}

	/**
	 * Gets the next request ID.
	 * @return the next request ID
	 */
	private int getNextRequestID() {
		return nextRequestID++;
	}

	/**
	 * Prepares a new {@link JsonRpcRequest} request, sets version string, request ID and authentication token.
	 * @return the json rpc request
	 */
	private JsonRpcRequest prepareRequest() {
		return this.prepareRequest(true);
	}

	/**
	 * Prepares a new {@link JsonRpcRequest} request, sets version string and request ID.
	 * The authentication token is set
	 * @param requiresAuthToken the requires auth token
	 * @return the json rpc request
	 */
	private JsonRpcRequest prepareRequest(boolean requiresAuthToken) {
		JsonRpcRequest requestStub = new JsonRpcRequest();
		requestStub.setJsonrpc(JSON_RPC_VERISON);
		requestStub.setId(this.getNextRequestID());
		if (requiresAuthToken==true) {
			requestStub.setToken(this.authToken);
		}
		return requestStub;
	}

	/**
	 * Sends an RPC call to register the client.
	 * @return true, if successful
	 */
	private boolean sendClientRegistration() {
			
		JsonRpcRequest registerRequest = this.prepareRequest();
		registerRequest.setMethod(RPC_METHOD_REGISTER_CLIENT);
		registerRequest.addParameter(RpcParams.CLIENT_NAME.getName(), this.getConnectionSettings().getClientName());
		registerRequest.addParameter(RpcParams.CLIENT_UUID.getName(), this.getConnectionSettings().getClientUUID());
		registerRequest.addParameter(RpcParams.SERVER_UUID.getName(), this.getConnectionSettings().getServerUUID());
		
		JsonRpcResponse registerResponse = this.sendRequest(registerRequest);
		if (registerResponse!=null) {
			return registerResponse.getStatus().equals("success");
		}
			
		return false;
	}
	
	/**
	 * Sends the RPC handshake request.
	 * @return true, if successful
	 */
	private boolean sendRpcHandshake() {
		JsonRpcRequest handshakeRequest = this.prepareRequest();
		handshakeRequest.setMethod(RPC_METHOD_HANDSHAKE);
		
		JsonRpcResponse handshakeResponse = this.sendRequest(handshakeRequest);
		if (handshakeResponse!=null) {
			return handshakeResponse.getStatus().equals("success");
		}
		return false;
	}
	
	/**
	 * Sends a user authentication request.
	 * @return the authentication token if successful, null if not
	 */
	public boolean authenticateUser() {
		JsonRpcRequest authenticationRequest = this.prepareRequest();
		authenticationRequest.setMethod(RPC_METHOD_AUTHENTICATE);
		authenticationRequest.addParameter(RpcParams.USER_NAME.getName(), this.getConnectionSettings().getUserName());
		authenticationRequest.addParameter(RpcParams.PASSWORD.getName(), this.getConnectionSettings().getPassword());
		authenticationRequest.addParameter(RpcParams.DEVICE_NAME.getName(), this.getConnectionSettings().getClientName());
		
		JsonRpcResponse authenticationResponse = this.sendRequest(authenticationRequest);
		if (authenticationResponse!=null && authenticationResponse.isSuccess()==true) {
			Object successParam = authenticationResponse.getParameter("success");
			if (successParam!=null && successParam instanceof Boolean && ((Boolean)successParam)==true) {
				Object tokenParam = authenticationResponse.getParameter("token");
				if (tokenParam!=null && tokenParam instanceof String) {
					this.authToken = (String) tokenParam;
				}
				
			} else {
				System.err.println("[" + this.getClass().getSimpleName() + "] Authentication failed!");
			}
		}
			
		return (this.authToken!=null);
	}

	/**
	 * Gets the current power balance.
	 * @return the power balance
	 */
	public boolean getPowerBalance() {
		JsonRpcRequest powerBalanceRequest = this.prepareRequest();
		powerBalanceRequest.setMethod(RPC_METHOD_POWER_BALANCE);
		
		JsonRpcResponse powerBalanceResponse = this.sendRequest(powerBalanceRequest);
		if (powerBalanceResponse!=null) {
			return powerBalanceResponse.isSuccess();
		}
		return false;
	}
	
	/**
	 * Gets the power balance logs for the specified time.
	 * @param from the start time (java timestamp in milliseconds) 
	 * @param to the end time (java timestamp in milliseconds)
	 * @param sampleRate the sample rate
	 * @return the power balance logs
	 */
	public boolean getPowerBalanceLogs(long from, long to, SampleRate sampleRate) {
		
		// --- The backend expects timestamps in seconds ----------------------
		long fromSeconds = from/1000;
		long toSeconds = to/1000;
		
		JsonRpcRequest powerLogsRequest = this.prepareRequest();
		powerLogsRequest.setMethod(RPC_METHOD_POWER_LOGS);
		powerLogsRequest.addParameter(RpcParams.FROM.getName(), String.valueOf(fromSeconds));
		powerLogsRequest.addParameter(RpcParams.TO.getName(), String.valueOf(toSeconds));
		powerLogsRequest.addParameter(RpcParams.SAMPLE_RATE.getName(), sampleRate.getValue());
		
		JsonRpcResponse powerLogsResponse = this.sendRequest(powerLogsRequest);
		
		if (powerLogsResponse!=null) {
			return powerLogsResponse.isSuccess();
		}
		return false;
		
	}
	
	/**
	 * Gets the power log entries for one specific thing for a time range.   
	 * @param thingID the thing ID
	 * @param from the beginning of the time range
	 * @param to the end of the time range
	 * @param sampleRate the sample rate 
	 * @return the resulting power log entries
	 */
	public ArrayList<PowerLogEntry> getThingPowerLogs(String thingID, long from, long to, SampleRate sampleRate) {
		
		// --- Nymea expects time stamps in seconds -------
		long fromSeconds = from/1000;
		long toSeconds = to/1000;
		
		// --- Thing IDs must be provided as list --------- 
		ArrayList<String> thingIDsList = new ArrayList<String>();
		thingIDsList.add(thingID);
		
		// --- Prepare the request ------------------------
		JsonRpcRequest powerLogsRequest = this.prepareRequest();
		powerLogsRequest.setMethod(RPC_METHOD_POWER_LOGS_FOR_THING);
		powerLogsRequest.addParameter(RpcParams.FROM.getName(), String.valueOf(fromSeconds));
		powerLogsRequest.addParameter(RpcParams.TO.getName(), String.valueOf(toSeconds));
		powerLogsRequest.addParameter(RpcParams.SAMPLE_RATE.getName(), sampleRate.getValue());
		powerLogsRequest.addListParameter(RpcParams.THING_IDS.getName(), thingIDsList);

		// --- Send the request ---------------------------
		JsonRpcResponse powerLogsResponse = this.sendRequest(powerLogsRequest);
		if (powerLogsResponse!=null && powerLogsResponse.isSuccess()==true) {
			
			// --- Extract the actual payload -------------
			@SuppressWarnings("unchecked")
			ArrayList<Object> entries = (ArrayList<Object>) powerLogsResponse.getParameter("thingPowerLogEntries");
			ArrayList<PowerLogEntry> powerLogEntries = new ArrayList<PowerLogEntry>();
			
			// --- Convert the generic elements back to json first, to parse to the correct class then.
			Gson gson = new Gson();
			for (Object entry : entries) {
				String plsJson = gson.toJson(entry);
				PowerLogEntry ple = gson.fromJson(plsJson, PowerLogEntry.class);
				powerLogEntries.add(ple);
			}
			
			return powerLogEntries;
		}
		return null;
	}
	
	/**
	 * Sends an introspection request to the server.
	 * @return the hash map
	 */
	public HashMap<String, Object> sendIntrospectionRequest(){
		JsonRpcRequest introRequest = this.prepareRequest();
		introRequest.setMethod(RPC_METHOD_INTROSPECT);
		
		JsonRpcResponse introResponse = this.sendRequest(introRequest);
		
		if (introResponse!=null && introResponse.isSuccess()) {
			return introResponse.getParams();
		} else {
			System.err.println("[" + this.getClass().getSimpleName() + "] Introspection request failed!");
			return null;
		}
	}
	
	/**
	 * Gets the available things from the HEMS system.
	 * @return the available things
	 */
	public ArrayList<?> getAvailableThings(){
		JsonRpcRequest thingsRequest = this.prepareRequest();
		thingsRequest.setMethod(RPC_METHOD_GET_THINGS);
		
		JsonRpcResponse thingsResponse = this.sendRequest(thingsRequest);
		
		if (thingsResponse!=null && thingsResponse.isSuccess()) {
			return (ArrayList<?>) thingsResponse.getParameter("things");
		} else {
			System.err.println("[" + this.getClass().getSimpleName() + "] Introspection request failed!");
			return null;
		}
	}
	
	public ArrayList<?> getThingClasses(){
		JsonRpcRequest classesRequest = this.prepareRequest();
		classesRequest.setMethod(RPC_METHOD_GET_THING_CLASSES);
		
		JsonRpcResponse classesResponse = this.sendRequest(classesRequest);
		
		if (classesResponse!=null && classesResponse.isSuccess()) {
			return (ArrayList<?>) classesResponse.getParameter("thingClasses");
		} else {
			System.err.println("[" + this.getClass().getSimpleName() + "] Introspection request failed!");
			return null;
		}
	}
	
	public ArrayList<StateType> getStateTypes(String thingClassID){
		JsonRpcRequest stateTypeRequest = this.prepareRequest();
		stateTypeRequest.setMethod(RPC_METHOD_GET_STATE_TYPES);
		stateTypeRequest.addParameter("thingClassId", thingClassID);
		
		JsonRpcResponse stateTypesResponse = this.sendRequest(stateTypeRequest);
		
		if (stateTypesResponse!=null && stateTypesResponse.isSuccess()) {
			
			ArrayList<?> resultList = (ArrayList<?>)stateTypesResponse.getParameter("stateTypes");
			
			ArrayList<StateType> stateTypes = new ArrayList<StateType>();
			for (Object stateType : resultList) {
				Map<?,?> gsonMap = (Map<?, ?>) stateType;
				stateTypes.add(StateType.fromGsonMap(gsonMap));
			}
			return stateTypes;
		} else {
			System.err.println("[" + this.getClass().getSimpleName() + "] Introspection request failed!");
			return null;
		}
	}
	
	
	/**
	 * Helper method to print debug messages.
	 * @param message the message
	 */
	private void debugPrint(String message) {
		if (debug==true) {
			System.out.println("[" + this.getClass().getSimpleName() + "] " + message);
		}
	}
	
	/**
	 * Checks if the connection was established.
	 * @return true, if is connected
	 */
	public boolean isConnected() {
		return (this.sslSocket!=null && this.sslSocket.isClosed()==true && this.sslSocket.isClosed()==false);
	}
	
	/**
	 * Checks if the connection is authenticated.
	 * @return true, if is authenticated
	 */
	public boolean isAuthenticated() {
		return (this.authToken!=null);
	}
	
	public JsonRpcResponse executeRpcMethod(String method, HashMap<String, String> parameters) {
		JsonRpcRequest request = this.prepareRequest();
		request.setMethod(method);
		for (String paramName : parameters.keySet()) {
			request.addParameter(paramName, parameters.get(paramName));
		}
		
		JsonRpcResponse response = this.sendRequest(request);
		return response;
	}
	
}
