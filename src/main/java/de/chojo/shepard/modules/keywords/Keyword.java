package de.chojo.shepard.modules.keywords;

import de.chojo.shepard.collections.KeyWordCollection;
import de.chojo.shepard.modules.ContextSensitive;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Keyword extends ContextSensitive {
    protected String[] keywords = new String[0];

    /**
     * Create a new Keyword object and add it to the {@link KeyWordCollection}.
     */
    public Keyword() {
        KeyWordCollection.getInstance().addKeyword(this);
    }

    /**
     * Get all variants of the keyword.
     *
     * @return an array of all variants.
     */
    public String[] getKeywords() {
        return keywords;
    }

    /**
     * Performs an action if a keyword was found.
     *
     * @param event the event in which the keyword was found.
     * @param key the key found in the event.
     * @return {@code true} if it was executed successfully, {@code false} otherwise.
     */
    public boolean execute(MessageReceivedEvent event, String key) {
        return false;
    }

    /**
     * Get a word if the message contains one of the keywords.
     *
     * @param event the event of the message.
     * @return the keyword if something was found.
     * @deprecated although it returns a {@link String}, the method name starts with {@code has}.
     */
    @Deprecated
    public String hasKeyword(MessageReceivedEvent event) {
        for (String key : keywords) {
            if (event.getMessage().getContentRaw().toLowerCase().contains(key)) {
                return key;
            }
        }
        return null;
    }
}
