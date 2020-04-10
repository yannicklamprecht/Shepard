package de.eldoria.shepard.database.types;

import lombok.Getter;
import net.dv8tion.jda.api.entities.User;

@Getter
public class Rank {
    private final User user;
    private final int score;
    private int rank;

    /**
     * Creates a new rank.
     *
     * @param user  user for rank
     * @param score score of the user.
     */
    public Rank(User user, int score) {
        this.user = user;
        this.score = score;
    }

    /**
     * Creates a new rank.
     *
     * @param user  user for rank
     * @param score score of the user.
     */
    public Rank(int rank, User user, int score) {
        this(user, score);
        this.rank = rank;
    }
}
