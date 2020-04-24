package de.eldoria.shepard.webapi.botlistreporter;

import de.eldoria.shepard.commandmodules.kudos.data.KudoData;
import de.eldoria.shepard.webapi.apiobjects.botlists.votes.VoteWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import javax.sql.DataSource;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import static java.lang.System.lineSeparator;

@Slf4j
public class VoteHandler implements Consumer<VoteWrapper> {


    private final String[] messages = {
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
    private final JDA jda;
    private final KudoData kudoData;

    /**
     * Create a new Vote handler.
     *
     * @param jda jda for user lookup
     * @param source source for database connection
     */
    public VoteHandler(JDA jda, DataSource source) {
        this.jda = jda;
        kudoData = new KudoData(source);
    }

    @Override
    public void accept(VoteWrapper vote) {
        log.debug("Processing vote for user {}", vote.getId());
        User userById = jda.getUserById(vote.getId());
        if (userById == null) {
            log.debug("No user found for vote");
            return;
        }

        int pointsToAdd = ThreadLocalRandom.current().nextInt(15, 30);
        pointsToAdd = pointsToAdd * (vote.isWeekend() ? 2 : 1);

        kudoData.addFreeRubberPoints(userById, pointsToAdd, null);
        kudoData.addRubberPoints(userById, pointsToAdd, null);
        int finalPointsToAdd = pointsToAdd;
        userById.openPrivateChannel()
                .queue(c -> c.sendMessage(messages[ThreadLocalRandom.current().nextInt(messages.length)]
                        .replace("%0%", finalPointsToAdd + "")).queue());
        log.debug("Vote processed");
    }
}
