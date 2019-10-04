package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.collections.PrivateMessageCollection;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.Emoji;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.User;

import static java.lang.System.lineSeparator;

public class PrivateAnswer extends Command {
    public PrivateAnswer() {
        commandName = "privateAnswer";
        commandAliases = new String[] {"reply", "answer"};
        commandDesc = "Answer a user on a private message.";
        commandArgs = new CommandArg[] {
                new CommandArg("name",
                        "name of the user to answer" + lineSeparator()
                                + " Start of the name is enough." + lineSeparator()
                                + "You can only answer the last 50 contacts.",
                        true),
                new CommandArg("text",
                        "The text you want to send",
                        true)};
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (messageContext.getChannel() != Normandy.getPrivateAnswerChannel()) {
            MessageSender.sendMessage("This is only available in a specific channel!", messageContext.getChannel());
            return;
        }

        User user = PrivateMessageCollection.getInstance().getUser(args[0]);

        if (user == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, messageContext.getChannel());
            return;
        }

        String text = TextFormatting.getRangeAsString(" ", args, 1, args.length);

        MessageSender.sendMessage(user, messageContext.getMessage().getAttachments(), text, messageContext);

        messageContext.getMessage().addReaction(Emoji.CHECK_MARK_BUTTON.unicode).queue();
    }
}
