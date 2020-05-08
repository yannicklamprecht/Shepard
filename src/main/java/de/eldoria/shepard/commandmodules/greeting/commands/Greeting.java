package de.eldoria.shepard.commandmodules.greeting.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.greeting.data.GreetingData;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqShardManager;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.sql.DataSource;

import java.util.Optional;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_CHANNEL_MENTION_OR_EXECUTE;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_MESSAGE_MENTION;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_CHANNEL;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_MESSAGE;
import static de.eldoria.shepard.localization.enums.commands.admin.GreetingsLocale.C_REMOVE_CHANNEL;
import static de.eldoria.shepard.localization.enums.commands.admin.GreetingsLocale.C_SET_CHANNEL;
import static de.eldoria.shepard.localization.enums.commands.admin.GreetingsLocale.C_SET_MESSAGE;
import static de.eldoria.shepard.localization.enums.commands.admin.GreetingsLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.admin.GreetingsLocale.M_REMOVED_CHANNEL;
import static de.eldoria.shepard.localization.enums.commands.admin.GreetingsLocale.M_SET_CHANNEL;
import static de.eldoria.shepard.localization.enums.commands.admin.GreetingsLocale.M_SET_MESSAGE;

/**
 * Command to configure the greeting message and channel.
 */
public class Greeting extends Command implements Executable, ReqShardManager, ReqInit, ReqDataSource {
    private ShardManager shardManager;
    private GreetingData data;
    private DataSource source;


    /**
     * Creates a new greeting command object.
     */
    public Greeting() {
        super("greeting",
                null,
                DESCRIPTION.tag,
                SubCommand.builder("greeting")
                        .addSubcommand(C_SET_CHANNEL.tag,
                                Parameter.createCommand("setChannel"),
                                Parameter.createInput(A_CHANNEL.tag, AD_CHANNEL_MENTION_OR_EXECUTE.tag, false))
                        .addSubcommand(C_REMOVE_CHANNEL.tag,
                                Parameter.createCommand("removeChannel"))
                        .addSubcommand(C_SET_MESSAGE.tag,
                                Parameter.createCommand("setMessage"),
                                Parameter.createInput(A_MESSAGE.tag, AD_MESSAGE_MENTION.tag, true))
                        .build(),
                CommandCategory.ADMIN);
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        if (isSubCommand(cmd, 0)) {
            setChannel(args, messageContext);
            return;
        }

        if (isSubCommand(cmd, 1)) {
            removeChannel(messageContext);
            return;
        }

        if (isSubCommand(cmd, 2)) {
            setMessage(args, messageContext);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());
    }

    private void setMessage(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length > 1) {
            String message = ArgumentParser.getMessage(args, 1);

            if (data.setGreetingText(messageContext.getGuild(), message, messageContext)) {
                MessageSender.sendSimpleTextBox("**" + M_SET_MESSAGE + "**",
                        message, messageContext.getTextChannel());
            }
            return;
        }
        MessageSender.sendSimpleError(ErrorType.NO_MESSAGE_FOUND, messageContext.getTextChannel());
    }

    private void removeChannel(MessageEventDataWrapper messageContext) {
        if (data.removeGreetingChannel(messageContext.getGuild(), messageContext)) {
            MessageSender.sendMessage(M_REMOVED_CHANNEL.tag, messageContext.getTextChannel());
        }
    }

    private void setChannel(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 1) {
            if (data.setGreetingChannel(messageContext.getGuild(),
                    messageContext.getChannel(), messageContext)) {
                MessageSender.sendMessage(M_SET_CHANNEL + " "
                        + messageContext.getTextChannel().getAsMention(), messageContext.getTextChannel());
            }
            return;
        } else if (args.length == 2) {
            Optional<TextChannel> channel = ArgumentParser.getTextChannel(messageContext.getGuild(), args[1]);

            if (channel.isPresent()) {
                if (data.setGreetingChannel(messageContext.getGuild(), channel.get(), messageContext)) {
                    MessageSender.sendMessage(
                            M_SET_CHANNEL + " "
                                    + channel.get().getAsMention(), messageContext.getTextChannel());
                }
                return;
            }
        }
        MessageSender.sendSimpleError(ErrorType.TOO_MANY_ARGUMENTS, messageContext.getTextChannel());
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
}
