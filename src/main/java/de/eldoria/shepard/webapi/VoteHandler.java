package de.eldoria.shepard.webapi;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.queries.KudoData;
import de.eldoria.shepard.webapi.apiobjects.VoteInformation;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static java.lang.System.lineSeparator;

public class VoteHandler implements Consumer<VoteInformation> {
    private String[] messages = new String[] {
            "Thank you, that you believe in me!" + lineSeparator()
                    + "Nothing makes me happier than your vote!" + lineSeparator()
                    + "Pls accept %0% Kudos a gift for you :3",
            "Thanks for your vote! Because you are my favourite user, I give you some extra %0% Kudos."
                    + lineSeparator()
                    + "But pls dont tell my developers!",
            "Thank you very much for your vote." + lineSeparator()
                    + "I become always happier when you vote for me :3 I send you %0% Kudos. It's out little secret!",
            "I had a rough day, but your vote cheered me up again. " + lineSeparator()
                    + "Thank you so much <3. Pls accept %0% Kudos as a gift.",
            "Thank you for the vote." + lineSeparator()
                    + "I have only %0% Kudos left to give you, but i hope you are as happy as I am."

    };

    @Override
    public void accept(VoteInformation voteInformation) {
        ShepardBot.getLogger().info("Processing vote for user " + voteInformation.getUser());
        User userById = ShepardBot.getJDA().getUserById(voteInformation.getUser());
        if (userById == null) {
            ShepardBot.getLogger().info("No user found for vote");
            return;
        }
        int pointsToAdd = Math.min(15, Math.round((float) Math.random() * 25));
        pointsToAdd = pointsToAdd * (voteInformation.isWeekend() ? 2 : 1);


        KudoData.addFreeRubberPoints(userById, pointsToAdd, null);
        KudoData.addRubberPoints(userById, pointsToAdd, null);
        int finalPointsToAdd = pointsToAdd;
        userById.openPrivateChannel()
                .queue(c -> {
                    c.sendMessage(messages[Math.round((float) Math.random() * messages.length - 1)]
                            .replace("%0%", finalPointsToAdd + "")).queue();
                });
        ShepardBot.getLogger().info("Vote processed");
    }

    @NotNull
    @Override
    public Consumer<VoteInformation> andThen(@NotNull Consumer<? super VoteInformation> after) {
        return null;
    }
}
