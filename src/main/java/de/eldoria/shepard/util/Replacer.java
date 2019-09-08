package de.eldoria.shepard.util;

import net.dv8tion.jda.api.entities.User;

public final class Replacer {
    private Replacer() {
    }

    /**
     * Applies the common placeholder for users to a message.
     *
     * @param user    User for which the message should be customized
     * @param message the message which should be customized
     * @return customized message
     */
    public static String applyUserPlaceholder(User user, String message) {
        return message.replace("{user_name}", user.getName())
                .replace("{user_tag}", user.getAsTag())
                .replace("{user_mention}", user.getAsMention());
    }
}
