package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.database.queries.PrefixData;
import de.eldoria.shepard.localization.enums.commands.util.HelpLocale;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.util.TextLocalizer.fastLocaleAndReplace;

/**
 * A command for listing all possible commands.
 */
public class Help extends Command {

    /**
     * Creates new help command object.
     */
    public Help() {
        commandName = "help";
        commandAliases = new String[] {"sendhelp"};
        commandDesc = HelpLocale.DESCRIPTION.tag;
        commandArgs = new CommandArg[] {
                new CommandArg("Command", false,
                        new SubArg("Command", HelpLocale.A_COMMAND.tag))
        };
        category = ContextCategory.UTIL;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        //Command List
        if (args.length == 0) {
            listCommands(messageContext);
            return;
        }

        Command command = CommandCollection.getInstance().getCommand(args[0]);
        if (command == null || !command.isContextValid(messageContext)) {
            MessageSender.sendSimpleError(ErrorType.COMMAND_NOT_FOUND, messageContext.getTextChannel());
            return;
        }

        //Command Help
        if (args.length == 1) {
            commandHelp(messageContext, command);
            return;
        }

        String prefix = PrefixData.getPrefix(messageContext.getGuild(), messageContext);
        MessageSender.sendMessage(fastLocaleAndReplace(HelpLocale.M_USAGE.tag, messageContext.getGuild(),
                prefix), messageContext.getTextChannel());
    }

    /* Sends help for a specific command with description, alias and usage.*/
    private void commandHelp(MessageEventDataWrapper messageContext, Command command) {
        command.sendCommandUsage(messageContext.getTextChannel());
    }

    /* Sends a list of all commands with description */
    private void listCommands(MessageEventDataWrapper messageContext) {
        Map<ContextCategory, List<Command>> commands = new HashMap<>();

        List<LocalizedField> fields = new ArrayList<>();

        for (Command command : CommandCollection.getInstance().getCommands()) {
            if (!command.isContextValid(messageContext)) {
                continue;
            }
            commands.putIfAbsent(command.getCategory(), new ArrayList<>());
            commands.get(command.getCategory()).add(command);
        }

        fields.add(getCommandField(commands, ContextCategory.BOT_CONFIG, messageContext));
        fields.add(getCommandField(commands, ContextCategory.ADMIN, messageContext));
        fields.add(getCommandField(commands, ContextCategory.EXCLUSIVE, messageContext));
        fields.add(getCommandField(commands, ContextCategory.FUN, messageContext));
        fields.add(getCommandField(commands, ContextCategory.UTIL, messageContext));
        fields.add(new LocalizedField("", fastLocaleAndReplace(HelpLocale.M_LIST_COMMANDS.tag,
                messageContext.getGuild(),
                "`" + PrefixData.getPrefix(messageContext.getGuild(), messageContext) + "<command> help`"),
                false, messageContext));
        fields.add(new LocalizedField(HelpLocale.M_MAYBE_USEFUL.tag,
                "**[" + HelpLocale.M_INVITE_ME + "]"
                        + "(https://discordapp.com/oauth2/authorize?client_id=512413049894731780&scope=bot&permissions=1544027254), "
                        + "[" + HelpLocale.M_SUPPORT_SERVER + "](https://discord.gg/AJyFGAj)**", false, messageContext));

        fields.removeIf(Objects::isNull);

        MessageSender.sendTextBox("__**" + HelpLocale.M_COMMANDS + "**__", fields, messageContext.getTextChannel(),
                Color.green);
    }

    private String getCommandNames(List<Command> commands) {
        return commands.stream().map(command -> "`" + command.getCommandName() + "`").collect(Collectors.joining(", "));
    }

    private LocalizedField getCommandField(Map<ContextCategory, List<Command>> commands, ContextCategory category,
                                           MessageEventDataWrapper messageContext) {
        List<Command> list = commands.getOrDefault(category, Collections.emptyList());
        if (!list.isEmpty()) {
            return new LocalizedField(category.categoryName,
                    getCommandNames(list), false, messageContext);
        }
        return null;
    }
}
