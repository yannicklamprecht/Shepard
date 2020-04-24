package de.eldoria.shepard.webapi.apiobjects;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

@Getter
public class GuildInfo {
    private long id;
    private String name;
    private String iconUrl;
    private int userCount;

    /**
     * Create a new guild info from a guild object.
     *
     * @param guild guild object
     */
    public GuildInfo(Guild guild) {
        id = guild.getIdLong();
        name = guild.getName();
        iconUrl = guild.getIconUrl();
        userCount = guild.getMemberCount();
    }
}
