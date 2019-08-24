package de.chojo.shepard.contexts.commands.util;

import de.chojo.shepard.collections.CommandCollection;
import de.chojo.shepard.database.queries.Prefix;
import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.contexts.commands.Command;
import de.chojo.shepard.contexts.commands.CommandArg;
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
    public boolean execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        String prefix = Prefix.getPrefix(receivedEvent.getGuild(), receivedEvent);

        //Command List
        if (args.length == 0) {
            return listCommands(receivedEvent);
        }

        Command command = CommandCollection.getInstance().getCommand(args[0]);
        if (command == null || !command.isContextValid(receivedEvent)) {
            Messages.sendError(new MessageEmbed.Field[] {new MessageEmbed.Field("Command not found!",
                    "Type " + prefix + "help for a full list of available commands!", false)},
                    receivedEvent.getChannel());
            return true;
        }

        //Command Help
        if (args.length == 1) {
            return commandHelp(receivedEvent.getChannel(), command);
        }


        //Arg help
        if (args.length == 2) {
            return argumentHelp(args[1], receivedEvent.getChannel(), command);

        }

        Messages.sendError(new MessageEmbed.Field[] {new MessageEmbed.Field("Usage:", "Type:\n"
                + prefix + "help for a list of commands.\n"
                + prefix + "help [command] for help for a specific command.\n"
                + prefix + "help [command] [arg] for a description of the argument.", false)},
                receivedEvent.getChannel());
        return true;
    }

    /* Sends help for a specific argument of a command.*/
    private boolean argumentHelp(String argument, MessageChannel channel, Command command) {
        command.sendCommandArgHelp(argument, channel);
        return true;
    }

    /* Sends help for a specific command with description, alias and usage.*/
    private boolean commandHelp(MessageChannel channel, Command command) {
        command.sendCommandUsage(channel);
        return true;
    }

    /* Sends a list of all commands with description */
    private boolean listCommands(MessageReceivedEvent event) {
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
        Messages.sendMessage("**__HELP__**" + System.lineSeparator() + output, event.getChannel());


        return true;
    }
}
