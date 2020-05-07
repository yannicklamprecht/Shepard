package de.eldoria.shepard.commandmodules.standalone.fun;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import static de.eldoria.shepard.localization.enums.commands.fun.SayLocale.A_SAY;
import static de.eldoria.shepard.localization.enums.commands.fun.SayLocale.DESCRIPTION;

public class Say extends Command implements Executable {

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
                CommandCategory.FUN);
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        messageContext.getTextChannel().sendMessage(ArgumentParser.getMessage(args, 0)).queue();

        if (label.equalsIgnoreCase("sayd")) {
            messageContext.getMessage().delete().queue();
        }
    }
}