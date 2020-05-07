package de.eldoria.shepard.webapi.apiobjects;

import lombok.Getter;
import net.dv8tion.jda.api.entities.User;

@Getter
public class UserInfo {
    private final long id;
    private final String tag;
    private final String avatarUrl;

    /**
     * Create a new user info from user object.
     *
     * @param user user object
     */
    public UserInfo(User user) {
        id = user.getIdLong();
        tag = user.getAsTag();
        avatarUrl = user.getEffectiveAvatarUrl();
    }
}
