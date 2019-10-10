package de.eldoria.shepard.scheduler.monitoring;

import de.eldoria.shepard.database.types.Address;
import de.eldoria.shepard.util.PingMinecraftServer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.Color;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Analyzer implements Runnable {

    /**
     * Address object to store address information.
     */
    protected Address address;
    /**
     * Text channel for monitoring feedback.
     */
    protected TextChannel channel;
    /**
     * Only Report Errors.
     */
    private final boolean onlyError;

    private String time;

    Analyzer(Address address, TextChannel channel, boolean onlyError) {
        this.address = address;
        this.channel = channel;
        this.onlyError = onlyError;
    }

    @Override
    public void run() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(" HH:mm dd.MM.yyyy");
        LocalDateTime now = LocalDateTime.now();
        time = dtf.format(now);

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

            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("Status of " + address.getName())
                    .addField("IP", minecraftPing.getIp() + "", true)
                    .addField("PORT", minecraftPing.getPort() + "", true)
                    .addField("HOST", minecraftPing.getHostname() + "", true)
                    .addField("MOTD", String.join(System.lineSeparator(), minecraftPing.getMotd().getClean()), false)
                    .addField("PLAYER COUNT", minecraftPing.getPlayers().getOnline() + "/"
                            + minecraftPing.getPlayers().getMax(), false)
                    .addField("VERSION", minecraftPing.getVersion().replace("Requires MC ", "")
                            + "", false)
                    .setColor(Color.green)
                    .setFooter(time);
            channel.sendMessage(builder.build()).queue();
        } else if (!minecraftPing.isOnline()) {
            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("WARNING: SERVER DOWN")
                    .setDescription("Server **" + address.getName() + "** under IP " + address.getFullAddress()
                            + " is currently unavailable!")
                    .setColor(Color.red)
                    .setFooter(time);

            channel.sendMessage(builder.build()).queue();
            if (!MonitoringScheduler.getInstance().markedAsUnreachable(channel.getGuild().getIdLong(), address)) {
                channel.sendMessage("@here").queue();
            }
            MonitoringScheduler.getInstance().markAsUnreachable(channel.getGuild().getIdLong(), address);
        }

    }

    private void analyzeNonMinecraftAddress() {
        if (!isAddressReachable()) {
            channel.sendMessage("Service under " + address.getFullAddress() + " is currently unavailable!").queue();
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
