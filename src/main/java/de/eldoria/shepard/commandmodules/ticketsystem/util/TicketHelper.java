package de.eldoria.shepard.commandmodules.ticketsystem.util;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.ticketsystem.commands.Ticket;
import de.eldoria.shepard.commandmodules.ticketsystem.commands.TicketSettings;
import de.eldoria.shepard.commandmodules.ticketsystem.data.TicketData;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.HierarchyException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Holds methods used in {@link Ticket} and {@link TicketSettings}.
 */
public final class TicketHelper {
    private TicketHelper() {
    }

    /**
     * Removes the roles from a user, but secures, that he keeps all necessary roles for other tickets.
     *
     * @param ticketData    data for updating
     * @param parser        parser for user and role parsing
     * @param wrapper       Received event of the message.
     * @param member        member to change roles
     * @param rolesToRemove roles as string list
     * @return role which cant be changed.
     * @throws HierarchyException when the role of the user is higher than shepards role.
     */
    public static Role removeAndUpdateTicketRoles(TicketData ticketData, ArgumentParser parser,
                                                  EventWrapper wrapper,
                                                  Member member, List<Role> rolesToRemove) throws HierarchyException {
        //Get all other ticket channels of the owner
        List<String> channelIds = ticketData.getChannelIdsByOwner(wrapper.getGuild().get(),
                member.getUser(), wrapper);

        List<TextChannel> channels = parser.getTextChannels(wrapper.getGuild().get(), channelIds);

        //Create a set of all roles the player should keep.
        Set<Role> newRoleSet = new HashSet<>();
        for (TextChannel channel : channels) {
            List<Role> roles = parser.getRoles(wrapper.getGuild().get(),
                    ticketData.getChannelOwnerRoles(wrapper.getGuild().get(), channel));
            newRoleSet.addAll(roles);
        }

        //Removes all roles for the current ticket
        for (Role role : rolesToRemove) {
            if (!wrapper.getGuild().get().getSelfMember().canInteract(role)) {
                return role;
            }
            wrapper.getGuild().get().removeRoleFromMember(member, role).queue();
        }

        //Adds all roles for the other tickets. needed if two ticket types use the same role or
        // if there are more than one ticket channel with this type.
        for (Role role : newRoleSet) {
            if (!wrapper.getGuild().get().getSelfMember().canInteract(role)) {
                return role;
            }
            wrapper.getGuild().get().addRoleToMember(member, role).queue();
        }
        return null;
    }
}
