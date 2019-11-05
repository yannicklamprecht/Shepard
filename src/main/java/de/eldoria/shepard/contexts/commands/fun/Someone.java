package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.fun.SomeoneLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.fun.SomeoneLocale.M_NO_ONLINE;
import static de.eldoria.shepard.localization.enums.commands.fun.SomeoneLocale.M_SOMEONE;
import static de.eldoria.shepard.localization.util.TextLocalizer.fastLocaleAndReplace;

public class Someone extends Command {
    public Someone() {
        commandName = "Someone";
        commandDesc = DESCRIPTION.tag;
        category = ContextCategory.FUN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        GuildChannel guildChannelById = messageContext.getGuild()
                .getGuildChannelById(messageContext.getChannel().getId());
        if (guildChannelById != null) {
            List<Member> members = guildChannelById.getMembers().stream()
                    .filter(member -> member.getOnlineStatus() != OnlineStatus.OFFLINE
                            && member.getIdLong() != messageContext.getAuthor().getIdLong()
                            && !member.getUser().isBot())
                    .collect(Collectors.toList());

            if (members.size() == 0) {
                MessageSender.sendMessage(M_NO_ONLINE.tag, messageContext.getTextChannel());
                return;
            }

            Random rand = new Random();

            Member member = members.get(rand.nextInt(members.size()));

            MessageSender.sendMessage(fastLocaleAndReplace(M_SOMEONE.tag, messageContext.getGuild(),
                    member.getAsMention()), messageContext.getTextChannel());
        }
    }
}
