package de.eldoria.shepard.contexts;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.ListType;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.database.types.ContextSettings;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A {@link ListenerAdapter} filtering events for different contexts.
 */
public abstract class ContextSensitive {
    /**
     * Category of the context.
     */
    protected ContextCategory category;

    /**
     * Creates a new context Sensitive event.
     */
    protected ContextSensitive() {
        loadCache();
    }

    /**
     * Get whether a command is valid or not.
     * A command is valid if it is not excluded to use on a specific guild
     * or if the author is restricted to use it.
     *
     * @param context the context of the command.
     * @return {@code true} if the command is valid, {@code false} otherwise.
     */
    public boolean isContextValid(MessageEventDataWrapper context) {
        if (context.getChannel() instanceof TextChannel) {
            TextChannel textChannel = (TextChannel) context.getChannel();
            if (getContextData().isNsfw() && !textChannel.isNSFW()) {
                return false;
            }
        }

        if (canBeExecutedHere(context)) {
            return hasPermission(context);
        }
        return false;
    }

    /**
     * Returns if the context can be executed on this guild by this user, if he has the permission.
     *
     * @param context the context of the command.
     * @return {@code true} if the context can be executed on guild by user with permissions.
     */
    public boolean canBeExecutedHere(MessageEventDataWrapper context) {
        return canExecutedByUser(context) && canExecutedOnGuild(context);
    }

    private boolean canExecutedOnGuild(MessageEventDataWrapper context) {
        ContextSettings data = getContextData();
        if (data.isGuildCheckActive()) {
            if (data.getGuildList().contains(context.getGuild().getId())) {
                return data.getGuildListType() == ListType.WHITELIST;
            }
            return data.getGuildListType() != ListType.WHITELIST;
        }
        return true;
    }

    private boolean canExecutedByUser(MessageEventDataWrapper context) {
        ContextSettings data = getContextData();
        if (data.isUserCheckActive()) {
            if (data.getUserList().contains(context.getAuthor().getId())) {
                return data.getUserListType() == ListType.WHITELIST;
            }
            return data.getUserListType() != ListType.WHITELIST;
        }
        return true;
    }


    private boolean hasPermission(MessageEventDataWrapper context) {
        Member member = context.getMember();
        if (!getContextData().isAdminOnly()
                || (member != null && member.hasPermission(Permission.ADMINISTRATOR))) {
            return true;
        }


        List<Role> memberRoles = member != null ? member.getRoles() : Collections.emptyList();

        List<String> allowedRoles = getRolePermissions(context).get(context.getGuild().getId());

        if (allowedRoles == null) {
            allowedRoles = Collections.emptyList();
        }

        for (Role r : memberRoles) {
            if (allowedRoles.contains(r.getId())) {
                return true;
            }
        }

        List<String> allowedUsers = getUserPermissions(context).get(context.getGuild().getId());
        if (allowedUsers == null) {
            allowedUsers = Collections.emptyList();
        }

        return allowedUsers.contains(context.getAuthor().getId());
    }

    private void loadCache() {
        getContextData();
        getRolePermissions(null);
        getUserPermissions(null);
    }

    private String getDebugInfo() {
        JDA jda = ShepardBot.getJDA();
        StringBuilder builder = new StringBuilder();
        builder.append(System.lineSeparator())
                .append("Context \"")
                .append(getClass().getSimpleName().toUpperCase())
                .append("\" initialised with settings:")
                .append(System.lineSeparator())
                .append(getContextData().toString())
                .append("  Roles with access to this context:").append(System.lineSeparator());

        for (Map.Entry<String, List<String>> roles : getRolePermissions(null).entrySet()) {
            StringBuilder names = new StringBuilder();

            for (String roleId : roles.getValue()) {
                Guild currentGuild = jda.getGuildById(roles.getKey());
                if (currentGuild != null) {
                    Role role = currentGuild.getRoleById(roleId);
                    if (role != null) {
                        names.append("      ").append(role.getName()).append(System.lineSeparator());
                    }
                }
            }

            builder.append("    Guild: ").append(jda.getGuildById(roles.getKey()))
                    .append(" (").append(roles.getKey()).append("):")
                    .append(System.lineSeparator()).append(names.toString())
                    .append(System.lineSeparator());
        }


        builder.append("  Users with access to this context:");
        for (Map.Entry<String, List<String>> userPermission : getUserPermissions(null).entrySet()) {
            String guild = jda.getGuildById(userPermission.getKey()) + " (" + userPermission.getKey() + "):";

            StringBuilder names = new StringBuilder();
            for (String userId : userPermission.getValue()) {
                User user = jda.getUserById(userId);
                if (user != null) {
                    names.append("      ").append(user.getAsTag()).append(System.lineSeparator());
                }
            }

            builder.append("    Guild: ").append(guild).append(System.lineSeparator()).append(names.toString())
                    .append(System.lineSeparator());
        }
        return builder.toString();
    }

    /**
     * Prints debug info of the context.
     */
    public void printDebugInfo() {
        ShepardBot.getLogger().info(getDebugInfo());
    }

    /**
     * Get the settings of the context.
     * @return a context settings object.
     */
    public ContextSettings getContextData() {
        return ContextData.getContextData(getClass().getSimpleName(), null);
    }

    private Map<String, List<String>> getRolePermissions(MessageEventDataWrapper context) {
        return ContextData.getContextRolePermissions(getClass().getSimpleName(), context);
    }

    private Map<String, List<String>> getUserPermissions(MessageEventDataWrapper context) {
        return ContextData.getContextUserPermissions(getClass().getSimpleName(), context);
    }

    /**
     * Get the category of the context.
     *
     * @return category
     */
    public ContextCategory getCategory() {
        return category;
    }

    /**
     * Get the context name. Context names are always all caps!
     *
     * @return context name
     */
    public String getContextName() {
        return getClass().getSimpleName().toUpperCase();
    }
}
