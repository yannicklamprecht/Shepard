package de.eldoria.shepard.contexts.commands.argument;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
        this.required = required;
        this.subArgs = subArgs;
        generateShortCommands();
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

    private boolean areShortCommandsUnique() {
        Set<String> shortCommands = new HashSet<>();
        for (var subArg : subArgs) {
            if (subArg.isSubCommand()) {
                if (!shortCommands.add(subArg.getShortCommand())) {
                    return false;
                }
            }
        }
        return true;
    }

    private void generateShortCommands() {
        int iteration = 0;
        do {
            int finalIteration = iteration;
            Arrays.stream(subArgs).forEach(subArg -> subArg.generateShortCommand(finalIteration));
            iteration++;
        } while (!areShortCommandsUnique());
    }
}

