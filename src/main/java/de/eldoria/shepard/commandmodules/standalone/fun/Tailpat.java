package de.eldoria.shepard.commandmodules.standalone.fun;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.fun.TailpatLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.util.Colors;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.concurrent.ThreadLocalRandom;


/**
 * Command which provides magic conch sentences.
 */
@CommandUsage(EventContext.GUILD)
public class Tailpat extends Command implements Executable, ReqParser {
    private ArgumentParser parser;

    /**
     * Creates a new MagicConch command object.
     */
    public Tailpat() {
        super("tailpat",
                null,
                "command.tailpat.description",
                SubCommand.builder("tailpat")
                        .addSubcommand("command.tailpat.command.someone",
                                Parameter.createInput("command.general.argument.user",
                                        "command.general.argumentDescription.user", true))
                        .build(),
                CommandCategory.FUN);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        boolean success = ThreadLocalRandom.current().nextBoolean();

        Member target = parser.getGuildMember(wrapper.getGuild().get(), args[0]);

        if(target == null){
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, wrapper);
            return;
        }

        String[] decisions = locale.getLanguageString(wrapper.getGuild().get(),
                success ? TailpatLocale.ANSWER_POSITIVE.localeCode : TailpatLocale.ANSWER_NEGATIVE.localeCode)
                .split("\\|");

        String part = decisions[ThreadLocalRandom.current().nextInt(decisions.length)];

        MessageEmbed build = new LocalizedEmbedBuilder(wrapper)
                .setDescription(
                        TextLocalizer.localizeAllAndReplace(
                                (success ? TailpatLocale.M_ANSWER_POSITIVE.tag : TailpatLocale.M_ANSWER_NEGATIVE) + " " + part, wrapper,
                                wrapper.getActor().getAsMention(), target.getAsMention()))
                .setColor(Colors.Pastel.ORANGE)
                .build();

        wrapper.getMessageChannel().sendMessage(build).queue();
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }
}
