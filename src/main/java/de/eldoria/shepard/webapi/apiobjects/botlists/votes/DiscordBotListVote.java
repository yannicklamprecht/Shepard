package de.eldoria.shepard.webapi.apiobjects.botlists.votes;

import lombok.Getter;

@Getter
public class DiscordBotListVote {
    private long id;

    public DiscordBotListVote(long id) {
        this.id = id;
    }
}
