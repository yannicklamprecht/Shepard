package de.eldoria.shepard.webapi.apiobjects;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

@Getter
@Slf4j
public class ApiRank {
    private final int rank;
    private final long id;
    private final String tag;
    private final String avatarUrl;
    private final long score;

    /**
     * Create a new API Rank object.
     *
     * @param rank  rank as number
     * @param user  user of the rank
     * @param score score of the user
     */
    public ApiRank(int rank, User user, long score) {
        id = user.getIdLong();
        tag = user.getAsTag();
        avatarUrl = user.getEffectiveAvatarUrl();
        this.rank = rank;
        this.score = score;
    }

    /**
     * Creates a new API Rank object without assigning a user.
     * This rank has a default {@link ApiRank#avatarUrl} and the id as {@link ApiRank#tag}
     *  @param shardManager jda object
     * @param rank  rank rank as number
     * @param id    id of the user will used as name
     * @param score score of the user
     */
    public ApiRank(ShardManager shardManager, int rank, long id, long score) {
        this.rank = rank;
        this.tag = id + "";
        avatarUrl = shardManager.getShardById(0).getSelfUser().getAvatarUrl();
        this.id = id;
        this.score = score;
    }
}
