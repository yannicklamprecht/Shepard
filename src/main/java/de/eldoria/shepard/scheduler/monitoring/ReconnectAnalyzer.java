package de.eldoria.shepard.scheduler.monitoring;

import de.eldoria.shepard.database.types.Address;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.ShepardReactions;
import de.eldoria.shepard.util.PingMinecraftServer;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.Color;

import static de.eldoria.shepard.localization.enums.scheduler.AnalyzerLocale.*;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;
import static de.eldoria.shepard.util.TextFormatting.getTimeAsString;

@Slf4j
public class ReconnectAnalyzer extends Analyzer {
    /**
     * Creates a new reconnect analyzer.
     *
     * @param address address to check
     * @param channel channel for result
     */
    ReconnectAnalyzer(Address address, TextChannel channel) {
        super(address, channel, false);
    }

    @Override
    public void run() {

    	log.debug("Checking Address {}", address.getFullAddress());

        if (address.isMinecraftIp()) {
            PingMinecraftServer.MinecraftPing minecraftPing = checkMinecraftServer();
            if (minecraftPing != null && minecraftPing.isOnline()) {
                LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(channel.getGuild())
                        .setTitle(localizeAllAndReplace(M_SERVER_REACHABLE.tag, channel.getGuild(),
                                "**" + address.getName() + "**"))
                        .addField("IP", minecraftPing.getIp(), true)
                        .addField("PORT", String.valueOf(minecraftPing.getPort()), true)
                        .addField("HOST", minecraftPing.getHostname(), true)
                        .addField("MOTD", String.join(System.lineSeparator(),
                                minecraftPing.getMotd().getClean()), false)
                        .addField(M_PLAYER_COUNT.tag, minecraftPing.getPlayers().getOnline() + "/"
                                + minecraftPing.getPlayers().getMax(), false)
                        .addField(M_VERSION.tag, minecraftPing.getVersion().replace("Requires MC ", ""), false)
                        .setColor(Color.green)
                        .setFooter(getTimeAsString())
                        .setThumbnail(ShepardReactions.WINK.thumbnail);
                channel.sendMessage(builder.build()).queue();
                MonitoringScheduler.getInstance().markAsReachable(channel.getGuild().getIdLong(), address);
					log.debug("Service is reachable again: {}", address.getFullAddress());
            } else {
					log.debug("Service is still down: {}", address.getFullAddress());
            }
        } else {
            boolean addressReachable = isAddressReachable();
            if (addressReachable) {
                LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(channel.getGuild())
                        .setTitle(localizeAllAndReplace(M_SERVICE_REACHABLE.tag, channel.getGuild(),
                                "**" + address.getName() + "**"))
                        .setDescription(M_SERVICE_ADDRESS + address.getFullAddress())
                        .setFooter(getTimeAsString())
                        .setThumbnail(ShepardReactions.WINK.thumbnail);
                channel.sendMessage(builder.build()).queue();
                MonitoringScheduler.getInstance().markAsReachable(channel.getGuild().getIdLong(), address);
                log.debug("Service is reachable again: {}", address.getFullAddress());
            } else {
                LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(channel.getGuild())
                        .setTitle(localizeAllAndReplace(M_SERVICE_STILL_DOWN.tag, channel.getGuild(),
                                "**" + address.getName() + "**"))
                        .setDescription(M_SERVICE_ADDRESS + address.getFullAddress())
                        .setFooter(getTimeAsString())
                        .setThumbnail(ShepardReactions.WINK.thumbnail);
                channel.sendMessage(builder.build()).queue();
                MonitoringScheduler.getInstance().markAsReachable(channel.getGuild().getIdLong(), address);
                
                log.debug("Service is still down: {}", address.getFullAddress());
            }
        }
    }
}
