package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class Say extends Command {

    /**
     * Creates a new Sayd command object.
     */
    public Say() {
        commandName = "say";
        commandDesc = "Let shepard say something - Use \"sayd\" to delete your command afterwards.";
        commandArgs = new CommandArg[]
                {new CommandArg("Message", "Message Shepard should say.", true),};
        commandAliases = new String[] {"sayd"};
        category = ContextCategory.FUN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 0) {
            MessageSender.sendError(
                    new MessageEmbed.Field[] {
                            new MessageEmbed.Field("Too few arguments",
                                    "Use \"&help sayd\" for more information", false)},
                    messageContext.getChannel());
        } else {
            String message = "";
            for (String arg : args) {
                message = message.concat(arg + " ");
            }
            MessageSender.sendMessage(message, messageContext.getChannel());
        }

        if (label.equalsIgnoreCase("sayd")) {
            messageContext.getMessage().delete().queue();
        }
    }
}
