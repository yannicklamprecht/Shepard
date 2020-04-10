package de.eldoria.shepard.webapi.apiobjects;

import de.eldoria.shepard.database.types.ApiRank;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;

@Getter
public class GuildRankingResponse extends GlobalRankingResponse {
    /**
     * Url of the guild.
     */
    private long id;
    /**
     * Name of the guild.
     */
    private String name;
    /**
     * Icon url of the guild.
     */
    private String iconUrl;

    public GuildRankingResponse(Guild guild, List<ApiRank> ranking, int page, int maxPages) {
        super(ranking, page, maxPages);
        if (guild != null) {
            id = guild.getIdLong();
            name = guild.getName();
            iconUrl = guild.getIconUrl();
        }
    }
}
