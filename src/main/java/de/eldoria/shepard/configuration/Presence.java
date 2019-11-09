package de.eldoria.shepard.configuration;

public class Presence {
    private String[] playing;
    private String[] listening;

    /**
     * Get the playing messages.
     *
     * @return array of messages
     */
    public String[] getPlaying() {
        return playing;
    }

    /**
     * Set the playing messages.
     *
     * @param playing messages
     */
    public void setPlaying(String[] playing) {
        this.playing = playing;
    }

    /**
     * Get the listening messages.
     *
     * @return array of messages
     */
    public String[] getListening() {
        return listening;
    }

    /**
     * Set the listening messages.
     *
     * @param listening message
     */
    public void setListening(String[] listening) {
        this.listening = listening;
    }
}
