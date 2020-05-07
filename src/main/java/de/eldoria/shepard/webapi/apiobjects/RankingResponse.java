package de.eldoria.shepard.webapi.apiobjects;

import lombok.Getter;

import java.util.List;

@Getter
public class RankingResponse {
    /**
     * Ranked list.
     */
    private final List<ApiRank> ranking;
    /**
     * Page of the ranked list.
     */
    private final int page;
    /**
     * Max amount of pages.
     */
    private final int maxPages;

    /**
     * A global ranking response object.
     *
     * @param ranking  list of api ranks.
     * @param page     number of the displayed page
     * @param maxPages number of maximum pages
     */
    public RankingResponse(List<ApiRank> ranking, int page, int maxPages) {
        this.ranking = ranking;
        this.page = page;
        this.maxPages = maxPages;
    }
}
