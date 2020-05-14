package de.eldoria.shepard.commandmodules.privatemessages.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.command.GuildChannelOnly;
import de.eldoria.shepard.commandmodules.privatemessages.util.PrivateMessageHelper;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.core.util.Normandy;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqNormandy;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.User;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_USER;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_USER;
import static de.eldoria.shepard.localization.enums.commands.admin.PrivateMessageLocale.MESSAGE_DESCRIPTION;

/**
 * Command which makes it possible to send private messages as the bot.
 */
public class SendPrivateMessage extends Command implements Executable, GuildChannelOnly, ReqParser, ReqNormandy {
    private ArgumentParser parser;
    private Normandy normandy;

    /**
     * Creates a new private message command object.
     */
    public SendPrivateMessage() {
        super("privateMessage",
                new String[] {"pm", "sendMessage"},
                MESSAGE_DESCRIPTION.tag,
                SubCommand.builder("privateMessage")
                        .addSubcommand(null,
                                Parameter.createInput(GeneralLocale.A_MESSAGE.tag, null, true),
                                Parameter.createInput(A_USER.tag, AD_USER.tag, true))
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
