package de.eldoria.shepard.commandmodules.standalone.fun.percentcommands;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.util.Colors;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


/**
 * Command which provides magic conch sentences.
 */
@CommandUsage(EventContext.GUILD)
public class AbstractPercentCommand extends Command implements Executable, ReqParser {
    private final String messageLocale;
    private ArgumentParser parser;

    private final Cache<Long, Integer> cache = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES).build();

    /**
     * Creates a new waifu command object.
     */
    public AbstractPercentCommand(String commandName, String[] commandAliases, String commandDesc,
                                  String commandLocale, String standaloneDescription, String messageLocale) {
        super(commandName,
                commandAliases,
                commandDesc,
                SubCommand.builder(commandName).addSubcommand(
                        commandLocale,
                        Parameter.createInput("command.general.argument.user",
                                "command.general.argumentDescription.user", false))
                        .build(),
                standaloneDescription,
                CommandCategory.FUN);
        this.messageLocale = messageLocale;
    }

    @SneakyThrows
    @Override
    public final void execute(String label, String[] args, EventWrapper event) {
        Member member = args.length == 0 ? event.getMember().orElseThrow() : parser.getGuildMember(event.getGuild().orElseThrow(), args[0]);

        if (member == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, event);
            return;
        }

        int value = cache.get(member.getIdLong(), () -> getRandom(member));

        event.getMessageChannel().sendMessage(getEmbed(event, label, member, value)).queue();
    }

    protected MessageEmbed getEmbed(EventWrapper event, String label, Member member, int value) {
        return new LocalizedEmbedBuilder(event)
                .setDescription(
                        TextLocalizer.localizeAllAndReplace(messageLocale, event,
                                "**" + value + "**", "**" + member.getEffectiveName() + "**"))
                .setColor(Colors.Pastel.ORANGE)
                .build();
    }

    protected int getRandom(Member member) {
        return ThreadLocalRandom.current().nextInt(101);
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }
}
