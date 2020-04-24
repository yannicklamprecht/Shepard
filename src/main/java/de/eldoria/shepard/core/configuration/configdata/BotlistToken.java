package de.eldoria.shepard.core.configuration.configdata;

import lombok.Data;

@Data
public class BotlistToken {
    /**
     * top.gg api token.
     */
    private String topgg = null;
    /**
     * discordbotlist.com api token
     */
    private String discordBotListCom = null;
    /**
     * discord.bots.gg api token
     */
    private String discordBotsgg = null;
    /**
     * bots.ondiscord.xyz api token
     */
    private String botsOnDiscordxyz = null;
}
