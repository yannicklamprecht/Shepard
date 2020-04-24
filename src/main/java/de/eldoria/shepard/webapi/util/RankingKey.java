package de.eldoria.shepard.webapi.util;

import com.google.common.base.Objects;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.User;

import java.util.List;
import java.util.stream.Collectors;

public class RankingKey {
    private final int page;
    private final int pagesize;
    private final long guild;
    private final Long[] users;

    public RankingKey(int page, int pagesize) {
        this.page = page;
        this.pagesize = pagesize;
        this.guild = 0;
        this.users = new Long[0];
    }

    public RankingKey(int page, int pagesize, List<User> users) {
        this.page = page;
        this.pagesize = pagesize;
        this.guild = 0;
        Long[] userArray = new Long[users.size()];
        this.users = users.stream().map(ISnowflake::getIdLong).collect(Collectors.toList()).toArray(userArray);
    }

    public RankingKey(int page, int pagesize, long guild) {
        this.page = page;
        this.pagesize = pagesize;
        this.guild = guild;
        this.users = new Long[0];
    }

    public RankingKey(int page, int pagesize, long guild, List<User> users) {
        this.page = page;
        this.pagesize = pagesize;
        this.guild = guild;
        Long[] userArray = new Long[users.size()];
        this.users = users.stream().map(ISnowflake::getIdLong).collect(Collectors.toList()).toArray(userArray);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RankingKey that = (RankingKey) o;
        return page == that.page &&
                pagesize == that.pagesize &&
                guild == that.guild &&
                Objects.equal(users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(page, pagesize, guild, users);
    }
}
