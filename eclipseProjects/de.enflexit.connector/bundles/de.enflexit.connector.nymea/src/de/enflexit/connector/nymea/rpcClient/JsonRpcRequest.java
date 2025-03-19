package de.enflexit.connector.nymea.rpcClient;

import java.util.HashMap;

import com.google.gson.Gson;

/**
 * This class implements a JSON RPC request as used  by Nymea/Consolinno, which slightly diverges from the 
 * official JSON RPC 2.0 specification by adding a token field for identifying/authenticating loged-in users.
 *  
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class JsonRpcRequest {
	
	private String jsonrpc;
	private int id;
	private String method;
	private String token;
	
	private HashMap<String, Object> params;
	
	/**
	 * Gets the JSON RPC version.
	 * @return the JSON RPC version
	 */
	public String getJsonrpc() {
		return jsonrpc;
	}
	
	/**
	 * Sets the JSON RPC version.
	 * @param jsonrpc the new JSON RPC version
	 */
	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}
	
	/**
	 * Gets the id of the request.
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Sets the id of the request.
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Gets the method to be invoked.
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}
	
	/**
	 * Sets the method to be invoked.
	 * @param method the new method
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	
	/**
	 * Gets the authentication token.
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	
	/**
	 * Sets the authentication token.
	 * @param token the new token
	 */
	public void setToken(String token) {
		this.token = token;
	}
	
	/**
	 * Gets the list of parameters for the JSON RPC method call.
	 * @return the parameters 
	 */
	public HashMap<String, Object> getParams() {
		return params;
	}
	
	/**
	 * Sets the list of parameters for the JSON RPC method call.
	 * @param params the parameters
	 */
	public void setParams(HashMap<String, Object> params) {
		this.params = params;
	}
	
	/**
	 * Gets the parameters list, creates an empty instance if null.
	 * @return the parameters list
	 */
	private HashMap<String, Object> getParamsNotNull(){
		if (params==null) {
			params = new HashMap<>();
		}
		return params;
	}
	
	/**
	 * Adds a parameter to the list.
	 * @param name the parameter name
	 * @param value the parameter value
	 */
	public void addParameter(String name, String value) {
		this.getParamsNotNull().put(name, value);
	}
	
	/**
	 * Gets the specified parameter from the list.
	 * @param name the parameter name
	 * @return the parameter value, null if not found
	 */
	public Object getParameter(String name) {
		return this.getParamsNotNull().get(name);
	}
	
	/**
	 * Gets a JSON string representing this {@link JsonRpcRequest}.
	 * @return the JSON string
	 */
	public String toJsonString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	/**
	 * Creates a new {@link JsonRpcRequest} from the provided JSON string.
	 * @param jsonString the JSON string
	 * @return the json rpc request
	 */
	public static JsonRpcRequest fromJsonString(String jsonString) {
		Gson gson = new Gson();
		return gson.fromJson(jsonString, JsonRpcRequest.class);
	}
	
}
