package de.eldoria.shepard.collections;

import de.eldoria.shepard.ShepardBot;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PrivateMessageCollection {

    private static PrivateMessageCollection instance;

    private List<MessageUser> lastMessageUsers = new ArrayList<>();

    private PrivateMessageCollection() {
    }

    public static PrivateMessageCollection getInstance() {
        if (instance == null) {
            instance = new PrivateMessageCollection();
        }
        return instance;
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
                .filter(messageUser -> messageUser.getName().startsWith(name.toLowerCase()))
                .collect(Collectors.toList());
        if (!matchedUsers.isEmpty()) {
            return ShepardBot.getJDA().getUserById(matchedUsers.get(0).getId());
        }
        return null;
    }


    private static class MessageUser {
        private String name;
        private long id;

        MessageUser(String name, long id) {

            this.name = name.toLowerCase();
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public long getId() {
            return id;
        }
    }
}


