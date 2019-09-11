package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.database.queries.TicketData;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.eldoria.shepard.database.queries.TicketData.getChannelOwnerRoles;

final class TicketHelper {
    private TicketHelper() {
    }

    /**
     * Removes the roles from a user, but secures, that he keeps all necessary roles for other tickets.
     *
     * @param receivedEvent Received event of the message.
     * @param member        member to change roles
     * @param rolesToRemove roles as string list
     */
    static void removeAndUpdateTicketRoles(MessageEventDataWrapper receivedEvent,
                                           Member member, List<String> rolesToRemove) {
        List<Role> removeRoles = new ArrayList<>();

        //Get the role objects if the role exists.
        for (String roleId : rolesToRemove) {
            Role roleById = receivedEvent.getGuild().getRoleById(roleId);
            if (roleById != null) {
                removeRoles.add(roleById);
            }
        }

        //Get all other ticket channels of the owner
        List<String> channelIdsByOwner = TicketData.getChannelIdsByOwner(receivedEvent.getGuild(),
                member.getUser(), receivedEvent);


        List<TextChannel> channels = new ArrayList<>();

        //Get the channel objects
        for (String channelId : channelIdsByOwner) {
            TextChannel textChannel = receivedEvent.getGuild().getTextChannelById(channelId);
            if (textChannel != null) {
                channels.add(textChannel);
            }
        }

        //Create a set of all roles the player should keep.
        Set<Role> newRoleSet = new HashSet<>();
        for (TextChannel channel : channels) {
            for (String s : getChannelOwnerRoles(receivedEvent.getGuild(), channel, receivedEvent)) {
                Role role = receivedEvent.getGuild().getRoleById(s);
                if (role != null) {
                    newRoleSet.add(role);
                }
            }
        }

        //Removes all roles for the current ticket
        for (Role role : removeRoles) {
            receivedEvent.getGuild().removeRoleFromMember(member, role).queue();
        }

        //Adds all roles for the other tickets. needed if two ticket types use the same role or
        // if there are more than one ticket channel with this type.
        for (Role role : newRoleSet) {
            receivedEvent.getGuild().addRoleToMember(member, role).queue();
        }
    }

}
