package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.collections.KeyWordCollection;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.keywords.Keyword;

import java.util.List;

public class ShowKeyword extends Command {
    /**
     * Creates a new show keyword command object.
     */
    public ShowKeyword() {
        commandName = "showKeywords";
        commandDesc = "Show all valid Keywords";
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        List<Keyword> keywords = KeyWordCollection.getInstance().getKeywords();

        TextFormatting.TableBuilder tableBuilder = TextFormatting.getTableBuilder(
                keywords, "ContextName", "", "Keywords");

        for (Keyword kw : keywords) {
            tableBuilder.next();
            tableBuilder.setRow(kw.getClass().getSimpleName(), "->", kw.toString());
        }

        MessageSender.sendMessage("Keywords" + System.lineSeparator() + tableBuilder, messageContext.getChannel());
    }
}
