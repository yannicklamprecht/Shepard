package de.eldoria.shepard.commandmodules.standalone.fun;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.command.GuildChannelOnly;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.Colors;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.fun.SomeoneLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.fun.SomeoneLocale.M_NO_ONLINE;
import static de.eldoria.shepard.localization.enums.commands.fun.SomeoneLocale.M_SOMEONE;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

/**
 * Command to tag someone in a channel who is online.
 */
public class Someone extends Command implements GuildChannelOnly, Executable {
    /**
     * Creates a new someone command object.
     */
    public Someone() {
        super("Someone",
                null,
                DESCRIPTION.tag,
                CommandCategory.FUN);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        GuildChannel guildChannelById = wrapper.getGuild().get()
                .getGuildChannelById(wrapper.getMessageChannel().getId());
        if (guildChannelById != null) {
            List<Member> members = guildChannelById.getMembers().stream()
                    .filter(member -> member.getOnlineStatus() != OnlineStatus.OFFLINE
                            && member.getIdLong() != wrapper.getAuthor().getIdLong()
                            && !member.getUser().isBot())
                    .collect(Collectors.toList());

            if (members.size() == 0) {
                MessageSender.sendMessage(M_NO_ONLINE.tag, wrapper.getMessageChannel());
                return;
            }

            Random rand = new Random();

            Member member = members.get(rand.nextInt(members.size()));

            LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(wrapper)
                    .setDescription(localizeAllAndReplace("**" + M_SOMEONE + "**", wrapper,
                            member.getAsMention())).setColor(Colors.Pastel.ORANGE);
            wrapper.getMessageChannel().sendMessage(builder.build()).queue();
        }
    }
}
