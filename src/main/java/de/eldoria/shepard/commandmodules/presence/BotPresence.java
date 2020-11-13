package de.eldoria.shepard.commandmodules.presence;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;

import static de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale.*;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

/**
 * Command to set the presence for the bot.
 */
@CommandUsage(EventContext.GUILD)
public class BotPresence extends Command implements Executable {

    private final PresenceChanger presenceChanger;

    /**
     * Creates a new bot presence command object.
     *
     * @param presenceChanger presence changer for presence handling.
     */
    public BotPresence(PresenceChanger presenceChanger) {
        super("presence",
                null,
                "command.botPresence.description",
                SubCommand.builder("presence")
                        .addSubcommand("command.botPresence.subcommand.playing",
                                Parameter.createCommand("playing"),
                                Parameter.createInput("command.general.argument.message", null, true))
                        .addSubcommand("command.botPresence.subcommand.streaming",
                                Parameter.createCommand("streaming"),
                                Parameter.createInput("command.general.argument.message", null, true),
                                Parameter.createInput("command.general.argument.url", "command.botPresence.argument.twitchUrl", true))
                        .addSubcommand("command.botPresence.subcommand.listening",
                                Parameter.createCommand("listening"),
                                Parameter.createInput("command.general.argument.message", null, true))
                        .addSubcommand("command.botPresence.subcommand.clear",
                                Parameter.createCommand("clear"))
                        .build(),
                CommandCategory.BOT_CONFIG);
        this.presenceChanger = presenceChanger;
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        String activity = args[0];

        if (isSubCommand(activity, 3)) {
            presenceChanger.clearPresence();
            MessageSender.sendMessage(M_CLEAR.tag, wrapper.getMessageChannel());
            return;
        }

        if (args.length < 2) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, wrapper);
            return;
        }

        if (isSubCommand(activity, 0)) {
            String message = ArgumentParser.getMessage(args, 1);
            presenceChanger.setPlaying(message);
            MessageSender.sendMessage(localizeAllAndReplace(M_PLAYING.tag, wrapper,
                    "**" + message + "**"), wrapper.getMessageChannel());
            return;
        }

        if (isSubCommand(activity, 1)) {
            if (args.length > 2) {
                String message = ArgumentParser.getMessage(args, 1, -1);
                String url = ArgumentParser.getMessage(args, -1);
                presenceChanger.setStreaming(message, url);
                MessageSender.sendMessage(localizeAllAndReplace(M_STREAMING.tag, wrapper,
                        "**" + message + "**", url), wrapper.getMessageChannel());
                return;
            } else {
                MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, wrapper);
            }
        }

        //Not supported yet
        /*if (isArgument(activity, "watching", "w")) {
            presence.setActivity(Activity.watching(
                    TextFormatting.getRangeAsString(" ", args, 1, args.length)
            ));
            return;
        }*/

        if (isSubCommand(activity, 2)) {
            String message = ArgumentParser.getMessage(args, 1);
            presenceChanger.setListening(message);
            MessageSender.sendMessage(localizeAllAndReplace(M_LISTENING.tag, wrapper,
                    "**" + message + "**"), wrapper.getMessageChannel());
            return;
        }
    }
}
