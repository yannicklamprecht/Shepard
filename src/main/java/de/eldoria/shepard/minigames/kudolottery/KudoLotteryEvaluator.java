package de.eldoria.shepard.minigames.kudolottery;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.queries.KudoData;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.minigames.Evaluator;
import de.eldoria.shepard.minigames.EvaluatorImpl;
import de.eldoria.shepard.util.reactions.EmoteCollection;
import net.dv8tion.jda.api.EmbedBuilder;
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

import static java.lang.System.lineSeparator;

public class KudoLotteryEvaluator extends EvaluatorImpl {
    private final Map<Long, Integer> bet = new HashMap<>();

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
            MessageSender.sendMessage("There was only one attendee. The Kudos will be returned!", guildChannel);
            KudoData.addFreeRubberPoints(guildChannel.getGuild(), userById, sum, null);
            Evaluator.getKudoLotteryScheduler().evaluationDone(guildChannel);
            return;
        }

        int winnerPoints = bet.entrySet().stream().filter(set -> set.getKey().equals(userId))
                .map(Map.Entry::getValue)
                .mapToInt(Integer::intValue).sum();

        KudoData.addRubberPoints(guildChannel.getGuild(), userById, sum - winnerPoints, null);

        KudoData.addFreeRubberPoints(guildChannel.getGuild(), userById, winnerPoints, null);

        MessageSender.sendMessage("**Congratulation to " + userById.getAsMention() + "!**" + System.lineSeparator()
                + "You win " + sum + " Kudos!", guildChannel);

        Evaluator.getKudoLotteryScheduler().evaluationDone(guildChannel);
    }

    public void addBet(Guild guild, User user, int amount) {

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

        if(bet.containsKey(user.getIdLong())){
            bet.put(user.getIdLong(), bet.get(user.getIdLong()) + finalAmount);
        }else{
            bet.put(user.getIdLong(), finalAmount);
        }

        int sum = bet.values().stream().mapToInt(Integer::intValue).sum();

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("KUDO LOTTERY")
                .setDescription("A new round is starting. Please place your bets!" + lineSeparator()
                        + " You have 3 minutes!")
                .addField("Currently there are " + sum + " Kudos in the pot!",
                        "Press " + EmoteCollection.INFINITY.getEmote().getAsMention()
                                + " to buy as much Tickets as you can." + lineSeparator()
                                + "Press " + EmoteCollection.PLUS_X.getEmote().getAsMention()
                                + " to buy 10 Tickets for 10 Kudos." + lineSeparator()
                                + "Press " + EmoteCollection.PLUS_I.getEmote().getAsMention()
                                + " to buy 1 Ticket for 1 Kudo.", true)
                .setColor(Color.orange);

        ShepardBot.getJDA().getTextChannelById(channelId).retrieveMessageById(messageId)
                .queue(a -> {
                    a.editMessage(builder.build()).queue();
                });
    }
}
