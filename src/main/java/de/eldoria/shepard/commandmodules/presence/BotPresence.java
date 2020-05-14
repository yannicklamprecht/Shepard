package de.eldoria.shepard.commandmodules.presence;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_TEXT;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_URL;
import static de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale.A_TWITCH_URL;
import static de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale.C_CLEAR;
import static de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale.C_LISTENING;
import static de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale.C_PLAYING;
import static de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale.C_STREAMING;
import static de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale.M_CLEAR;
import static de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale.M_LISTENING;
import static de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale.M_PLAYING;
import static de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale.M_STREAMING;
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
                BotPresenceLocale.DESCRIPTION.tag,
                SubCommand.builder("presence")
                        .addSubcommand(C_PLAYING.tag,
                                Parameter.createCommand("playing"),
                                Parameter.createInput(A_TEXT.tag, null, true))
                        .addSubcommand(C_STREAMING.tag,
                                Parameter.createCommand("streaming"),
                                Parameter.createInput(A_TEXT.tag, null, true),
                                Parameter.createInput(A_URL.tag, A_TWITCH_URL.tag, true))
                        .addSubcommand(C_LISTENING.tag,
                                Parameter.createCommand("listening"),
                                Parameter.createInput(A_TEXT.tag, null, true))
                        .addSubcommand(C_CLEAR.tag,
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
