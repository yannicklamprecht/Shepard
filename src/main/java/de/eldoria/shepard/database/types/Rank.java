package de.eldoria.shepard.database.types;

import net.dv8tion.jda.api.entities.User;

public class Rank {
    private final User user;
    private final int score;

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
     * Get the user object.
     *
     * @return get user object
     */
    public User getUser() {
        return user;
    }

    /**
     * Get the score of the user.
     *
     * @return score
     */
    public int getScore() {
        return score;
    }
}
