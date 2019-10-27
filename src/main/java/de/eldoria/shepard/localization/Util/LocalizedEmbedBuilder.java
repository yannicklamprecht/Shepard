package de.eldoria.shepard.localization.Util;

import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.awt.*;
import java.time.temporal.TemporalAccessor;

import static de.eldoria.shepard.localization.Util.TextLocalizer.fastLocale;

/**
 * Wrapper for auto localization of embeds.
 */
public class LocalizedEmbedBuilder extends EmbedBuilder {
    private Guild guild;

    public LocalizedEmbedBuilder(MessageEventDataWrapper messageContext) {
        this.guild = messageContext.getGuild();
    }

    public LocalizedEmbedBuilder(Guild guild) {
        this.guild = guild;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder addField(@Nullable String name, @Nullable String value, boolean inline) {
        super.addField(fastLocale(name, guild), fastLocale(value, guild), inline);
        return this;
    }

    @Nonnull
    @Override
    @Deprecated
    public LocalizedEmbedBuilder addField(@Nullable MessageEmbed.Field field) {
        super.addField(field);
        return this;
    }

    public LocalizedEmbedBuilder addField(LocalizedField field) {
        return addField(field.getField());
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setTitle(@Nullable String title) {
        super.setTitle(fastLocale(title, guild));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setTitle(@Nullable String title, @Nullable String url) {
        super.setTitle(fastLocale(title, guild), url);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setFooter(@Nullable String text) {
        super.setFooter(fastLocale(text, guild));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setFooter(@Nullable String text, @Nullable String iconUrl) {
        super.setFooter(fastLocale(text, guild), iconUrl);
        return this;
    }

    /**
     * Set the description with auto translation.
     *
     * @param text text to set
     * @return the builder after the description has been set
     */
    public LocalizedEmbedBuilder setDescription(String text) {
        super.setDescription(fastLocale(text, guild));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder appendDescription(@Nonnull CharSequence description) {
        super.appendDescription(fastLocale(description.toString(), guild));
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
    public EmbedBuilder setThumbnail(@Nullable String url) {
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
        super.setAuthor(fastLocale(name, guild));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setAuthor(@Nullable String name, @Nullable String url) {
        super.setAuthor(fastLocale(name, guild), url);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setAuthor(@Nullable String name, @Nullable String url, @Nullable String iconUrl) {
        super.setAuthor(fastLocale(name, guild), url, iconUrl);
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
