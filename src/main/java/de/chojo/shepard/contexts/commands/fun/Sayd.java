package de.chojo.shepard.contexts.commands.fun;

import de.chojo.shepard.messagehandler.MessageSender;
import de.chojo.shepard.contexts.commands.Command;
import de.chojo.shepard.contexts.commands.CommandArg;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class Sayd extends Command {

    /**
     * Creates a new Sayd command object.
     */
    public Sayd() {
        commandName = "sayd";
        commandDesc = "Say and delete";
        arguments = new CommandArg[]
                {new CommandArg("Message", "Message Shepard should say.", true),};
    }

    @Override
    public boolean execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        try {
            if (args.length == 0) {
                MessageSender.sendError(new MessageEmbed.Field[] {
                                new MessageEmbed.Field("Too few arguments",
                                        "Use \"&help sayd\" for more information", false)},
                        receivedEvent.getChannel());
                return true;
            } else {
                String message = "";
                for (String arg : args) {
                    message = message.concat(arg + " ");
                }
                MessageSender.deleteMessage(receivedEvent);
                MessageSender.sendMessage(message, receivedEvent.getChannel());
                return true;
            }

        } catch (InsufficientPermissionException e) {
            MessageSender.sendError(new MessageEmbed.Field[] {
                            new MessageEmbed.Field("Lack of Permission",
                                    "Missing permission: MESSAGE_MANAGE", false)},
                    receivedEvent.getChannel());
        }
        return true;
    }
}
