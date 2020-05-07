package de.eldoria.shepard.webapi.apiobjects.botlists.votes;

import lombok.Getter;

@Getter
public class DiscordBotListVote {
    private final long id;

    /**
     * Create a vote object.
     * @param id user snowflake
     */
    public DiscordBotListVote(long id) {
        this.id = id;
    }
}
