package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.database.queries.GreetingData;
import de.eldoria.shepard.listener.MessageEventDataWrapper;
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
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper dataWrapper) {
        String cmd = args[0];
        if (cmd.equalsIgnoreCase("setChannel") || cmd.equalsIgnoreCase("sc")) {
            setChannel(args, dataWrapper);
            return;
        }

        if (cmd.equalsIgnoreCase("removeChannel") || cmd.equalsIgnoreCase(("rc"))) {
            removeChannel(dataWrapper);
            return;
        }

        if (cmd.equalsIgnoreCase("setMessage") || cmd.equalsIgnoreCase("sm")) {
            setMessage(args, dataWrapper);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, dataWrapper.getChannel());
        sendCommandUsage(dataWrapper.getChannel());
    }

    private void setMessage(String[] args, MessageEventDataWrapper receivedEvent) {
        if (args.length > 1) {
            String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            if (GreetingData.setGreetingText(receivedEvent.getGuild(), message, receivedEvent)) {
                MessageSender.sendMessage("Changed greeting message to " + lineSeparator()
                        + message, receivedEvent.getChannel());
            }
            return;
        }
        MessageSender.sendSimpleError(ErrorType.NO_MESSAGE_FOUND, receivedEvent.getChannel());
    }

    private void removeChannel(MessageEventDataWrapper receivedEvent) {
        GreetingData.removeGreetingChannel(receivedEvent.getGuild(), receivedEvent);

        MessageSender.sendMessage("Removed greeting channel.", receivedEvent.getChannel());
    }

    private void setChannel(String[] args, MessageEventDataWrapper receivedEvent) {
        if (args.length == 1) {
            if (GreetingData.setGreetingChannel(receivedEvent.getGuild(), receivedEvent.getChannel(), receivedEvent)) {
                MessageSender.sendMessage("Greeting Channel set to "
                        + ((TextChannel) receivedEvent.getChannel()).getAsMention(), receivedEvent.getChannel());
            }
            return;
        } else if (args.length == 2) {
            TextChannel channel = receivedEvent.getGuild().getTextChannelById(DbUtil.getIdRaw(args[1]));
            if (channel != null) {
                if (GreetingData.setGreetingChannel(receivedEvent.getGuild(), channel, receivedEvent)) {
                    MessageSender.sendMessage("Greeting channel set to "
                            + channel.getAsMention(), receivedEvent.getChannel());
                }

                return;
            }
        }
        MessageSender.sendSimpleError(ErrorType.TOO_MANY_ARGUMENTS, receivedEvent.getChannel());
        sendCommandUsage(receivedEvent.getChannel());
    }
}
