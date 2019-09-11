package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.listener.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class Sayd extends Command {

    /**
     * Creates a new Sayd command object.
     */
    public Sayd() {
        commandName = "sayd";
        commandDesc = "Say and delete";
        commandArgs = new CommandArg[]
                {new CommandArg("Message", "Message Shepard should say.", true),};
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper dataWrapper) {
        try {
            if (args.length == 0) {
                MessageSender.sendError(
                        new MessageEmbed.Field[] {
                                new MessageEmbed.Field("Too few arguments",
                                        "Use \"&help sayd\" for more information", false)},
                        dataWrapper.getChannel());
            } else {
                String message = "";
                for (String arg : args) {
                    message = message.concat(arg + " ");
                }
                MessageSender.deleteMessage(dataWrapper);
                MessageSender.sendMessage(message, dataWrapper.getChannel());
            }

        } catch (InsufficientPermissionException e) {
            MessageSender.sendError(
                    new MessageEmbed.Field[] {new MessageEmbed.Field("Lack of Permission",
                            "Missing permission: MESSAGE_MANAGE", false)},
                    dataWrapper.getChannel());
        }
    }
}
