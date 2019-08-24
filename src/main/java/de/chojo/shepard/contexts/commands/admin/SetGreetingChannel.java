package de.chojo.shepard.contexts.commands.admin;

import de.chojo.shepard.contexts.commands.Command;
import de.chojo.shepard.contexts.commands.CommandArg;
import de.chojo.shepard.database.queries.Greetings;
import de.chojo.shepard.messagehandler.Messages;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetGreetingChannel extends Command {
    private static final Pattern CHANNEL_MENTION_PATTERN = Pattern.compile("(?:<#)?(?<id>[0-9]{18})(?:>)?");

    public SetGreetingChannel(){
        commandName = "setGreetingChannel";
        commandDesc = "Set the greeting channel";
        arguments = new CommandArg[] {new CommandArg("ChannelName", "Name des Channels", true)};
    }

    @Override
    public boolean execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        Matcher matcher = CHANNEL_MENTION_PATTERN.matcher(args[0]);
        if (!matcher.find()) {
            System.out.println(args[1]);
            receivedEvent.getChannel().sendMessage("NEEEE " + args[1]).queue();
        }
        String channelId = matcher.group(1);
        TextChannel channel = receivedEvent.getGuild().getTextChannelById(channelId);
        if (channel == null) {
            Messages.sendSimpleError("Invalid Channel", receivedEvent.getChannel());
            return false; // invalid channel
        }
        Greetings.setGreetingChannel(receivedEvent.getGuild().getId(), channelId, receivedEvent);
        Messages.sendMessage("I will greet every newcomer in " + channel.getAsMention(), receivedEvent.getChannel());
        return true;
    }
}
