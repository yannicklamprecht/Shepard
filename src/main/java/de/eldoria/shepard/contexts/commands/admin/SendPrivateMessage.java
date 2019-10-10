package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.collections.Normandy;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.User;

public class SendPrivateMessage extends Command {
    public SendPrivateMessage() {
        commandName = "privateMessage";
        commandAliases = new String[] {"pm", "sendMessage"};
        commandDesc = "Sends a user a private message.";
        commandArgs = new CommandArg[] {
                new CommandArg("name",
                        "Tag of the User",
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

        User user;
        try {
            user = ShepardBot.getJDA().getUserByTag(args[0]);
        } catch (IllegalArgumentException e) {
            user = ShepardBot.getJDA().getUserById(DbUtil.getIdRaw(args[0]));

        }

        PrivateMessageHelper.sendPrivateMessage(args, messageContext, user);
    }
}
