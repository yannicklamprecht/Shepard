package de.eldoria.shepard.commandmodules.greeting.commands;

import de.eldoria.shepard.basemodules.commanddispatching.dialogue.Dialog;
import de.eldoria.shepard.basemodules.commanddispatching.dialogue.DialogHandler;
import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.greeting.data.GreetingData;
import de.eldoria.shepard.commandmodules.greeting.types.GreetingSettings;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.Replacement;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.*;
import de.eldoria.shepard.util.Replacer;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.util.Optional;

/**
 * Command to configure the greeting message and channel.
 */
@CommandUsage(EventContext.GUILD)
public class Greeting extends Command implements Executable, ReqShardManager, ReqInit, ReqDataSource, ReqDialogHandler, ReqParser {
    private ShardManager shardManager;
    private GreetingData data;
    private DataSource source;
    private DialogHandler dialogHandler;
    private ArgumentParser parser;


    /**
     * Creates a new greeting command object.
     */
    public Greeting() {
        super("greeting",
                null,
                "command.greeting.description",
                SubCommand.builder("greeting")
                        .addSubcommand("command.greeting.subcommand.setChannel",
                                Parameter.createCommand("setChannel"),
                                Parameter.createInput("command.general.argument.channel",
                                        "command.general.argumentDescription.channelMentionOrExecution", false))
                        .addSubcommand("command.greeting.subcommand.setMessage",
                                Parameter.createCommand("setMessage"),
                                Parameter.createInput("command.general.argument.message",
                                        "command.general.argumentDescription.messageMention", true))
                        .addSubcommand("command.greeting.subcommand.setPrivateMessage",
                                Parameter.createCommand("setPrivateMessage"),
                                Parameter.createInput("command.general.argument.message",
                                        "command.general.argumentDescription.messageMention", true))
                        .addSubcommand("command.greeting.subcommand.setJoinGroup",
                                Parameter.createCommand("setJoinRole"),
                                Parameter.createInput("command.general.argument.role",
                                        "command.general.argumentDescription.role", true))
                        .addSubcommand("command.greeting.subcommand.config",
                                Parameter.createCommand("config"))
                        .addSubcommand("command.greeting.subcommand.info",
                                Parameter.createCommand("info"))
                        .build(),
                CommandCategory.ADMIN);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        String cmd = args[0];
        if (isSubCommand(cmd, "setChannel")) {
            setChannel(args, wrapper);
            return;
        }

        if (isSubCommand(cmd, "setMessage")) {
            setMessage(args, wrapper);
            return;
        }
        if (isSubCommand(cmd, "setPrivateMessage")) {
            setPrivateMessage(args, wrapper);
            return;
        }
        if (isSubCommand(cmd, "setJoinRole")) {
            setRole(args, wrapper);
            return;
        }
        if (isSubCommand(cmd, "config")) {
            config(args, wrapper);
            return;
        }
        if (isSubCommand(cmd, "info")) {
            sendSettingEmbed(wrapper);
            return;
        }
    }

    private void config(String[] args, EventWrapper wrapper) {
        @Nullable GreetingSettings settings = data.getGreeting(wrapper.getGuild().get());

        String startText;
        if (settings != null && settings.getChannel() == null) {
            startText = TextLocalizer.localizeByWrapper("command.greeting.dialog.setChannel", wrapper);
        } else {
            startText = TextLocalizer.localizeByWrapper("command.greeting.dialog.changeChannel", wrapper,
                    Replacement.create("CHANNEL", settings.getChannel().getAsMention()));
        }

        dialogHandler.startDialog(wrapper, startText, new Dialog() {
            private boolean channelSet = false;
            private boolean greetingSet = false;
            private boolean privateGreetingSet = false;
            private boolean roleSet = false;
            @Nullable private GreetingSettings curSettings = settings;

            @Override
            public boolean invoke(EventWrapper wrapper, Message message) {
                String content = message.getContentRaw();
                boolean skip = TextLocalizer.localizeByWrapper("dialog.skip", wrapper).equalsIgnoreCase(content);
                boolean remove = TextLocalizer.localizeByWrapper("dialog.remove", wrapper).equalsIgnoreCase(content);

                if (!channelSet) {
                    if (remove) {
                        data.setGreetingChannel(wrapper.getGuild().get(), null, wrapper);
                        channelSet = true;
                        MessageSender.sendLocalized("command.greeting.messages.removedChannel", wrapper);
                    } else if (!skip) {
                        Optional<TextChannel> textChannel = ArgumentParser.getTextChannel(wrapper.getGuild().get(), content);
                        if (textChannel.isEmpty()) {
                            MessageSender.sendSimpleError(ErrorType.INVALID_CHANNEL, wrapper);
                            return false;
                        }
                        data.setGreetingChannel(wrapper.getGuild().get(), textChannel.get(), wrapper);
                        MessageSender.sendLocalized("command.greeting.messages.setChannel",
                                wrapper, Replacement.create("CHANNEL", textChannel.get().getAsMention()));
                    }
                    channelSet = true;
                    curSettings = data.getGreeting(wrapper.getGuild().get());
                    if (curSettings != null && curSettings.getChannel() == null) {
                        greetingSet = true;
                        if (curSettings.getPrivateMessage() != null) {
                            MessageSender.sendLocalized("command.greeting.dialog.currentPrivateGreeting", wrapper);
                            MessageSender.sendLocalized(Replacer.applyUserPlaceholder(wrapper.getActor(), curSettings.getPrivateMessage()), wrapper);
                            MessageSender.sendLocalized("command.greeting.dialog.changePrivateGreeting", wrapper);
                            MessageSender.sendLocalized("command.general.argumentDescription.messageMention", wrapper);
                        } else {
                            MessageSender.sendLocalized("command.greeting.dialog.setPrivateGreeting", wrapper);
                            MessageSender.sendLocalized("command.general.argumentDescription.messageMention", wrapper);
                        }
                    } else {
                        curSettings = data.getGreeting(wrapper.getGuild().get());
                        MessageSender.sendLocalized("command.greeting.dialog.currentGreeting", wrapper);
                        MessageSender.sendGreeting(wrapper.getAuthor(), curSettings, null, wrapper.getTextChannel().get());
                        MessageSender.sendLocalized("command.greeting.dialog.changeGreeting", wrapper);
                        MessageSender.sendLocalized("command.general.argumentDescription.messageMention", wrapper);
                    }
                    return false;
                }

                if (!greetingSet) {
                    if (content.length() > 1000) {
                        MessageSender.sendSimpleError(ErrorType.TEXT_TOO_LONG, wrapper,
                                Replacement.create("COUNT", content.length()),
                                Replacement.create("MAX", 1000));
                        return false;
                    }

                    if (!skip) {
                        data.setGreetingMessage(wrapper.getGuild().get(), content, wrapper);
                        MessageSender.sendLocalized("command.greeting.messages.setMessage", wrapper);
                        curSettings = data.getGreeting(wrapper.getGuild().get());
                        MessageSender.sendGreeting(wrapper.getActor(), curSettings, null, wrapper.getTextChannel().get());
                    }
                    greetingSet = true;
                    if (curSettings != null && curSettings.getPrivateMessage() != null) {
                        MessageSender.sendLocalized("command.greeting.dialog.currentPrivateGreeting", wrapper);
                        MessageSender.sendLocalized(Replacer.applyUserPlaceholder(wrapper.getActor(), curSettings.getPrivateMessage()), wrapper);
                        MessageSender.sendLocalized("command.greeting.dialog.changePrivateGreeting", wrapper);
                        MessageSender.sendLocalized("command.general.argumentDescription.messageMention", wrapper);
                    } else {
                        MessageSender.sendLocalized("command.greeting.dialog.setPrivateGreeting", wrapper);
                        MessageSender.sendLocalized("command.general.argumentDescription.messageMention", wrapper);
                    }
                    return false;
                }

                if (!privateGreetingSet) {
                    if (content.length() > 2000) {
                        MessageSender.sendSimpleError(ErrorType.TEXT_TOO_LONG, wrapper,
                                Replacement.create("COUNT", content.length()),
                                Replacement.create("MAX", 2000));
                        return false;
                    }

                    if (remove) {
                        data.setPrivateGreetingMessage(wrapper.getGuild().get(), null, wrapper);
                        MessageSender.sendLocalized("command.greeting.messages.removedPrivateMessage", wrapper);
                    } else if (!skip) {
                        data.setPrivateGreetingMessage(wrapper.getGuild().get(), content, wrapper);
                        MessageSender.sendLocalized("command.greeting.messages.setPrivateMessage", wrapper);
                        MessageSender.sendLocalized(Replacer.applyUserPlaceholder(wrapper.getActor(), content), wrapper);
                    }
                    privateGreetingSet = true;
                    if (curSettings != null && curSettings.getRole() != null) {
                        MessageSender.sendLocalized("command.greeting.dialog.currentRole", wrapper,
                                Replacement.createMention(curSettings.getRole()));
                    } else {
                        MessageSender.sendLocalized("command.greeting.dialog.setRole", wrapper);
                    }
                    return false;
                }

                if (!roleSet) {
                    if (remove) {
                        data.setJoinRole(wrapper.getGuild().get(), null, wrapper);
                        MessageSender.sendLocalized("command.greeting.messages.removedJoinRole", wrapper);
                    }
                    if (!skip) {
                        Optional<Role> role = parser.getRole(wrapper.getGuild().get(), content);

                        if (role.isPresent()) {
                            if (!wrapper.getSelfMember().get().canInteract(role.get())) {
                                MessageSender.sendSimpleError(ErrorType.HIERARCHY_EXCEPTION, wrapper, Replacement.createMention(role.get()));
                                return false;
                            }

                            if (data.setJoinRole(wrapper.getGuild().get(), role.get(), wrapper)) {
                                MessageSender.sendLocalized("command.greeting.messages.setJoinGroup",
                                        wrapper, Replacement.createMention(role.get()));
                            }
                        } else {
                            MessageSender.sendSimpleError(ErrorType.INVALID_ROLE, wrapper);
                        }
                    }
                    sendSettingEmbed(wrapper);
                    return true;
                }
                return false;
            }
        });
    }

    private void setPrivateMessage(String[] args, EventWrapper wrapper) {
        String message = ArgumentParser.getMessage(args, 1);

        if ("none".equalsIgnoreCase(message)) {
            if (data.setPrivateGreetingMessage(wrapper.getGuild().get(), null, wrapper)) {
                MessageSender.sendLocalized("command.greeting.messages.removedPrivateMessage", wrapper);
            }
            return;
        }

        if (message.length() > 2000) {
            MessageSender.sendSimpleError(ErrorType.TEXT_TOO_LONG, wrapper,
                    Replacement.create("COUNT", message.length()),
                    Replacement.create("MAX", 2000));
            return;
        }

        if (data.setPrivateGreetingMessage(wrapper.getGuild().get(), message, wrapper)) {
            MessageSender.sendLocalized("command.greeting.messages.setPrivateMessage", wrapper);
            wrapper.getMessageChannel().sendMessage(Replacer.applyUserPlaceholder(wrapper.getActor(), message)).queue();
        }
    }

    private void setMessage(String[] args, EventWrapper wrapper) {
        String message = ArgumentParser.getMessage(args, 1);

        if (message.length() > 1000) {
            MessageSender.sendSimpleError(ErrorType.TEXT_TOO_LONG, wrapper,
                    Replacement.create("COUNT", message.length()),
                    Replacement.create("MAX", 1000));
            return;
        }

        if (data.setGreetingMessage(wrapper.getGuild().get(), message, wrapper)) {
            MessageSender.sendGreeting(wrapper.getActor(), data.getGreeting(wrapper.getGuild().get()), null, wrapper.getTextChannel().get());
        }
    }

    private void setChannel(String[] args, EventWrapper wrapper) {
        if (args.length == 1) {
            if (data.setGreetingChannel(wrapper.getGuild().get(),
                    wrapper.getTextChannel().get(), wrapper)) {
                MessageSender.sendLocalized("command.greeting.messages.setChannel",
                        wrapper, Replacement.create("CHANNEL", wrapper.getTextChannel().get().getAsMention()));
            }
        } else if (args.length == 2) {
            if ("none".equalsIgnoreCase(args[1])) {
                data.setGreetingChannel(wrapper.getGuild().get(), null, wrapper);
                MessageSender.sendLocalized("command.greeting.messages.removedChannel", wrapper);
                return;
            }
            Optional<TextChannel> channel = ArgumentParser.getTextChannel(wrapper.getGuild().get(), args[1]);

            if (channel.isPresent()) {
                if (data.setGreetingChannel(wrapper.getGuild().get(), channel.get(), wrapper)) {
                    MessageSender.sendLocalized("command.greeting.messages.setChannel",
                            wrapper, Replacement.create("CHANNEL", channel.get().getAsMention()));
                }
            } else {
                MessageSender.sendSimpleError(ErrorType.INVALID_CHANNEL, wrapper);
            }
        }
    }

    private void setRole(String[] args, EventWrapper wrapper) {
        if ("none".equalsIgnoreCase(args[1])) {
            data.setJoinRole(wrapper.getGuild().get(), null, wrapper);
            MessageSender.sendLocalized("command.greeting.messages.removedJoinRole", wrapper);
            return;
        }

        Optional<Role> role = parser.getRole(wrapper.getGuild().get(), args[1]);

        if (role.isPresent()) {
            if (!wrapper.getSelfMember().get().canInteract(role.get())) {
                MessageSender.sendSimpleError(ErrorType.HIERARCHY_EXCEPTION, wrapper, Replacement.createMention(role.get()));
                return;
            }

            if (data.setJoinRole(wrapper.getGuild().get(), role.get(), wrapper)) {
                MessageSender.sendLocalized("command.greeting.messages.setJoinGroup",
                        wrapper, Replacement.create("ROLE", role.get().getAsMention()));
            }
        } else {
            MessageSender.sendSimpleError(ErrorType.INVALID_ROLE, wrapper);
        }
    }

    private void sendSettingEmbed(EventWrapper wrapper) {
        GreetingSettings settings = data.getGreeting(wrapper.getGuild().get());
        MessageEmbed embed = new LocalizedEmbedBuilder(wrapper)
                .setTitle("command.greeting.embed.title")
                .addField("command.greeting.embed.channel",
                        settings.getChannel() != null ? settings.getChannel().getAsMention() : "words.disabled", true)
                .addField("command.greeting.embed.joinRole",
                        settings.getRole() != null ? settings.getRole().getAsMention() : "words.disabled", true)
                .addField("command.greeting.embed.greeting",
                        settings.getChannel() != null
                                ? Replacer.applyUserPlaceholder(wrapper.getActor(), settings.getMessage().length() > 700
                                ? StringUtils.truncate(settings.getMessage(), 700) + "[...]"
                                : settings.getMessage())
                                : "words.disabled", false)
                .addField("command.greeting.embed.privateGreeting",
                        settings.getPrivateMessage() != null
                                ? Replacer.applyUserPlaceholder(wrapper.getActor(), settings.getPrivateMessage().length() > 700
                                ? StringUtils.truncate(settings.getPrivateMessage(), 700) + "[...]"
                                : settings.getPrivateMessage())
                                : "words.disabled", false)
                .build();
        wrapper.getMessageChannel().sendMessage(embed).queue();
    }

    @Override
    public void init() {
        data = new GreetingData(shardManager, source);
    }

    @Override
    public void addShardManager(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    @Override
    public void addDataSource(DataSource source) {
        this.source = source;
    }

    @Override
    public void addDialogHandler(DialogHandler dialogHandler) {
        this.dialogHandler = dialogHandler;
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }
}
