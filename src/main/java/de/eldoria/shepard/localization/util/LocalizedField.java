package de.eldoria.shepard.localization.util;

import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class LocalizedField {
    private final MessageEmbed.Field field;

    public LocalizedField(String title, String description, boolean inline, MessageEventDataWrapper messageContext) {
        field = new MessageEmbed.Field(TextLocalizer.localizeAll(title, messageContext.getGuild()),
                TextLocalizer.localizeAll(description, messageContext.getGuild()),
                inline);
    }

    public LocalizedField(String title, String description, boolean inline, TextChannel channel) {
        field = new MessageEmbed.Field(TextLocalizer.localizeAll(title, channel.getGuild()),
                TextLocalizer.localizeAll(description, channel.getGuild()),
                inline);
    }

    public MessageEmbed.Field getField() {
        return field;
    }
}
