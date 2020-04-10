package de.eldoria.shepard.contexts.commands.argument;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class to save a {@link CommandArgument} for serialization.
 */
public class CommandArgumentInfo {
    private final String argumentName;
    private final List<SubArgumentInfo> subArguments;
    private final boolean required;

    /**
     * Creates a new Command arg info object.
     *
     * @param arg Command arg for information retrieval.
     */
    public CommandArgumentInfo(CommandArgument arg) {
        argumentName = arg.getArgName();
        required = arg.isRequired();
        subArguments = Arrays.stream(arg.getSubArguments()).map(subArg -> new SubArgumentInfo(subArg)).collect(Collectors.toList());
    }
}
