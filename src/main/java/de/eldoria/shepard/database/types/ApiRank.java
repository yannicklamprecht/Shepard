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

    public ApiRank(int rank, User user, long score) {
        id = user.getIdLong();
        tag = user.getAsTag();
        avatarUrl = user.getEffectiveAvatarUrl();
        this.rank = rank;
        this.score = score;
    }

    public ApiRank(int rank, long id, long score) {
        this.rank = rank;
        this.tag = id + "";
        avatarUrl = ShepardBot.getJDA().getSelfUser().getAvatarUrl();
        this.id = id;
        this.score = score;
    }
}
