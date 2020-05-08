package de.eldoria.shepard.webapi.apiobjects;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;

@Getter
public class ShardStatistic {
    private final int shard;
    private final JDA.Status status;
    private final long usercount;
    private final long guildCount;
    private final long commandsDispatched;
    private final long eventsFired;

    public ShardStatistic(int shard, JDA.Status status, long userCount, long guildCount, long commandsDispatched, long eventsFired) {
        this.shard = shard;
        this.status = status;
        this.usercount = userCount;
        this.guildCount = guildCount;
        this.commandsDispatched = commandsDispatched;
        this.eventsFired = eventsFired;
    }
}
