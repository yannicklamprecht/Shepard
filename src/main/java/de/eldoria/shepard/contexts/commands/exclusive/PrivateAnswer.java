package de.eldoria.shepard.contexts.commands.exclusive;

import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.collections.PrivateMessageCollection;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.Parameter;
import de.eldoria.shepard.contexts.commands.argument.SubCommand;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.User;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_USER;
import static de.eldoria.shepard.localization.enums.commands.admin.PrivateMessageLocale.ANSWER_DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.admin.PrivateMessageLocale.A_MESSAGE;

/**
 * Command to answer a received message by a bot instance.
 * Only available for user which wrote shepard. otherwise {@link SendPrivateMessage} should be used.
 */
public class PrivateAnswer extends Command {
    /**
     * Create a new private answer command object.
     */
    public PrivateAnswer() {
        super("privateAnswer",
                new String[] {"reply", "answer"},
                ANSWER_DESCRIPTION.tag,
                SubCommand.builder("privateAnswer")
                        .addSubcommand(null,
                                Parameter.createInput(A_USER.tag, GeneralLocale.AD_USER.tag, true),
                                Parameter.createInput(A_MESSAGE.tag, null, true))
                        .build(),
                ContextCategory.EXCLUSIVE);
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
