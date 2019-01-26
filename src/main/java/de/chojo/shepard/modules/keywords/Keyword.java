package de.chojo.shepard.modules.keywords;

import de.chojo.shepard.collections.KeyWordCollection;
import de.chojo.shepard.modules.ContextSensitive;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Keyword extends ContextSensitive {
    protected String[] keywords = new String[0];

    public boolean execute(MessageReceivedEvent event, String key) {
        return false;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public Keyword() {
        KeyWordCollection.getInstance().addKeyword(this);
    }

    public String hasKeyword(MessageReceivedEvent event) {
        for (String key : keywords) {
            if (event.getMessage().getContentRaw().toLowerCase().contains(key)) {
                return key;
            }
        }
        return null;
    }
}
