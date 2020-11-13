package de.eldoria.shepard.commandmodules.privatemessages.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.privatemessages.util.PrivateMessageHelper;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.core.util.Normandy;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqNormandy;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.User;

/**
 * Command which makes it possible to send private messages as the bot.
 */
@CommandUsage(EventContext.GUILD)
public class SendPrivateMessage extends Command implements Executable, ReqParser, ReqNormandy {
    private ArgumentParser parser;
    private Normandy normandy;

    /**
     * Creates a new private message command object.
     */
    public SendPrivateMessage() {
        super("privateMessage",
                new String[] {"pm", "sendMessage"},
                "command.privateMessage.description",
                SubCommand.builder("privateMessage")
                        .addSubcommand(null,
                                Parameter.createInput("command.general.argument.message", null, true),
                                Parameter.createInput("command.general.argument.user", "command.general.argumentDescription.user", true))
                        .build(),
                CommandCategory.EXCLUSIVE
        );
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        if (!Verifier.equalSnowflake(wrapper.getMessageChannel(), normandy.getPrivateAnswerChannel())) {
            MessageSender.sendSimpleError(ErrorType.EXCLUSIVE_CHANNEL, wrapper);
            return;
        }

        User user = parser.getUser(args[0]);

        if (user == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, wrapper);
            return;
        }

        PrivateMessageHelper.sendPrivateMessage(args, wrapper, user);
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }

    @Override
    public void addNormandy(Normandy normandy) {
        this.normandy = normandy;
    }
}
