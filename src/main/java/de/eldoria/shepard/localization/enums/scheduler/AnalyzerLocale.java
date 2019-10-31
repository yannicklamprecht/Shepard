package de.eldoria.shepard.localization.enums.scheduler;

public enum AnalyzerLocale {
    /**
     * Localization key for message status of.
     */
    M_STATUS_OF("monitoringAnalyzer.message.statusOf"),
    /**
     * Localization key for message "player count".
     */
    M_PLAYER_COUNT("monitoringAnalyzer.message.playerCount"),
    /**
     * Localization key for message "version".
     */
    M_VERSION("monitoringAnalyzer.message.version"),
    /**
     * Localization key for message "server down".
     */
    M_SERVER_DOWN("monitoringAnalyzer.message.serverDown"),
    /**
     * Localization key for message "server unavailable".
     */
    M_SERVER_DOWN_MESSAGE("monitoringAnalyzer.message.serverDownMessage"),
    /**
     * Localization key for message "service unavailable".
     */
    M_SERVICE_NAME_UNAVAILABLE("monitoringAnalyzer.message.serviceNameUnavailable"),
    /**
     * Localization key for message "service address".
     */
    M_SERVICE_ADDRESS("monitoringAnalyzer.message.serviceAddress"),
    /**
     * Localization key for message "service reachable again".
     */
    M_SERVICE_REACHABLE("monitoringAnalyzer.message.serviceReachable"),
    /**
     * Localization key for message "server reachable again".
     */
    M_SERVER_REACHABLE("monitoringAnalyzer.message.serverReachable"),
    /**
     * Localization key for message "service still down".
     */
    M_SERVICE_STILL_DOWN("monitoringAnalyzer.message.serviceStillDown");

    /**
     * Get the normal locale code for direct translation.
     */
    public final String localeCode;
    /**
     * Get the escaped locale code for auto translation.
     */
    public final String tag;

    AnalyzerLocale(String localeCode) {
        this.localeCode = localeCode;
        this.tag = "$" + localeCode + "$";
    }

    @Override
    public String toString() {
        return tag;
    }

}
