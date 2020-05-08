package de.eldoria.shepard.commandmodules.greeting.types;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.sharding.ShardManager;

public class GreetingSettings {
    private final String text;
    private final TextChannel channel;

    /**
     * Creates a new Greeting Object.
     *  @param jda       jda for parsing
     * @param guildId   guild id
     * @param channelId channel id
     * @param text      greeting text
     */
    public GreetingSettings(ShardManager jda, String guildId, String channelId, String text) {
        this.text = text;

        Guild guild = jda.getGuildById(guildId);
        channel = guild != null && channelId != null && !channelId.isEmpty()
                ? guild.getTextChannelById(channelId) : null;
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
