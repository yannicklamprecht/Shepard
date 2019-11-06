package de.eldoria.shepard.contexts.commands.argument;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.util.HelpLocale.W_OPTIONAL;
import static de.eldoria.shepard.localization.enums.commands.util.HelpLocale.W_REQUIRED;
import static java.lang.System.lineSeparator;

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
     * Get whether the argument is required or not.
     *
     * @return {@code true} if the argument is required, {@code false} otherwise.
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Get the argument name. Adds [] or <> to indicate if a argument is required or not.
     * The argument name is all upper case.
     *
     * @return String of the argument type.
     */
    public String getHelpString() {
        if (isRequired()) {
            return "[" + argName.toUpperCase() + "]";
        } else {
            return "<" + argName.toUpperCase() + ">";
        }
    }

    /**
     * Argument name with subarguments.
     *
     * @return string with more information
     */
    public String getArgHelpString() {
        return "**" + argName.toUpperCase() + "** " + (isRequired() ? W_REQUIRED : W_OPTIONAL) + lineSeparator()
                + getSubArgHelpString();
    }

    private void generateShortCommands() {
        int iteration = 0;
        do {
            int finalIteration = iteration;
            if (finalIteration != 0) {
                getNotUniqueSubArgs().forEach(subArg -> subArg.generateShortCommand(finalIteration));
            } else {
                Arrays.stream(subArgs).forEach(subArg -> subArg.generateShortCommand(finalIteration));
            }
            iteration++;
        } while (!getNotUniqueSubArgs().isEmpty());
    }

    private String getSubArgHelpString() {
        return Arrays.stream(subArgs).map(SubArg::getArgumentDesc)
                .collect(Collectors.joining(lineSeparator()));
    }

    public boolean isSubCommand(String cmd, int index) {
        if (index >= subArgs.length || index < 0) {
            return false;
        }
        return subArgs[index].isSubCommand(cmd);
    }

    public boolean isSubCommand(String cmd, String subCommand) {
        for (SubArg arg : subArgs) {
            if (arg.getArgumentName().equalsIgnoreCase(subCommand)) {
                return arg.isSubCommand(cmd);
            }
        }
        return false;
    }

    private Set<SubArg> getNotUniqueSubArgs() {
        Set<SubArg> result = new HashSet<>();
        for (SubArg arg : subArgs) {
            if (arg.isSubCommand()) {
                for (SubArg otherArg : subArgs) {
                    if (arg != otherArg && arg.getShortCommand().equals(otherArg.getShortCommand())) {
                        result.add(otherArg);
                    }
                }
            }
        }
        return result;
    }
}

