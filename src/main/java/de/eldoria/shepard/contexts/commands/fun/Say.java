package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.localization.enums.commands.fun.SayLocale;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

public class Say extends Command {

    /**
     * Creates a new Sayd command object.
     */
    public Say() {
        commandName = "say";
        commandDesc = SayLocale.DESCRIPTION.tag;
        commandArgs = new CommandArg[] {
                new CommandArg("Message", true,
                        new SubArg("message", SayLocale.A_SAY.tag))
        };
        commandAliases = new String[] {"sayd"};
        category = ContextCategory.FUN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        MessageSender.sendMessage(ArgumentParser.getMessage(args, 0), messageContext.getTextChannel());

        if (label.equalsIgnoreCase("sayd")) {
            messageContext.getMessage().delete().queue();
        }

    }
}