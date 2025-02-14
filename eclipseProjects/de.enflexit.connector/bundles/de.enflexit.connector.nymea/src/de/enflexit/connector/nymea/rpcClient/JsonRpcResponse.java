package de.enflexit.connector.nymea.rpcClient;

import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This class implements a JSON RPC response as used  by Nymea/Consolinno, which 
 * diverges from the official JSON RPC 2.0 specification in a number of points.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class JsonRpcResponse {
	
	private static final String STATUS_SUCCESS = "success";
	private static final String STATUS_ERROR = "error";
	
	private int id;
	private HashMap<String, Object> params;
	private String status;
	
	/**
	 * Gets the ID of the request this response refers to.
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Sets the ID of the request this response refers to.
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Gets the parameters list, which contains the actual response data.
	 * @return the parameters list
	 */
	public HashMap<String, Object> getParams() {
		return params;
	}
	
	/**
	 * Sets the parameters list, which contains the actual response data.
	 * @param params the parameters list
	 */
	public void setParams(HashMap<String, Object> params) {
		this.params = params;
	}
	
	/**
	 * Gets the status, indicating if the method was executed successfully.
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * Sets the status.
	 * @param status the new status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * Gets the params not null.
	 * @return the params not null
	 */
	private HashMap<String, Object> getParamsNotNull(){
		if (params==null) {
			params = new HashMap<>();
		}
		return params;
	}
	
	/**
	 * Adds a parameter to the list-.
	 * @param name the parameter name
	 * @param value the parameter value
	 */
	public void addParameter(String name, String value) {
		this.getParamsNotNull().put(name, value);
	}
	
	/**
	 * Gets the parameter with the provided name from the list.
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
		return this.toJsonString(false);
	}
	
	/**
	 * Gets a JSON string representing this {@link JsonRpcRequest}. Optionally uses pretty printing.
	 * @param prettyPrint specifies if pretty printing should be enabled.
	 * @return the JSON string
	 */
	public String toJsonString(boolean prettyPrint) {
		Gson gson;
		if (prettyPrint==true) {
			gson  = new GsonBuilder().setPrettyPrinting().create();
		} else {
			gson = new Gson();
		}
		return gson.toJson(this);
	}
	
	/**
	 * Creates a new {@link JsonRpcRequest} from the provided JSON string.
	 * @param jsonString the JSON string
	 * @return the json rpc request
	 */
	public static JsonRpcResponse fromJsonString(String jsonString) {
		Gson gson = new Gson();
		return gson.fromJson(jsonString, JsonRpcResponse.class);
	}
	
	public boolean isSuccess() {
		return this.getStatus().equals(STATUS_SUCCESS);
	}
	
	public boolean isError() {
		return this.getStatus().equals(STATUS_ERROR);
	}
	
}
