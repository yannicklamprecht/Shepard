package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.database.queries.Prefix;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

/**
 * A command for listing all possible commands.
 */
public class Help extends Command {

    /**
     * Creates new help command object.
     */
    public Help() {
        commandName = "help";
        commandAliases = new String[] {"Hilfe", "sendhelp"};
        commandDesc = "Alles was du wissen musst.";
        arguments = new CommandArg[]
                {new CommandArg("Command", "Name or Alias of Command", false),
                        new CommandArg("Argument", "One Argument of the Command", false)};
    }

    @Override
    public void execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String prefix = Prefix.getPrefix(receivedEvent.getGuild(), receivedEvent);

        //Command List
        if (args.length == 0) {
            listCommands(receivedEvent);
            return;
        }

        Command command = CommandCollection.getInstance().getCommand(args[0]);
        if (command == null || !command.isContextValid(receivedEvent)) {
            MessageSender.sendError(new MessageEmbed.Field[] {new MessageEmbed.Field("Command not found!",
                    "Type " + prefix + "help for a full list of available commands!", false)},
                    receivedEvent.getChannel());
            return;
        }

        //Command Help
        if (args.length == 1) {
            commandHelp(receivedEvent.getChannel(), command);
            return;
        }


        //Arg help
        if (args.length == 2) {
            argumentHelp(args[1], receivedEvent.getChannel(), command);
            return;

        }

        MessageSender.sendError(new MessageEmbed.Field[] {new MessageEmbed.Field("Usage:", "Type:\n"
                + prefix + "help for a list of commands.\n"
                + prefix + "help [command] for help for a specific command.\n"
                + prefix + "help [command] [arg] for a description of the argument.", false)},
                receivedEvent.getChannel());
    }

    /* Sends help for a specific argument of a command.*/
    private void argumentHelp(String argument, MessageChannel channel, Command command) {
        command.sendCommandArgHelp(argument, channel);
    }

    /* Sends help for a specific command with description, alias and usage.*/
    private void commandHelp(MessageChannel channel, Command command) {
        command.sendCommandUsage(channel);
    }

    /* Sends a list of all commands with description */
    private void listCommands(MessageReceivedEvent event) {
        List<Command> commands = CommandCollection.getInstance().getCommands();

        String aliases = "";
        StringBuilder output = new StringBuilder();

        for (Command command : commands) {
            if (!command.isContextValid(event)) {
                continue;
            }

            aliases = aliases.concat(Prefix.getPrefix(event.getGuild(), event));
            aliases = aliases.concat(command.getCommandName() + " ");

            //Build aliases string
            if (command.getCommandAliases() != null && command.getCommandAliases().length != 0) {
                for (String alias : command.getCommandAliases()) {
                    aliases = aliases.concat(" / " + alias + "");
                }
            }

            aliases = "**" + aliases + "**";

            output.append(aliases).append(System.lineSeparator()).append("`")
                    .append(command.getCommandDesc()).append("`").append(System.lineSeparator());
            aliases = "";
        }

        //fields.add(new MessageEmbed.Field("help", output, false));
        MessageSender.sendMessage("**__HELP__**" + System.lineSeparator() + output, event.getChannel());


    }
}
