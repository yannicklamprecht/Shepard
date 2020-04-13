package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArgument;
import de.eldoria.shepard.contexts.commands.argument.SubArgument;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.localization.enums.commands.util.AvatarLocale;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.FileHelper;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.User;

import java.io.File;

public class Avatar extends Command {
    /**
     * Create a new Avatar command.
     */
    public Avatar() {
        commandName = "avatar";
        commandDesc = AvatarLocale.DESCRIPTION.tag;
        commandArguments = new CommandArgument[] {new CommandArgument("user", false,
                new SubArgument("user", GeneralLocale.A_USER.tag))
        };
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 0) {
            sendAvatar(messageContext.getAuthor(), messageContext);
            return;
        }

        User user = ArgumentParser.getUserDeepSearch(ArgumentParser.getMessage(args, 0), messageContext.getGuild());

        if (user == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, messageContext.getTextChannel());
            return;
        }

        sendAvatar(user, messageContext);
    }

    private void sendAvatar(User user, MessageEventDataWrapper messageContext) {
        File fileFromURL = FileHelper.getFileFromURL(user.getEffectiveAvatarUrl());
        if (fileFromURL == null) {
            MessageSender.sendSimpleError(ErrorType.SERVICE_UNAVAILABLE, messageContext.getTextChannel());
            return;
        }
        MessageSender.sendMessage(TextLocalizer.localizeAllAndReplace(AvatarLocale.M_AVATAR.tag,
                messageContext.getGuild(), "**" + user.getAsTag() + "**"),
                messageContext.getTextChannel());
        messageContext.getTextChannel().sendFile(fileFromURL).queue();

    }
}
