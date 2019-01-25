package de.chojo.shepard.Collections;

import de.chojo.shepard.ShepardBot;
import net.dv8tion.jda.api.entities.Guild;

public class ServerCollection {

    private static Guild normandy = null;

    public static Guild getNormandy(){
        if(normandy == null){
            normandy = ShepardBot.getJDA().getGuildById("538084337984208906");
        }
        return normandy;
    }
}
