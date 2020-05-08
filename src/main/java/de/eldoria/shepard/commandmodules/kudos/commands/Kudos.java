package de.eldoria.shepard.commandmodules.kudos.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
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
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
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
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 0) {
            int freePoints = kudoData.getFreePoints(
                    messageContext.getGuild(), messageContext.getAuthor(), messageContext);
            int userPoints = kudoData.getUserScore(
                    messageContext.getGuild(), messageContext.getAuthor(), messageContext);
            int globalUserPoints = kudoData.getGlobalUserScore(messageContext.getAuthor(), messageContext);

            String message = localizeAllAndReplace(M_DESCRIPTION_GENERAL.tag, messageContext.getGuild(),
                    "**" + freePoints + "**", "**100**", "1", "**" + userPoints + "**");
            message = userPoints != globalUserPoints
                    ? message + lineSeparator() + localizeAllAndReplace(M_DESCRIPTION_EXTENDED.tag,
                    messageContext.getGuild(), "**" + globalUserPoints + "**")
                    : message;

            MessageSender.sendMessage(message, messageContext.getTextChannel());
            return;
        }

        String cmd = args[0];

        if (isSubCommand(cmd, 0)) {
            give(args, messageContext);
            return;
        }

        if (isSubCommand(cmd, 1)) {
            sendTopScores(false, messageContext);
            return;
        }

        if (isSubCommand(cmd, 2)) {
            sendTopScores(true, messageContext);
            return;
        }
        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());
    }

    private void give(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }

        Member member = parser.getGuildMember(messageContext.getGuild(), args[1]);

        if (member == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, messageContext.getTextChannel());
            return;
        }

        if (Verifier.equalSnowflake(member.getUser(), messageContext.getAuthor())) {
            MessageSender.sendSimpleError(ErrorType.SELF_ASSIGNMENT, messageContext.getTextChannel());
            return;
        }

        OptionalInt points = ArgumentParser.parseInt(args[2]);

        if (points.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, messageContext.getTextChannel());
            return;
        }

        if (points.getAsInt() <= 0) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }


        if (!kudoData.tryTakeCompletePoints(
                messageContext.getGuild(), messageContext.getAuthor(), points.getAsInt(), messageContext)) {
            MessageSender.sendSimpleError(ErrorType.NOT_ENOUGH_KUDOS, messageContext.getTextChannel());
            return;
        }
        if (!kudoData.addRubberPoints(
                messageContext.getGuild(), member.getUser(), points.getAsInt(), messageContext)) {
            return;
        }
        MessageSender.sendMessage(localizeAllAndReplace(M_RECEIVED_KUDOS.tag, messageContext.getGuild(),
                "**" + member.getEffectiveName() + "**", "**" + points.getAsInt()
                        + "**", "**" + messageContext.getMember().getEffectiveName() + "**"),
                messageContext.getTextChannel());
    }

    private void sendTopScores(boolean global, MessageEventDataWrapper messageContext) {
        List<Rank> ranks = global
                ? kudoData.getGlobalTopScore(25, messageContext, shardManager)
                : kudoData.getTopScore(messageContext.getGuild(), 25, messageContext, shardManager);

        String rankTable = TextFormatting.getRankTable(ranks, messageContext);

        String rankingPage;
        String title = global ? M_GLOBAL_RANKING.tag : M_SERVER_RANKING.tag;
        if (global) {
            rankingPage = "https://www.shepardbot.de/kudos";
        } else {
            rankingPage = "https://www.shepardbot.de/kudos?guildId=" + messageContext.getGuild().getId();
        }

        LocalizedEmbedBuilder embedBuilder = new LocalizedEmbedBuilder(messageContext.getGuild())
                .setTitle("**" + title + "**", rankingPage)
                .addField("", rankTable, false)
                .addField("", "[" + title + "](" + rankingPage + ")", false);

        messageContext.getTextChannel().sendMessage(embedBuilder.build()).queue();
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
