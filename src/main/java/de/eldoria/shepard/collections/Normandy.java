package de.eldoria.shepard.collections;

import de.eldoria.shepard.ShepardBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * The normandy provides several static channel for logging.
 * Should be removed in future update and replaced by webhooks.
 */
public class Normandy {
    private static final long normandy = 538084337984208906L;
    private static final long errorChannel = 616986329447792661L;
    private static final long generalLogChannel = 538094461381640192L;
    private static final long commandLogChannel = 538087478960324630L;
    private static final long privateAnswerChannel = 627891573430157334L;
    private static final long testOutputChannel = 633630420231258112L;
    private static final long logChannel = 657368050194055190L;

    /**
     * Get the normandy instance.
     *
     * @return normandy guild.
     */
    public static Guild getNormandy() {
        JDA jda = ShepardBot.getJDA();
        if (jda != null) {
            return jda.getGuildById(normandy);
        }
        return null;
    }

    /**
     * Get the error channel.
     *
     * @return error text channel
     */
    public static TextChannel getErrorChannel() {
        if (getNormandy() != null) {
            return getNormandy().getTextChannelById(errorChannel);
        }
        return null;
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
     * @return command log channel
     */

    public static TextChannel getTestOutputChannel() {
        return getNormandy().getTextChannelById(testOutputChannel);
    }

    /**
     * Get the private answer channel.
     *
     * @return private answer channel
     */
    public static TextChannel getPrivateAnswerChannel() {
        return getNormandy().getTextChannelById(privateAnswerChannel);
    }

    /**
     * get the log channel if this instance has a connection to normandy guild.
     *
     * @return text channel or null if normandy is not connected.
     */
    public static TextChannel getLogChannel() {
        if (getNormandy() != null) {
            return getNormandy().getTextChannelById(logChannel);
        }
        return null;
    }
}
