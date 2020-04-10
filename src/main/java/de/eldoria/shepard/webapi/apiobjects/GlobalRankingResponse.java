package de.eldoria.shepard.webapi.apiobjects;

import de.eldoria.shepard.database.types.ApiRank;
import lombok.Getter;

import java.util.List;

@Getter
public class GlobalRankingResponse {
    /**
     * Ranked list.
     */
    private List<ApiRank> ranking;
    /**
     * Page of the ranked list.
     */
    private int page;
    /**
     * Max amount of pages.
     */
    private int maxPages;

    /**
     * A global ranking response object.
     *
     * @param ranking  list of api ranks.
     * @param page     number of the displayed page
     * @param maxPages number of maximum pages
     */
    public GlobalRankingResponse(List<ApiRank> ranking, int page, int maxPages) {
        this.ranking = ranking;
        this.page = page;
        this.maxPages = maxPages;
    }
}
