package de.eldoria.shepard.localization.enums.scheduler;

public enum AnalyzerLocale {
    M_STATUS_OF("monitoringAnalyzer.message.statusOf"),
    M_PLAYER_COUNT("monitoringAnalyzer.message.playerCount"),
    M_VERSION("monitoringAnalyzer.message.version"),
    M_SERVER_DOWN("monitoringAnalyzer.message.serverDown"),
    M_SERVER_DOWN_MESSAGE("monitoringAnalyzer.message.serverDownMessage"),
    M_SERVICE_NAME_UNAVAILABLE("monitoringAnalyzer.message.serviceNameUnavailable"),
    M_SERVICE_ADDRESS("monitoringAnalyzer.message.serviceAddress"),
    M_SERVICE_REACHABLE("monitoringAnalyzer.message.serviceReachable"),
    M_SERVER_REACHABLE("monitoringAnalyzer.message.serverReachable"),
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
