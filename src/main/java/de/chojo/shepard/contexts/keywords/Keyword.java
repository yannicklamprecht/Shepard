package de.chojo.shepard.contexts.keywords;

import de.chojo.shepard.collections.KeyWordCollection;
import de.chojo.shepard.contexts.ContextSensitive;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class Keyword extends ContextSensitive {
    /**
     * Array of keywords.
     */
    protected String[] keywords = new String[0];

    /**
     * Create a new Keyword object and add it to the {@link KeyWordCollection}.
     */
    protected Keyword() {
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
     * @param key   the key found in the event.
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
     */
    public boolean hasKeyword(MessageReceivedEvent event) {
        return getKey(event) != null;
    }

    @Override
    public String toString() {
        return Arrays.toString(keywords);
    }

    private String getKey(MessageReceivedEvent event) {
        for (String key : keywords) {
            if (event.getMessage().getContentRaw().toLowerCase().contains(key)) {
                return key;
            }
        }
        return null;
    }

    /**
     * Get Keywordargs from a event if the message contains a keyword.
     * @param event to search on.
     * @return Keywordargs containing keyword and the key which matched.
     */
    public KeywordArgs getKeyWordArgs(MessageReceivedEvent event) {
        String key = getKey(event);
        if (key != null) {
            return new KeywordArgs(key, this);
        }
        return null;
    }
}
