package de.eldoria.shepard.webapi.apiobjects.botlists;

import lombok.Data;

@Data
public class BotsOnDiscordxyzRequest {
    private int guildCount;

    /**
     * Create new Request payload for botsondiscord.
     *
     * @param guildCount guildcount of sending
     */
    public BotsOnDiscordxyzRequest(int guildCount) {
        this.guildCount = guildCount;
    }
}
