package de.eldoria.shepard.contexts.keywords.keyword;

import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.keywords.Keyword;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SomeoneKeyword extends Keyword {
    public SomeoneKeyword() {
        keywords = new String[] {"@someone"};
    }

    @Override
    public void execute(MessageReceivedEvent event, String key) {
        Command someone = CommandCollection.getInstance().getCommand("someone");
        if (someone != null) {
            someone.execute("", new String[0], new MessageEventDataWrapper<>(event));

        }
    }
}
