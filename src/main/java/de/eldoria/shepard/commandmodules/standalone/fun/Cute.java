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
import de.eldoria.shepard.localization.enums.commands.fun.CuteLocale;
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
public class Cute extends Command implements Executable, ReqParser {
    private ArgumentParser parser;

    private final Cache<Long, Integer> cache = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES).build();

    /**
     * Creates a new MagicConch command object.
     */
    public Cute() {
        super("cute",
                null,
                CuteLocale.DESCRIPTION.tag,
                SubCommand.builder("cute").addSubcommand(
                        CuteLocale.C_OTHER.tag,
                        Parameter.createInput(GeneralLocale.A_USER.tag,
                                GeneralLocale.AD_USER.tag, false))
                        .build(),
                CuteLocale.C_EMPTY.tag,
                CommandCategory.FUN);
    }

    @SneakyThrows
    @Override
    public void execute(String label, String[] args, EventWrapper event) {
        Member member = event.getMember().orElseThrow();

        if (args.length != 0) {
            member = parser.getGuildMember(event.getGuild().orElseThrow(), args[0]);
            if (member == null) {
                MessageSender.sendSimpleError(ErrorType.INVALID_USER, event);
                return;
            }
        }

        Member finalMember = member;
        int cute = cache.get(member.getIdLong(), () -> {
            if (finalMember.getUser().getName().toLowerCase().startsWith("ch")) {
                return 100;
            }
            return ThreadLocalRandom.current().nextInt(71) + 30;
        });

        MessageEmbed build = new LocalizedEmbedBuilder(event)
                .setDescription(
                        TextLocalizer.localizeAllAndReplace(CuteLocale.OTHER.tag, event,
                                "**"+ cute + "**", "**" + member.getEffectiveName() + "**"))
                .setColor(Colors.Pastel.ORANGE)
                .build();
        event.getMessageChannel().sendMessage(build).queue();
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }
}
