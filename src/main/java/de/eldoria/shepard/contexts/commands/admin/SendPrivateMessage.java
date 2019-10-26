package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.User;

import static de.eldoria.shepard.localization.enums.GeneralLocale.A_USER;
import static de.eldoria.shepard.localization.enums.admin.PrivateMessageLocale.A_MESSAGE;
import static de.eldoria.shepard.localization.enums.admin.PrivateMessageLocale.MESSAGE_DESCRIPTION;

public class SendPrivateMessage extends Command {
    public SendPrivateMessage() {
        commandName = "privateMessage";
        commandAliases = new String[] {"pm", "sendMessage"};
        commandDesc = MESSAGE_DESCRIPTION.replacement;
        commandArgs = new CommandArg[] {
                new CommandArg("name", true,
                        new SubArg("name", A_USER.replacement)),
                new CommandArg("message", false,
                        new SubArg("message", A_MESSAGE.replacement))
        };
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (messageContext.getChannel() != Normandy.getPrivateAnswerChannel()) {
            MessageSender.sendSimpleError(ErrorType.EXCLUSIVE_CHANNEL, messageContext);
            return;
        }

        User user = ArgumentParser.getUser(args[0]);

        if (user == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, messageContext);
            return;
        }

        PrivateMessageHelper.sendPrivateMessage(args, messageContext, user);
    }
}
