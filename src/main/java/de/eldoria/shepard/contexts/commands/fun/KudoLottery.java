package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.database.queries.KudoData;
import de.eldoria.shepard.localization.enums.commands.fun.KudoLotteryLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.minigames.ChannelEvaluator;
import de.eldoria.shepard.minigames.Evaluator;
import de.eldoria.shepard.minigames.kudolottery.KudoLotteryEvaluator;
import de.eldoria.shepard.util.reactions.ShepardEmote;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.awt.Color;

import static de.eldoria.shepard.localization.enums.commands.fun.KudoLotteryLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.fun.KudoLotteryLocale.M_LOTTERY_RUNNING;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

public class KudoLottery extends Command {
    /**
     * Creates a new kudo lottery command object.
     */
    public KudoLottery() {
        commandName = "kudoLottery";
        commandAliases = new String[] {"lottery", "kl"};
        commandArgs = new CommandArg[] {
                new CommandArg("maximumBet", false,
                        new SubArg("maximumBet", KudoLotteryLocale.C_MAX_BET.tag))
        };
        commandDesc = DESCRIPTION.tag;
        category = ContextCategory.FUN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        boolean success = KudoData.tryTakePoints(messageContext.getGuild(),
                messageContext.getAuthor(), 1, messageContext);

        int maxBet = 200;

        if (args.length > 0) {
            Integer amount = ArgumentParser.parseInt(args[0]);
            if (amount == null) {
                MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, messageContext.getTextChannel());
                return;
            }
            maxBet = Math.min(Math.max(amount, 1), 200);
        }

        if (!success) {
            MessageSender.sendSimpleError(ErrorType.NOT_ENOUGH_KUDOS, messageContext.getTextChannel());
            return;
        }

        ChannelEvaluator<KudoLotteryEvaluator> kudoLotteryScheduler
                = Evaluator.getKudoLotteryScheduler();

        if (kudoLotteryScheduler.isEvaluationActive(messageContext.getTextChannel())) {
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
                                ShepardEmote.INFINITY.getEmote().getAsMention(),
                                ShepardEmote.PLUS_X.getEmote().getAsMention(),
                                ShepardEmote.PLUS_I.getEmote().getAsMention()),
                        true)
                .setColor(Color.orange);

        int finalMaxBet = maxBet;
        messageContext.getChannel().sendMessage(builder.build()).queue(message -> {
            message.addReaction(ShepardEmote.INFINITY.getEmote()).queue();
            message.addReaction(ShepardEmote.PLUS_X.getEmote()).queue();
            message.addReaction(ShepardEmote.PLUS_I.getEmote()).queue();
            kudoLotteryScheduler.scheduleEvaluation(message, ShepardBot.getConfig().isBeta() ? 30 : 180,
                    new KudoLotteryEvaluator(message, messageContext.getAuthor(), finalMaxBet));
        });
    }
}
