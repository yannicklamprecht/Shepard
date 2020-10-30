package de.eldoria.shepard.commandmodules.reactions;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.util.Colors;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import lombok.Data;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.concurrent.ThreadLocalRandom;

@CommandUsage(EventContext.GUILD)
public abstract class Reaction extends Command implements Executable, ReqParser {

    private ArgumentParser parser;
    @Getter
    private final Reactions reactions;

    public Reaction(String commandName, String[] commandAliases, String commandDesc, String otherCommandTag, String standaloneDescription) {
        super(commandName, commandAliases, commandDesc,
                SubCommand.builder(commandName).addSubcommand(
                        otherCommandTag,
                        Parameter.createInput(GeneralLocale.A_USER.tag,
                                GeneralLocale.AD_USER.tag, false))
                        .build(),
                standaloneDescription, CommandCategory.FUN);

        Yaml yaml = new Yaml(new Constructor(Reactions.class));

        reactions = yaml.load(getClass().getResourceAsStream("/reactions/reactions.yml"));
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        Member target = wrapper.getMember().orElseThrow();
        String message = "";
        if (args.length != 0) {
            target = parser.getGuildMember(wrapper.getGuild().get(), args[0]);
            message = TextLocalizer.localizeAllAndReplace(getOtherMessageLocaleCode(), wrapper,
                    wrapper.getActor().getAsMention(), target.getAsMention());
        }

        if (Verifier.equalSnowflake(target, wrapper.getAuthor())) {
            message = TextLocalizer.localizeAllAndReplace(getSelfMessageLocaleCode(), wrapper, target.getAsMention());
        }

        MessageEmbed build = new LocalizedEmbedBuilder(wrapper)
                .setDescription(message)
                .setImage(getImages()[ThreadLocalRandom.current().nextInt(getImages().length)])
                .setColor(Colors.Pastel.ORANGE)
                .build();
        wrapper.getMessageChannel().sendMessage(build).queue();
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }

    protected abstract String[] getImages();

    protected abstract String getOtherMessageLocaleCode();

    protected abstract String getSelfMessageLocaleCode();

    @Data
    public static class Reactions {
        private String[] hug = null;
        private String[] kiss = null;
        private String[] slap = null;
        private String[] spank = null;
        private String[] cry = null;
        private String[] blush = null;
        private String[] lick = null;
        private String[] pat = null;
    }
}
