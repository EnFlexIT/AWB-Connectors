
package de.enflexit.awbRemote.jsonCommand;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.processing.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Parameter {

    /**
     * The parameter name, must be one from a set of known names.
     * 
     */
    @SerializedName("paramName")
    @Expose
    private Parameter.ParamName paramName;
    /**
     * The parameter value, must match what is expected for this parameter
     * 
     */
    @SerializedName("paramValue")
    @Expose
    private String paramValue;

    /**
     * The parameter name, must be one from a set of known names.
     * 
     */
    public Parameter.ParamName getParamName() {
        return paramName;
    }

    /**
     * The parameter name, must be one from a set of known names.
     * 
     */
    public void setParamName(Parameter.ParamName paramName) {
        this.paramName = paramName;
    }

    /**
     * The parameter value, must match what is expected for this parameter
     * 
     */
    public String getParamValue() {
        return paramValue;
    }

    /**
     * The parameter value, must match what is expected for this parameter
     * 
     */
    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Parameter.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("paramName");
        sb.append('=');
        sb.append(((this.paramName == null)?"<null>":this.paramName));
        sb.append(',');
        sb.append("paramValue");
        sb.append('=');
        sb.append(((this.paramValue == null)?"<null>":this.paramValue));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.paramName == null)? 0 :this.paramName.hashCode()));
        result = ((result* 31)+((this.paramValue == null)? 0 :this.paramValue.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Parameter) == false) {
            return false;
        }
        Parameter rhs = ((Parameter) other);
        return (((this.paramName == rhs.paramName)||((this.paramName!= null)&&this.paramName.equals(rhs.paramName)))&&((this.paramValue == rhs.paramValue)||((this.paramValue!= null)&&this.paramValue.equals(rhs.paramValue))));
    }


    /**
     * The parameter name, must be one from a set of known names.
     * 
     */
    @Generated("jsonschema2pojo")
    public enum ParamName {

        @SerializedName("SimulationStartTime")
        SIMULATION_START_TIME("SimulationStartTime"),
        @SerializedName("SimulationEndTime")
        SIMULATION_END_TIME("SimulationEndTime"),
        @SerializedName("SimulationStepLength")
        SIMULATION_STEP_LENGTH("SimulationStepLength");
        private final String value;
        private final static Map<String, Parameter.ParamName> CONSTANTS = new HashMap<String, Parameter.ParamName>();

        static {
            for (Parameter.ParamName c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        ParamName(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static Parameter.ParamName fromValue(String value) {
            Parameter.ParamName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
