package de.eldoria.shepard.listener;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.database.queries.PrefixData;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.InteractableMessageSender;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.exceptions.CommandException;
import de.eldoria.shepard.messagehandler.ShepardReactions;
import de.eldoria.shepard.reactionactions.ExecuteCommand;
import de.eldoria.shepard.reactionactions.SendCommandHelp;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;


public class CommandListener extends ListenerAdapter {

    @Override
    public void onGuildMessageUpdate(@Nonnull GuildMessageUpdateEvent event) {
        if (event.getMessage().getTimeCreated().isAfter(OffsetDateTime.now().minusMinutes(5))) {
            onCommand(new MessageEventDataWrapper(event));
        }
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        onCommand(new MessageEventDataWrapper(event));
    }

    private void onCommand(MessageEventDataWrapper messageContext) {
        String receivedMessage = messageContext.getMessage().getContentRaw();
        String[] args = receivedMessage.split(" ");

        boolean isCommand = false;

        if (Verifier.checkPrefix(receivedMessage, messageContext)) {
            isCommand = true;
            args[0] = args[0].replaceFirst(PrefixData.getPrefix(messageContext.getGuild(), messageContext), "");

        } else if (DbUtil.getIdRaw(args[0]).contentEquals(ShepardBot.getJDA().getSelfUser().getId())) {
            args = Arrays.copyOfRange(args, 1, args.length);
            isCommand = true;
        }

        String label = args[0];

        if (isCommand) {
            //BotCheck
            if (messageContext.getAuthor().isBot()) {
                MessageSender.sendMessage("I'm not allowed to talk to you " + messageContext.getAuthor().getName()
                        + ". Please leave me alone ._.", messageContext.getChannel());
                return;
            }

            //Command execution
            Command command = CommandCollection.getInstance().getCommand(label);

            if (args.length > 1) {
                args = Arrays.copyOfRange(args, 1, args.length);
            } else {
                args = new String[0];
            }
            if (command != null && command.isContextValid(messageContext)) {
                if(args.length > 0 && args[0].equalsIgnoreCase("help")){
                    command.sendCommandUsage(messageContext.getChannel());
                    return;
                }
                if (command.checkArguments(args)) {
                    try {
                        command.execute(label, args, messageContext);
                    } catch (CommandException | InsufficientPermissionException e) {
                        try {
                            MessageSender.sendSimpleErrorEmbed(e.getMessage(), messageContext.getChannel());
                        } catch (InsufficientPermissionException ex) {
                            messageContext.getAuthor().openPrivateChannel().queue(privateChannel ->
                                    MessageSender.sendSimpleErrorEmbed(ex.getMessage(), privateChannel));
                        }
                    }
                } else {
                    try {
                        MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getChannel());
                        command.sendCommandUsage(messageContext.getChannel());
                    } catch (InsufficientPermissionException ex) {
                        messageContext.getAuthor().openPrivateChannel().queue(privateChannel ->
                                MessageSender.sendSimpleErrorEmbed(ex.getMessage(), privateChannel));
                    }

                }
                return;
            } else if (command != null && command.canBeExecutedHere(messageContext)) {
                MessageSender.sendMessage("Insufficient permission for context **"
                        + command.getClass().getSimpleName().toUpperCase()
                        + "**. Ask a Server Administrator for permission.", messageContext.getChannel());
                return;
            }

            List<Command> similarCommand = CommandCollection.getInstance().getSimilarCommands(label);
            if (similarCommand.size() != 0) {
                for (Command cmd : similarCommand) {
                    if (cmd.isContextValid(messageContext)) {
                        InteractableMessageSender.sendSimpleTextBox("Command not found!",
                                "I don't have a command with this name. Maybe you meant: "
                                        + System.lineSeparator() + "**" + cmd.getCommandName() + "**",
                                Color.green, ShepardReactions.WINK, messageContext.getTextChannel(),
                                new ExecuteCommand(messageContext.getAuthor(), cmd, args, messageContext),
                                new SendCommandHelp(cmd, messageContext));
                        return;
                    }
                }
            }

            MessageSender.sendError(new MessageEmbed.Field[] {new MessageEmbed.Field("Command not found!", "Type "
                    + PrefixData.getPrefix(messageContext.getGuild(), messageContext)
                    + "help for a full list of available commands!", false)}, messageContext.getChannel());

        }

    }

}

