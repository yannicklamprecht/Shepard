package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.reactions.EmojiCollection;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.User;

public final class PrivateMessageHelper {

    private PrivateMessageHelper(){
    }

    static void sendPrivateMessage(String[] args, MessageEventDataWrapper messageContext, User user) {
        if (user == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, messageContext);
            return;
        }

        String text = ArgumentParser.getMessage(args,1);

        MessageSender.sendAttachment(user, messageContext.getMessage().getAttachments(), text, messageContext);

        messageContext.getMessage().addReaction(EmojiCollection.CHECK_MARK_BUTTON.unicode).queue();
    }
}
