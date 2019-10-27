package de.eldoria.shepard.localization.util;

import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class LocalizedField {
    private MessageEmbed.Field field;

    public LocalizedField(String title, String description, boolean inline, MessageEventDataWrapper messageContext) {
        field = new MessageEmbed.Field(TextLocalizer.fastLocale(title, messageContext),
                TextLocalizer.fastLocale(description, messageContext),
                inline);
    }

    public MessageEmbed.Field getField() {
        return field;
    }
}
