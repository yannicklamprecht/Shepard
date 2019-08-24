package de.chojo.shepard.database.types;

import de.chojo.shepard.database.ListType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ContextData {
    private boolean adminOnly;
    private boolean nsfw;
    private boolean userCheckActive;
    private ListType userListType;
    private List<String> userList;
    private boolean guildCheckActive;
    private ListType guildListType;
    private List<String> guildList;

    public boolean isAdminOnly() {
        return adminOnly;
    }

    public void setAdminOnly(boolean admin_only) {
        this.adminOnly = admin_only;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    public void setNsfw(boolean nsfw) {
        this.nsfw = nsfw;
    }

    public boolean isUserCheckActive() {
        return userCheckActive;
    }

    public void setUserCheckActive(boolean userCheckActive) {
        this.userCheckActive = userCheckActive;
    }

    public ListType getUserListType() {
        return userListType;
    }

    public void setUserListType(ListType userListType) {
        this.userListType = userListType;
    }

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(String[] userList) {
        this.userList = Arrays.asList(userList);
    }

    public boolean isGuildCheckActive() {
        return guildCheckActive;
    }

    public void setGuildCheckActive(boolean guildCheckActive) {
        this.guildCheckActive = guildCheckActive;
    }

    public ListType getGuildListType() {
        return guildListType;
    }

    public void setGuildListType(ListType guildListType) {
        this.guildListType = guildListType;
    }

    public List<String> getGuildList() {
        return guildList;
    }

    public void setGuildList(String[] guildList) {
        this.guildList = Arrays.asList(guildList);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("  admin only: ")
                .append(isAdminOnly())
                .append(System.lineSeparator())
                .append("  nsfw: ")
                .append(isNsfw())
                .append(System.lineSeparator())
                .append("  user check active: ")
                .append(isUserCheckActive())
                .append(System.lineSeparator());
        if (isUserCheckActive()) {
            builder.append("    List Type: ")
                    .append(getUserListType())
                    .append(System.lineSeparator())
                    .append("    Users on List: ")
                    .append(Collections.singletonList(getUserList()))
                    .append(System.lineSeparator());
        }
        builder.append("  guild check active: ")
                .append(isGuildCheckActive())
                .append(System.lineSeparator());
        if (isGuildCheckActive()) {
            builder.append("    List Type: ")
                    .append(getGuildListType())
                    .append(System.lineSeparator())
                    .append("    Guilds on List: ")
                    .append(Collections.singletonList(getGuildList()));
        }
        return builder.toString();
    }
}
