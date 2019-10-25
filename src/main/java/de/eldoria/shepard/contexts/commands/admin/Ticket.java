package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.database.queries.TicketData;
import de.eldoria.shepard.database.types.TicketType;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.Replacer;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.eldoria.shepard.database.queries.TicketData.getTypeOwnerRoles;
import static de.eldoria.shepard.database.queries.TicketData.getTypeSupportRoles;
import static de.eldoria.shepard.database.queries.TicketData.removeChannel;
import static de.eldoria.shepard.util.Verifier.isArgument;
import static java.lang.System.lineSeparator;

public class Ticket extends Command {

    /**
     * Create ticket command object.
     */
    public Ticket() {
        commandName = "ticket";
        commandAliases = new String[] {"t"};
        commandDesc = "Ticket system for creation of channels to help users";
        commandArgs = new CommandArg[] {
                new CommandArg("action",
                        "**__o__pen** -> Open a new ticket" + lineSeparator()
                                + "**__c__lose** -> Close a ticket" + lineSeparator()
                                + "**__l__ist** -> Shows a list of all available ticket types",
                        true),
                new CommandArg("value",
                        "**open** -> [ticket_type] [user_name]" + lineSeparator()
                                + "**close** -> Leave empty. Execute in channel which you want to close."
                                + lineSeparator()
                                + "**list** -> Leave empty for a overview or type keyword for further type infos",
                        false)};
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        if (isArgument(cmd, "open", "o")) {
            openTicket(args, messageContext);
            return;
        }

        if (isArgument(cmd, "close", "c")) {
            close(args, messageContext);
            return;
        }

        if (isArgument(cmd, "list", "l")) {
            typeInfo(args, messageContext);
            return;
        }
        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getChannel());
    }

    private void close(String[] args, MessageEventDataWrapper receivedEvent) {
        if (args.length != 1) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, receivedEvent.getChannel());
            return;
        }


        TextChannel channel = receivedEvent.getTextChannel();
        String channelOwnerId = TicketData.getChannelOwnerId(receivedEvent.getGuild(), channel, receivedEvent);

        if (channelOwnerId == null) {
            MessageSender.sendSimpleError(ErrorType.NOT_TICKET_CHANNEL, receivedEvent.getChannel());
            return;
        }

        //Get the ticket type for caching.
        TicketType type = TicketData.getTypeByChannel(receivedEvent.getGuild(), channel, receivedEvent);

        //Removes channel from database. needed for further role checking.
        if (removeChannel(receivedEvent.getGuild(), channel, receivedEvent)) {


            //Get the ticket owner member object
            Member member = ArgumentParser.getGuildMember(receivedEvent.getGuild(), channelOwnerId);

            //If Member is present remove roles for this ticket.
            if (member != null && type != null) {
                //Get the owner roles of the current ticket. They should be removed.
                List<Role> roles = ArgumentParser.getRoles(receivedEvent.getGuild(),
                        getTypeOwnerRoles(receivedEvent.getGuild(), type.getKeyword(), receivedEvent));
                TicketHelper.removeAndUpdateTicketRoles(receivedEvent, member, roles);
            }

            //Finally delete the channel.
            channel.delete().queue();
        }
    }


    private void typeInfo(String[] args, MessageEventDataWrapper receivedEvent) {
        List<TicketType> tickets = TicketData.getTypes(receivedEvent.getGuild(), receivedEvent);
        if (tickets.isEmpty()) {
            MessageSender.sendMessage("No ticket types defined", receivedEvent.getChannel());
            return;
        }

        //Return list fo available ticket types
        if (args.length == 1) {

            TextFormatting.TableBuilder tableBuilder = TextFormatting.getTableBuilder(
                    tickets, "Keyword", "", "Category");

            for (TicketType type : tickets) {
                tableBuilder.next();
                tableBuilder.setRow(type.getKeyword(), ":", type.getCategory().getName());
            }

            MessageSender.sendMessage("**__Ticket Types with Categories:__**" + lineSeparator()
                    + tableBuilder, receivedEvent.getChannel());
        } else if (args.length == 2) {
            //Return info for one ticket type.
            TicketType type = TicketData.getTypeByKeyword(receivedEvent.getGuild(), args[1], receivedEvent);

            if (type == null) {
                MessageSender.sendSimpleError(ErrorType.TYPE_NOT_FOUND, receivedEvent.getChannel());
                return;
            }

            List<String> ownerMentions = ArgumentParser.getRoles(receivedEvent.getGuild(),
                    getTypeOwnerRoles(receivedEvent.getGuild(), type.getKeyword(), receivedEvent))
                    .stream().map(IMentionable::getAsMention).collect(Collectors.toList());

            List<String> supporterMentions = ArgumentParser.getRoles(receivedEvent.getGuild(),
                    getTypeSupportRoles(receivedEvent.getGuild(), type.getKeyword(), receivedEvent))
                    .stream().map(IMentionable::getAsMention).collect(Collectors.toList());

            List<MessageEmbed.Field> fields = new ArrayList<>();
            fields.add(new MessageEmbed.Field("Channel Category:", type.getCategory().getName(), false));
            fields.add(new MessageEmbed.Field("Creation Message:", type.getCreationMessage(), false));
            fields.add(new MessageEmbed.Field("Ticket Owner Groups:",
                    String.join(lineSeparator() + "", ownerMentions), false));
            fields.add(new MessageEmbed.Field("Ticket Supporter Groups:",
                    String.join(lineSeparator() + "", supporterMentions), false));

            MessageSender.sendTextBox("Information about Ticket Type: \"" + type.getKeyword() + "\"",
                    fields, receivedEvent.getChannel());
        }
    }

    private void openTicket(String[] args, MessageEventDataWrapper receivedEvent) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, receivedEvent.getChannel());
        }

        Member member = ArgumentParser.getGuildMember(receivedEvent.getGuild(), args[2]);
        if (member == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, receivedEvent.getChannel());
            return;
        }

        if (Verifier.equalSnowflake(member, receivedEvent.getAuthor())) {
            MessageSender.sendSimpleError(ErrorType.TICKET_SELF_ASSIGNMENT, receivedEvent.getChannel());
            return;
        }

        TicketType ticket = TicketData.getTypeByKeyword(receivedEvent.getGuild(), args[1], receivedEvent);

        if (ticket == null) {
            MessageSender.sendSimpleError(ErrorType.TYPE_NOT_FOUND, receivedEvent.getChannel());
            return;
        }

        //Set Channel Name
        String channelName = TicketData.getNextTicketCount(receivedEvent.getGuild(), receivedEvent)
                + " " + member.getUser().getName();

        //Create channel and wait for creation
        receivedEvent.getGuild()
                .createTextChannel(channelName)
                .setParent(ticket.getCategory())
                .queue(channel -> {
                    //Manage permissions for @everyone and deny read permission
                    Role everyone = receivedEvent.getGuild().getPublicRole();
                    ChannelManager manager = channel.getManager().getChannel().getManager();

                    PermissionOverrideAction everyoneOverride = manager.getChannel().createPermissionOverride(everyone);
                    everyoneOverride.setDeny(Permission.MESSAGE_READ).queue();

                    //Gives ticket owner read permission in channel
                    PermissionOverrideAction memberOverride = manager.getChannel().createPermissionOverride(member);
                    memberOverride.setAllow(Permission.MESSAGE_READ).queue();

                    //Saves channel in database

                    TicketData.createChannel(receivedEvent.getGuild(), channel,
                            member.getUser(), ticket.getKeyword(), receivedEvent);

                    //Get ticket support and owner roles
                    List<Role> supportRoles = ArgumentParser.getRoles(receivedEvent.getGuild(),
                            getTypeSupportRoles(receivedEvent.getGuild(), ticket.getKeyword(), receivedEvent));

                    List<Role> ownerRoles = ArgumentParser.getRoles(receivedEvent.getGuild(),
                            getTypeOwnerRoles(receivedEvent.getGuild(), ticket.getKeyword(), receivedEvent));
                    //Assign ticket support and owner roles
                    for (Role role : ownerRoles) {
                        receivedEvent.getGuild().addRoleToMember(member, role).queue();
                    }

                    for (Role role : supportRoles) {
                        PermissionOverrideAction override = manager.getChannel().createPermissionOverride(role);
                        override.setAllow(Permission.MESSAGE_READ).queue();
                    }

                    //Greet ticket owner in ticket channel
                    MessageSender.sendMessage(
                            Replacer.applyUserPlaceholder(member.getUser(), ticket.getCreationMessage()),
                            channel);
                });
    }
}
