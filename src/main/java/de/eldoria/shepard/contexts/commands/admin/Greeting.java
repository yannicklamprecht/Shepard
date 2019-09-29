package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.database.queries.GreetingData;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;

import static java.lang.System.lineSeparator;

public class Greeting extends Command {
    /**
     * Creates a new greeting command object.
     */
    public Greeting() {
        commandName = "greeting";
        commandDesc = "Manage greeting settings.";
        commandArgs = new CommandArg[] {
                new CommandArg("action",
                        "**__s__et__C__hannel** -> Set or change the greeting Channel" + lineSeparator()
                                + "**__r__emove__C__hannel** -> Remove channel and disable greeting." + lineSeparator()
                                + "**__s__et__M__essage** -> Set or change the greeting message", true),
                new CommandArg("value",
                        "**setChannel** -> Channel Mention or execute in greeting Channel." + lineSeparator()
                                + "**removeChannel** -> leave empty" + lineSeparator()
                                + "**setMessage** -> Type your text message" + lineSeparator()
                                + "Supported Placeholders: {user_tag} {user_name} {user_mention}", false)};
    }


    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        if (cmd.equalsIgnoreCase("setChannel") || cmd.equalsIgnoreCase("sc")) {
            setChannel(args, messageContext);
            return;
        }

        if (cmd.equalsIgnoreCase("removeChannel") || cmd.equalsIgnoreCase(("rc"))) {
            removeChannel(messageContext);
            return;
        }

        if (cmd.equalsIgnoreCase("setMessage") || cmd.equalsIgnoreCase("sm")) {
            setMessage(args, messageContext);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getChannel());
        sendCommandUsage(messageContext.getChannel());
    }

    private void setMessage(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length > 1) {
            String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            if (GreetingData.setGreetingText(messageContext.getGuild(), message, messageContext)) {
                MessageSender.sendMessage("Changed greeting message to " + lineSeparator()
                        + message, messageContext.getChannel());
            }
            return;
        }
        MessageSender.sendSimpleError(ErrorType.NO_MESSAGE_FOUND, messageContext.getChannel());
    }

    private void removeChannel(MessageEventDataWrapper messageContext) {
        if (GreetingData.removeGreetingChannel(messageContext.getGuild(), messageContext)) {
            MessageSender.sendMessage("Removed greeting channel.", messageContext.getChannel());
        }

    }

    private void setChannel(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 1) {
            if (GreetingData.setGreetingChannel(messageContext.getGuild(),
                    messageContext.getChannel(), messageContext)) {
                MessageSender.sendMessage("Greeting Channel set to "
                        + ((TextChannel) messageContext.getChannel()).getAsMention(), messageContext.getChannel());
            }
            return;
        } else if (args.length == 2) {
            TextChannel channel = messageContext.getGuild().getTextChannelById(DbUtil.getIdRaw(args[1]));
            if (channel != null) {
                if (GreetingData.setGreetingChannel(messageContext.getGuild(), channel, messageContext)) {
                    MessageSender.sendMessage("Greeting channel set to "
                            + channel.getAsMention(), messageContext.getChannel());
                }

                return;
            }
        }
        MessageSender.sendSimpleError(ErrorType.TOO_MANY_ARGUMENTS, messageContext.getChannel());
        sendCommandUsage(messageContext.getChannel());
    }
}
