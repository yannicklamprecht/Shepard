package de.eldoria.shepard.util.reactions;

import de.eldoria.shepard.ShepardBot;
import net.dv8tion.jda.api.entities.Emote;

public enum EmoteCollection {
    PLUS_1(635490403709222949L),
    PLUS_2(635490403055042590L),
    PLUS_3(635490402828419104L),
    PLUS_4(635490403084402739L),
    PLUS_5(635490403134603265L),
    PLUS_6(635490403142991874L),
    PLUS_7(635490403058974722L),
    PLUS_8(635490402677293109L),
    PLUS_9(635490403197386754L),
    PLUS_I(635490402887008257L),
    PLUS_X(635490402908110881L),
    INFINITY(635490402966700052L),
    ANIM_CHECKMARK(635493968691593277L),
    ANIM_CROSS(635493968460644412L);

    private final long id;

    public Emote getEmote() {
        return ShepardBot.getJDA().getGuildById(635460587341479951L).getEmoteById(this.id);
    }

    EmoteCollection(long id) {
        this.id = id;
    }
}
