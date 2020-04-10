package de.eldoria.shepard.database.types;

import de.eldoria.shepard.ShepardBot;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;

@Getter
@Slf4j
public class ApiRank {
    private int rank;
    private long id;
    private String tag;
    private String avatarUrl;
    private long score;

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
     *
     * @param rank  rank rank as number
     * @param id    id of the user will used as name
     * @param score score of the user
     */
    public ApiRank(int rank, long id, long score) {
        this.rank = rank;
        this.tag = id + "";
        avatarUrl = ShepardBot.getJDA().getSelfUser().getAvatarUrl();
        this.id = id;
        this.score = score;
    }
}
