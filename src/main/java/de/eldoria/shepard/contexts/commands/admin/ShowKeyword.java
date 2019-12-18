package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.collections.KeyWordCollection;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.keywords.Keyword;
import de.eldoria.shepard.localization.enums.WordsLocale;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.util.List;

import static de.eldoria.shepard.localization.enums.commands.admin.ShowKeywordLocale.DESCRIPTION;

public class ShowKeyword extends Command {
    /**
     * Creates a new show keyword command object.
     */
    public ShowKeyword() {
        commandName = "showKeywords";
        commandDesc = DESCRIPTION.tag;
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        List<Keyword> keywords = KeyWordCollection.getInstance().getKeywords();

        TextFormatting.TableBuilder tableBuilder = TextFormatting.getTableBuilder(
                keywords, WordsLocale.CONTEXT_NAME.tag, WordsLocale.KEYWORDS.tag);

        for (Keyword kw : keywords) {
            tableBuilder.next();
            tableBuilder.setRow(kw.getClass().getSimpleName(), "->", kw.toString());
        }

        MessageSender.sendMessage(WordsLocale.KEYWORDS + System.lineSeparator() + tableBuilder,
                messageContext.getTextChannel());
    }
}
