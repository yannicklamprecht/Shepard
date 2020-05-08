package de.eldoria.shepard.util.reactions;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;

public enum ShepardEmote {
    /**
     * +1 emote.
     */
    PLUS_1(635490403709222949L),
    /**
     * +2 emote.
     */
    PLUS_2(635490403055042590L),
    /**
     * +3 emote.
     */
    PLUS_3(635490402828419104L),
    /**
     * +4 emote.
     */
    PLUS_4(635490403084402739L),
    /**
     * +5 emote.
     */
    PLUS_5(635490403134603265L),
    /**
     * +6 emote.
     */
    PLUS_6(635490403142991874L),
    /**
     * +7 emote.
     */
    PLUS_7(635490403058974722L),
    /**
     * +8 emote.
     */
    PLUS_8(635490402677293109L),
    /**
     * +9 emote.
     */
    PLUS_9(635490403197386754L),
    /**
     * +I emote.
     */
    PLUS_I(635490402887008257L),
    /**
     * +X emote.
     */
    PLUS_X(635490402908110881L),
    /**
     * infinity sign emote.
     */
    INFINITY(635490402966700052L),
    /**
     * animated green checkmark emote.
     */
    ANIM_CHECKMARK(635493968691593277L),
    /**
     * animated red cross emote.
     */
    ANIM_CROSS(635493968460644412L);

    private final long id;

    /**
     * Creates a new emote.
     *
     * @param id id of the emote.
     */
    ShepardEmote(long id) {
        this.id = id;
    }

    /**
     * Get the emote object.
     *
     * @param shardManager JDA instance for emoji loading
     * @return emote object or null if emote was not found
     */
    public Emote getEmote(ShardManager shardManager) {
        Guild guildById = shardManager.getGuildById(635460587341479951L);

        return guildById == null ? null : guildById.getEmoteById(this.id);
    }
}
