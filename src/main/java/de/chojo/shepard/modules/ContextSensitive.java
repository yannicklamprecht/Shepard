package de.chojo.shepard.modules;

import de.chojo.shepard.util.ListType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ContextSensitive extends ListenerAdapter {

    //Server sensitive
    protected ListType serverListType = ListType.BLACKLIST;
    protected String[] listedServer = new String[0];
    protected boolean serverCheckEnabled = false;

    //Character sensitive
    protected ListType characterListType = ListType.BLACKLIST;
    protected String[] characterList = new String[0];
    protected boolean characterCheckEnabled = false;

    protected boolean isNSFW = false;


    //** Returns true ID the command execution is allowed
    // @return boolean
    // */
    public boolean isCommandValid(MessageReceivedEvent event) {
        if (!serverCheckEnabled && !characterCheckEnabled) {
            return true;
        }

        boolean value = false;



        //Server Check
        if (serverCheckEnabled) {
            for (String server : listedServer) {
                if (server.equalsIgnoreCase(event.getGuild().getId())) {
                    if (serverListType == ListType.BLACKLIST) {
                        return false;
                    } else {
                        value = true;
                    }
                }
            }
            if (!value) return false;
        }


        //Character Check
        if (characterCheckEnabled) {
            for (String character : characterList) {
                if (character.equalsIgnoreCase(event.getAuthor().getId())) {
                    if (characterListType == ListType.BLACKLIST) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return true;
        }
    }
}
