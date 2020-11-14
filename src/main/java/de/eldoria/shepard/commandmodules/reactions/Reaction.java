package de.eldoria.shepard.commandmodules.reactions;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.Replacement;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.util.Colors;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import lombok.Data;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.text.WordUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.concurrent.ThreadLocalRandom;

@CommandUsage(EventContext.GUILD)
public abstract class Reaction extends Command implements Executable, ReqParser {

    private ArgumentParser parser;
    @Getter
    private static final Reactions REACTIONS;

    static {
        Yaml yaml = new Yaml(new Constructor(Reactions.class));
        REACTIONS = yaml.load(ShepardBot.class.getResourceAsStream("/reactions/reactions.yml"));
    }

    public Reaction(String commandName, String[] commandAliases) {
        this(commandName, commandAliases,
                "command.reaction.description." + commandName,
                "command.reaction.command.other" + WordUtils.capitalize(commandName),
                "command.reaction.command." + commandName);
    }

    public Reaction(String commandName, String[] commandAliases, String commandDesc, String otherCommandTag, String standaloneDescription) {
        super(commandName,
                commandAliases,
                commandDesc,
                SubCommand.builder(commandName).addSubcommand(
                        otherCommandTag,
                        Parameter.createInput("command.general.argument.user",
                                "command.general.argumentDescription.user", false))
                        .build(),
                standaloneDescription, CommandCategory.REACTION);

    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        Member target = wrapper.getMember().orElseThrow();
        String message = "";
        if (args.length != 0) {
            target = parser.getGuildMember(wrapper.getGuild().get(), args[0]);
            if (target == null) {
                MessageSender.sendSimpleError(ErrorType.INVALID_USER, wrapper);
                return;
            }
            message = TextLocalizer.localizeByWrapper(getOtherMessageLocaleCode(), wrapper,
                    Replacement.createMention("ACTOR", wrapper.getActor()), Replacement.createMention("TARGET", target));
        }

        if (Verifier.equalSnowflake(target, wrapper.getAuthor())) {
            message = TextLocalizer.localizeByWrapper(getSelfMessageLocaleCode(), wrapper, Replacement.createMention("ACTOR", target));
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

    protected String getOtherMessageLocaleCode() {
        return "command.reaction.message." + commandName;
    }

    protected String getSelfMessageLocaleCode() {
        return "command.reaction.message.self" + WordUtils.capitalize(commandName);
    }

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

        private String[] wave = null;
        private String[] sleep = null;
        private String[] punish = null;
        private String[] confused = null;
        private String[] dance = null;
        private String[] shrug = null;
        private String[] eat = null;
        private String[] poke = null;
        private String[] smug = null;
    }
}
