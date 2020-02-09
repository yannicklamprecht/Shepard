package de.eldoria.shepard.contexts.commands.argument;

import de.eldoria.shepard.localization.util.TextLocalizer;

public class SubArgumentInfo {
    private final String argumentName;
    private final String text;
    private final boolean isSubCommand;
    private String shortCommand;

    /**
     * Creates a new sub arg info object.
     *
     * @param arg subarg for information retrieval
     */
    public SubArgumentInfo(SubArgument arg) {
        argumentName = arg.getArgumentName();
        shortCommand = arg.getShortCommand();
        isSubCommand = arg.isSubCommand();
        text = TextLocalizer.localizeAllAndReplace(arg.getLocaleTag(), null);
    }

}
