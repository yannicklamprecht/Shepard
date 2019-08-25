package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.database.queries.Greetings;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

import static java.lang.System.lineSeparator;

public class Greeting extends Command {
    public Greeting() {
        commandName = "greeting";
        commandDesc = "Manage greeting settings.";
        arguments = new CommandArg[] {
                new CommandArg("action",
                        "setChannel -> Set or change the greeting ChannelremoveChannel | setMessage", true),
                new CommandArg("value",
                        "**setChannel** -> Channel Mention or execute in greeting Channel." + lineSeparator()
                                + "**removeChannel** -> leave empty" + lineSeparator()
                                + "**setMessage** -> Type your text message" + lineSeparator()
                                + "Supported Placeholders: {user_tag} {user_name} {user_mention}", false)};
    }


    @Override
    public void execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        if (args[0].equalsIgnoreCase("setChannel")) {
            setChannel(args, receivedEvent);
            return;
        }

        if (args[0].equalsIgnoreCase("removeChannel")) {
            removeChannel(receivedEvent);
            return;
        }

        if (args[0].equalsIgnoreCase("setMessage")) {
            setMessage(args, receivedEvent);
            return;
        }

        MessageSender.sendSimpleError("Invalid argument", receivedEvent.getChannel());
        sendCommandUsage(receivedEvent.getChannel());
    }

    private void setMessage(String[] args, MessageReceivedEvent receivedEvent) {
        if (args.length > 1) {
            String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            Greetings.setGreetingText(receivedEvent.getGuild(), message, receivedEvent);

            MessageSender.sendMessage("Changed greeting message to " + lineSeparator()
                    + message, receivedEvent.getChannel());
            return;
        }
        MessageSender.sendSimpleError("No message was found.", receivedEvent.getChannel());
    }

    private void removeChannel(MessageReceivedEvent receivedEvent) {
        Greetings.removeGreetingChannel(receivedEvent.getGuild(), receivedEvent);
        MessageSender.sendMessage("Removed greeting channel.", receivedEvent.getChannel());
    }

    private void setChannel(String[] args, MessageReceivedEvent receivedEvent) {
        if (args.length == 1) {
            Greetings.setGreetingChannel(receivedEvent.getGuild(),
                    receivedEvent.getChannel(), receivedEvent);
            MessageSender.sendMessage("Greeting Channel set to "
                    + ((TextChannel) receivedEvent.getChannel()).getAsMention(), receivedEvent.getChannel());
            return;
        } else if (args.length == 2) {
            TextChannel channel = receivedEvent.getGuild().getTextChannelById(DbUtil.getIdRaw(args[1]));
            if (channel != null) {
                Greetings.setGreetingChannel(receivedEvent.getGuild(), channel, receivedEvent);
                MessageSender.sendMessage("Greeting channel set to "
                        + channel.getAsMention(), receivedEvent.getChannel());
                return;
            }
        }
        MessageSender.sendSimpleError("Too many arguments", receivedEvent.getChannel());
        sendCommandUsage(receivedEvent.getChannel());
    }
}
