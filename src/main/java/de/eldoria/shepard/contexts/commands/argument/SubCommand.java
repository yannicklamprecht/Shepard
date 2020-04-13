package de.eldoria.shepard.contexts.commands.argument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An argument of a command.
 * An CommandArg is the highest instance to define the arguments of a command.
 */
public class SubCommand {
    private String commandName;
    // Short description of the sub command
    private final String commandDescription;
    private final Parameter[] parameters;
    private String commandPattern;

    /**
     * Create a new argument with a name, description and whether it is required or not.
     *
     * @param commandName        the name of the argument.
     * @param commandDescription Description what the sub command does.
     * @param parameters         parameters of the subcommand. order matters.
     */
    public SubCommand(String commandName, String commandDescription, Parameter... parameters) {
        this.commandName = commandName;
        this.commandDescription = commandDescription;
        this.parameters = parameters;
    }

    /**
     * Argument name with subarguments.
     *
     * @param commandName
     * @return string with more information
     */
    public String generateCommandPatternHelp(String commandName) {
        List<String> params = new ArrayList<>();
        List<String> paramsDescription = new ArrayList<>();
        for (Parameter p : parameters) {
            // check if command.
            if (p.isCommand()) {
                params.add(p.getCommandName() + "|" + p.getShortCommand());
            } else {
                params.add(p.isRequired() ? "<" + p.getInputName() + ">" : "[" + p.getInputName() + "]");
                if (p.getInputDescription() != null) {
                    paramsDescription.add(p.getInputDesc(p.isRequired()));
                }
            }
        }
        return "> *__" + commandDescription + "__*\n"
                + "> {prefix}" + commandName + " " + String.join(" ", params)
                + (paramsDescription.isEmpty() ? "" : "\n> " + String.join("\n> ", paramsDescription));
    }


    /**
     * Checks if a string matches the command or alias of a subcommand.
     *
     * @param cmd command to check
     * @return true if the command or alias matches. case ignore
     */
    public boolean isSubCommand(String cmd) {
        for (var p : parameters) {
            if (p.isCommand()) {
                return p.isCommandParameter(cmd);
            }
        }
        return false;
    }

    private Set<Parameter> getNotUniqueSubArgs() {
        Set<Parameter> result = new HashSet<>();
        for (Parameter arg : parameters) {
            if (arg.isCommand()) {
                for (Parameter otherArg : parameters) {
                    if (arg != otherArg && arg.getShortCommand().equals(otherArg.getShortCommand())) {
                        result.add(otherArg);
                    }
                }
            }
        }
        return result;
    }


    /**
     * Get the sub arguments of the argument.
     *
     * @return array of sub arguments Is empty when no arguments are set.
     */
    public Parameter[] getParameters() {
        return parameters;
    }

    public int getRequiredParamCount() {
        int i = 0;
        for (Parameter p : parameters) {
            if (p.isRequired()) i++;
        }
        return i;
    }

    public String getCommandPattern() {
        if (commandPattern == null) {
            commandPattern = generateCommandPatternHelp(commandName);
        }
        return commandPattern;
    }

    public static SubCommandBuilder builder(String commandName) {
        return new SubCommandBuilder(commandName);
    }

    public boolean matchArgs(String[] args) {
        boolean match = true;
        for (int i = 0; i < parameters.length; i++) {
            if (args.length > i) {
                match = parameters[i].isCommandParameter(args[i]);
            }
            if (!match) {
                return false;
            }
        }
        return true;
    }

    public static class SubCommandBuilder {
        private String commandName;

        List<SubCommand> subCommands = new ArrayList<>();

        private SubCommandBuilder(String commandName) {
            this.commandName = commandName;
        }

        public SubCommandBuilder addSubcommand(String description, Parameter... parameters) {
            subCommands.add(new SubCommand(commandName, description, parameters));
            return this;
        }

        public SubCommand[] build() {
            SubCommand[] sc = new SubCommand[subCommands.size()];
            return subCommands.toArray(sc);
        }
    }

    public SubCommandInfo getSubCommandInfo() {
        ParameterInfo[] pI = new ParameterInfo[parameters.length];
        return new SubCommandInfo(commandDescription,
                Arrays.stream(parameters)
                        .map(Parameter::getParameterInfo)
                        .collect(Collectors.toList())
                        .toArray(pI));
    }
}

