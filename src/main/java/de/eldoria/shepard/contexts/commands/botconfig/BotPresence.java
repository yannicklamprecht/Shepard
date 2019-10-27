package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_EMPTY;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_TEXT;
import static de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale.A_TWITCH_URL;
import static de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale.C_CLEAR;
import static de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale.C_LISTENING;
import static de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale.C_PLAYING;
import static de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale.C_STREAMING;
import static de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale.M_CLEAR;
import static de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale.M_LISTENING;
import static de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale.M_PLAYING;
import static de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale.M_STREAMING;
import static de.eldoria.shepard.util.Verifier.isArgument;

public class BotPresence extends Command {

    public BotPresence() {
        commandName = "presence";
        commandDesc = "Set Shepards presence";
        commandArgs = new CommandArg[] {
                new CommandArg("action", true,
                        new SubArg("playing", C_PLAYING.replacement, true),
                        new SubArg("streaming", C_STREAMING.replacement, true),
                        new SubArg("listening", C_LISTENING.replacement, true),
                        new SubArg("clear", C_CLEAR.replacement, true)),
                new CommandArg("values", false,
                        new SubArg("playing", A_TEXT.replacement),
                        new SubArg("streaming", A_TEXT + " " + A_TWITCH_URL),
                        new SubArg("listening", A_TEXT.replacement),
                        new SubArg("clear", A_EMPTY.replacement))
        };
        category = ContextCategory.BOTCONFIG;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (args.length < 1) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext);
            return;
        }
        Presence presence = ShepardBot.getJDA().getPresence();

        String activity = args[0];

        if (isArgument(activity, "clear", "c")) {
            presence.setActivity(null);
            MessageSender.sendMessage(M_CLEAR.replacement, messageContext);
            return;
        }

        if (args.length < 2) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext);
            return;
        }

        if (isArgument(activity, "playing", "p")) {
            String message = ArgumentParser.getMessage(args, 1);
            presence.setActivity(Activity.playing(message));
            MessageSender.sendMessage(M_PLAYING + message, messageContext);
            return;
        }
        if (isArgument(activity, "streaming", "s")) {
            if (args.length > 2) {
                String message = ArgumentParser.getMessage(args, 1, -1);
                String url = ArgumentParser.getMessage(args, -1);
                presence.setActivity(Activity.streaming(message, url));
                MessageSender.sendMessage(locale.getReplacedString(M_STREAMING.localeCode, messageContext.getGuild(),
                        message) + url + "!", messageContext);
                return;
            } else {
                MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext);
            }
        }

        //Not supported yet
        /*if (isArgument(activity, "watching", "w")) {
            presence.setActivity(Activity.watching(
                    TextFormatting.getRangeAsString(" ", args, 1, args.length)
            ));
            return;
        }*/

        if (isArgument(activity, "listening", "l")) {
            String message = ArgumentParser.getMessage(args, 1);
            presence.setActivity(Activity.listening(message));
            MessageSender.sendMessage(M_LISTENING + message, messageContext);
            return;
        }


        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext);


    }
}
