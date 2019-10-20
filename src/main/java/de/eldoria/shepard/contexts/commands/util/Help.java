package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.admin.Prefix;
import de.eldoria.shepard.database.queries.PrefixData;
import de.eldoria.shepard.register.ContextRegister;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.collections4.iterators.AbstractUntypedIteratorDecorator;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

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
        commandDesc = "Help for all commands and arguments.";
        commandArgs = new CommandArg[]
                {new CommandArg("Command", "Name or Alias of Command", false),
                        new CommandArg("Argument", "One Argument of the Command", false)};
        category = ContextCategory.UTIL;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String prefix = PrefixData.getPrefix(messageContext.getGuild(), messageContext);

        //Command List
        if (args.length == 0) {
            listCommands(messageContext);
            return;
        }

        Command command = CommandCollection.getInstance().getCommand(args[0]);
        if (command == null || !command.isContextValid(messageContext)) {
            MessageSender.sendError(new MessageEmbed.Field[] {new MessageEmbed.Field("Command not found!",
                            "Type " + prefix + "help for a full list of available commands!", false)},
                    messageContext.getChannel());
            return;
        }

        //Command Help
        if (args.length == 1) {
            commandHelp(messageContext.getChannel(), command);
            return;
        }


        //Arg help
        if (args.length == 2) {
            argumentHelp(args[1], messageContext.getChannel(), command);
            return;

        }

        MessageSender.sendError(new MessageEmbed.Field[] {new MessageEmbed.Field("Usage:", "Type:\n"
                        + prefix + "help for a list of commands.\n"
                        + prefix + "help [command] for help for a specific command.\n"
                        + prefix + "help [command] [arg] for a description of the argument.", false)},
                messageContext.getChannel());
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
    private void listCommands(MessageEventDataWrapper event) {
        Map<ContextCategory, List<Command>> commands = new HashMap<>();

        List<MessageEmbed.Field> fields = new ArrayList<>();

        for (Command command : CommandCollection.getInstance().getCommands()) {
            if (!command.isContextValid(event)) {
                continue;
            }
            commands.putIfAbsent(command.getCategory(), new ArrayList<>());
            commands.get(command.getCategory()).add(command);
        }

        fields.add(getCommandField(commands, ContextCategory.BOTCONFIG));
        fields.add(getCommandField(commands, ContextCategory.ADMIN));
        fields.add(getCommandField(commands, ContextCategory.EXCLUSIVE));
        fields.add(getCommandField(commands, ContextCategory.FUN));
        fields.add(getCommandField(commands, ContextCategory.UTIL));
        fields.add(new MessageEmbed.Field("", "**Use `" + PrefixData.getPrefix(event.getGuild(), event)
                + "<command> help` for more information about a command.**", false));
        fields.add(new MessageEmbed.Field("Maybe useful:",
                "**[Invite me](https://discordapp.com/oauth2/authorize?client_id=512413049894731780&scope=bot&permissions=1544027254), "
                        + "[Support Server](https://discord.gg/AJyFGAj)**", false));

        fields.removeIf(Objects::isNull);

        MessageSender.sendTextBox("__**COMMANDS**__", fields, event.getChannel(), Color.green);
    }

    private String getCommandNames(List<Command> commands) {
        return commands.stream().map(command -> "`" + command.getCommandName() + "`").collect(Collectors.joining(", "));
    }

    private MessageEmbed.Field getCommandField(Map<ContextCategory, List<Command>> commands, ContextCategory category) {
        List<Command> list = commands.getOrDefault(category, Collections.emptyList());
        if (!list.isEmpty()) {
            return new MessageEmbed.Field(category.category_Name,
                    getCommandNames(list), false);
        }
        return null;
    }
}
