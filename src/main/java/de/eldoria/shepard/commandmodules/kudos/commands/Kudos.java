package de.eldoria.shepard.commandmodules.kudos.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.kudos.data.KudoData;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.core.configuration.Config;
import de.eldoria.shepard.database.types.Rank;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqConfig;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqShardManager;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.sql.DataSource;
import java.util.List;
import java.util.OptionalInt;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_AMOUNT;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_USER;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_USER;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.A_POINTS;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.C_EMPTY;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.C_GIVE;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.C_TOP;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.C_TOP_GLOBAL;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.M_DESCRIPTION_EXTENDED;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.M_DESCRIPTION_GENERAL;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.M_GLOBAL_RANKING;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.M_RECEIVED_KUDOS;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.M_SERVER_RANKING;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;
import static java.lang.System.lineSeparator;

/**
 * Command to give Kudos and see the top Kudos owner.
 */
@CommandUsage(EventContext.GUILD)
public class Kudos extends Command implements Executable, ReqShardManager, ReqParser, ReqConfig, ReqDataSource, ReqInit {
    private ArgumentParser parser;
    private ShardManager shardManager;
    private KudoData kudoData;
    private DataSource source;

    /**
     * Create a new kudos command object.
     */
    public Kudos() {
        super("kudo",
                new String[] {"kudos"},
                DESCRIPTION.tag,
                SubCommand.builder("kudo")
                        .addSubcommand(C_GIVE.tag,
                                Parameter.createCommand("give"),
                                Parameter.createInput(A_USER.tag, AD_USER.tag, true),
                                Parameter.createInput(A_POINTS.tag, AD_AMOUNT.tag, true))
                        .addSubcommand(C_TOP.tag,
                                Parameter.createCommand("top"))
                        .addSubcommand(C_TOP_GLOBAL.tag,
                                Parameter.createCommand("topGlobal"))
                        .build(),
                C_EMPTY.tag,
                CommandCategory.FUN);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        if (args.length == 0) {
            int freePoints = kudoData.getFreePoints(
                    wrapper.getGuild().get(), wrapper.getAuthor(), wrapper);
            int userPoints = kudoData.getUserScore(
                    wrapper.getGuild().get(), wrapper.getAuthor(), wrapper);
            int globalUserPoints = kudoData.getGlobalUserScore(wrapper.getAuthor(), wrapper);

            String message = localizeAllAndReplace(M_DESCRIPTION_GENERAL.tag, wrapper.getGuild().get(),
                    "**" + freePoints + "**", "**100**", "1", "**" + userPoints + "**");
            message = userPoints != globalUserPoints
                    ? message + lineSeparator() + localizeAllAndReplace(M_DESCRIPTION_EXTENDED.tag,
                    wrapper.getGuild().get(), "**" + globalUserPoints + "**")
                    : message;

            MessageSender.sendMessage(message, wrapper.getMessageChannel());
            return;
        }

        String cmd = args[0];

        if (isSubCommand(cmd, 0)) {
            give(args, wrapper);
            return;
        }

        if (isSubCommand(cmd, 1)) {
            sendTopScores(false, wrapper);
            return;
        }

        if (isSubCommand(cmd, 2)) {
            sendTopScores(true, wrapper);
            return;
        }
    }

    private void give(String[] args, EventWrapper wrapper) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, wrapper);
            return;
        }

        Member member = parser.getGuildMember(wrapper.getGuild().get(), args[1]);

        if (member == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, wrapper);
            return;
        }

        if (Verifier.equalSnowflake(member.getUser(), wrapper.getAuthor())) {
            MessageSender.sendSimpleError(ErrorType.SELF_ASSIGNMENT, wrapper);
            return;
        }

        OptionalInt points = ArgumentParser.parseInt(args[2]);

        if (points.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, wrapper);
            return;
        }

        if (points.getAsInt() <= 0) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, wrapper);
            return;
        }


        if (!kudoData.tryTakeCompletePoints(
                wrapper.getGuild().get(), wrapper.getAuthor(), points.getAsInt(), wrapper)) {
            MessageSender.sendSimpleError(ErrorType.NOT_ENOUGH_KUDOS, wrapper);
            return;
        }
        if (!kudoData.addRubberPoints(
                wrapper.getGuild().get(), member.getUser(), points.getAsInt(), wrapper)) {
            return;
        }
        MessageSender.sendMessage(localizeAllAndReplace(M_RECEIVED_KUDOS.tag, wrapper,
                "**" + member.getEffectiveName() + "**", "**" + points.getAsInt()
                        + "**", "**" + wrapper.getMember().get().getEffectiveName() + "**"),
                wrapper.getMessageChannel());
    }

    private void sendTopScores(boolean global, EventWrapper wrapper) {
        List<Rank> ranks = global
                ? kudoData.getGlobalTopScore(25, wrapper, shardManager)
                : kudoData.getTopScore(wrapper.getGuild().get(), 25, wrapper, shardManager);

        String rankTable = TextFormatting.getRankTable(ranks, wrapper);

        String rankingPage;
        String title = global ? M_GLOBAL_RANKING.tag : M_SERVER_RANKING.tag;
        if (global) {
            rankingPage = "https://www.shepardbot.de/kudos";
        } else {
            rankingPage = "https://www.shepardbot.de/kudos?guildId=" + wrapper.getGuild().get().getId();
        }

        LocalizedEmbedBuilder embedBuilder = new LocalizedEmbedBuilder(wrapper)
                .setTitle("**" + title + "**", rankingPage)
                .addField("", rankTable, false)
                .addField("", "[" + title + "](" + rankingPage + ")", false);

        wrapper.getMessageChannel().sendMessage(embedBuilder.build()).queue();
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }

    @Override
    public void addConfig(Config config) {
    }

    @Override
    public void addShardManager(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    @Override
    public void addDataSource(DataSource source) {
        this.source = source;
    }

    @Override
    public void init() {
        kudoData = new KudoData(source);
    }
}
