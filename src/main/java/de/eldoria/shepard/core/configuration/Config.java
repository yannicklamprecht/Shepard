package de.eldoria.shepard.core.configuration;

import de.eldoria.shepard.core.configuration.configdata.Api;
import de.eldoria.shepard.core.configuration.configdata.BotList;
import de.eldoria.shepard.core.configuration.configdata.Database;
import de.eldoria.shepard.core.configuration.configdata.GeneralSettings;
import de.eldoria.shepard.core.configuration.configdata.Presence;
import de.eldoria.shepard.core.configuration.configdata.ThirdPartyApis;
import de.eldoria.shepard.core.configuration.configdata.Webhooks;
import lombok.Data;

/**
 * Class to deserialize the config.
 */
@Data
public class Config {
    private GeneralSettings generalSettings = null;

    /**
     * Api configuration.
     */
    private Api api = null;

    /**
     * Botlist Configuration.
     */
    private BotList botlist = null;

    private ThirdPartyApis thirdPartyApis = null;
    /**
     * Presence Configuration.
     */
    private Presence presence = null;

    /**
     * Database Configuration.
     */
    private Database database = null;

    /**
     * Webhook address.
     */
    private Webhooks webhooks = null;
}
