package de.eldoria.shepard.commandmodules.standalone.fun;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.localization.enums.commands.fun.LoveLocale;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Member;

import java.util.Collections;
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
                new String[] {"match"},
                LoveLocale.DESCRIPTION.tag,
                SubCommand.builder("love")
                        .addSubcommand(LoveLocale.C_OTHER.tag,
                                Parameter.createInput(GeneralLocale.A_USER.tag,
                                        GeneralLocale.AD_USER.tag, true),
                                Parameter.createInput(GeneralLocale.A_USER.tag,
                                        GeneralLocale.AD_USER.tag, true))
                        .addSubcommand(LoveLocale.C_SOMEONE.tag,
                                Parameter.createInput(GeneralLocale.A_USER.tag,
                                        GeneralLocale.AD_USER.tag, true))
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

        MessageSender.sendTextBox(null,
                Collections.singletonList(new LocalizedField("",
                        TextLocalizer.localizeAllAndReplace(LoveLocale.OTHER.tag, event,
                                String.valueOf(simp),
                                "**" + first.getEffectiveName() + "**",
                                "**" + second.getEffectiveName() + "**"), false, event)),
                event);

    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }
}
