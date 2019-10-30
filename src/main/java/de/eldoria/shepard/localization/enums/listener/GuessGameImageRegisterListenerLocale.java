package de.eldoria.shepard.localization.enums.listener;

public enum GuessGameImageRegisterListenerLocale {
    M_COPPED_REGISTERED("listener.guessGameImageRegisterListener.message.coppedRegistered"),
    M_ADDED_NSFW("listener.guessGameImageRegisterListener.message.addedNsfw"),
    M_ADDED_SFW("listener.guessGameImageRegisterListener.message.addedSfw"),
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
