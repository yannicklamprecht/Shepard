package de.eldoria.shepard.commandmodules.standalone.util;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.util.AvatarLocale;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.util.FileHelper;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.User;

import java.io.File;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_USER;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_USER;

public class Avatar extends Command implements ExecutableAsync, ReqParser {
    private ArgumentParser parser;

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
                CommandCategory.UTIL);
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 0) {
            sendAvatar(messageContext.getAuthor(), messageContext);
            return;
        }

        User user = parser.getUserDeepSearch(ArgumentParser.getMessage(args, 0), messageContext.getGuild());

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

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }
}
