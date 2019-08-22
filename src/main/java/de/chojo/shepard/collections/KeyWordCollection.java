package de.chojo.shepard.collections;

import de.chojo.shepard.contexts.keywords.KeywordArgs;
import de.chojo.shepard.contexts.keywords.Keyword;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class KeyWordCollection {
    private static KeyWordCollection instance;

    private List<Keyword> keywords = new ArrayList<>();

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

    private KeyWordCollection() {
    }

    public void addKeyword(Keyword keyword) {
        keywords.add(keyword);
    }

    public KeywordArgs getKeyword(MessageReceivedEvent event) {
        for (Keyword keyword : keywords) {
            if (keyword.hasKeyword(event)) {
                return keyword.getKeyWordArgs(event);
            }
        }
        return null;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public Keyword getKeywordWithContextName(String contextName, MessageReceivedEvent event) {
        for (Keyword k : keywords) {
            if (k.getClass().getSimpleName().equalsIgnoreCase(contextName) && k.isContextValid(event)) {
                return k;
            }
        }
        return null;
    }
}
