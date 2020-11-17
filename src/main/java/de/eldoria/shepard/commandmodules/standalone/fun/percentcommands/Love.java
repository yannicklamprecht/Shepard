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
import de.eldoria.shepard.localization.enums.commands.fun.LoveLocale;
import de.eldoria.shepard.localization.util.Format;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.Replacement;
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
public class Love extends Command implements Executable, ReqParser {
    private ArgumentParser parser;

    private final Cache<Long, Integer> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build();

    /**
     * Creates a new MagicConch command object.
     */
    public Love() {
        super("love",
                new String[]{"match"},
                LoveLocale.DESCRIPTION.tag,
                SubCommand.builder("love")
                        .addSubcommand(LoveLocale.C_OTHER.tag,
                                Parameter.createInput("command.general.argument.user",
                                        "command.general.argumentDescription.user", true),
                                Parameter.createInput("command.general.argument.user",
                                        "command.general.argumentDescription.user", true))
                        .addSubcommand(LoveLocale.C_SOMEONE.tag,
                                Parameter.createInput("command.general.argument.user",
                                        "command.general.argumentDescription.user", true))
                        .build(),
                CommandCategory.FUN);
    }

    @SneakyThrows
    @Override
    public void execute(String label, String[] args, EventWrapper event) {
        Member first;
        Member second;

        if (args.length == 1) {
            first = event.getMember().orElseThrow();
            second = parser.getGuildMember(event.getGuild().get(), args[0]);
        } else {
            first = parser.getGuildMember(event.getGuild().get(), args[0]);
            if (first == null) {
                MessageSender.sendSimpleError(ErrorType.INVALID_USER, event);
                return;
            }
            second = parser.getGuildMember(event.getGuild().get(), args[1]);
        }
        if (second == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, event);
            return;
        }

        long key = first.getIdLong() ^ second.getIdLong();
        int simp = cache.get(key, () -> key == 0 ? 100 : ThreadLocalRandom.current().nextInt(101));

        MessageEmbed build = new LocalizedEmbedBuilder(event)
                .setDescription(
                        TextLocalizer.localizeByWrapper("command.love.outputOther", event,
                                Replacement.create("PERC", simp, Format.BOLD),
                                Replacement.create("SOURCE", first.getEffectiveName(), Format.BOLD),
                                Replacement.create("TARGET", second.getEffectiveName(), Format.BOLD)))
                .setColor(Colors.Pastel.ORANGE)
                .build();
        event.getMessageChannel().sendMessage(build).queue();
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }
}
