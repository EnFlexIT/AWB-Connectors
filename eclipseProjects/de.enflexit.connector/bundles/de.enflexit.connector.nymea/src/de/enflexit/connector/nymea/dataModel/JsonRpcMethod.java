package de.enflexit.connector.nymea.dataModel;

import java.util.ArrayList;
import java.util.Map;

public class JsonRpcMethod {
	
	private static final String INTROSPECTION_DATA_KEY_DESCRIPTION = "description";
	private static final String INTROSPECTION_DATA_KEY_PARAMETERS = "params";
	private static final String INTROSPECTION_DATA_KEY_PERMISSION_SCOPE = "permissionScope";
	
	private String name;
	private String description;
	private PermissionScopes permissionScope;
	private ArrayList<Parameter> parameters;
	
	public JsonRpcMethod(String name, Map<?,?> introspectionData) {
		this.name = name;
		this.description = (String) introspectionData.get(INTROSPECTION_DATA_KEY_DESCRIPTION);
		this.permissionScope = PermissionScopes.valueOf((String) introspectionData.get(INTROSPECTION_DATA_KEY_PERMISSION_SCOPE));
		this.parameters = this.buildParametersList(introspectionData.get(INTROSPECTION_DATA_KEY_PARAMETERS));
	}
	
	private ArrayList<Parameter> buildParametersList(Object paramsData){
		
		this.parameters = new ArrayList<>();
		
		Map<?,?> paramsMap = (Map<?, ?>) paramsData;
		for (Object key : paramsMap.keySet()) {
			String paramName = (String) key;
			Object paramType = paramsMap.get(key);
			
			String paramTypeString = null;
			if (paramType instanceof String) {
				paramTypeString = (String) paramType;
			} else if (paramType instanceof ArrayList<?>) {
				ArrayList<?> typesList = (ArrayList<?>) paramType;
				if (typesList.size()==1) {
					try {
						paramTypeString = (String) typesList.get(0);
					} catch (ClassCastException cce) {
						System.err.println("Unexpected parameter type " + typesList.get(0).getClass().getSimpleName() + " for parameter " + paramName);
					}
				} else {
					System.err.println("Multiple possible types for parameter " + paramName + ":");
					for (Object possibleType : typesList) {
						System.err.println("- " + possibleType);
					}
				}
			}
			
			try {
			} catch (ClassCastException cce) {
				System.err.println("Cannot cast type info for " + key + " from " + paramType.getClass().getSimpleName() + " to String!");
			}
			this.parameters.add(new Parameter(paramName, paramTypeString));
		}
		
		return parameters;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public PermissionScopes getPermissionScope() {
		return permissionScope;
	}

	public void setPermissionScope(PermissionScopes permissionScope) {
		this.permissionScope = permissionScope;
	}

	public ArrayList<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<Parameter> parameters) {
		this.parameters = parameters;
	}

	public class Parameter {
		
		// --- This prefix to the parameter name marks an optional parameter
		private static final String PARAM_PREFIX_OPTIONAL = "o:";
		
		private String name;
		private String type;
		private boolean required;
		
		public Parameter(String paramName, String paramType) {
			if (paramName.startsWith(PARAM_PREFIX_OPTIONAL)) {
				this.required = false;
				this.name = paramName.substring(PARAM_PREFIX_OPTIONAL.length());
			} else {
				this.name = paramName;
				this.required = true;
			}
			this.type = paramType;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public boolean isRequired() {
			return required;
		}
		public void setRequired(boolean isRequired) {
			this.required = isRequired;
		}
		
	}
}
