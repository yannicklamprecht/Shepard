package de.eldoria.shepard.commandmodules.kudos.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.kudos.data.KudoData;
import de.eldoria.shepard.commandmodules.kudos.listener.KudoLotteryListener;
import de.eldoria.shepard.commandmodules.kudos.util.KudoLotteryEvaluator;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.fun.KudoLotteryLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.minigameutil.ChannelEvaluator;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqJDA;
import de.eldoria.shepard.util.reactions.ShepardEmote;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.JDA;

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
public class KudoLottery extends Command implements Executable, ReqJDA, ReqDataSource, ReqInit {
    private JDA jda;
    private ChannelEvaluator<KudoLotteryEvaluator> evaluator;
    private KudoData kudoData;

    /**
     * Creates a new kudo lottery command object.
     */
    public KudoLottery() {
        super("kudoLottery",
                new String[] {"lottery", "kl"},
                DESCRIPTION.tag,
                SubCommand.builder("kudoLottery")
                        .addSubcommand(C_MAX_BET.tag,
                                Parameter.createInput(A_AMOUNT.tag, AD_AMOUNT.tag, false))
                        .build(),
                KudoLotteryLocale.C_DEFAULT.tag,
                CommandCategory.FUN);
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        boolean success = kudoData.tryTakePoints(messageContext.getGuild(),
                messageContext.getAuthor(), 1, messageContext);

        int maxBet = 100;

        if (args.length > 0) {
            OptionalInt amount = ArgumentParser.parseInt(args[0]);
            if (amount.isEmpty()) {
                MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, messageContext.getTextChannel());
                return;
            }
            maxBet = Math.min(Math.max(amount.getAsInt(), 1), 500);
        }

        if (!success) {
            MessageSender.sendSimpleError(ErrorType.NOT_ENOUGH_KUDOS, messageContext.getTextChannel());
            return;
        }


        if (evaluator.isEvaluationActive(messageContext.getTextChannel())) {
            MessageSender.sendMessage(M_LOTTERY_RUNNING.tag, messageContext.getTextChannel());
            return;
        }

        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(messageContext)
                .setTitle(KudoLotteryLocale.M_EMBED_TITLE.tag)
                .setDescription(localizeAllAndReplace(KudoLotteryLocale.M_EMBED_DESCRIPTION.tag,
                        messageContext.getGuild(), "3"))
                .addField(localizeAllAndReplace(KudoLotteryLocale.M_EMBED_KUDOS_IN_POT.tag,
                        messageContext.getGuild(), "**1**", "**" + maxBet + "**"),
                        localizeAllAndReplace(KudoLotteryLocale.M_EMBED_EXPLANATION.tag,
                                messageContext.getGuild(),
                                ShepardEmote.INFINITY.getEmote(jda).getAsMention(),
                                ShepardEmote.PLUS_X.getEmote(jda).getAsMention(),
                                ShepardEmote.PLUS_I.getEmote(jda).getAsMention()),
                        true)
                .setColor(Color.orange);

        int finalMaxBet = maxBet;
        messageContext.getChannel().sendMessage(builder.build()).queue(message -> {
            message.addReaction(ShepardEmote.INFINITY.getEmote(jda)).queue();
            message.addReaction(ShepardEmote.PLUS_X.getEmote(jda)).queue();
            message.addReaction(ShepardEmote.PLUS_I.getEmote(jda)).queue();
            evaluator.scheduleEvaluation(message, 180,
                    new KudoLotteryEvaluator(kudoData, evaluator, jda, message,
                            messageContext.getAuthor(), finalMaxBet));
        });
    }

    @Override
    public void addJDA(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void init() {
        evaluator = new ChannelEvaluator<>(5);
        KudoLotteryListener listener = new KudoLotteryListener(jda, evaluator);
        jda.addEventListener(listener);
    }

    @Override
    public void addDataSource(DataSource source) {
        kudoData = new KudoData(source);
    }
}
