package de.eldoria.shepard.commandmodules.saucenao.data;

import de.eldoria.shepard.core.configuration.configdata.SaucenaoConfig;
import lombok.Getter;
import lombok.ToString;

@Getter
public class SauceRequests {
    private final int totalLong;
    private final int waitTotalLong;
    private final int totalShort;
    private final int waitTotalShort;
    private final int userLong;
    private final int waitUserLong;
    private final int userShort;
    private final int waitUserShort;
    private final int guildLong;
    private final int waitGuildLong;
    private final int guildShort;
    private final int waitGuildShort;

    public SauceRequests(int totalLong, int waitTotalLong, int totalShort, int waitTotalShort, int userLong,
                         int waitUserLong, int userShort, int waitUserShort, int guildLong, int waitGuildLong,
                         int guildShort, int waitGuildShort) {
        this.totalLong = totalLong;
        this.waitTotalLong = waitTotalLong;
        this.totalShort = totalShort;
        this.waitTotalShort = waitTotalShort;
        this.userLong = userLong;
        this.waitUserLong = waitUserLong;
        this.userShort = userShort;
        this.waitUserShort = waitUserShort;
        this.guildLong = guildLong;
        this.waitGuildLong = waitGuildLong;
        this.guildShort = guildShort;
        this.waitGuildShort = waitGuildShort;
    }

    public boolean totalLongLimitExceeded(SaucenaoConfig config) {
        return totalLong >= config.getLongLimit();
    }

    public boolean totalShortLimitExceeded(SaucenaoConfig config) {
        return totalShort >= config.getShortLimit();
    }

    public boolean userLongLimitExceeded(SaucenaoConfig config) {
        return userLong >= config.getLongUserLimit();
    }

    public boolean userShortLimitExceeded(SaucenaoConfig config) {
        return userShort >= config.getShortUserLimit();
    }

    public boolean guildLongLimitExceeded(SaucenaoConfig config) {
        return guildLong >= config.getLongGuildLimit();
    }

    public boolean guildShortLimitExceeded(SaucenaoConfig config) {
        return guildShort >= config.getShortGuildLimit();
    }

}
