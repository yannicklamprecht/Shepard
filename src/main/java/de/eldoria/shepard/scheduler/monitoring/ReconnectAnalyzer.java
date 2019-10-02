package de.eldoria.shepard.scheduler.monitoring;

import de.eldoria.shepard.database.types.Address;
import de.eldoria.shepard.util.PingMinecraftServer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.Color;

public class ReconnectAnalyzer extends Analyzer {
    ReconnectAnalyzer(Address address, TextChannel channel) {
        super(address, channel);
    }

    @Override
    public void run() {
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
                        .setColor(Color.green);
                channel.sendMessage(builder.build()).queue();
                MonitoringScheduler.getInstance().markAsReachable(channel.getGuild().getIdLong(), address);
            }
        } else {
            boolean addressReachable = isAddressReachable();
            if (addressReachable) {
                channel.sendMessage("Service on " + address.getFullAddress() + " is back again!").queue();
                MonitoringScheduler.getInstance().markAsReachable(channel.getGuild().getIdLong(), address);
            }
        }

    }
}
