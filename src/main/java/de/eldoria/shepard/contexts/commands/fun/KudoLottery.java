package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.database.queries.KudoData;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.minigames.EvaluationScheduler;
import de.eldoria.shepard.minigames.EvaluationSchedulerCollection;
import de.eldoria.shepard.minigames.kudolottery.KudoLotteryEvaluator;
import de.eldoria.shepard.util.reactions.EmoteCollection;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;

import static java.lang.System.lineSeparator;

public class KudoLottery extends Command {
    public KudoLottery() {
        commandName = "kudoLottery";
        commandAliases = new String[] {"lottery", "kl"};
        commandDesc = "Play for Kudos! You need at least 1 Kudo to start a round! 1 ticket = 1 Kudo";
        category = ContextCategory.FUN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        boolean success = KudoData.tryTakePoints(messageContext.getGuild(),
                messageContext.getAuthor(), 1, messageContext);

        if (!success) {
            MessageSender.sendSimpleError(ErrorType.NOT_ENOUGH_KUDOS, messageContext.getChannel());
            return;
        }

        EvaluationScheduler<KudoLotteryEvaluator> kudoLotteryScheduler
                = EvaluationSchedulerCollection.getKudoLotteryScheduler();

        if (kudoLotteryScheduler.isEvaluationActive(messageContext.getTextChannel())) {
            MessageSender.sendMessage("There is an active Lottery in this channel.", messageContext.getChannel());
            return;
        }

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("KUDO LOTTERY")
                .setDescription("A new round is starting. Please place your bets!" + lineSeparator()
                        + " You have 3 minutes!")
                .addField("Currently there is 1 Kudo in the pot!",
                        "Press " + EmoteCollection.INFINITY.getEmote().getAsMention()
                                + " to buy as much Tickets as you can." + lineSeparator()
                                + "Press " + EmoteCollection.PLUS_X.getEmote().getAsMention()
                                + " to buy 10 Tickets for 10 Kudos." + lineSeparator()
                                + "Press " + EmoteCollection.PLUS_I.getEmote().getAsMention()
                                + " to buy 1 Ticket for 1 Kudo.",
                        true)
                .setColor(Color.orange);

        messageContext.getChannel().sendMessage(builder.build()).queue(message -> {
            message.addReaction(EmoteCollection.INFINITY.getEmote()).queue();
            message.addReaction(EmoteCollection.PLUS_X.getEmote()).queue();
            message.addReaction(EmoteCollection.PLUS_I.getEmote()).queue();
            kudoLotteryScheduler.scheduleEvaluation(message, 180,
                    new KudoLotteryEvaluator(message, messageContext.getAuthor()));
        });
    }
}
