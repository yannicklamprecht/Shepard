package de.eldoria.shepard.collections;

import de.eldoria.shepard.ShepardBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class Normandy {
    private static final long normandy = 538084337984208906L;
    private static final long errorChannel = 616986329447792661L;
    private static final long generalLogChannel = 538094461381640192L;
    private static final long commandLogChannel = 538087478960324630L;
    private static final long privateAnswerChannel = 627891573430157334L;

    /**
     * Get the normandy instance.
     *
     * @return normandy guild.
     */
    public static Guild getNormandy() {
        return ShepardBot.getJDA().getGuildById(normandy);
    }

    /**
     * Get the error channel.
     *
     * @return error text channel
     */
    public static TextChannel getErrorChannel() {
        return getNormandy().getTextChannelById(errorChannel);
    }

    /**
     * Get the private answer channel.
     *
     * @return general log channel
     */
    public static TextChannel getGeneralLogChannel() {
        return getNormandy().getTextChannelById(generalLogChannel);

    }

    /**
     * Get the private answer channel.
     *
     * @return command log channel
     */

    public static TextChannel getCommandLogChannel() {
        return getNormandy().getTextChannelById(commandLogChannel);
    }

    /**
     * Get the private answer channel.
     *
     * @return private answer channel
     */
    public static TextChannel getPrivateAnswerChannel() {
        return getNormandy().getTextChannelById(privateAnswerChannel);
    }


}
