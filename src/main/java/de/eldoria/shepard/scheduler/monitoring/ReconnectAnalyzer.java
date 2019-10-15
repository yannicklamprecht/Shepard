package de.eldoria.shepard.scheduler.monitoring;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.types.Address;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.messagehandler.ShepardReactions;
import de.eldoria.shepard.util.PingMinecraftServer;
import de.eldoria.shepard.util.TextFormatting;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.Color;

import static de.eldoria.shepard.util.TextFormatting.getTimeAsString;

public class ReconnectAnalyzer extends Analyzer {
    ReconnectAnalyzer(Address address, TextChannel channel) {
        super(address, channel, false);
    }

    @Override
    public void run() {

        if (ShepardBot.getConfig().debugActive()) {
            ShepardBot.getLogger().info("Checking Address " + address.getFullAddress());
        }

        if (address.isMinecraftIp()) {
            PingMinecraftServer.MinecraftPing minecraftPing = checkMinecraftServer();
            if (minecraftPing != null && minecraftPing.isOnline()) {
                EmbedBuilder builder = new EmbedBuilder()
                        .setTitle("Server " + address.getName() + " is reachable again!")
                        .addField("IP", minecraftPing.getIp() + "", true)
                        .addField("PORT", minecraftPing.getPort() + "", true)
                        .addField("HOST", minecraftPing.getHostname() + "", true)
                        .addField("MOTD", String.join(System.lineSeparator(),
                                minecraftPing.getMotd().getClean()), false)
                        .addField("PLAYER COUNT", minecraftPing.getPlayers().getOnline() + "/"
                                + minecraftPing.getPlayers().getMax(), false)
                        .addField("VERSION", minecraftPing.getVersion().replace("Requires MC ", "")
                                + "", false)
                        .setColor(Color.green)
                        .setFooter(getTimeAsString())
                        .setThumbnail(ShepardReactions.WINK.thumbnail);
                channel.sendMessage(builder.build()).queue();
                MonitoringScheduler.getInstance().markAsReachable(channel.getGuild().getIdLong(), address);
                if (ShepardBot.getConfig().debugActive()) {
                    ShepardBot.getLogger().info("Service is reachable again: " + address.getFullAddress());
                }
            } else {
                if (ShepardBot.getConfig().debugActive()) {
                    ShepardBot.getLogger().info("Service is still down: " + address.getFullAddress());
                }
            }
        } else {
            boolean addressReachable = isAddressReachable();
            if (addressReachable) {
                EmbedBuilder builder = new EmbedBuilder()
                        .setTitle("Service " + address.getName() + " is back again!")
                        .setDescription("Service Address: " + address.getFullAddress())
                        .setFooter(getTimeAsString())
                        .setThumbnail(ShepardReactions.WINK.thumbnail);
                channel.sendMessage(builder.build()).queue();
                MonitoringScheduler.getInstance().markAsReachable(channel.getGuild().getIdLong(), address);
                if (ShepardBot.getConfig().debugActive()) {
                    ShepardBot.getLogger().info("Service is reachable again: " + address.getFullAddress());
                }
            } else {
                if (ShepardBot.getConfig().debugActive()) {
                    ShepardBot.getLogger().info("Service is still down: " + address.getFullAddress());
                }

            }
        }
    }
}
