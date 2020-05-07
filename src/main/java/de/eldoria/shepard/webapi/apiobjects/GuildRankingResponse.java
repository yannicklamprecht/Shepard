package de.eldoria.shepard.webapi.apiobjects;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;

@Getter
public class GuildRankingResponse extends RankingResponse {
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

    /**
     * Create a new guild ranking response.
     *
     * @param guild    guild which was requested
     * @param ranking  list of ranking objects
     * @param page     page which was requests
     * @param maxPages amount of pages which can requested
     */
    public GuildRankingResponse(Guild guild, List<ApiRank> ranking, int page, int maxPages) {
        super(ranking, page, maxPages);
        if (guild != null) {
            id = guild.getIdLong();
            name = guild.getName();
            iconUrl = guild.getIconUrl();
        }
    }
}
