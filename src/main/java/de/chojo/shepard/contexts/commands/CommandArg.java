package de.chojo.shepard.contexts.commands;

/**
 * An argument of a command.
 */
public class CommandArg {
    private String argName;
    private String argDesc;
    private boolean required;

    /**
     * Create a new argument with a name, description and whether it is required or not.
     * @param argName the name of the argument.
     * @param argDesc the description of the argument.
     * @param required whether the argument is required or not.
     */
    public CommandArg(String argName, String argDesc, boolean required) {
        this.argName = argName;
        this.argDesc = argDesc;
        this.required = required;
    }

    /**
     * Get the name of the argument.
     *
     * @return the name.
     */
    public String getArgName() {
        return argName;
    }

    /**
     * Get the description of the argument.
     *
     * @return the description.
     */
    public String getArgDesc() {
        return argDesc;
    }

    /**
     * Get whether the argument is required or not.
     *
     * @return {@code true} if the argument is required, {@code false} otherwise.
     */
    public Boolean isRequired() {
        return required;
    }
}

