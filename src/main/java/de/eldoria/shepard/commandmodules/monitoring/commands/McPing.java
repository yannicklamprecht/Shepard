package de.eldoria.shepard.commandmodules.monitoring.commands;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.monitoring.util.PingMinecraftServer;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.admin.McPingLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.messagehandler.ShepardReactions;
import de.eldoria.shepard.util.AddressType;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.EventWrapper;

import java.awt.Color;

import static de.eldoria.shepard.localization.enums.commands.admin.MonitoringLocale.AD_ADDRESS;
import static de.eldoria.shepard.localization.enums.commands.admin.MonitoringLocale.A_ADDRESS;
import static de.eldoria.shepard.localization.enums.scheduler.AnalyzerLocale.M_PLAYER_COUNT;
import static de.eldoria.shepard.localization.enums.scheduler.AnalyzerLocale.M_SERVER_DOWN;
import static de.eldoria.shepard.localization.enums.scheduler.AnalyzerLocale.M_STATUS_OF;
import static de.eldoria.shepard.localization.enums.scheduler.AnalyzerLocale.M_VERSION;

/**
 * the McPing command uses the {@link PingMinecraftServer.MinecraftPing}
 * class to ping a minecraft server.
 * This command should be executed asynchronous.
 */
public class McPing extends Command implements Executable {
    /**
     * Creates a new Message Ping command.
     */
    public McPing() {
        super("mcping",
                null,
                McPingLocale.DESCRIPTION.tag,
                SubCommand.builder("mcping")
                        .addSubcommand(null,
                                Parameter.createInput(A_ADDRESS.tag, AD_ADDRESS.tag, true))
                        .build(),
                CommandCategory.ADMIN);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        AddressType addressType = Verifier.getAddressType(args[0]);
        if (addressType == AddressType.NONE) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ADDRESS, wrapper);
            return;
        }
        PingMinecraftServer.MinecraftPing minecraftPing = PingMinecraftServer.pingServer(args[0]);

        if (minecraftPing != null && minecraftPing.isOnline()) {

            LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(wrapper)
                    .setTitle(M_STATUS_OF + " " + minecraftPing.getHostname())
                    .addField("IP", minecraftPing.getIp() + "", true)
                    .addField("PORT", minecraftPing.getPort() + "", true)
                    .addField("HOST", minecraftPing.getHostname() + "", true)
                    .addField("MotD", String.join(System.lineSeparator(), minecraftPing.getMotd().getClean()), false)
                    .addField(M_PLAYER_COUNT.tag, minecraftPing.getPlayers().getOnline() + "/"
                            + minecraftPing.getPlayers().getMax(), false)
                    .addField(M_VERSION.tag, minecraftPing.getVersion().replace("Requires MC ", "")
                            + "", false)
                    .setColor(Color.green)
                    .setThumbnail(ShepardReactions.EXCITED.thumbnail);
            wrapper.getMessageChannel().sendMessage(builder.build()).queue();
        } else {
            LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(wrapper)
                    .setTitle(M_SERVER_DOWN.tag)
                    .setColor(Color.red)
                    .setThumbnail(ShepardReactions.SHULKY.thumbnail);

            wrapper.getMessageChannel().sendMessage(builder.build()).queue();
        }
    }
}