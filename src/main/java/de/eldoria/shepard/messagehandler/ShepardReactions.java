package de.eldoria.shepard.messagehandler;

public enum ShepardReactions {
    /**
     * Empty reaction.
     */
    NONE(""),
    /**
     * Asleep image.
     */
    ASLEEP("shep_asleep.png"),
    /**
     * Blush image.
     */
    BLUSH("shep_blush.png"),
    /**
     * Nekofied image.
     */
    CAT("shep_cat.png"),
    /**
     * Excited image.
     */
    EXCITED("shep_excited.png"),
    /**
     * Normal image.
     */
    NORMAL("shep_normal.png"),
    /**
     * Smirk image.
     */
    SMIRK("shep_smirk.png"),
    /**
     * Smug image.
     */
    SMUG("shep_smug.png"),
    /**
     * Shulky image.
     */
    SHULKY("shep_sulky.png"),
    /**
     * Wink image.
     */
    WINK("shep_wink.png"),
    /**
     * Confused image.
     */
    CONFUSED("shep_confused.png"),
    /**
     * Cry image.
     */
    CRY("shep_cry.png");

    /**
     * Returns image with 100x100px.
     */
    public final String emote;
    /**
     * Returns image with 250x250px.
     */
    public final String thumbnail;
    /**
     * Return image with 1000x1000px.
     */
    public final String full;

    /**
     * Creates a new shepard reaction.
     *
     * @param name name of the emote.
     */
    ShepardReactions(String name) {
        String root = "http://img.shepardbot.de/emotes/";
        this.emote = root + "emote/" + name;
        this.thumbnail = root + "thumbnail/" + name;
        this.full = root + "full/" + name;
    }
}
