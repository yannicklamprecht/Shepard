package de.eldoria.shepard;

import org.discordbots.api.client.DiscordBotListAPI;
import net.dv8tion.jda.api.entities.User;

import java.util.concurrent.atomic.AtomicBoolean;

public class BotListReporter {
    private DiscordBotListAPI api;

    BotListReporter() {
        api = new DiscordBotListAPI.Builder()
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjUxMjQxMzA0OTg5NDczMTc4MCIsImJvdCI6dHJ1ZSwiaWF0IjoxNTcxNjk2NDYwfQ.ERtbOsNSZfmytzNNKzHo7y79eGC8DTYMjzN00QTUFN8")
                .botId("512413049894731780")
                .build();
    }

    public void refreshInformation() {
        api.setStats(ShepardBot.getJDA().getGuilds().size());
    }

    public boolean hasVoted(User user) {
        AtomicBoolean voted = new AtomicBoolean(false);

        api.hasVoted(user.getId()).whenComplete((bool, e) -> {
            if (e != null) {
                voted.set(false);
                ShepardBot.getLogger().error(e);
                return;
            }
            voted.set(bool);
        });

        return voted.get();
    }
}
