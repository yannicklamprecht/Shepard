package de.eldoria.shepard.minigames.kudolottery;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.queries.KudoData;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.minigames.Evaluator;
import de.eldoria.shepard.util.Emoji;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.*;
import java.util.List;

import static java.lang.System.lineSeparator;

public class KudoLotteryEvaluator extends Evaluator {
    private Map<Long, Integer> bet = new HashMap<>();

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

        Long userId = pool.get(i);
        User userById = ShepardBot.getJDA().getUserById(userId);

        if (userById == null) {
            return;
        }


        int sum = bet.values().stream().mapToInt(Integer::intValue).sum();

        if (bet.size() == 1) {
            MessageSender.sendMessage("There was only one attendee. The Kudos will be returned!", guildChannel);
            KudoData.addFreeRubberPoints(guildChannel.getGuild(), userById, sum, null);
            return;
        }

        KudoData.addRubberPoints(guildChannel.getGuild(), userById, sum, null);

        MessageSender.sendMessage("**Congratulation to " + userById.getAsMention() + "!**" + System.lineSeparator()
                + "You win " + sum + " Kudos!", guildChannel);
    }

    public void addBet(Guild guild, User user, int amount) {
        if (!KudoData.tryTakePoints(guild, user, amount, null)) {
            return;
        }
        Integer currentAmount = bet.putIfAbsent(user.getIdLong(), amount);
        if (currentAmount != null) {
            bet.put(user.getIdLong(), currentAmount + amount);
        }

        int sum = bet.values().stream().mapToInt(Integer::intValue).sum();

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("KUDO LOTTERY")
                .setDescription("A new round is starting. Please place your bets!" + lineSeparator()
                        + " You have 1 minute!")
                .addField("Currently there are " + sum + " Kudos in the pot!",
                        "Press " + Emoji.MONEY_BAG.unicode + " to buy 10 Tickets for 10 Kudos." + lineSeparator()
                                + "Press " + Emoji.DOLLAR.unicode + "to buy 1 Ticket for 1 Kudo.", true)
                .setColor(Color.orange);

        ShepardBot.getJDA().getTextChannelById(channelId).retrieveMessageById(messageId)
                .queue(a -> {
                    a.editMessage(builder.build()).queue();
                });
    }
}
