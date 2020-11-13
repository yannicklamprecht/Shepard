package de.eldoria.shepard.commandmodules.standalone.util;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.util.AvatarLocale;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.util.FileHelper;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.User;

import java.io.File;

@CommandUsage(EventContext.GUILD)
public class Avatar extends Command implements ExecutableAsync, ReqParser {
    private ArgumentParser parser;

    /**
     * Create a new Avatar command.
     */
    public Avatar() {
        super("avatar",
                null,
                "command.avatar.description",
                SubCommand.builder("avatar")
                        .addSubcommand("command.avatar.command.other",
                                Parameter.createInput("command.general.argument.user", "command.general.argumentDescription.user", false))
                        .build(),
                "command.avatar.command.default",
                CommandCategory.UTIL);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        if (args.length == 0) {
            sendAvatar(wrapper.getAuthor(), wrapper);
            return;
        }

        User user = parser.getUserDeepSearch(ArgumentParser.getMessage(args, 0), wrapper.getGuild().get());

        if (user == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, wrapper);
            return;
        }

        sendAvatar(user, wrapper);
    }

    private void sendAvatar(User user, EventWrapper messageContext) {
        File fileFromURL = FileHelper.getFileFromURL(user.getEffectiveAvatarUrl());
        if (fileFromURL == null) {
            MessageSender.sendSimpleError(ErrorType.SERVICE_UNAVAILABLE, messageContext);
            return;
        }
        MessageSender.sendMessage(TextLocalizer.localizeAllAndReplace(AvatarLocale.M_AVATAR.tag,
                messageContext, "**" + user.getAsTag() + "**"),
                messageContext.getMessageChannel());
        messageContext.getMessageChannel().sendFile(fileFromURL).queue();
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }
}
