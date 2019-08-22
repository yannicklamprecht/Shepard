package de.chojo.shepard.database.types;

import de.chojo.shepard.database.ListType;

import java.util.Arrays;

public class ContextData {
    private boolean admin_only;
    private boolean nsfw;
    private boolean userCheckActive;
    private ListType userListType;
    private String[] userList;
    private boolean guildCheckActive;
    private ListType guildListType;
    private String[] guildList;

    public boolean isAdmin_only() {
        return admin_only;
    }

    public void setAdmin_only(boolean admin_only) {
        this.admin_only = admin_only;
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

    public String[] getUserList() {
        return userList;
    }

    public void setUserList(String[] userList) {
        this.userList = userList;
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

    public String[] getGuildList() {
        return guildList;
    }

    public void setGuildList(String[] guildList) {
        this.guildList = guildList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("  admin only: ")
                .append(isAdmin_only())
                .append(System.lineSeparator());
        builder.append("  nsfw: ")
                .append(isNsfw())
                .append(System.lineSeparator());
        builder.append("  user check active: ")
                .append(isUserCheckActive())
                .append(System.lineSeparator());
        if (isUserCheckActive()) {
            builder.append("    List Type: ")
                    .append(getUserListType())
                    .append(System.lineSeparator());
            builder.append("    Users on List: ")
                    .append(Arrays.asList(getUserList()))
                    .append(System.lineSeparator());
        }
        builder.append("  guild check active: ")
                .append(isGuildCheckActive())
                .append(System.lineSeparator());
        if (isGuildCheckActive()) {
            builder.append("    List Type: ")
                    .append(getGuildListType())
                    .append(System.lineSeparator());
            builder.append("    Guilds on List: ")
                    .append(Arrays.asList(getGuildList()));
        }
        return builder.toString();
    }
}
