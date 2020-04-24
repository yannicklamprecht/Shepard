package de.eldoria.shepard.commandmodules.guessgame.listener;

import de.eldoria.shepard.commandmodules.guessgame.util.GuessGameEvaluator;
import de.eldoria.shepard.minigameutil.ChannelEvaluator;
import de.eldoria.shepard.util.UniqueMessageIdentifier;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.util.reactions.ShepardEmote;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

import static de.eldoria.shepard.util.Verifier.equalSnowflake;

public class GuessGameListener extends ListenerAdapter {
    private final JDA jda;
    private final ChannelEvaluator<GuessGameEvaluator> evaluator;

    /**
     * Create a new guess game listener.
     *
     * @param jda       jda instance
     * @param evaluator evaluator for scheduler registration
     */
    public GuessGameListener(JDA jda, ChannelEvaluator<GuessGameEvaluator> evaluator) {
        this.jda = jda;
        this.evaluator = evaluator;
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (Verifier.equalSnowflake(event.getUser(), event.getJDA().getSelfUser())) return;
        UniqueMessageIdentifier identifier = UniqueMessageIdentifier.get(event);
        if (evaluator.isReactionMessage(identifier)) {
            GuessGameEvaluator channelEvaluator = evaluator.getChannelEvaluator(event.getChannel());

            if (channelEvaluator == null || event.getReaction().getReactionEmote().isEmoji()) {
                return;
            }

            Emote emote = event.getReaction().getReactionEmote().getEmote();

            if (equalSnowflake(emote, ShepardEmote.ANIM_CHECKMARK.getEmote(jda))) {
                channelEvaluator.addVote(event.getUser(), true);
            } else if (equalSnowflake(emote, ShepardEmote.ANIM_CROSS.getEmote(jda))) {
                channelEvaluator.addVote(event.getUser(), false);
            }

            event.getReaction().removeReaction(event.getUser()).queue();
        }
    }
}
