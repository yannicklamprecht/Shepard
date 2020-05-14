package de.eldoria.shepard.commandmodules.privatemessages.commands;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.privatemessages.PrivateMessageCollection;
import de.eldoria.shepard.commandmodules.privatemessages.util.PrivateMessageHelper;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.core.util.Normandy;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqNormandy;
import de.eldoria.shepard.modulebuilder.requirements.ReqPrivateMessages;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.User;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_USER;
import static de.eldoria.shepard.localization.enums.commands.admin.PrivateMessageLocale.ANSWER_DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.admin.PrivateMessageLocale.A_MESSAGE;

/**
 * Command to answer a received message by a bot instance.
 * Only available for user which wrote shepard. otherwise {@link SendPrivateMessage} should be used.
 */
public class PrivateAnswer extends Command implements Executable, ReqPrivateMessages, ReqNormandy {
    private PrivateMessageCollection privateMessages;
    private Normandy normandy;

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
                CommandCategory.EXCLUSIVE);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        if (Verifier.equalSnowflake(wrapper.getMessageChannel(), normandy.getPrivateAnswerChannel())) {
            MessageSender.sendSimpleError(ErrorType.EXCLUSIVE_CHANNEL, wrapper);
            return;
        }

        User user = privateMessages.getUser(args[0]);

        PrivateMessageHelper.sendPrivateMessage(args, wrapper, user);
    }

    @Override
    public void addPrivateMessages(PrivateMessageCollection privateMessages) {
        this.privateMessages = privateMessages;
    }

    @Override
    public void addNormandy(Normandy normandy) {
        this.normandy = normandy;
    }
}
