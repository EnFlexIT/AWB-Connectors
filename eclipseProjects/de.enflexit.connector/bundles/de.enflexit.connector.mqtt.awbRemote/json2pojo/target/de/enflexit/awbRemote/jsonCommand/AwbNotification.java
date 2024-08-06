
package de.enflexit.awbRemote.jsonCommand;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * AwbNotification
 * <p>
 * A status update that is sent from the controlled AWB to the controlling instance
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "awbState",
    "stateDetails"
})
@Generated("jsonschema2pojo")
public class AwbNotification {

    /**
     * The state to notify the controlling entity about.
     * 
     */
    @JsonProperty("awbState")
    @JsonPropertyDescription("The state to notify the controlling entity about.")
    private AwbNotification.AwbState awbState;
    /**
     * Optional further details about the state.
     * 
     */
    @JsonProperty("stateDetails")
    @JsonPropertyDescription("Optional further details about the state.")
    private String stateDetails;

    /**
     * The state to notify the controlling entity about.
     * 
     */
    @JsonProperty("awbState")
    public AwbNotification.AwbState getAwbState() {
        return awbState;
    }

    /**
     * The state to notify the controlling entity about.
     * 
     */
    @JsonProperty("awbState")
    public void setAwbState(AwbNotification.AwbState awbState) {
        this.awbState = awbState;
    }

    /**
     * Optional further details about the state.
     * 
     */
    @JsonProperty("stateDetails")
    public String getStateDetails() {
        return stateDetails;
    }

    /**
     * Optional further details about the state.
     * 
     */
    @JsonProperty("stateDetails")
    public void setStateDetails(String stateDetails) {
        this.stateDetails = stateDetails;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AwbNotification.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("awbState");
        sb.append('=');
        sb.append(((this.awbState == null)?"<null>":this.awbState));
        sb.append(',');
        sb.append("stateDetails");
        sb.append('=');
        sb.append(((this.stateDetails == null)?"<null>":this.stateDetails));
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
        result = ((result* 31)+((this.stateDetails == null)? 0 :this.stateDetails.hashCode()));
        result = ((result* 31)+((this.awbState == null)? 0 :this.awbState.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof AwbNotification) == false) {
            return false;
        }
        AwbNotification rhs = ((AwbNotification) other);
        return (((this.stateDetails == rhs.stateDetails)||((this.stateDetails!= null)&&this.stateDetails.equals(rhs.stateDetails)))&&((this.awbState == rhs.awbState)||((this.awbState!= null)&&this.awbState.equals(rhs.awbState))));
    }


    /**
     * The state to notify the controlling entity about.
     * 
     */
    @Generated("jsonschema2pojo")
    public enum AwbState {

        AWB_READY("AwbReady"),
        PROJECT_LOADED("ProjectLoaded"),
        SETUP_LOADED("SetupLoaded"),
        CONFIGURATION_SET("ConfigurationSet"),
        MAS_STARTED("MasStarted"),
        READY_FOR_NEXT_STEP("ReadyForNextStep"),
        SIMULATION_FINISHED("SimulationFinished"),
        MAS_STOPPED("MasStopped"),
        COMMAND_FAILED("CommandFailed"),
        COMMAND_OUT_OF_SEQUENCE("CommandOutOfSequence");
        private final String value;
        private final static Map<String, AwbNotification.AwbState> CONSTANTS = new HashMap<String, AwbNotification.AwbState>();

        static {
            for (AwbNotification.AwbState c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        AwbState(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static AwbNotification.AwbState fromValue(String value) {
            AwbNotification.AwbState constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
