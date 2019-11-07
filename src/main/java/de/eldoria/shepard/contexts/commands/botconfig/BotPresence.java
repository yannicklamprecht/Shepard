package de.eldoria.shepard.contexts.commands.botconfig;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.localization.enums.commands.botconfig.BotPresenceLocale;
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
import static de.eldoria.shepard.localization.util.TextLocalizer.fastLocaleAndReplace;

public class BotPresence extends Command {

    /**
     * Creates a new bot presence command object.
     */
    public BotPresence() {
        commandName = "presence";
        commandDesc = BotPresenceLocale.DESCRIPTION.tag;
        commandArgs = new CommandArg[] {
                new CommandArg("action", true,
                        new SubArg("playing", C_PLAYING.tag, true),
                        new SubArg("streaming", C_STREAMING.tag, true),
                        new SubArg("listening", C_LISTENING.tag, true),
                        new SubArg("clear", C_CLEAR.tag, true)),
                new CommandArg("values", false,
                        new SubArg("playing", A_TEXT.tag),
                        new SubArg("streaming", A_TEXT + " " + A_TWITCH_URL),
                        new SubArg("listening", A_TEXT.tag),
                        new SubArg("clear", A_EMPTY.tag))
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
        CommandArg arg = commandArgs[0];


        if (arg.isSubCommand(activity, 3)) {
            presence.setActivity(null);
            MessageSender.sendMessage(M_CLEAR.tag, messageContext.getTextChannel());
            return;
        }

        if (args.length < 2) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getTextChannel());
            return;
        }

        if (arg.isSubCommand(activity, 0)) {
            String message = ArgumentParser.getMessage(args, 1);
            presence.setActivity(Activity.playing(message));
            MessageSender.sendMessage(M_PLAYING + message, messageContext.getTextChannel());
            return;
        }
        if (arg.isSubCommand(activity, 1)) {
            if (args.length > 2) {
                String message = ArgumentParser.getMessage(args, 1, -1);
                String url = ArgumentParser.getMessage(args, -1);
                presence.setActivity(Activity.streaming(message, url));
                MessageSender.sendMessage(fastLocaleAndReplace(M_STREAMING.tag, messageContext.getGuild(),
                        message) + url + "!", messageContext.getTextChannel());
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
            presence.setActivity(Activity.listening(message));
            MessageSender.sendMessage(M_LISTENING + message, messageContext.getTextChannel());
            return;
        }


        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());


    }
}
