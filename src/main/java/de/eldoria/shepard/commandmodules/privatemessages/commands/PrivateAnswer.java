package de.eldoria.shepard.commandmodules.privatemessages.commands;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.privatemessages.PrivateMessageCollection;
import de.eldoria.shepard.commandmodules.privatemessages.util.PrivateMessageHelper;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.core.util.Normandy;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqNormandy;
import de.eldoria.shepard.modulebuilder.requirements.ReqPrivateMessages;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

/**
 * Command to answer a received message by a bot instance.
 * Only available for user which wrote shepard. otherwise {@link SendPrivateMessage} should be used.
 */
@CommandUsage(EventContext.GUILD)
public class PrivateAnswer extends Command implements Executable, ReqPrivateMessages, ReqNormandy {
    private PrivateMessageCollection privateMessages;
    private Normandy normandy;

    /**
     * Create a new private answer command object.
     */
    public PrivateAnswer() {
        super("privateAnswer",
                new String[] {"reply", "answer"},
                "command.privateAnswer.description",
                SubCommand.builder("privateAnswer")
                        .addSubcommand(null,
                                Parameter.createInput("command.general.argument.user", "command.general.argumentDescription.user", true),
                                Parameter.createInput("command.general.argument.message", null, true))
                        .build(),
                CommandCategory.EXCLUSIVE);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        TextChannel privateAnswerChannel = normandy.getPrivateAnswerChannel();
        MessageChannel messageChannel = wrapper.getMessageChannel();
        if (!Verifier.equalSnowflake(messageChannel, privateAnswerChannel)) {
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
