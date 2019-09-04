package de.eldoria.shepard.collections;

import de.eldoria.shepard.ShepardBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class Normandy {
    private static Guild normandy = null;
    private static TextChannel errorChannel = null;
    private static TextChannel generalLogChannel = null;
    private static TextChannel commandLogChannel = null;

    /**
     * Get the normandy instance.
     *
     * @return normandy guild.
     */
    public static Guild getNormandy() {
        if (normandy == null) {
            normandy = ShepardBot.getJDA().getGuildById("538084337984208906");
        }
        return normandy;
    }

    /**
     * Get the error channel.
     *
     * @return error text channel
     */
    public static TextChannel getErrorChannel() {
        if (errorChannel == null) {
            errorChannel = getNormandy().getTextChannelById("616986329447792661");
        }
        return errorChannel;
    }

    public static TextChannel getGeneralLogChannel() {
        if (generalLogChannel == null) {
            generalLogChannel = getNormandy().getTextChannelById("538094461381640192");
        }
        return generalLogChannel;
    }

    public static TextChannel getCommandLogChannel() {
        if (commandLogChannel == null) {
            commandLogChannel = getNormandy().getTextChannelById("538087478960324630");
        }
        return commandLogChannel;
    }
}
