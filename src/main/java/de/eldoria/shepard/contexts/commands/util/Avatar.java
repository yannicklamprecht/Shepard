package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.Parameter;
import de.eldoria.shepard.contexts.commands.argument.SubCommand;
import de.eldoria.shepard.localization.enums.commands.util.AvatarLocale;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.FileHelper;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.User;

import java.io.File;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_USER;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_USER;

public class Avatar extends Command {
    /**
     * Create a new Avatar command.
     */
    public Avatar() {
        super("avatar",
                null,
                AvatarLocale.DESCRIPTION.tag,
                SubCommand.builder("avatar")
                        .addSubcommand(AvatarLocale.C_OTHER.tag,
                                Parameter.createInput(A_USER.tag, AD_USER.tag, false))
                        .build(),
                AvatarLocale.C_DEFAULT.tag,
                ContextCategory.UTIL);
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 0) {
            String effectiveAvatarUrl = messageContext.getAuthor().getEffectiveAvatarUrl();
            File fileFromURL = FileHelper.getFileFromURL(effectiveAvatarUrl);
            if (fileFromURL == null) {
                MessageSender.sendSimpleError(ErrorType.SERVICE_UNAVAILABLE, messageContext.getTextChannel());
                return;
            }
            MessageSender.sendMessage(
                    TextLocalizer.localizeAllAndReplace(
                            AvatarLocale.M_AVATAR.tag,
                            messageContext.getGuild(),
                            "**" + messageContext.getAuthor().getAsTag() + "**"),
                    messageContext.getTextChannel());
            messageContext.getChannel().sendFile(fileFromURL).queue();
            return;
        }

        User user = ArgumentParser.getUserDeepSearch(ArgumentParser.getMessage(args, 0), messageContext.getGuild());

        if (user == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, messageContext.getTextChannel());
            return;
        }

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
