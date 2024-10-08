
package de.enflexit.awbRemote.jsonCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * AwbRemoteCommand
 * <p>
 * A remote command, that can be sent to the AWB using an AwbRemoteControl implementation
 * 
 */
@Generated("jsonschema2pojo")
public class AwbCommand {

    /**
     * One from a set of valid remote control commands
     * 
     */
    @SerializedName("command")
    @Expose
    private AwbCommand.Command command;
    /**
     * Allows to specify a single string parameter for simple commands
     * 
     */
    @SerializedName("parameter")
    @Expose
    private String parameter;
    /**
     * Allows to specify a number of named parameters for more complex commands
     * 
     */
    @SerializedName("parameterList")
    @Expose
    private List<Parameter> parameterList = new ArrayList<Parameter>();

    /**
     * One from a set of valid remote control commands
     * 
     */
    public AwbCommand.Command getCommand() {
        return command;
    }

    /**
     * One from a set of valid remote control commands
     * 
     */
    public void setCommand(AwbCommand.Command command) {
        this.command = command;
    }

    /**
     * Allows to specify a single string parameter for simple commands
     * 
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * Allows to specify a single string parameter for simple commands
     * 
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    /**
     * Allows to specify a number of named parameters for more complex commands
     * 
     */
    public List<Parameter> getParameterList() {
        return parameterList;
    }

    /**
     * Allows to specify a number of named parameters for more complex commands
     * 
     */
    public void setParameterList(List<Parameter> parameterList) {
        this.parameterList = parameterList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AwbCommand.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("command");
        sb.append('=');
        sb.append(((this.command == null)?"<null>":this.command));
        sb.append(',');
        sb.append("parameter");
        sb.append('=');
        sb.append(((this.parameter == null)?"<null>":this.parameter));
        sb.append(',');
        sb.append("parameterList");
        sb.append('=');
        sb.append(((this.parameterList == null)?"<null>":this.parameterList));
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
        result = ((result* 31)+((this.parameterList == null)? 0 :this.parameterList.hashCode()));
        result = ((result* 31)+((this.command == null)? 0 :this.command.hashCode()));
        result = ((result* 31)+((this.parameter == null)? 0 :this.parameter.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof AwbCommand) == false) {
            return false;
        }
        AwbCommand rhs = ((AwbCommand) other);
        return ((((this.parameterList == rhs.parameterList)||((this.parameterList!= null)&&this.parameterList.equals(rhs.parameterList)))&&((this.command == rhs.command)||((this.command!= null)&&this.command.equals(rhs.command))))&&((this.parameter == rhs.parameter)||((this.parameter!= null)&&this.parameter.equals(rhs.parameter))));
    }


    /**
     * One from a set of valid remote control commands
     * 
     */
    @Generated("jsonschema2pojo")
    public enum Command {

        @SerializedName("StartSimulation")
        START_SIMULATION("StartSimulation"),
        @SerializedName("StopSimulation")
        STOP_SIMULATION("StopSimulation"),
        @SerializedName("LoadProject")
        LOAD_PROJECT("LoadProject"),
        @SerializedName("SelectSetup")
        SELECT_SETUP("SelectSetup"),
        @SerializedName("SetTimeConfiguration")
        SET_TIME_CONFIGURATION("SetTimeConfiguration"),
        @SerializedName("NextStep")
        NEXT_STEP("NextStep");
        private final String value;
        private final static Map<String, AwbCommand.Command> CONSTANTS = new HashMap<String, AwbCommand.Command>();

        static {
            for (AwbCommand.Command c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        Command(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static AwbCommand.Command fromValue(String value) {
            AwbCommand.Command constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
