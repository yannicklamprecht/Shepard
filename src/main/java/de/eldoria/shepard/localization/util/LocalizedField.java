package de.eldoria.shepard.localization.util;

import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class LocalizedField {
    private final MessageEmbed.Field field;

    /**
     * Creates a localized field.
     *  @param title          title of the field
     * @param description    description of the field
     * @param inline         true if inline
     * @param eventWrapper message context for language detection
     */
    public LocalizedField(String title, String description, boolean inline, EventWrapper eventWrapper) {
        field = new MessageEmbed.Field(TextLocalizer.localizeByWrapper(title, eventWrapper),
                TextLocalizer.localizeByWrapper(description, eventWrapper),
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
