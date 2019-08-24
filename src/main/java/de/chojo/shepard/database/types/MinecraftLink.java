package de.chojo.shepard.database.types;

import de.chojo.shepard.ShepardBot;
import net.dv8tion.jda.api.entities.User;

public class MinecraftLink {
    private final User user;
    private final String uuid;

    public MinecraftLink(String userId, String uuid){
        this.uuid = uuid;

        user = ShepardBot.getJDA().getUserById(userId);
    }

    public MinecraftLink(User user, String uuid) {
        this.user = user;
        this.uuid = uuid;
    }

    public User getUser() {
        return user;
    }

    public String getUuid() {
        return uuid;
    }
}
