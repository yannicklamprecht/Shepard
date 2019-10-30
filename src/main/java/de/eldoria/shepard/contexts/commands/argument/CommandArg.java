package de.eldoria.shepard.contexts.commands.argument;

import de.eldoria.shepard.localization.enums.commands.util.HelpLocale;

import java.util.*;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.util.HelpLocale.W_OPTIONAL;
import static de.eldoria.shepard.localization.enums.commands.util.HelpLocale.W_REQUIRED;
import static java.lang.System.console;
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

    /**
     * Get the argument name.
     *
     * @return
     */
    public String getHelpString() {
        if (isRequired()) {
            return "[" + getArgName().toUpperCase() + "]";
        } else {
            return "<" + getArgName().toUpperCase() + ">";
        }
    }

    /**
     * Argument name with subarguments.
     *
     * @return string with more information
     */
    public String getArgHelpString() {
        return "**" + getArgName().toUpperCase() + "** " + (isRequired() ? W_REQUIRED : W_OPTIONAL) + lineSeparator()
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

    public String getSubArgHelpString() {
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

