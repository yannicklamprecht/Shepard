package de.eldoria.shepard.database.types;

import net.dv8tion.jda.api.entities.User;

public class Rank {
    private User user;
    private int score;

    public Rank(User user, int score) {
        this.user = user;
        this.score = score;
    }

    public User getUser() {
        return user;
    }

    public int getScore() {
        return score;
    }
}
