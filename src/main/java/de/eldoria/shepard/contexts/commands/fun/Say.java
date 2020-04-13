package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.Parameter;
import de.eldoria.shepard.contexts.commands.argument.SubCommand;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import static de.eldoria.shepard.localization.enums.commands.fun.SayLocale.A_SAY;
import static de.eldoria.shepard.localization.enums.commands.fun.SayLocale.DESCRIPTION;

public class Say extends Command {

    /**
     * Creates a new Sayd command object.
     */
    public Say() {
        super("say",
                new String[] {"sayd"},
                DESCRIPTION.tag,
                SubCommand.builder("say")
                        .addSubcommand(null,
                                Parameter.createInput(GeneralLocale.A_MESSAGE.tag, A_SAY.tag, true))
                        .build(),
                ContextCategory.FUN);
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        MessageSender.sendMessage(ArgumentParser.getMessage(args, 0), messageContext.getTextChannel());

        if (label.equalsIgnoreCase("sayd")) {
            messageContext.getMessage().delete().queue();
        }

    }
}