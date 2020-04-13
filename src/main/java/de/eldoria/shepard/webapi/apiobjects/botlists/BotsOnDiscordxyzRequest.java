package de.eldoria.shepard.webapi.apiobjects.botlists;

import lombok.Data;

@Data
public class BotsOnDiscordxyzRequest {
    private int guildCount;

    public BotsOnDiscordxyzRequest(int guildCount) {
        this.guildCount = guildCount;
    }
}
