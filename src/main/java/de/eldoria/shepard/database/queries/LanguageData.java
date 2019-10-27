package de.eldoria.shepard.database.queries;

import de.eldoria.shepard.localization.util.LanguageCode;
import net.dv8tion.jda.api.entities.Guild;

public class LanguageData {
    public static LanguageCode getGuildLanguage(Guild guild) {
        //TODO: Implement database
        return LanguageCode.EN_US;
    }
}
