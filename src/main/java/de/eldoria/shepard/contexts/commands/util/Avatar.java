package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.FileHelper;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class Avatar extends Command {
    public Avatar() {
        commandName = "avatar";
        commandDesc = "Get the avatar of yourself or a user.";
        commandArgs = new CommandArg[] {
                new CommandArg("name", "Mention of the user or id.", false)
        };
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
            MessageSender.sendMessage("Avatar of user " + messageContext.getAuthor().getAsTag(),
                    messageContext.getTextChannel());
            messageContext.getChannel().sendFile(fileFromURL).queue();
            return;
        }

        User user = null;
        String idRaw = DbUtil.getIdRaw(args[0]);
        if (Verifier.isValidId(idRaw)) {
            user = ShepardBot.getJDA().getUserById(idRaw);
        }

        if (user == null) {
            try {
                user = ShepardBot.getJDA().getUserByTag(args[0]);
            } catch (IllegalArgumentException e) {

            }
        }

        if (user == null) {
            List<User> usersByName = ShepardBot.getJDA().getUsersByName(args[0], true);
            if (!usersByName.isEmpty()) {
                user = usersByName.get(0);
            }
        }

        if (user == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, messageContext.getTextChannel());
            return;
        }

        File fileFromURL = FileHelper.getFileFromURL(user.getEffectiveAvatarUrl());
        if (fileFromURL == null) {
            MessageSender.sendSimpleError(ErrorType.SERVICE_UNAVAILABLE, messageContext.getTextChannel());
            return;
        }
        MessageSender.sendMessage("Avatar of user " + user.getAsTag(), messageContext.getTextChannel());
        messageContext.getTextChannel().sendFile(fileFromURL).queue();
    }
}
