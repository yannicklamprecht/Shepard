package de.eldoria.shepard.database.types;

import de.eldoria.shepard.ShepardBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class Greeting {
    private final Guild guild;
    private final String text;
    private final TextChannel channel;

    /**
     * Creates a new Greeting Object.
     *
     * @param guildId   guild id
     * @param channelId channel id
     * @param text      greeting text
     */
    public Greeting(String guildId, String channelId, String text) {
        this.text = text;

        guild = ShepardBot.getJDA().getGuildById(guildId);
        channel = guild != null ? guild.getTextChannelById(channelId) : null;
    }

    /**
     * Get the greeting text.
     *
     * @return Greeting text
     */
    public String getText() {
        return text;
    }

    /**
     * Get the greeting channel.
     *
     * @return Text channel. Can be null. If no channel with this id is found on guild
     */
    public TextChannel getChannel() {
        return channel;
    }
}
