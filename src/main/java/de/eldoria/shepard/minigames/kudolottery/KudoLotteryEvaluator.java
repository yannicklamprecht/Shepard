package de.eldoria.shepard.minigames.kudolottery;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.queries.KudoData;
import de.eldoria.shepard.localization.enums.commands.fun.KudoLotteryLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.minigames.Evaluator;
import de.eldoria.shepard.minigames.BaseEvaluator;
import de.eldoria.shepard.util.reactions.ShepardEmote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static de.eldoria.shepard.localization.enums.minigames.KudoLotteryEvaluatorLocale.M_CONGRATULATION;
import static de.eldoria.shepard.localization.enums.minigames.KudoLotteryEvaluatorLocale.M_NO_WINNER;
import static de.eldoria.shepard.localization.util.TextLocalizer.fastLocaleAndReplace;

public class KudoLotteryEvaluator extends BaseEvaluator {
    private final Map<Long, Integer> bet = new HashMap<>();

    /**
     * Creates a new Kudo lottery evaluator.
     *
     * @param message message for evaluation
     * @param user    user for first bet.
     */
    public KudoLotteryEvaluator(Message message, User user) {
        super(message.getIdLong(), message.getChannel().getIdLong());
        bet.put(user.getIdLong(), 1);
    }

    @Override
    public void run() {
        TextChannel guildChannel = ShepardBot.getJDA().getTextChannelById(channelId);
        if (guildChannel == null) {
            return;
        }


        List<Long> pool = new ArrayList<>();

        bet.forEach((key, value) -> {
            for (int i = 0; i < value; i++) {
                pool.add(key);
            }
        });


        Random random = new Random();
        int i = random.nextInt(pool.size());

        long userId = pool.get(i);
        User userById = ShepardBot.getJDA().getUserById(userId);

        if (userById == null) {
            return;
        }


        int sum = bet.values().stream().mapToInt(Integer::intValue).sum();

        if (bet.size() == 1) {
            MessageSender.sendMessage(M_NO_WINNER.tag, guildChannel);
            KudoData.addFreeRubberPoints(guildChannel.getGuild(), userById, sum, null);
            Evaluator.getKudoLotteryScheduler().evaluationDone(guildChannel);
            return;
        }

        int winnerPoints = bet.entrySet().stream().filter(set -> set.getKey().equals(userId))
                .map(Map.Entry::getValue)
                .mapToInt(Integer::intValue).sum();

        KudoData.addRubberPoints(guildChannel.getGuild(), userById, sum - winnerPoints, null);

        KudoData.addFreeRubberPoints(guildChannel.getGuild(), userById, winnerPoints, null);

        MessageSender.sendMessage(fastLocaleAndReplace(M_CONGRATULATION.tag, guildChannel.getGuild(),
                "**" + userById.getAsMention() + "**", "**" + sum + "**"), guildChannel);

        Evaluator.getKudoLotteryScheduler().evaluationDone(guildChannel);
    }

    public void addBet(Guild guild, User user, int amount) {
        TextChannel textChannel = ShepardBot.getJDA().getTextChannelById(channelId);
        if (textChannel == null) {
            return;
        }

        if (amount != -1 && !KudoData.tryTakePoints(guild, user, amount, null)) {
            return;
        }
        int finalAmount = amount;
        if (amount == -1) {
            finalAmount = 0;
            while (KudoData.tryTakePoints(guild, user, 50, null)) {
                finalAmount += 50;
            }
            while (KudoData.tryTakePoints(guild, user, 20, null)) {
                finalAmount += 20;
            }
            while (KudoData.tryTakePoints(guild, user, 10, null)) {
                finalAmount += 10;
            }
            while (KudoData.tryTakePoints(guild, user, 5, null)) {
                finalAmount += 5;
            }
            while (KudoData.tryTakePoints(guild, user, 1, null)) {
                finalAmount += 1;
            }
        }

        if (bet.containsKey(user.getIdLong())) {
            bet.put(user.getIdLong(), bet.get(user.getIdLong()) + finalAmount);
        } else {
            bet.put(user.getIdLong(), finalAmount);
        }

        int sum = bet.values().stream().mapToInt(Integer::intValue).sum();

        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(textChannel.getGuild())
                .setTitle(KudoLotteryLocale.M_EMBED_TITLE.tag)
                .setDescription(fastLocaleAndReplace(KudoLotteryLocale.M_EMBED_DESCRIPTION.tag,
                        textChannel.getGuild(), "3"))
                .addField(fastLocaleAndReplace(KudoLotteryLocale.M_EMBED_KUDOS_IN_POT.tag,
                        textChannel.getGuild(), sum + ""),
                        fastLocaleAndReplace(KudoLotteryLocale.M_EMBED_EXPLANATION.tag,
                                textChannel.getGuild(),
                                ShepardEmote.INFINITY.getEmote().getAsMention(),
                                ShepardEmote.PLUS_X.getEmote().getAsMention(),
                                ShepardEmote.PLUS_I.getEmote().getAsMention()),
                        true)
                .setColor(Color.orange);

        textChannel.retrieveMessageById(messageId)
                .queue(a -> a.editMessage(builder.build()).queue());
    }
}
