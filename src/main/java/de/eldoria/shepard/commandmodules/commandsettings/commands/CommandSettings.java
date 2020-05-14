package de.eldoria.shepard.commandmodules.commandsettings.commands;

import de.eldoria.shepard.basemodules.commanddispatching.CommandHub;
import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.basemodules.commanddispatching.util.CommandSearchResult;
import de.eldoria.shepard.basemodules.commanddispatching.util.ExecutionValidator;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.commandsettings.data.CommandData;
import de.eldoria.shepard.commandmodules.commandsettings.types.ListType;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.WordsLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqCommands;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqExecutionValidator;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_CHANNELS;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_COMMAND_NAME;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_LIST_TYPE;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_CHANNELS;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_COMMAND_NAME;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_LIST_TYPE;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.C_ADD_CHANNEL;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.C_CHANNEL_LIST;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.C_DISABLE_CHANNEL_CHECK;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.C_DISABLE_COMMAND;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.C_ENABLE_CHANNEL_CHECK;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.C_ENABLE_COMMAND;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.C_REMOVE_CHANNEL;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.C_SET_LIST_TYPE;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.C_STATE_LIST;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.M_ADD_CHANNEL;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.M_BLACKLIST;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.M_CAN_BE_USED_EVERYWHERE;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.M_CAN_BE_USED_IN_CHANNEL;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.M_CAN_BE_USED_NOW;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.M_CAN_NOT_BE_USED_IN_CHANNEL;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.M_CAN_NOT_BE_USED_NOW;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.M_CAN_NOT_DISABLE_COMMAND;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.M_CAN_NOT_ENABLE_CHANNEL_CHECK;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.M_CHANNEL_SETTINGS;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.M_CHECK_NOT_ACTIVE;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.M_DISABLED_CHANNEL_CHECK;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.M_DISABLED_COMMAND;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.M_ENABLED_CHANNEL_CHECK;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.M_ENABLED_COMMAND;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.M_REMOVED_CHANNEL;
import static de.eldoria.shepard.localization.enums.commands.admin.CommandSettingsLocale.M_WHITELIST;
import static de.eldoria.shepard.localization.enums.listener.CommandListenerLocale.M_INSUFFICIENT_PERMISSION;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

@CommandUsage(EventContext.GUILD)
public class CommandSettings extends Command implements ExecutableAsync, ReqParser, ReqExecutionValidator,
        ReqDataSource, ReqCommands, ReqInit {

    private CommandData commandData;
    private DataSource source;
    private ArgumentParser parser;
    private ExecutionValidator validator;
    private CommandHub commandHub;

    /**
     * Create a new command settings command.
     */
    public CommandSettings() {
        super("commandSettings",
                new String[] {"cSettings"},
                DESCRIPTION.tag,
                SubCommand.builder("commandSettings")
                        .addSubcommand(C_ENABLE_COMMAND.tag,
                                Parameter.createCommand("enable"),
                                Parameter.createInput(A_COMMAND_NAME.tag, AD_COMMAND_NAME.tag, true))
                        .addSubcommand(C_DISABLE_COMMAND.tag,
                                Parameter.createCommand("disable"),
                                Parameter.createInput(A_COMMAND_NAME.tag, AD_COMMAND_NAME.tag, true))
                        .addSubcommand(C_STATE_LIST.tag,
                                Parameter.createCommand("stateList"))
                        .addSubcommand(C_ENABLE_CHANNEL_CHECK.tag,
                                Parameter.createCommand("enableCheck"),
                                Parameter.createInput(A_COMMAND_NAME.tag, AD_COMMAND_NAME.tag, true))
                        .addSubcommand(C_DISABLE_CHANNEL_CHECK.tag,
                                Parameter.createCommand("disableCheck"),
                                Parameter.createInput(A_COMMAND_NAME.tag, AD_COMMAND_NAME.tag, true))
                        .addSubcommand(C_SET_LIST_TYPE.tag,
                                Parameter.createCommand("setListType"),
                                Parameter.createInput(A_COMMAND_NAME.tag, AD_COMMAND_NAME.tag, true),
                                Parameter.createInput(A_LIST_TYPE.tag, AD_LIST_TYPE.tag, true))
                        .addSubcommand(C_ADD_CHANNEL.tag,
                                Parameter.createCommand("addChannel"),
                                Parameter.createInput(A_COMMAND_NAME.tag, AD_COMMAND_NAME.tag, true),
                                Parameter.createInput(A_CHANNELS.tag, AD_CHANNELS.tag, true))
                        .addSubcommand(C_REMOVE_CHANNEL.tag,
                                Parameter.createCommand("removeChannel"),
                                Parameter.createInput(A_COMMAND_NAME.tag, AD_COMMAND_NAME.tag, true),
                                Parameter.createInput(A_CHANNELS.tag, AD_CHANNELS.tag, true))
                        .addSubcommand(C_CHANNEL_LIST.tag,
                                Parameter.createCommand("channelList"),
                                Parameter.createInput(A_COMMAND_NAME.tag, AD_COMMAND_NAME.tag, true))
                        .build(),
                CommandCategory.ADMIN);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        String cmd = args[0];

        // list command states
        if (isSubCommand(cmd, 2)) {
            listcommandStates(wrapper);
            return;
        }


        String commandName = args[1];
        CommandSearchResult searchResult = parser.searchCommand(commandName);


        if (searchResult.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.COMMAND_SEARCH_EMPTY, wrapper);
            return;
        }

        if (!validator.canAccess(searchResult.command().get(), wrapper)) {
            MessageSender.sendSimpleError(ErrorType.COMMAND_SEARCH_EMPTY, wrapper);
            return;
        }

        if (!validator.canUse(searchResult.getIdentifier(), wrapper)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_INSUFFICIENT_PERMISSION.tag,
                    wrapper, "**" + searchResult.getIdentifier() + "**"),
                    wrapper.getMessageChannel());
            return;
        }

        if (commandName.contains(".") && searchResult.subCommand().isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.SUB_COMMAND_SEARCH_EMPTY, wrapper);
            return;
        }

        // enable command
        if (isSubCommand(cmd, 0)) {
            enableCommand(wrapper, searchResult);
            return;
        }

        // disable command
        if (isSubCommand(cmd, 1)) {
            disableCommand(wrapper, searchResult);
            return;
        }


        // enable channel check
        if (isSubCommand(cmd, 3)) {
            enableChannelCheck(wrapper, searchResult);
            return;
        }

        // disable channel check
        if (isSubCommand(cmd, 4)) {
            disableChannelCheck(wrapper, searchResult);
            return;
        }

        // set channel check list type
        if (isSubCommand(cmd, 5)) {
            setChannelCheckListType(args, wrapper, searchResult);
            return;
        }

        if (isSubCommand(cmd, 6)) {
            addChannel(args, wrapper, searchResult);
            return;
        }

        if (isSubCommand(cmd, 7)) {
            removeChannel(args, wrapper, searchResult);
            return;
        }

        // channel list
        if (isSubCommand(cmd, 8)) {
            channelList(wrapper, searchResult);
        }
    }

    private void listcommandStates(EventWrapper wrapper) {
        wrapper.getMessageChannel().sendTyping().queue();
        List<Command> collect = commandHub.getCommands().stream()
                .filter(c -> validator.canAccess(c, wrapper))
                .filter(c -> validator.canUse(c, wrapper))
                .collect(Collectors.toList());

        String name = TextLocalizer.localizeAll(WordsLocale.NAME.tag, wrapper);
        String stateLocale = TextLocalizer.localizeAll(WordsLocale.STATE.tag, wrapper);
        TextFormatting.TableBuilder tableBuilder =
                TextFormatting.getTableBuilder(collect, " ", name, stateLocale)
                        .setHighlighting("diff");

        for (var c : collect) {
            boolean state = commandData.getState(c, wrapper.getGuild().get());
            tableBuilder.setNextRow(
                    TextFormatting.mapBooleanTo(state, "+", "-"),
                    c.getCommandName(),
                    TextFormatting.mapBooleanTo(state,
                            TextLocalizer.localizeAll(WordsLocale.ENABLED.tag, wrapper),
                            TextLocalizer.localizeAll(WordsLocale.DISABLED.tag, wrapper)));
        }

        wrapper.getMessageChannel().sendMessage(tableBuilder.toString()).queue();
    }

    private void removeChannel(String[] args, EventWrapper wrapper,
                               CommandSearchResult searchResult) {
        boolean channelCheckActive = commandData.isChannelCheckActive(
                searchResult.command().get(), wrapper.getGuild().get());
        if (!channelCheckActive) {
            MessageSender.sendMessage(M_CHECK_NOT_ACTIVE.tag, wrapper.getMessageChannel());
            return;
        }

        Optional<TextChannel> textChannel = ArgumentParser.getTextChannel(wrapper.getGuild().get(), args[2]);
        if (textChannel.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.INVALID_CHANNEL, wrapper);
            return;
        }
        // remove channel
        if (!commandData.removeChannel(searchResult.command().get(), wrapper.getGuild().get(), textChannel.get())) {
            MessageSender.sendSimpleError(ErrorType.DATABASE_ERROR, wrapper);
            return;
        }
        ListType listType = commandData.getChannelListType(searchResult.command().get(), wrapper.getGuild().get());
        StringBuilder builder = new StringBuilder(
                localizeAllAndReplace(M_REMOVED_CHANNEL.tag, wrapper,
                        textChannel.get().getAsMention())).append("\n");
        if (listType == ListType.WHITELIST) {
            builder.append(localizeAllAndReplace(M_CAN_NOT_BE_USED_NOW.tag, wrapper,
                    searchResult.command().get().getCommandIdentifier()));
        } else {
            builder.append(localizeAllAndReplace(M_CAN_BE_USED_NOW.tag, wrapper,
                    searchResult.command().get().getCommandIdentifier()));
        }

        wrapper.getMessageChannel().sendMessage(builder.toString()).queue();
    }

    private void addChannel(String[] args, EventWrapper wrapper, CommandSearchResult searchResult) {
        Guild guild = wrapper.getGuild().get();
        boolean channelCheckActive = commandData.isChannelCheckActive(
                searchResult.command().get(), guild);
        if (!channelCheckActive) {
            MessageSender.sendMessage(M_CHECK_NOT_ACTIVE.tag, wrapper.getMessageChannel());
            return;
        }

        List<String> channelIds = ArgumentParser.getRangeAsList(args, 2);
        List<TextChannel> collect = channelIds.stream()
                .map(c -> ArgumentParser.getTextChannel(guild, c))
                .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());

        if (collect.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.INVALID_CHANNEL, wrapper);
            return;
        }
        // add channel
        for (var a : collect) {
            if (!commandData.addChannel(searchResult.command().get(), guild, a)) {
                MessageSender.sendSimpleError(ErrorType.DATABASE_ERROR, wrapper);
                return;
            }
        }

        ListType listType = commandData.getChannelListType(searchResult.command().get(), guild);
        StringBuilder builder = new StringBuilder(M_ADD_CHANNEL.tag).append("\n");

        if (listType == ListType.WHITELIST) {
            builder.append(localizeAllAndReplace(M_CAN_BE_USED_NOW.tag, guild,
                    searchResult.command().get().getCommandIdentifier()));
        } else {
            builder.append(localizeAllAndReplace(M_CAN_NOT_BE_USED_NOW.tag, guild,
                    searchResult.command().get().getCommandIdentifier()));
        }

        builder.append("\n");

        String mentions = collect.stream().map(c -> "> " + c.getAsMention()).collect(Collectors.joining("\n"));
        builder.append(mentions);

        wrapper.getMessageChannel().sendMessage(builder.toString()).queue();
    }

    private void enableCommand(EventWrapper wrapper, CommandSearchResult searchResult) {
        if (!commandData.setState(searchResult.command().get(), wrapper.getGuild().get(), true)) {
            MessageSender.sendSimpleError(ErrorType.DATABASE_ERROR, wrapper);
        }
        wrapper.getMessageChannel().sendMessage(
                localizeAllAndReplace(M_ENABLED_COMMAND.tag, wrapper.getGuild().get(),
                        searchResult.command().get().getCommandIdentifier())).queue();
    }

    private void disableCommand(EventWrapper wrapper, CommandSearchResult searchResult) {
        if (searchResult.command().get().getCommandIdentifier().equals(getCommandIdentifier())) {
            MessageSender.sendMessage(M_CAN_NOT_DISABLE_COMMAND.tag, wrapper.getMessageChannel());
            return;
        }
        if (!commandData.setState(searchResult.command().get(), wrapper.getGuild().get(), false)) {
            MessageSender.sendSimpleError(ErrorType.DATABASE_ERROR, wrapper);
            return;
        }
        wrapper.getMessageChannel().sendMessage(
                localizeAllAndReplace(M_DISABLED_COMMAND.tag, wrapper.getGuild().get(),
                        searchResult.command().get().getCommandIdentifier())).queue();
    }

    private void channelList(EventWrapper wrapper, CommandSearchResult searchResult) {
        Guild guild = wrapper.getGuild().get();
        ListType listType = commandData.getChannelListType(searchResult.command().get(), guild);
        boolean checkActive = commandData.isChannelCheckActive(searchResult.command().get(), guild);
        List<String> commandChannelList =
                commandData.getChannelList(searchResult.command().get(), guild);
        List<TextChannel> textChannels = parser.getTextChannels(guild, commandChannelList);

        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(wrapper)
                .setTitle(localizeAllAndReplace(M_CHANNEL_SETTINGS.tag, guild,
                        searchResult.command().get().getCommandName()));
        if (checkActive) {
            switch (listType) {
                case WHITELIST:
                    builder.setDescription(M_CAN_BE_USED_IN_CHANNEL + "\n");
                    break;
                case BLACKLIST:
                    builder.setDescription(M_CAN_NOT_BE_USED_IN_CHANNEL + "\n");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + listType);
            }
            for (var channel : textChannels) {
                builder.appendDescription("> " + channel.getAsMention() + "\n");
            }
        } else {
            builder.setDescription(M_CAN_BE_USED_EVERYWHERE.tag);
        }
        wrapper.getMessageChannel().sendMessage(builder.build()).queue();
    }

    private void setChannelCheckListType(String[] args, EventWrapper wrapper, CommandSearchResult searchResult) {
        ListType type = ListType.getType(args[2]);

        if (type == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_LIST_TYPE, wrapper);
            return;
        }

        if (!commandData.setChannelListType(searchResult.command().get(), wrapper.getGuild().get(), type)) {
            MessageSender.sendSimpleError(ErrorType.DATABASE_ERROR, wrapper);
            return;
        }

        MessageSender.sendMessage(localizeAllAndReplace(
                type == ListType.WHITELIST ? M_WHITELIST.tag : M_BLACKLIST.tag, wrapper,
                searchResult.command().get().getCommandName()), wrapper.getMessageChannel());
    }

    private void enableChannelCheck(EventWrapper wrapper, CommandSearchResult searchResult) {
        if (searchResult.command().get().getCommandIdentifier().equals(getCommandIdentifier())) {
            MessageSender.sendMessage(M_CAN_NOT_ENABLE_CHANNEL_CHECK.tag, wrapper.getMessageChannel());
            return;
        }
        if (!commandData.setChannelCheckActive(searchResult.command().get(), wrapper.getGuild().get(), true)) {
            MessageSender.sendSimpleError(ErrorType.DATABASE_ERROR, wrapper);
            return;
        }
        MessageSender.sendMessage(
                localizeAllAndReplace(M_ENABLED_CHANNEL_CHECK.tag, wrapper,
                        searchResult.command().get().getCommandName()),
                wrapper.getMessageChannel());
    }

    private void disableChannelCheck(EventWrapper wrapper, CommandSearchResult searchResult) {
        if (!commandData.setChannelCheckActive(searchResult.command().get(), wrapper.getGuild().get(), false)) {
            MessageSender.sendSimpleError(ErrorType.DATABASE_ERROR, wrapper);
            return;
        }
        MessageSender.sendMessage(
                localizeAllAndReplace(M_DISABLED_CHANNEL_CHECK.tag, wrapper,
                        searchResult.command().get().getCommandName()),
                wrapper.getMessageChannel());
    }

    @Override
    public void addDataSource(DataSource source) {
        this.source = source;
    }

    @Override
    public void init() {
        commandData = new CommandData(source);
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }

    @Override
    public void addExecutionValidator(ExecutionValidator validator) {
        this.validator = validator;
    }

    @Override
    public void addCommands(CommandHub commandHub) {
        this.commandHub = commandHub;
    }
}
