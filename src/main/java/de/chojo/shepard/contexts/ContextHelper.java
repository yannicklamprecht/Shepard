package de.chojo.shepard.contexts;

import de.chojo.shepard.collections.CommandCollection;
import de.chojo.shepard.collections.KeyWordCollection;
import de.chojo.shepard.contexts.commands.Command;
import de.chojo.shepard.contexts.keywords.Keyword;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public final class ContextHelper {
    private ContextHelper(){}

    public static String getContextName(String name, MessageReceivedEvent receivedEvent) {
        Command command = CommandCollection.getInstance().getCommand(name);
        Keyword keyword = KeyWordCollection.getInstance().getKeywordWithContextName(name, receivedEvent);

        String contextName = null;
        if (keyword != null) {
            contextName = keyword.getClass().getSimpleName();
        } else if (command != null) {
            contextName = command.getClass().getSimpleName();
        }

        return contextName;
    }

}
