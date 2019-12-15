package de.eldoria.shepard.contexts.commands.argument;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandArgInfo {
    private final String argName;
    private final List<SubArgInfo> subArgs;
    private final boolean required;

    /**
     * Creates a new Command arg info object.
     * @param arg Command arg for information retrieval.
     */
    public CommandArgInfo(CommandArg arg) {
        argName = arg.getArgName();
        required = arg.isRequired();
        subArgs = Arrays.stream(arg.getSubArgs()).map(subArg -> new SubArgInfo(subArg)).collect(Collectors.toList());
    }
}
