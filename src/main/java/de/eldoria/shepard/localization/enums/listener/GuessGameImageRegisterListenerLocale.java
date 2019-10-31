package de.eldoria.shepard.localization.enums.listener;

public enum GuessGameImageRegisterListenerLocale {
    /**
     * Localization key for message cropped image registered.
     */
    M_COPPED_REGISTERED("listener.guessGameImageRegisterListener.message.coppedRegistered"),
    /**
     * Localization key for message added nsfw.
     */
    M_ADDED_NSFW("listener.guessGameImageRegisterListener.message.addedNsfw"),
    /**
     * Localization key for message added sfw.
     */
    M_ADDED_SFW("listener.guessGameImageRegisterListener.message.addedSfw"),
    /**
     * Localization key for message set registered.
     */
    M_SET_REGISTERED("listener.guessGameImageRegisterListener.message.setRegistered");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    GuessGameImageRegisterListenerLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
