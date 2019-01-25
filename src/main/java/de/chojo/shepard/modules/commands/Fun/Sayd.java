package de.chojo.shepard.modules.commands.Fun;

import de.chojo.shepard.messageHandler.Messages;
import de.chojo.shepard.modules.commands.Command;
import de.chojo.shepard.modules.commands.CommandArg;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class Sayd extends Command {
    public Sayd() {
        commandName = "sayd";
        commandAliases = null;
        commandDesc = "Say and delete";
        args = new CommandArg[]
                {new CommandArg("Message", "Message Shepard should say.", true),};
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent receivedEvent) {
        try {
            if (args.length == 1) {
                Messages.sendError(new MessageEmbed.Field[]{new MessageEmbed.Field("Too few arguments", "Use \"&help sayd\" for more information", false)}, receivedEvent.getChannel());
                return true;
            } else {
                String message = "";
                args[0] = "";
                for (String arg : args) {
                    message = message.concat(arg + " ");
                }
                Messages.deleteMessage(receivedEvent);
                Messages.sendMessage(message, receivedEvent.getChannel());
                return true;
            }

        }catch (InsufficientPermissionException e){
            Messages.sendError(new MessageEmbed.Field[] {new MessageEmbed.Field("Lack of Permission","Missing permission: MESSAGE_MANAGE",false)},receivedEvent.getChannel());
        }
        return true;
    }
}
