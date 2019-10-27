package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.database.queries.GreetingData;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.entities.TextChannel;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_CHANNEL_MENTION_OR_EXECUTE;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_EMPTY;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_MESSAGE_MENTION;
import static de.eldoria.shepard.localization.enums.commands.admin.GreetingsLocale.C_REMOVE_CHANNEL;
import static de.eldoria.shepard.localization.enums.commands.admin.GreetingsLocale.C_SET_CHANNEL;
import static de.eldoria.shepard.localization.enums.commands.admin.GreetingsLocale.C_SET_MESSAGE;
import static de.eldoria.shepard.localization.enums.commands.admin.GreetingsLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.admin.GreetingsLocale.M_REMOVED_CHANNEL;
import static de.eldoria.shepard.localization.enums.commands.admin.GreetingsLocale.M_SET_CHANNEL;
import static de.eldoria.shepard.localization.enums.commands.admin.GreetingsLocale.M_SET_MESSAGE;
import static de.eldoria.shepard.util.Verifier.isArgument;
import static java.lang.System.lineSeparator;

public class Greeting extends Command {
    /**
     * Creates a new greeting command object.
     */
    public Greeting() {
        commandName = "greeting";
        commandDesc = DESCRIPTION.replacement;
        commandArgs = new CommandArg[] {
                new CommandArg("action", true,
                        new SubArg("setChannel", C_SET_CHANNEL.replacement, true),
                        new SubArg("removeChannel", C_REMOVE_CHANNEL.replacement, true),
                        new SubArg("setMessage", C_SET_MESSAGE.replacement, true)),
                new CommandArg("value", false,
                        new SubArg("setChannel", A_CHANNEL_MENTION_OR_EXECUTE.replacement),
                        new SubArg("removeChannel", A_EMPTY.replacement),
                        new SubArg("setMessage", A_MESSAGE_MENTION.replacement))
        };
        category = ContextCategory.ADMIN;
    }


    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        if (isArgument(cmd, "setChannel", "sc")) {
            setChannel(args, messageContext);
            return;
        }

        if (isArgument(cmd, "removeChannel", "rc")) {
            removeChannel(messageContext);
            return;
        }

        if (isArgument(cmd, "setMessage", "sm")) {
            setMessage(args, messageContext);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext);
    }

    private void setMessage(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length > 1) {
            String message = ArgumentParser.getMessage(args, 1);

            if (GreetingData.setGreetingText(messageContext.getGuild(), message, messageContext)) {
                MessageSender.sendMessage(M_SET_MESSAGE + lineSeparator()
                        + message, messageContext);
            }
            return;
        }
        MessageSender.sendSimpleError(ErrorType.NO_MESSAGE_FOUND, messageContext);
    }

    private void removeChannel(MessageEventDataWrapper messageContext) {
        if (GreetingData.removeGreetingChannel(messageContext.getGuild(), messageContext)) {
            MessageSender.sendMessage(M_REMOVED_CHANNEL.replacement, messageContext);
        }
    }

    private void setChannel(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 1) {
            if (GreetingData.setGreetingChannel(messageContext.getGuild(),
                    messageContext.getChannel(), messageContext)) {
                MessageSender.sendMessage(M_SET_CHANNEL + " "
                        + messageContext.getTextChannel().getAsMention(), messageContext);
            }
            return;
        } else if (args.length == 2) {
            TextChannel channel = ArgumentParser.getTextChannel(messageContext.getGuild(), args[1]);

            if (channel != null) {
                if (GreetingData.setGreetingChannel(messageContext.getGuild(), channel, messageContext)) {
                    MessageSender.sendMessage(
                            M_SET_CHANNEL + " "
                                    + channel.getAsMention(), messageContext);
                }
                return;
            }
        }
        MessageSender.sendSimpleError(ErrorType.TOO_MANY_ARGUMENTS, messageContext);
    }
}
