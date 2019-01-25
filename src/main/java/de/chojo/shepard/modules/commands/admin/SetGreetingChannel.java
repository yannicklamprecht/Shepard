package de.chojo.shepard.modules.commands.admin;

import de.chojo.shepard.ShepardBot;
import de.chojo.shepard.database.DatabaseQuery;
import de.chojo.shepard.modules.commands.Command;
import de.chojo.shepard.modules.commands.CommandArg;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetGreetingChannel extends Command {
    private static final Pattern CHANNEL_MENTION_PATTERN = Pattern.compile("(?:<#)?(?<id>[0-9]{18})(?:>)?");
    public SetGreetingChannel(){
        commandName = "setGreetingChannel";
        commandDesc = "Set the greeting channel";
        args = new CommandArg[] {new CommandArg("ChannelName", "Name des Channels", true)};
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent receivedEvent) {
        Matcher matcher = CHANNEL_MENTION_PATTERN.matcher(args[1]);
        if (!matcher.find()) {
            System.out.println(args[1]);
            receivedEvent.getChannel().sendMessage("NEEEE " + args[1]).queue();
        }
        String channelId = matcher.group(1);
        if (receivedEvent.getGuild().getTextChannelById(channelId) == null) {
            return false; // invalid channel
        }
        DatabaseQuery.saveGreetingChannel(receivedEvent.getGuild().getId(), channelId);
        receivedEvent.getChannel().sendMessage(String.format("Es wird nun in %s gegrüßt!", args[1])).queue();
        return true;
    }
}
