package de.eldoria.shepard.commandmodules.kudos.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.command.GuildChannelOnly;
import de.eldoria.shepard.commandmodules.kudos.data.KudoData;
import de.eldoria.shepard.commandmodules.kudos.util.KudoLotteryEvaluator;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.fun.KudoLotteryLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.minigameutil.ChannelEvaluator;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqShardManager;
import de.eldoria.shepard.util.reactions.ShepardEmote;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.sql.DataSource;
import java.awt.Color;
import java.util.OptionalInt;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_AMOUNT;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_AMOUNT;
import static de.eldoria.shepard.localization.enums.commands.fun.KudoLotteryLocale.C_MAX_BET;
import static de.eldoria.shepard.localization.enums.commands.fun.KudoLotteryLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.fun.KudoLotteryLocale.M_LOTTERY_RUNNING;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

/**
 * Command to start a new KudoLottery.
 * A started lottery will be handled by {@link KudoLotteryEvaluator}
 */
public class KudoLottery extends Command implements Executable, GuildChannelOnly, ReqShardManager, ReqDataSource, ReqInit {
    private ShardManager jda;
    private ChannelEvaluator<KudoLotteryEvaluator> evaluator;
    private KudoData kudoData;

    /**
     * Creates a new kudo lottery command object.
     */
    public KudoLottery(ChannelEvaluator<KudoLotteryEvaluator> evaluator) {
        super("kudoLottery",
                new String[] {"lottery", "kl"},
                DESCRIPTION.tag,
                SubCommand.builder("kudoLottery")
                        .addSubcommand(C_MAX_BET.tag,
                                Parameter.createInput(A_AMOUNT.tag, AD_AMOUNT.tag, false))
                        .build(),
                KudoLotteryLocale.C_DEFAULT.tag,
                CommandCategory.FUN);
        this.evaluator = evaluator;
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        boolean success = kudoData.tryTakePoints(wrapper.getGuild().get(),
                wrapper.getAuthor(), 1, wrapper);

        int maxBet = 100;

        if (args.length > 0) {
            OptionalInt amount = ArgumentParser.parseInt(args[0]);
            if (amount.isEmpty()) {
                MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, wrapper);
                return;
            }
            maxBet = Math.min(Math.max(amount.getAsInt(), 1), 500);
        }

        if (!success) {
            MessageSender.sendSimpleError(ErrorType.NOT_ENOUGH_KUDOS, wrapper);
            return;
        }


        if (evaluator.isEvaluationActive(wrapper.getTextChannel().get())) {
            MessageSender.sendMessage(M_LOTTERY_RUNNING.tag, wrapper.getMessageChannel());
            return;
        }

        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(wrapper)
                .setTitle(KudoLotteryLocale.M_EMBED_TITLE.tag)
                .setDescription(localizeAllAndReplace(KudoLotteryLocale.M_EMBED_DESCRIPTION.tag, wrapper, "3"))
                .addField(localizeAllAndReplace(KudoLotteryLocale.M_EMBED_KUDOS_IN_POT.tag, wrapper,
                        "**1**", "**" + maxBet + "**"),
                        localizeAllAndReplace(KudoLotteryLocale.M_EMBED_EXPLANATION.tag, wrapper,
                                ShepardEmote.INFINITY.getEmote(jda).getAsMention(),
                                ShepardEmote.PLUS_X.getEmote(jda).getAsMention(),
                                ShepardEmote.PLUS_I.getEmote(jda).getAsMention()),
                        true)
                .setColor(Color.orange);

        int finalMaxBet = maxBet;
        wrapper.getMessageChannel().sendMessage(builder.build()).queue(message -> {
            message.addReaction(ShepardEmote.INFINITY.getEmote(jda)).queue();
            message.addReaction(ShepardEmote.PLUS_X.getEmote(jda)).queue();
            message.addReaction(ShepardEmote.PLUS_I.getEmote(jda)).queue();
            evaluator.scheduleEvaluation(message, 180,
                    new KudoLotteryEvaluator(kudoData, evaluator, jda, message,
                            wrapper.getAuthor(), finalMaxBet));
        });
    }

    @Override
    public void addShardManager(ShardManager shardManager) {
        this.jda = shardManager;
    }

    @Override
    public void init() {
    }

    @Override
    public void addDataSource(DataSource source) {
        kudoData = new KudoData(source);
    }
}
