package de.chojo.shepard.database.types;

import de.chojo.shepard.ShepardBot;
import de.chojo.shepard.database.ListType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
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

    /**
     * Get if the Context is admin only.
     *
     * @return true if command is admin only
     */
    public boolean isAdminOnly() {
        return adminOnly;
    }

    /**
     * Sets a context as admin only or not.
     *
     * @param adminOnly true if admin only should be on
     */
    public void setAdminOnly(boolean adminOnly) {
        this.adminOnly = adminOnly;
    }

    /**
     * Get if the context is nsfw.
     *
     * @return true if context is nsfw
     */
    public boolean isNsfw() {
        return nsfw;
    }

    /**
     * Set context as nsfw.
     *
     * @param nsfw true if a context should be nsfw
     */
    public void setNsfw(boolean nsfw) {
        this.nsfw = nsfw;
    }

    /**
     * Get if the user check of the context is enabled.
     *
     * @return true if the user check is enabled
     */
    public boolean isUserCheckActive() {
        return userCheckActive;
    }

    /**
     * Set the user check.
     *
     * @param userCheckActive true if the user check should be enabled.
     */
    public void setUserCheckActive(boolean userCheckActive) {
        this.userCheckActive = userCheckActive;
    }

    /**
     * Gets user list type.
     *
     * @return List type enum.
     */
    public ListType getUserListType() {
        return userListType;
    }

    /**
     * Set user list type.
     *
     * @param userListType set list type as enum
     */
    public void setUserListType(ListType userListType) {
        this.userListType = userListType;
    }

    /**
     * Get user a list of users associated with this context.
     *
     * @return string list of user ids
     */
    public List<String> getUserList() {
        return userList;
    }

    /**
     * Set the user list.
     *
     * @param userList Sets a list of users.
     */
    public void setUserList(String[] userList) {
        this.userList = Arrays.asList(userList);
    }

    /**
     * Get if the guild check of the context is enabled.
     *
     * @return true if the guild check is enabled
     */
    public boolean isGuildCheckActive() {
        return guildCheckActive;
    }

    /**
     * Set the guild check.
     *
     * @param guildCheckActive true if the guild check should be enabled.
     */
    public void setGuildCheckActive(boolean guildCheckActive) {
        this.guildCheckActive = guildCheckActive;
    }

    /**
     * Gets guild list type.
     *
     * @return List type enum.
     */
    public ListType getGuildListType() {
        return guildListType;
    }

    /**
     * Set guild list type.
     *
     * @param guildListType set list type as enum
     */
    public void setGuildListType(ListType guildListType) {
        this.guildListType = guildListType;
    }

    /**
     * Get user a list of guilds associated with this context.
     *
     * @return string list of guild ids
     */
    public List<String> getGuildList() {
        return guildList;
    }

    /**
     * Set user list.
     *
     * @param guildList set guild list
     */
    public void setGuildList(String[] guildList) {
        this.guildList = Arrays.asList(guildList);
    }

    @Override
    public String toString() {
        JDA jda = ShepardBot.getJDA();
        StringBuilder builder = new StringBuilder();
        builder.append("  admin_only: ")
                .append(isAdminOnly())
                .append(System.lineSeparator())
                .append("  nsfw: ")
                .append(isNsfw())
                .append(System.lineSeparator())
                .append("  user_check_active: ")
                .append(isUserCheckActive())
                .append(System.lineSeparator());
        if (isUserCheckActive()) {
            builder.append("    List_Type: ")
                    .append(getUserListType())
                    .append(System.lineSeparator())
                    .append("    Users_on_List: ");
            List<String> names = new ArrayList<>();
            getUserList().stream().forEach(u -> {
                User user = jda.getUserById(u);
                if (user != null) {
                    names.add(user.getAsTag());
                }
            });
            builder.append(String.join(", ", names))
                    .append(System.lineSeparator());
        }
        builder.append("  guild_check_active: ")
                .append(isGuildCheckActive())
                .append(System.lineSeparator());
        if (isGuildCheckActive()) {
            builder.append("    List_Type: ")
                    .append(getGuildListType())
                    .append(System.lineSeparator());
            List<String> names = new ArrayList<>();
            getGuildList().stream().forEach(g -> {
                Guild guild = jda.getGuildById(g);
                if (guild != null) {
                    Member member = guild.getOwner();
                    if (member != null) {
                        names.add(guild.getName() + " by " + member.getUser().getAsTag());
                    }
                }
            });

            builder.append("    Guilds_on_List: ")
                    .append(String.join(", ", names));
        }
        return builder.toString();
    }
}
