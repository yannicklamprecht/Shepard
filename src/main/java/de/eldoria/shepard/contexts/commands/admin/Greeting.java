package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArgument;
import de.eldoria.shepard.contexts.commands.argument.SubArgument;
import de.eldoria.shepard.database.queries.GreetingData;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
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
import static java.lang.System.lineSeparator;

/**
 * Command to configure the greeting message and channel.
 */
public class Greeting extends Command {
    /**
     * Creates a new greeting command object.
     */
    public Greeting() {
        commandName = "greeting";
        commandDesc = DESCRIPTION.tag;
        commandArguments = new CommandArgument[] {
                new CommandArgument("action", true,
                        new SubArgument("setChannel", C_SET_CHANNEL.tag, true),
                        new SubArgument("removeChannel", C_REMOVE_CHANNEL.tag, true),
                        new SubArgument("setMessage", C_SET_MESSAGE.tag, true)),
                new CommandArgument("value", false,
                        new SubArgument("setChannel", A_CHANNEL_MENTION_OR_EXECUTE.tag),
                        new SubArgument("removeChannel", A_EMPTY.tag),
                        new SubArgument("setMessage", A_MESSAGE_MENTION.tag))
        };
        category = ContextCategory.ADMIN;
    }


    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        CommandArgument arg = commandArguments[0];
        if (arg.isSubCommand(cmd, 0)) {
            setChannel(args, messageContext);
            return;
        }

        if (arg.isSubCommand(cmd, 1)) {
            removeChannel(messageContext);
            return;
        }

        if (arg.isSubCommand(cmd, 2)) {
            setMessage(args, messageContext);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());
    }

    private void setMessage(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length > 1) {
            String message = ArgumentParser.getMessage(args, 1);

            if (GreetingData.setGreetingText(messageContext.getGuild(), message, messageContext)) {
                MessageSender.sendMessage(M_SET_MESSAGE + lineSeparator()
                        + message, messageContext.getTextChannel());
            }
            return;
        }
        MessageSender.sendSimpleError(ErrorType.NO_MESSAGE_FOUND, messageContext.getTextChannel());
    }

    private void removeChannel(MessageEventDataWrapper messageContext) {
        if (GreetingData.removeGreetingChannel(messageContext.getGuild(), messageContext)) {
            MessageSender.sendMessage(M_REMOVED_CHANNEL.tag, messageContext.getTextChannel());
        }
    }

    private void setChannel(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 1) {
            if (GreetingData.setGreetingChannel(messageContext.getGuild(),
                    messageContext.getChannel(), messageContext)) {
                MessageSender.sendMessage(M_SET_CHANNEL + " "
                        + messageContext.getTextChannel().getAsMention(), messageContext.getTextChannel());
            }
            return;
        } else if (args.length == 2) {
            TextChannel channel = ArgumentParser.getTextChannel(messageContext.getGuild(), args[1]);

            if (channel != null) {
                if (GreetingData.setGreetingChannel(messageContext.getGuild(), channel, messageContext)) {
                    MessageSender.sendMessage(
                            M_SET_CHANNEL + " "
                                    + channel.getAsMention(), messageContext.getTextChannel());
                }
                return;
            }
        }
        MessageSender.sendSimpleError(ErrorType.TOO_MANY_ARGUMENTS, messageContext.getTextChannel());
    }
}
