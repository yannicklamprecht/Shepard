package de.chojo.shepard.modules.commands.util;

import de.chojo.shepard.ShepardBot;
import de.chojo.shepard.collections.CommandCollection;
import de.chojo.shepard.database.queries.Prefix;
import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.Settings;
import de.chojo.shepard.modules.commands.Command;
import de.chojo.shepard.modules.commands.CommandArg;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;

/**
 * A command for listing all possible commands.
 */
public class Help extends Command {

    public Help() {
        commandName = "help";
        commandAliases = new String[] {"Hilfe", "sendhelp"};
        commandDesc = "Alles was du wissen musst.";
        args = new CommandArg[]
                {new CommandArg("Command", "Name or Alias of Command", false),
                        new CommandArg("Argument", "One Argument of the Command", false)};
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent receivedEvent) {
        String prefix = Prefix.getPrefixes().getOrDefault(receivedEvent.getGuild().getId());

        //Command List
        if (args.length == 1) {
            return listCommands(receivedEvent);
        }

        Command command = CommandCollection.getInstance().getCommand(args[1]);
        if (command == null || !command.isCommandValid(receivedEvent)) {
            Messages.sendError(new MessageEmbed.Field[] {new MessageEmbed.Field("Command not found!", "Type " + prefix + "help for a full list of available commands!", false)}, receivedEvent.getChannel());
            return true;
        }

        //Command Help
        if (args.length == 2) {
            return commandHelp(receivedEvent.getChannel(), command);
        }


        //Arg help
        if (args.length == 3) {
            return argumentHelp(args[2], receivedEvent.getChannel(), command);

        }

        Messages.sendError(new MessageEmbed.Field[] {new MessageEmbed.Field("Usage:", "Type:\n"
                + prefix + "help for a list of commands.\n"
                + prefix + "help [command] for help for a specific command.\n"
                + prefix + "help [command] [arg] for a description of the argument.", false)}, receivedEvent.getChannel());
        return true;
    }

    /* Sends help for a specific argument of a command.*/
    private boolean argumentHelp(String arg1, MessageChannel channel, Command command) {
        CommandArg[] helpArgs = command.getArgs();


        if (helpArgs == null || helpArgs.length == 0) {
            Messages.sendError(new MessageEmbed.Field[] {new MessageEmbed.Field("No Argument found!", "This command, doesn't have any arguments.", false)}, channel);
        }

        for (CommandArg arg : helpArgs) {
            if (arg.getArgName().equalsIgnoreCase(arg1)) {
                ArrayList<MessageEmbed.Field> fields = new ArrayList<>();
                fields.add(new MessageEmbed.Field("Description:", arg.getArgDesc(), false));
                fields.add(new MessageEmbed.Field("Required", arg.getRequired().toString(), false));
                Messages.sendTextBox("Help for Argument: \"" + arg.getArgName() + "\" of command \"" + command.getCommandName() + "\"", fields, channel);
                return true;
            }
        }
        String argsAsString = "";
        for (CommandArg arg : helpArgs) {
            argsAsString = argsAsString.concat(arg.getArgName() + " ");
        }
        Messages.sendError(new MessageEmbed.Field[] {new MessageEmbed.Field("Argument not found!", "Try one of these: " + argsAsString, false)}, channel);
        return true;
    }

    /* Sends help for a specific command with description, alias and usage.*/
    private boolean commandHelp(MessageChannel channel, Command command) {
        ArrayList<MessageEmbed.Field> fields = new ArrayList<>();

        fields.add(new MessageEmbed.Field("Description:", command.getCommandDesc(), false));

        String field = "";
        String desc = "";

        if (command.getCommandAliases() != null && command.getCommandAliases().length != 0) {
            field = field.concat("Aliases");
            for (String alias : command.getCommandAliases()) {
                desc = desc.concat(alias + " ");
            }
            fields.add(new MessageEmbed.Field(field, desc, false));
        }


        field = "Usage:";
        desc = "";

        desc = desc.concat(command.getCommandName() + " ");
        if (command.getArgs() != null) {
            for (CommandArg arg : command.getArgs()) {
                if (arg.getRequired()) {
                    desc = desc.concat("[" + arg.getArgName() + "] ");
                } else {
                    desc = desc.concat("<" + arg.getArgName() + "> ");
                }
            }
        }

        fields.add(new MessageEmbed.Field(field, desc, false));

        Messages.sendTextBox("Help for command " + command.getCommandName(), fields, channel);
        return true;
    }

    /* Sends a list of all commands with description */
    private boolean listCommands(MessageReceivedEvent event) {
        ArrayList<Command> commands = CommandCollection.getInstance().getCommands();
        ArrayList<MessageEmbed.Field> fields = new ArrayList<>();

        String aliases = "";
        String output = "";

        for (Command command : commands) {
            if (!command.isCommandValid(event)) {
                continue;
            }

            aliases = aliases.concat(Prefix.getPrefixes().get(event.getGuild().getId()));
            aliases = aliases.concat(command.getCommandName() + " ");

            //Build aliases string
            if (command.getCommandAliases() != null && command.getCommandAliases().length != 0) {
                for (String alias : command.getCommandAliases()) {
                    aliases = aliases.concat(" / " + alias + "");
                }
            }

            aliases = "**" + aliases + "**";

            output = output + aliases + System.lineSeparator() + "`" + command.getCommandDesc() + "`" + System.lineSeparator();
            aliases = "";
        }

        //fields.add(new MessageEmbed.Field("help", output, false));
        Messages.sendMessage("**__HELP__**" + System.lineSeparator() + output, event.getChannel());


        return true;
    }
}
