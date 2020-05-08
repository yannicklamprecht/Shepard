package de.eldoria.shepard.commandmodules.kudos.util;

import de.eldoria.shepard.commandmodules.kudos.data.KudoData;
import de.eldoria.shepard.localization.enums.commands.fun.KudoLotteryLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.minigameutil.BaseEvaluator;
import de.eldoria.shepard.minigameutil.ChannelEvaluator;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.util.reactions.ShepardEmote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static de.eldoria.shepard.localization.enums.minigames.KudoLotteryEvaluatorLocale.M_CONGRATULATION;
import static de.eldoria.shepard.localization.enums.minigames.KudoLotteryEvaluatorLocale.M_NO_WINNER;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

/**
 * Creates a new Kudo lottery evaluator.
 * The evaluator has a {@link KudoLotteryEvaluator#maxBet} which defines the max amount a single user can bet.
 * via the {@link KudoLotteryEvaluator#addBet(Guild, User, int)} a user can add a bet.
 * At least every user can set a amount of 1.
 */
public class KudoLotteryEvaluator extends BaseEvaluator {
    private final Map<Long, Integer> bet = new HashMap<>();

    private final int maxBet;
    private final ChannelEvaluator<KudoLotteryEvaluator> evaluator;
    private final ShardManager shardManager;
    private final KudoData kudoData;

    /**
     * Creates a new Kudo lottery evaluator.
     *  @param kudoData  data object
     * @param evaluator evaluator for lottery
     * @param shardManager       shardManager instance
     * @param message   message for evaluation
     * @param user      user for first bet.
     * @param maxBet    the max amount a single user can bet
     */
    public KudoLotteryEvaluator(KudoData kudoData, ChannelEvaluator<KudoLotteryEvaluator> evaluator, ShardManager shardManager,
                                Message message, User user, int maxBet) {
        super(message.getIdLong(), message.getChannel().getIdLong());
        this.kudoData = kudoData;
        this.evaluator = evaluator;
        this.shardManager = shardManager;
        bet.put(user.getIdLong(), 1);
        this.maxBet = maxBet;
    }

    @Override
    public void run() {
        TextChannel guildChannel = shardManager.getTextChannelById(channelId);
        if (guildChannel == null) {
            return;
        }

        List<Long> pool = new ArrayList<>();

        bet.forEach((key, value) -> {
            if (value == -1) {
                pool.add(key);
            }
            for (int i = 0; i < value; i++) {
                pool.add(key);
            }
        });


        Random random = new Random();
        int i = random.nextInt(pool.size());

        long userId = pool.get(i);
        User userById = shardManager.getUserById(userId);

        if (userById == null) {
            return;
        }


        int sum = bet.values().stream().mapToInt(Integer::intValue).map(num -> num == -1 ? 1 : num).sum();
        int realSum = bet.values().stream().mapToInt(Integer::intValue).sum();

        if (bet.size() == 1) {
            if (realSum == -1) {
                return;
            }
            MessageSender.sendMessage(M_NO_WINNER.tag, guildChannel);
            kudoData.addRubberPoints(guildChannel.getGuild(), userById, sum, null);
            evaluator.evaluationDone(guildChannel);
            return;
        }

        if (bet.get(userId) == -1) {
            sum -= 1;
        }

        kudoData.addRubberPoints(guildChannel.getGuild(), userById, sum, null);

        MessageSender.sendMessage(localizeAllAndReplace(M_CONGRATULATION.tag, guildChannel.getGuild(),
                "**" + userById.getAsMention() + "**", "**" + sum + "**"), guildChannel);

        evaluator.evaluationDone(guildChannel);
    }

    /**
     * Add the amount of kudos to the pot. Adds the given amount if the user has enough kudos or at least one per user.
     * After adding the amount, the {@link KudoLotteryEvaluator#refreshEmbed(TextChannel)} will be executed.
     *
     * @param guild  guild where the kudos should be taken
     * @param user   user where the kudos should be taken
     * @param amount amount of kudos. -1 to take all kudos.
     */
    public void addBet(Guild guild, User user, int amount) {
        if (Verifier.equalSnowflake(user, shardManager.getShardById(0).getSelfUser())) return;
        TextChannel textChannel = shardManager.getTextChannelById(channelId);
        if (textChannel == null) {
            return;
        }

        int tempAmount = amount;

        int currentAmount = bet.getOrDefault(user.getIdLong(), 0);

        if (currentAmount == maxBet) {
            return;
        }

        if (currentAmount + tempAmount > maxBet) {
            tempAmount = maxBet - currentAmount;
        }

        if (tempAmount != -1 && !kudoData.tryTakePoints(guild, user, tempAmount, null)) {
            if (kudoData.getUserScore(guild, user, null) != 0 && currentAmount != 0) {
                return;
            }
            bet.put(user.getIdLong(), -1);
            refreshEmbed(textChannel);
            return;
        }

        int finalAmount = tempAmount;
        if (finalAmount == -1) {
            finalAmount = 0;
            while (currentAmount + finalAmount + 50 <= maxBet && kudoData.tryTakePoints(guild, user, 50, null)) {
                finalAmount += 50;
            }
            while (currentAmount + finalAmount + 20 <= maxBet && kudoData.tryTakePoints(guild, user, 20, null)) {
                finalAmount += 20;
            }
            while (currentAmount + finalAmount + 10 <= maxBet && kudoData.tryTakePoints(guild, user, 10, null)) {
                finalAmount += 10;
            }
            while (currentAmount + finalAmount + 5 <= maxBet && kudoData.tryTakePoints(guild, user, 5, null)) {
                finalAmount += 5;
            }
            while (currentAmount + finalAmount + 1 <= maxBet && kudoData.tryTakePoints(guild, user, 1, null)) {
                finalAmount += 1;
            }
        }

        if (finalAmount == 0) {
            return;
        }

        if (bet.containsKey(user.getIdLong())) {
            bet.put(user.getIdLong(), bet.get(user.getIdLong()) + finalAmount);
        } else {
            bet.put(user.getIdLong(), finalAmount);
        }

        refreshEmbed(textChannel);
    }

    private void refreshEmbed(TextChannel textChannel) {
        int sum = bet.values().stream().mapToInt(Integer::intValue).map(num -> num == -1 ? 1 : num).sum();

        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(textChannel.getGuild())
                .setTitle(KudoLotteryLocale.M_EMBED_TITLE.tag)
                .setDescription(localizeAllAndReplace(KudoLotteryLocale.M_EMBED_DESCRIPTION.tag,
                        textChannel.getGuild(), "3"))
                .addField(localizeAllAndReplace(KudoLotteryLocale.M_EMBED_KUDOS_IN_POT.tag,
                        textChannel.getGuild(), "**" + sum + "**", "**" + maxBet + "**"),
                        localizeAllAndReplace(KudoLotteryLocale.M_EMBED_EXPLANATION.tag,
                                textChannel.getGuild(),
                                ShepardEmote.INFINITY.getEmote(shardManager).getAsMention(),
                                ShepardEmote.PLUS_X.getEmote(shardManager).getAsMention(),
                                ShepardEmote.PLUS_I.getEmote(shardManager).getAsMention()),
                        true)
                .setColor(Color.orange);

        textChannel.retrieveMessageById(messageId)
                .queue(a -> a.editMessage(builder.build()).queue());

    }
}
