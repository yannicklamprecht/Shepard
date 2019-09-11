package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.collections.KeyWordCollection;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.keywords.Keyword;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class ShowKeyword extends Command {
    /**
     * Creates a new show keyword command object.
     */
    public ShowKeyword() {
        commandName = "showKeywords";
        commandDesc = "Displays or valid Keywords";
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper dataWrapper) {
        List<Keyword> keywords = KeyWordCollection.getInstance().getKeywords();

        int maxContextLength = "Context Name".length() + 1;

        for (Keyword k : keywords) {
            if (k.isContextValid(dataWrapper)) {
                if (k.getClass().getSimpleName().length() + 1 > maxContextLength) {
                    maxContextLength = k.getClass().getSimpleName().length() + 1;
                }
            }
        }

        StringBuilder builder = new StringBuilder();
        builder.append("```md").append(System.lineSeparator())
                .append(StringUtils.rightPad("Context Name", maxContextLength)).append(" -> Keywords")
                .append(System.lineSeparator());

        for (Keyword k : keywords) {
            if (k.isContextValid(dataWrapper)) {
                builder.append(StringUtils.rightPad(k.getClass().getSimpleName(), maxContextLength)).append(" -> ")
                        .append(k.toString()).append(System.lineSeparator());
            }
        }
        builder.append("```");

        MessageSender.sendSimpleTextBox("Keywords", builder.toString(), dataWrapper.getChannel());
    }
}
