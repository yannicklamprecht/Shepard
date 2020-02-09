package de.eldoria.shepard.contexts.commands.exclusive;

import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.reactions.Emoji;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.User;

/**
 * Helper methods which are used in {@link PrivateAnswer} and {@link SendPrivateMessage}.
 */
public final class PrivateMessageHelper {

    private PrivateMessageHelper() {
    }

    /**
     * Sends a private message to a user.
     *
     * @param args           arguments to send. Are joined with white spaces
     * @param messageContext message context for error handling
     * @param user           user which should receive the message
     */
    static void sendPrivateMessage(String[] args, MessageEventDataWrapper messageContext, User user) {
        if (user == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, messageContext.getTextChannel());
            return;
        }

        String text = ArgumentParser.getMessage(args, 1);

        MessageSender.sendAttachment(user, messageContext.getMessage().getAttachments(), text,
                messageContext.getTextChannel());

        messageContext.getMessage().addReaction(Emoji.CHECK_MARK_BUTTON.unicode).queue();
    }
}
