package de.eldoria.shepard.commandmodules.greeting.types;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.sharding.ShardManager;

@Getter
public class GreetingSettings {
    /**
     * Gretting text.
     */
    private final String message;
    /**
     * Private greeting message.
     */
    private final String privateMessage;
    /**
     * Greeting channel. Can be null.
     */
    private final TextChannel channel;
    /**
     * Join Role. Can be null.
     */
    private final Role role;

    /**
     * Creates a new Greeting Object.
     *
     * @param jda       jda for parsing
     * @param guildId   guild id
     * @param channelId channel id
     * @param message   greeting text
     */
    public GreetingSettings(ShardManager jda, long guildId, Long channelId, String message, String privateMessage, Long roleId) {
        this.message = message;
        this.privateMessage = privateMessage;

        Guild guild = jda.getGuildById(guildId);
        channel = guild != null && channelId != null ? guild.getTextChannelById(channelId) : null;
        role = guild != null && roleId != null ? guild.getRoleById(roleId) : null;
    }
}
