package de.eldoria.shepard.commandmodules.privatemessages;

import de.eldoria.shepard.modulebuilder.requirements.ReqJDA;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Collection so save the last users, which send a private message to the bot instance.
 */
public final class PrivateMessageCollection implements ReqJDA {

    private final List<MessageUser> lastMessageUsers = new ArrayList<>();
    private JDA jda;

    /**
     * Create a new private message collection.
     */
    public PrivateMessageCollection() {
    }

    /**
     * Adds a user to the last contacted users. If the user is a last contacted user he is the newest contacted user.
     *
     * @param user User object
     */
    public void addUser(User user) {
        MessageUser messageUser = new MessageUser(user.getName(), user.getIdLong());
        lastMessageUsers.remove(messageUser);
        lastMessageUsers.add(0, messageUser);
        int size = lastMessageUsers.size();
        if (size > 50) {
            lastMessageUsers.remove(50);
        }
    }

    /**
     * Searches for a user in the last messages. The first user is returned. Users are sorted by last written time.
     *
     * @param name name for lookup. Can be the complete name or just a part
     * @return User or null if no match was found.
     */
    public User getUser(String name) {
        List<MessageUser> matchedUsers = lastMessageUsers.stream()
                .filter(messageUser -> messageUser.hasSimilarName(name))
                .collect(Collectors.toList());
        if (!matchedUsers.isEmpty()) {
            return jda.getUserById(matchedUsers.get(0).getId());
        }
        return null;
    }

    @Override
    public void addJDA(JDA jda) {
        this.jda = jda;
    }


    private static class MessageUser {
        private final String name;
        private final long id;

        MessageUser(String name, long id) {

            this.name = name.toLowerCase();
            this.id = id;
        }

        public String getName() {
            return name;
        }

        long getId() {
            return id;
        }

        boolean hasSimilarName(String name) {
            return this.name.startsWith(name.toLowerCase());
        }
    }
}


