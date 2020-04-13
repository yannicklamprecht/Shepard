package de.eldoria.shepard.contexts.commands.exclusive;

import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.Parameter;
import de.eldoria.shepard.contexts.commands.argument.SubCommand;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.User;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_USER;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_USER;
import static de.eldoria.shepard.localization.enums.commands.admin.PrivateMessageLocale.MESSAGE_DESCRIPTION;

/**
 * Command which makes it possible to send private messages as the bot.
 */
public class SendPrivateMessage extends Command {
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
                ContextCategory.EXCLUSIVE
        );
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (messageContext.getChannel() != Normandy.getPrivateAnswerChannel()) {
            MessageSender.sendSimpleError(ErrorType.EXCLUSIVE_CHANNEL, messageContext.getTextChannel());
            return;
        }

        User user = ArgumentParser.getUser(args[0]);

        if (user == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, messageContext.getTextChannel());
            return;
        }

        PrivateMessageHelper.sendPrivateMessage(args, messageContext, user);
    }
}
