package de.eldoria.shepard.contexts;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.database.ListType;
import de.eldoria.shepard.database.queries.ContextData;
import de.eldoria.shepard.database.types.ContextSettings;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
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
     * If the command is a NSFW Command and the channel is SFW, the context is not valid.
     * Checks also if a context can be executed by guild and user {@link #canBeExecutedHere(MessageEventDataWrapper)}
     *
     * @param context the context of the command.
     * @return {@code true} if the command is valid, {@code false} otherwise.
     */
    public boolean isContextValid(MessageEventDataWrapper context) {
        if (getContextData().isNsfw() && !context.getTextChannel().isNSFW()) {
            return false;
        }

        return canBeExecutedHere(context);
    }

    /**
     * Returns if the context can be executed on this guild by this user.
     * Checks first if the {@link Guild} is allowed to use the context.
     * If the guild is allowed to use it, it checks if the {@link Member} is allowed to use it.
     * {@code false} if the user is allowed to use it, but the guild is not.
     *
     * @param context the context of the command.
     * @return {@code true} if the context can be executed on guild by user.
     */
    private boolean canBeExecutedHere(MessageEventDataWrapper context) {
        return canExecutedByUser(context) && canExecutedOnGuild(context);
    }

    /**
     * Checks if the context has an active guild check and if the guild is listed.
     * Determines based on the {@link ListType} type if the guild is allowed to use this context or not.
     *
     * @param context context for lookup
     * @return true if the context is executeable on this guild
     */
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

    /**
     * Checks if the context has an active guild check and if the user is listed.
     * Determines based on the {@link ListType} type if the user is allowed to use this context or not.
     *
     * @param context context for lookup
     * @return true if the context is executeable by this user
     */
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


    /**
     * Checks if the user needs a permission to execute this context.
     * Applies guild permission override.
     * If a permission is needed, it checks if a role or the user himself has the permission to execute this context.
     * Should be used after {@link #canBeExecutedHere(MessageEventDataWrapper)}.
     *
     * @param context message context
     * @return true if the user is allowed to execute this command
     */
    protected boolean hasPermission(MessageEventDataWrapper context) {
        if (!needsPermission(context.getGuild())) {
            return true;
        }

        Member member = context.getMember();
        //Checks if a command is not admin only and override is inactive or if the member is a administrator
        if ((member != null && member.hasPermission(Permission.ADMINISTRATOR))) {
            return true;
        }


        List<Role> memberRoles = member != null ? member.getRoles() : Collections.emptyList();

        List<String> allowedRoles = getRolePermissions(context).get(context.getGuild().getId());

        if (allowedRoles == null) {
            allowedRoles = Collections.emptyList();
        }

        //Check if one of the user roles has the permission for this context.
        for (Role r : memberRoles) {
            if (allowedRoles.contains(r.getId())) {
                return true;
            }
        }

        //Check if the user has the permission
        List<String> allowedUsers = getUserPermissions(context).get(context.getGuild().getId());
        if (allowedUsers == null) {
            allowedUsers = Collections.emptyList();
        }

        return allowedUsers.contains(context.getAuthor().getId());
    }

    /**
     * Load the cache for the context.
     */
    private void loadCache() {
        getContextData();
        getRolePermissions(null);
        getUserPermissions(null);
    }

    /**
     * Get all information about a context.
     *
     * @return Information of context as a string.
     */
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
     *
     * @return a context settings object.
     */
    public ContextSettings getContextData() {
        return ContextData.getContextData(getClass().getSimpleName(), null);
    }

    public List<Role> getRolesWithPermissions(MessageEventDataWrapper messageContext) {
        return ArgumentParser.getRoles(messageContext.getGuild(),
                getRolePermissions(messageContext)
                        .getOrDefault(messageContext.getGuild().getId(), Collections.emptyList()));
    }

    public List<User> getUsersWithPermissions(MessageEventDataWrapper messageContext) {
        return ArgumentParser.getGuildUsers(messageContext.getGuild(),
                getUserPermissions(messageContext)
                        .getOrDefault(messageContext.getGuild().getId(), Collections.emptyList()));
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
     * Get the context name.
     *
     * @return context name
     */
    public String getContextName() {
        return getClass().getSimpleName();
    }

    /**
     * Check if the command can only be used by an administrator by default.
     *
     * @return true if only an administrator can use this command.
     */
    public boolean isAdmin() {
        return getContextData().isAdminOnly();
    }

    /**
     * Checks if a context needs a permission based on the permission override of the guild.
     *
     * @param guild guild for lookup
     * @return true of a permission is needed to execute this command
     */
    public boolean needsPermission(Guild guild) {
        return overrideActive(guild) ? !isAdmin() : isAdmin();
    }

    /**
     * Checks if a permission override is active for this guild.
     *
     * @param guild guild for lookup
     * @return true if a override is active
     */
    public boolean overrideActive(Guild guild) {
        return getContextData().hasGuildPermissionOverride(guild);
    }
}
