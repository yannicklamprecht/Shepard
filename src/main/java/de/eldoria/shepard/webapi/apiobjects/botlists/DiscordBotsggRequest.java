package de.eldoria.shepard.webapi.apiobjects.botlists;

import lombok.Data;

@Data
public class DiscordBotsggRequest {
    private int guildCount;
    private int shardCount;
    private int shardId;

    public DiscordBotsggRequest(int guildCount, int shardCount, int shardId) {
        this.guildCount = guildCount;
        this.shardCount = shardCount;
        this.shardId = shardId;
    }
}
