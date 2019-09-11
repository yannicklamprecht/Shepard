package de.eldoria.shepard.contexts;

import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.collections.KeyWordCollection;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.keywords.Keyword;
import de.eldoria.shepard.listener.MessageEventDataWrapper;

public final class ContextHelper {
    private ContextHelper() {
    }

    /**
     * Get the name of the context from a string.
     *
     * @param indicator for lookup
     * @param event     event from command sending for error handling. Can be null.
     * @return Name of the context or null if no context was found
     */
    public static String getContextName(String indicator, MessageEventDataWrapper event) {
        Command command = CommandCollection.getInstance().getCommand(indicator);
        Keyword keyword = KeyWordCollection.getInstance().getKeywordWithContextName(indicator, event);

        String contextName = null;
        if (keyword != null) {
            contextName = keyword.getClass().getSimpleName();
        } else if (command != null) {
            contextName = command.getClass().getSimpleName();
        }

        return contextName;
    }
}
