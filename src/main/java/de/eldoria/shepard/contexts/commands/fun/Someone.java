package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Someone extends Command {
    public Someone() {
        commandName = "Someone";
        commandDesc = "Tags someone who is online in this channel";
    }

    @Override
    public void execute(String label, String[] args, MessageReceivedEvent receivedEvent) {
        GuildChannel guildChannelById = receivedEvent.getGuild().getGuildChannelById(receivedEvent.getChannel().getId());
        if (guildChannelById != null) {
            List<Member> members = guildChannelById.getMembers().stream()
                    .filter(member -> member.getOnlineStatus() != OnlineStatus.OFFLINE).collect(Collectors.toList());

            members.removeIf(member -> member.getId().equalsIgnoreCase(receivedEvent.getAuthor().getId())
            || member.getUser().isBot());
            if (members.size() == 0) {
                MessageSender.sendMessage("No one is online :fearful:", receivedEvent.getChannel());
                return;
            }

            Random rand = new Random();

            Member member = members.get(rand.nextInt(members.size()));

            MessageSender.sendMessage(member.getAsMention() + " is someone!", receivedEvent.getChannel());

        }
    }
}
