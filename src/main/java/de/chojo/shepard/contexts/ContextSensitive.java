package de.chojo.shepard.contexts;

import de.chojo.shepard.ShepardBot;
import de.chojo.shepard.database.ListType;
import de.chojo.shepard.database.queries.Context;
import de.chojo.shepard.database.types.ContextData;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A {@link ListenerAdapter} filtering events for different contexts.
 */
public class ContextSensitive extends ListenerAdapter {

    protected ContextSensitive() {
        loadCache();
        printDebugInfo();
    }

    /**
     * Get whether a command is valid or not.
     * A command is valid if it is not excluded to use on a specific guild
     * or if the author is restricted to use it.
     *
     * @param event the event of the command.
     * @return {@code true} if the command is valid, {@code false} otherwise.
     */
    public boolean isContextValid(MessageReceivedEvent event) {
        if (event.getChannel() instanceof TextChannel) {
            TextChannel textChannel = (TextChannel) event.getChannel();
            if (getContextData(event).isNsfw() && !textChannel.isNSFW()) {
                return false;
            }
        }

        if (canExecutedByUser(event) && canExecutedOnGuild(event)) {
            return hasPermission(event);
        }
        return false;
    }

    private boolean canExecutedOnGuild(MessageReceivedEvent event) {
        if (getContextData(event).isGuildCheckActive()) {
            if (Collections.singletonList(getContextData(event).getGuildList()).contains(event.getGuild().getId())) {
                return getContextData(event).getGuildListType() != ListType.BLACKLIST;
            }
            return getContextData(event).getGuildListType() == ListType.BLACKLIST;
        }
        return true;
    }

    private boolean canExecutedByUser(MessageReceivedEvent event) {
        if (getContextData(event).isUserCheckActive()) {
            if (Collections.singletonList(getContextData(event).getUserList()).contains(event.getAuthor().getId())) {
                return getContextData(event).getUserListType() != ListType.BLACKLIST;
            }
            return getContextData(event).getUserListType() == ListType.BLACKLIST;
        }
        return true;
    }


    private boolean hasPermission(MessageReceivedEvent event) {
        if (!getContextData(event).isAdminOnly()) {
            return true;
        }

        List<Role> memberRoles = Collections.emptyList();

        Member member = event.getMember();

        if (member != null) {
            memberRoles = member.getRoles();
        }

        List<String> allowedRoles = getRolePermissions(event).get(event.getGuild().getId());

        for (Role r : memberRoles) {
            if (allowedRoles.contains(r.getId())) {
                return true;
            }
        }

        return getUserPermissions(event).get(event.getGuild().getId()).contains(event.getAuthor().getAvatarId())
                || event.getMember().hasPermission(Permission.ADMINISTRATOR);
    }

    private void loadCache() {
        getContextData(null);
        getRolePermissions(null);
        getUserPermissions(null);
    }

    private String getDebugInfo() {
        JDA jda = ShepardBot.getJDA();
        StringBuilder builder = new StringBuilder();
        builder.append("|+++++++++++++++++++++++++++++++|").append(System.lineSeparator());
        builder.append("Context \"")
                .append(getClass().getSimpleName().toUpperCase())
                .append("\" initialised with settings:")
                .append(System.lineSeparator());
        builder.append(getContextData(null).toString());
        builder.append("  Roles with access to this context:").append(System.lineSeparator());

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

            builder.append("    Guild: ").append(jda.getGuildById(roles.getKey())).append(" (").append(roles.getKey()).append("):")
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

    public void printDebugInfo() {
        System.out.println(getDebugInfo());
    }

    private ContextData getContextData(MessageReceivedEvent event) {
        return Context.getContextData(getClass().getSimpleName(), event);
    }

    private Map<String, List<String>> getRolePermissions(MessageReceivedEvent event) {
        return Context.getContextRolePermissions(getClass().getSimpleName(), event);
    }

    private Map<String, List<String>> getUserPermissions(MessageReceivedEvent event) {
        return Context.getContextUserPermissions(getClass().getSimpleName(), event);
    }
}
