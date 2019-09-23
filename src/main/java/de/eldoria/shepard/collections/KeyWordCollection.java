package de.eldoria.shepard.collections;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.keywords.KeywordArgs;
import de.eldoria.shepard.contexts.keywords.Keyword;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class KeyWordCollection {
    private static KeyWordCollection instance;

    private final List<Keyword> keywords = new ArrayList<>();

    private KeyWordCollection() {
    }

    /**
     * Get the keyword collection instance.
     *
     * @return KeywordCollection Instance
     */
    public static KeyWordCollection getInstance() {
        if (instance == null) {
            synchronized (KeyWordCollection.class) {
                if (instance == null) {
                    instance = new KeyWordCollection();
                }
            }
        }
        return instance;
    }

    /**
     * Adds a new Keyword to collection.
     * @param keyword keyword to add
     */
    public void addKeyword(Keyword keyword) {
        keywords.add(keyword);
    }

    /**
     * get the first found keyword in a message.
     * @param event message received event.
     * @return KeyWordArgs object or null if no keyword was found.
     */
    public KeywordArgs getKeyword(MessageReceivedEvent event) {
        for (Keyword keyword : keywords) {
            if (keyword.hasKeyword(event)) {
                return keyword.getKeyWordArgs(event);
            }
        }
        return null;
    }

    /**
     * Get all registered keywords.
     * @return Keyword list
     */
    public List<Keyword> getKeywords() {
        return Collections.unmodifiableList(keywords);
    }

    /**
     * Get a Keyword from context name.
     * @param contextName name of the context
     * @param event event for permission check
     * @return Keyword object
     */
    public Keyword getKeywordWithContextName(String contextName, MessageEventDataWrapper event) {
        for (Keyword k : keywords) {
            if (k.getClass().getSimpleName().equalsIgnoreCase(contextName) && k.isContextValid(event)) {
                return k;
            }
        }
        return null;
    }

    /**
     * Prints a debug for all keywords to console.
     */
    public void debug() {
        ShepardBot.getLogger().info("++++ DEBUG OF KEYWORDS ++++");
        for (Keyword c : keywords) {
            c.printDebugInfo();
        }
    }
}
