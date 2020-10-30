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
import de.eldoria.shepard.localization.enums.commands.fun.SimpLocale;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


/**
 * Command which provides magic conch sentences.
 */
@CommandUsage(EventContext.GUILD)
public class Simp extends Command implements Executable, ReqParser {
    private ArgumentParser parser;

    private final Cache<Long, Integer> cache = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES).build();

    /**
     * Creates a new MagicConch command object.
     */
    public Simp() {
        super("simp",
                null,
                SimpLocale.DESCRIPTION.tag,
                SubCommand.builder("simp").addSubcommand(
                        SimpLocale.C_OTHER.tag,
                        Parameter.createInput(GeneralLocale.A_USER.tag,
                                GeneralLocale.AD_USER.tag, false))
                        .build(),
                SimpLocale.C_EMPTY.tag,
                CommandCategory.FUN);
    }

    @SneakyThrows
    @Override
    public void execute(String label, String[] args, EventWrapper event) {
        Member user = event.getMember().orElseThrow();

        if (args.length != 0) {
            user = parser.getGuildMember(event.getGuild().get(), args[0]);
            if (user == null) {
                MessageSender.sendSimpleError(ErrorType.INVALID_USER, event);
                return;
            }
        }

        Member finalUser = user;
        int simp = cache.get(user.getIdLong(), () -> {
            if (finalUser.getIdLong() == 473173419056300032L) {
                return 100;
            }
            return ThreadLocalRandom.current().nextInt(101);
        });


        MessageSender.sendTextBox(null,
                Collections.singletonList(new LocalizedField("",
                        TextLocalizer.localizeAllAndReplace(SimpLocale.OTHER.tag, event,
                                String.valueOf(simp), "**" + user.getEffectiveName() + "**"), false, event)),
                event);

    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }
}
