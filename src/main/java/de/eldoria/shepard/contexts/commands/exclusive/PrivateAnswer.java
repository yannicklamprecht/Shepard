package de.eldoria.shepard.contexts.commands.exclusive;

import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.collections.PrivateMessageCollection;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArgument;
import de.eldoria.shepard.contexts.commands.argument.SubArgument;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.User;

import static de.eldoria.shepard.localization.enums.commands.admin.PrivateMessageLocale.*;

/**
 * Command to answer a received message by a bot instance.
 * Only available for user which wrote shepard. otherwise {@link SendPrivateMessage} should be used.
 */
public class PrivateAnswer extends Command {
    /**
     * Create a new private answer command object.
     */
    public PrivateAnswer() {
        commandName = "privateAnswer";
        commandAliases = new String[] {"reply", "answer"};
        commandDesc = ANSWER_DESCRIPTION.tag;
        commandArguments = new CommandArgument[] {
                new CommandArgument("name", true,
                        new SubArgument("name", A_NAME.tag)),
                new CommandArgument("message", false,
                        new SubArgument("message", A_MESSAGE.tag))
        };
        category = ContextCategory.EXCLUSIVE;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (messageContext.getChannel() != Normandy.getPrivateAnswerChannel()) {
            MessageSender.sendSimpleError(ErrorType.EXCLUSIVE_CHANNEL, messageContext.getTextChannel());
            return;
        }

        User user = PrivateMessageCollection.getInstance().getUser(args[0]);

        PrivateMessageHelper.sendPrivateMessage(args, messageContext, user);
    }

}
