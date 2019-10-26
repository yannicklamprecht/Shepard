package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.collections.PrivateMessageCollection;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.User;

import static de.eldoria.shepard.localization.enums.admin.PrivateMessageLocale.*;

public class PrivateAnswer extends Command {
    public PrivateAnswer() {
        commandName = "privateAnswer";
        commandAliases = new String[] {"reply", "answer"};
        commandDesc = "Answer a user on a private message.";
        commandArgs = new CommandArg[] {
                new CommandArg("name", true,
                        new SubArg("name", A_NAME.replacement)),
                new CommandArg("message", false,
                        new SubArg("message",A_MESSAGE.replacement))
        };
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (messageContext.getChannel() != Normandy.getPrivateAnswerChannel()) {
            MessageSender.sendSimpleError(ErrorType.EXCLUSIVE_CHANNEL, messageContext);
            return;
        }

        User user = PrivateMessageCollection.getInstance().getUser(args[0]);

        PrivateMessageHelper.sendPrivateMessage(args, messageContext, user);
    }

}
