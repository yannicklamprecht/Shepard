package de.chojo.shepard;

import de.chojo.shepard.messageHandler.Messages;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Settings {
    private static String prefix = "&";

    public static String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix, MessageChannel channel) {
        if (prefix.length() > 2) {
            Messages.sendMessage("Prefix too long. Max 2 chars.", channel);
            return;
        }

        this.prefix = prefix;
    }
}
