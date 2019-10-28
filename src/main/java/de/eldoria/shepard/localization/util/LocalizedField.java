package de.eldoria.shepard.localization.util;

import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class LocalizedField {
    private MessageEmbed.Field field;

    public LocalizedField(String title, String description, boolean inline, MessageEventDataWrapper messageContext) {
        field = new MessageEmbed.Field(TextLocalizer.fastLocale(title, messageContext.getGuild()),
                TextLocalizer.fastLocale(description, messageContext.getGuild()),
                inline);
    }
    public LocalizedField(String title, String description, boolean inline, TextChannel channel) {
        field = new MessageEmbed.Field(TextLocalizer.fastLocale(title, channel.getGuild()),
                TextLocalizer.fastLocale(description, channel.getGuild()),
                inline);
    }

    public MessageEmbed.Field getField() {
        return field;
    }
}
