package de.chojo.shepard.modules;

import de.chojo.shepard.database.ListType;
import de.chojo.shepard.database.queries.Context;
import de.chojo.shepard.database.types.ContextData;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
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

    public ContextSensitive() {
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
    public boolean isCommandValid(MessageReceivedEvent event) {
        if (event.getChannel() instanceof TextChannel) {
            TextChannel textChannel = (TextChannel) event.getChannel();
            if (getContextData().isNsfw() && !textChannel.isNSFW()) {
                return false;
            }
        }

        if (canExecutedByUser(event) && canExecutedOnGuild(event)) {
            return hasPermission(event);
        }
        return false;
    }

    private boolean canExecutedOnGuild(MessageReceivedEvent event) {
        if (getContextData().isGuildCheckActive()) {
            if (Arrays.asList(getContextData().getGuildList()).contains(event.getGuild().getId())) {
                return getContextData().getGuildListType() != ListType.BLACKLIST;
            }
            return getContextData().getGuildListType() == ListType.BLACKLIST;
        }
        return true;
    }

    private boolean canExecutedByUser(MessageReceivedEvent event) {
        if (getContextData().isUserCheckActive()) {
            if (Arrays.asList(getContextData().getUserList()).contains(event.getAuthor().getId())) {
                return getContextData().getUserListType() != ListType.BLACKLIST;
            }
            return getContextData().getUserListType() == ListType.BLACKLIST;
        }
        return true;
    }


    private boolean hasPermission(MessageReceivedEvent event) {
        if (!getContextData().isAdmin_only()) {
            return true;
        }

        List<Role> memberRoles = Collections.emptyList();

        try {
            memberRoles = event.getMember().getRoles();
        } catch (NullPointerException e) {
            System.out.print(e.getStackTrace());
        }

        List<String> allowedRoles = getRolePermissions().get(event.getGuild().getId());

        for (Role r : memberRoles) {
            if (allowedRoles.contains(r.getId())) {
                return true;
            }
        }

        return getUserPermissions().get(event.getGuild().getId()).contains(event.getAuthor().getAvatarId())
                || event.getMember().hasPermission(Permission.ADMINISTRATOR);
    }

    private void loadCache() {
        getContextData();
        getRolePermissions();
        getUserPermissions();
    }

    public String getDebugInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append("|+++++++++++++++++++++++++++++++|").append(System.lineSeparator());
        builder.append("Context \"")
                .append(getClass().getSimpleName())
                .append("\" initialised with settings:")
                .append(System.lineSeparator());
        builder.append(getContextData().toString());
        builder.append("Roles with access to this content:").append(System.lineSeparator());
        for (var a : getRolePermissions().entrySet()) {
            builder.append("    Guild: ").append(a.getKey()).append(" -> ")
                    .append(a.getValue()).append(System.lineSeparator());
        }

        builder.append("Users with access to this content:");
        for (var a : getUserPermissions().entrySet()) {
            builder.append("    Guild: ").append(a.getKey()).append(" -> ").append(a.getValue())
                    .append(System.lineSeparator());
        }
        return builder.toString();
    }

    public void printDebugInfo(){
        System.out.println(getDebugInfo());
    }

    protected ContextData getContextData() {
        return Context.getContextData(getClass().getSimpleName());
    }

    private Map<String, List<String>> getRolePermissions() {
        return Context.getContextRolePermissions(getClass().getSimpleName());
    }

    private Map<String, List<String>> getUserPermissions() {
        return Context.getContextUserPermissions(getClass().getSimpleName());
    }
}
