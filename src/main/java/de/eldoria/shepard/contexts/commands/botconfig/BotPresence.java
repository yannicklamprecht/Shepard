package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArgument;
import de.eldoria.shepard.contexts.commands.argument.SubArgument;
import de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.scheduler.PresenceChanger;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.managers.Presence;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_EMPTY;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_TEXT;
import static de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale.*;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

/**
 * Command to set the presence for the bot.
 */
public class BotPresence extends Command {
    private PresenceChanger presenceChanger;

    /**
     * Creates a new bot presence command object.
     */
    public BotPresence() {
        presenceChanger = PresenceChanger.getInstance();
        commandName = "presence";
        commandDesc = BotPresenceLocale.DESCRIPTION.tag;
        commandArguments = new CommandArgument[] {
                new CommandArgument("action", true,
                        new SubArgument("playing", C_PLAYING.tag, true),
                        new SubArgument("streaming", C_STREAMING.tag, true),
                        new SubArgument("listening", C_LISTENING.tag, true),
                        new SubArgument("clear", C_CLEAR.tag, true)),
                new CommandArgument("values", false,
                        new SubArgument("playing", A_TEXT.tag),
                        new SubArgument("streaming", A_TEXT + " " + A_TWITCH_URL),
                        new SubArgument("listening", A_TEXT.tag),
                        new SubArgument("clear", A_EMPTY.tag))
        };
        category = ContextCategory.BOT_CONFIG;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (args.length < 1) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getTextChannel());
            return;
        }
        Presence presence = ShepardBot.getJDA().getPresence();

        String activity = args[0];
        CommandArgument arg = commandArguments[0];


        if (arg.isSubCommand(activity, 3)) {
            presenceChanger.clearPresence();
            MessageSender.sendMessage(M_CLEAR.tag, messageContext.getTextChannel());
            return;
        }

        if (args.length < 2) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getTextChannel());
            return;
        }

        if (arg.isSubCommand(activity, 0)) {
            String message = ArgumentParser.getMessage(args, 1);
            presenceChanger.setPlaying(message);
            MessageSender.sendMessage(localizeAllAndReplace(M_PLAYING.tag, messageContext.getGuild(),
                    "**" + message + "**"), messageContext.getTextChannel());
            return;
        }
        if (arg.isSubCommand(activity, 1)) {
            if (args.length > 2) {
                String message = ArgumentParser.getMessage(args, 1, -1);
                String url = ArgumentParser.getMessage(args, -1);
                presenceChanger.setStreaming(message, url);
                MessageSender.sendMessage(localizeAllAndReplace(M_STREAMING.tag, messageContext.getGuild(),
                        "**" + message + "**", url), messageContext.getTextChannel());
                return;
            } else {
                MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getTextChannel());
            }
        }

        //Not supported yet
        /*if (isArgument(activity, "watching", "w")) {
            presence.setActivity(Activity.watching(
                    TextFormatting.getRangeAsString(" ", args, 1, args.length)
            ));
            return;
        }*/

        if (arg.isSubCommand(activity, 2)) {
            String message = ArgumentParser.getMessage(args, 1);
            presenceChanger.setListening(message);
            MessageSender.sendMessage(localizeAllAndReplace(M_LISTENING.tag, messageContext.getGuild(),
                    "**" + message + "**"), messageContext.getTextChannel());
            return;
        }


        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());


    }
}
