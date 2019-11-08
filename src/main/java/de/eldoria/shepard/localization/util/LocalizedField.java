package de.eldoria.shepard.localization.util;

import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class LocalizedField {
    private final MessageEmbed.Field field;

    /**
     * Creates a localized field.
     *
     * @param title          title of the field
     * @param description    description of the field
     * @param inline         true if inline
     * @param messageContext message context for language detection
     */
    public LocalizedField(String title, String description, boolean inline, MessageEventDataWrapper messageContext) {
        field = new MessageEmbed.Field(TextLocalizer.localizeAll(title, messageContext.getGuild()),
                TextLocalizer.localizeAll(description, messageContext.getGuild()),
                inline);
    }

    /**
     * Creates a localized field.
     *
     * @param title       title of the field
     * @param description description of the field
     * @param inline      true if inline
     * @param channel     channel for language detection
     */
    public LocalizedField(String title, String description, boolean inline, TextChannel channel) {
        field = new MessageEmbed.Field(TextLocalizer.localizeAll(title, channel.getGuild()),
                TextLocalizer.localizeAll(description, channel.getGuild()),
                inline);
    }

    /**
     * Get the localized field.
     *
     * @return localized field
     */
    public MessageEmbed.Field getField() {
        return field;
    }
}
