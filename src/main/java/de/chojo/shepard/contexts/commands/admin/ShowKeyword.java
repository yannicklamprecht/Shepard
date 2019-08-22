package de.chojo.shepard.contexts.commands.admin;

import de.chojo.shepard.collections.KeyWordCollection;
import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.contexts.commands.Command;
import de.chojo.shepard.contexts.keywords.Keyword;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class ShowKeyword extends Command {
    public ShowKeyword() {
        commandName = "showKeywords";
        commandDesc = "Displays or valid Keywords";
    }

    @Override
    public boolean execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        List<Keyword> keywords = KeyWordCollection.getInstance().getKeywords();

        int maxContextLengt = "Context Name".length() + 1;

        for (Keyword k : keywords) {
            if(k.isContextValid(receivedEvent)){
                if(k.getClass().getSimpleName().length() + 1 > maxContextLengt){
                    maxContextLengt = k.getClass().getSimpleName().length() + 1;
                }
            }
        }

        StringBuilder builder = new StringBuilder();
        builder.append("```md").append(System.lineSeparator())
                .append(StringUtils.rightPad("Context Name", maxContextLengt)).append(" -> Keywords").append(System.lineSeparator());

        for (Keyword k : keywords) {
            if (k.isContextValid(receivedEvent)) {
                builder.append(StringUtils.rightPad(k.getClass().getSimpleName(), maxContextLengt)).append(" -> ")
                        .append(k.toString()).append(System.lineSeparator());
            }
        }
        builder.append("```");

        Messages.sendSimpleTextBox("Keywords", builder.toString(), receivedEvent.getChannel());
        return true;
    }
}
