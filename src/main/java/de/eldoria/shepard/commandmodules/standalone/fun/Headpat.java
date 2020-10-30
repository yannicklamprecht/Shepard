package de.eldoria.shepard.commandmodules.standalone.fun;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.localization.enums.commands.fun.HeadpatLocale;
import de.eldoria.shepard.localization.enums.commands.fun.LoveLocale;
import de.eldoria.shepard.localization.enums.commands.fun.TailpatLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.LocalizedField;
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

import java.util.Collections;


/**
 * Command which provides magic conch sentences.
 */
@CommandUsage(EventContext.GUILD)
public class Headpat extends Command implements Executable, ReqParser {
    private ArgumentParser parser;

    /**
     * Creates a new MagicConch command object.
     */
    public Headpat() {
        super("headpat",
                new String[] {"pat"},
                HeadpatLocale.DESCRIPTION.tag,
                SubCommand.builder("headpat")
                        .addSubcommand(LoveLocale.C_SOMEONE.tag,
                                Parameter.createInput(GeneralLocale.A_USER.tag,
                                        GeneralLocale.AD_USER.tag, true))
                        .build(),
                CommandCategory.FUN);
    }

    @SneakyThrows
    @Override
    public void execute(String label, String[] args, EventWrapper event) {
        Member target = parser.getGuildMember(event.getGuild().get(), args[0]);

        if(target == null){
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, event);
            return;
        }

        MessageEmbed build = new LocalizedEmbedBuilder(event)
                .setDescription(TextLocalizer.localizeAllAndReplace(HeadpatLocale.SOMEONE.tag, event,
                        "**" + event.getMember().orElseThrow().getAsMention() + "**",
                        "**" + target.getAsMention() + "**"))
                .setColor(Colors.Pastel.ORANGE)
                .build();
        event.getMessageChannel().sendMessage(build).queue();
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }
}
