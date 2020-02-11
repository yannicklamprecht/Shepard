package de.eldoria.shepard.localization.util;

import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.time.temporal.TemporalAccessor;

import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAll;

/**
 * Wrapper for auto localization of embeds.
 */
public class LocalizedEmbedBuilder extends EmbedBuilder {
    private final Guild guild;

    /**
     * Creates a new localized embed builder.
     *
     * @param messageContext message context for guild and language detection
     */
    public LocalizedEmbedBuilder(MessageEventDataWrapper messageContext) {
        this.guild = messageContext.getGuild();
    }

    /**
     * Creates a new localized embed builder.
     *
     * @param guild guild for language detection
     */
    public LocalizedEmbedBuilder(Guild guild) {
        this.guild = guild;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder addField(@Nullable String name, @Nullable String value, boolean inline) {
        super.addField(localizeAll(name, guild), localizeAll(value, guild), inline);
        return this;
    }

    @Nonnull
    @Override
    @Deprecated
    public LocalizedEmbedBuilder addField(@Nullable MessageEmbed.Field field) {
        super.addField(field);
        return this;
    }

    /**
     * Add a localized field.
     *
     * @param field localized field to add.
     * @return the builder after the field has been set
     */
    public LocalizedEmbedBuilder addField(LocalizedField field) {
        return addField(field.getField());
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setTitle(@Nullable String title) {
        super.setTitle(localizeAll(title, guild));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setTitle(@Nullable String title, @Nullable String url) {
        super.setTitle(localizeAll(title, guild), url);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setFooter(@Nullable String text) {
        super.setFooter(localizeAll(text, guild));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setFooter(@Nullable String text, @Nullable String iconUrl) {
        super.setFooter(localizeAll(text, guild), iconUrl);
        return this;
    }

    /**
     * Set the description with auto translation.
     *
     * @param text text to set
     * @return the builder after the description has been set
     */
    public LocalizedEmbedBuilder setDescription(String text) {
        super.setDescription(localizeAll(text, guild));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder appendDescription(@Nonnull CharSequence description) {
        super.appendDescription(localizeAll(description.toString(), guild));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder clear() {
        super.clear();
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setTimestamp(@Nullable TemporalAccessor temporal) {
        super.setTimestamp(temporal);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setColor(@Nullable Color color) {
        super.setColor(color);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setColor(int color) {
        super.setColor(color);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setThumbnail(@Nullable String url) {
        super.setThumbnail(url);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setImage(@Nullable String url) {
        super.setImage(url);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setAuthor(@Nullable String name) {
        super.setAuthor(localizeAll(name, guild));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setAuthor(@Nullable String name, @Nullable String url) {
        super.setAuthor(localizeAll(name, guild), url);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setAuthor(@Nullable String name, @Nullable String url, @Nullable String iconUrl) {
        super.setAuthor(localizeAll(name, guild), url, iconUrl);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder addBlankField(boolean inline) {
        super.addBlankField(inline);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder clearFields() {
        super.clearFields();
        return this;
    }

}
