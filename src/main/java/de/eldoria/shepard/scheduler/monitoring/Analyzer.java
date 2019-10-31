package de.eldoria.shepard.scheduler.monitoring;

import de.eldoria.shepard.database.types.Address;
import de.eldoria.shepard.localization.LanguageHandler;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.ShepardReactions;
import de.eldoria.shepard.util.PingMinecraftServer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.Color;
import java.io.IOException;
import java.net.Socket;

import static de.eldoria.shepard.localization.enums.scheduler.AnalyzerLocale.M_PLAYER_COUNT;
import static de.eldoria.shepard.localization.enums.scheduler.AnalyzerLocale.M_SERVER_DOWN;
import static de.eldoria.shepard.localization.enums.scheduler.AnalyzerLocale.M_SERVER_DOWN_MESSAGE;
import static de.eldoria.shepard.localization.enums.scheduler.AnalyzerLocale.M_SERVICE_ADDRESS;
import static de.eldoria.shepard.localization.enums.scheduler.AnalyzerLocale.M_SERVICE_NAME_UNAVAILABLE;
import static de.eldoria.shepard.localization.enums.scheduler.AnalyzerLocale.M_STATUS_OF;
import static de.eldoria.shepard.localization.enums.scheduler.AnalyzerLocale.M_VERSION;
import static de.eldoria.shepard.util.TextFormatting.getTimeAsString;

class Analyzer implements Runnable {
    /**
     * Address object to store address information.
     */
    protected final Address address;
    /**
     * Text channel for monitoring feedback.
     */
    protected final TextChannel channel;
    /**
     * language handler instance.
     */
    protected final LanguageHandler locale;
    /**
     * Only Report Errors.
     */
    private final boolean onlyError;


    Analyzer(Address address, TextChannel channel, boolean onlyError) {
        this.address = address;
        this.channel = channel;
        this.onlyError = onlyError;
        this.locale = LanguageHandler.getInstance();
    }

    @Override
    public void run() {
        if (address.isMinecraftIp()) {
            analyzeMinecraftAddress();
        } else {
            analyzeNonMinecraftAddress();
        }
    }

    private void analyzeMinecraftAddress() {
        PingMinecraftServer.MinecraftPing minecraftPing = checkMinecraftServer();
        if (minecraftPing == null) {
            return;
        }

        if (minecraftPing.isOnline() && !onlyError) {

            LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(channel.getGuild())
                    .setTitle(M_STATUS_OF + " " + address.getName())
                    .addField("IP", minecraftPing.getIp() + "", true)
                    .addField("PORT", minecraftPing.getPort() + "", true)
                    .addField("HOST", minecraftPing.getHostname() + "", true)
                    .addField("MotD", String.join(System.lineSeparator(), minecraftPing.getMotd().getClean()), false)
                    .addField(M_PLAYER_COUNT.tag, minecraftPing.getPlayers().getOnline() + "/"
                            + minecraftPing.getPlayers().getMax(), false)
                    .addField(M_VERSION.tag, minecraftPing.getVersion().replace("Requires MC ", "")
                            + "", false)
                    .setColor(Color.green)
                    .setFooter(getTimeAsString())
                    .setThumbnail(ShepardReactions.EXCITED.thumbnail);
            channel.sendMessage(builder.build()).queue();
        } else if (!minecraftPing.isOnline()) {
            LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(channel.getGuild())
                    .setTitle(M_SERVER_DOWN.tag)
                    .setDescription(locale.getReplacedString(M_SERVER_DOWN_MESSAGE.localeCode, channel.getGuild(),
                            "**" + address.getName() + "**", "**" + address.getAddress() + "**"))
                    .setColor(Color.red)
                    .setThumbnail(ShepardReactions.SHULKY.thumbnail)
                    .setFooter(getTimeAsString());

            channel.sendMessage(builder.build()).queue();
            if (!MonitoringScheduler.getInstance().markedAsUnreachable(channel.getGuild().getIdLong(), address)) {
                channel.sendMessage("@here").queue();
            }
            MonitoringScheduler.getInstance().markAsUnreachable(channel.getGuild().getIdLong(), address);
        }

    }

    private void analyzeNonMinecraftAddress() {
        if (!isAddressReachable()) {
            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle(locale.getReplacedString(M_SERVICE_NAME_UNAVAILABLE.localeCode, channel.getGuild(),
                            "**" + address.getName() + "**"))
                    .setDescription(M_SERVICE_ADDRESS + " " + address.getFullAddress())
                    .setThumbnail(ShepardReactions.SHULKY.thumbnail);
            channel.sendMessage(builder.build()).queue();
            MonitoringScheduler.getInstance().markAsUnreachable(channel.getGuild().getIdLong(), address);
            if (!MonitoringScheduler.getInstance().markedAsUnreachable(channel.getGuild().getIdLong(), address)) {
                channel.sendMessage("@here").queue();
            }
        }
    }

    boolean isAddressReachable() {
        Socket checker = null;
        boolean reachable = false;
        try {
            checker = new Socket(address.getAddress(), address.getPort());
            reachable = true;
        } catch (IOException e) {
            //NOTHING
        } finally {
            if (checker != null) {
                try {
                    checker.close();
                } catch (IOException e) {
                    //NOTHING
                }
            }
        }
        return reachable;
    }

    PingMinecraftServer.MinecraftPing checkMinecraftServer() {
        return PingMinecraftServer.pingServer(address.getFullAddress());
    }


}
