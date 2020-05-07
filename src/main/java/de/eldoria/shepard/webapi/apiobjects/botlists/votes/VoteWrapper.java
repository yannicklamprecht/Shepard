package de.eldoria.shepard.webapi.apiobjects.botlists.votes;

import lombok.Getter;

@Getter
public class VoteWrapper {
    private final long id;
    private final boolean weekend;

    /**
     * Wrap a top.gg vote.
     *
     * @param vote vote to wrap
     */
    public VoteWrapper(TopGgVote vote) {
        id = vote.getUser();
        weekend = vote.isWeekend();
    }

    /**
     * Wrap a discordbotlist.com vote.
     *
     * @param vote vote to wrap
     */
    public VoteWrapper(DiscordBotListVote vote) {
        id = vote.getId();
        weekend = false;
    }
}
