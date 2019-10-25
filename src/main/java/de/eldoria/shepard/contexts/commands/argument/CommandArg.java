package de.eldoria.shepard.contexts.commands.argument;

/**
 * An argument of a command.
 */
public class CommandArg {
    private final String argName;
    private final SubArg[] subArgs;
    private final boolean required;

    /**
     * Create a new argument with a name, description and whether it is required or not.
     *
     * @param argName  the name of the argument.
     * @param subArgs  subarguments, which can be entered at this state of command.
     * @param required whether the argument is required or not.
     */
    public CommandArg(String argName, boolean required, SubArg... subArgs) {
        this.argName = argName;
        this.subArgs = subArgs;
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
    public SubArg[] getSubArgs() {
        return subArgs;
    }

    /**
     * Get whether the argument is required or not.
     *
     * @return {@code true} if the argument is required, {@code false} otherwise.
     */
    public boolean isRequired() {
        return required;
    }
}

