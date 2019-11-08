package de.eldoria.shepard.botlist;

public class BotListResponse {
    private long bot;
    private long user;
    private String type;
    private boolean isWeekend;
    private String query;

    /**
     * Get the bot id.
     *
     * @return id of the bot
     */
    public long getBot() {
        return bot;
    }

    /**
     * Get the user who has voted.
     *
     * @return id of user
     */
    public long getUser() {
        return user;
    }

    /**
     * Get the type of the vote. Should be always upvote.
     *
     * @return upvote type
     */
    public String getType() {
        return type;
    }

    /**
     * indicates if the weekend multiplier is active.
     *
     * @return true of the weekend multiplier was active
     */
    public boolean isWeekend() {
        return isWeekend;
    }

    /**
     * query of the vote.
     *
     * @return query as string
     */
    public String getQuery() {
        return query;
    }
}
