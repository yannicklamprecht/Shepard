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
 * An CommandArg is the highest instance to define the arguments of a command.
 */
public class CommandArgument {
    private final String argName;
    private final SubArgument[] subArguments;
    private final boolean required;

    /**
     * Create a new argument with a name, description and whether it is required or not.
     *
     * @param argName  the name of the argument.
     * @param subArguments  subarguments, which can be entered at this state of command.
     * @param required whether the argument is required or not.
     */
    public CommandArgument(String argName, boolean required, SubArgument... subArguments) {
        this.argName = argName;
        this.required = required;
        this.subArguments = subArguments;
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
                Arrays.stream(subArguments).forEach(subArg -> subArg.generateShortCommand(finalIteration));
            }
            iteration++;
        } while (!getNotUniqueSubArgs().isEmpty());
    }

    private String getSubArgHelpString() {
        return Arrays.stream(subArguments).map(SubArgument::getArgumentDesc)
                .collect(Collectors.joining(lineSeparator()));
    }

    /**
     * Checks if a string matches the command or alias of a subcommand.
     *
     * @param cmd   command to check
     * @param index index of subcommand.
     * @return true if the command or alias matches. case ignore
     */
    public boolean isSubCommand(String cmd, int index) {
        if (index >= subArguments.length || index < 0) {
            return false;
        }
        return subArguments[index].isSubCommand(cmd);
    }

    /**
     * Checks if a string matches the command or alias of a subcommand.
     *
     * @param cmd        command to check
     * @param subCommand name of the subcommand.
     * @return true if the command or alias matches. case ignore
     */
    public boolean isSubCommand(String cmd, String subCommand) {
        for (SubArgument arg : subArguments) {
            if (arg.getArgumentName().equalsIgnoreCase(subCommand)) {
                return arg.isSubCommand(cmd);
            }
        }
        return false;
    }

    private Set<SubArgument> getNotUniqueSubArgs() {
        Set<SubArgument> result = new HashSet<>();
        for (SubArgument arg : subArguments) {
            if (arg.isSubCommand()) {
                for (SubArgument otherArg : subArguments) {
                    if (arg != otherArg && arg.getShortCommand().equals(otherArg.getShortCommand())) {
                        result.add(otherArg);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Get the argument name.
     *
     * @return argument name
     */
    public String getArgName() {
        return argName;
    }

    /**
     * Get the sub arguments of the argument.
     *
     * @return array of sub arguments Is empty when no arguments are set.
     */
    public SubArgument[] getSubArguments() {
        return subArguments;
    }
}

