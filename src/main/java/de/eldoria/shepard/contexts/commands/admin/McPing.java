package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.localization.enums.commands.admin.McPingLocale;
import de.eldoria.shepard.localization.enums.commands.admin.MonitoringLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.messagehandler.ShepardReactions;
import de.eldoria.shepard.util.AddressType;
import de.eldoria.shepard.util.PingMinecraftServer;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.awt.Color;

import static de.eldoria.shepard.localization.enums.scheduler.AnalyzerLocale.M_PLAYER_COUNT;
import static de.eldoria.shepard.localization.enums.scheduler.AnalyzerLocale.M_SERVER_DOWN;
import static de.eldoria.shepard.localization.enums.scheduler.AnalyzerLocale.M_STATUS_OF;
import static de.eldoria.shepard.localization.enums.scheduler.AnalyzerLocale.M_VERSION;

/**
 * the McPing command uses the {@link de.eldoria.shepard.util.PingMinecraftServer.MinecraftPing}
 * class to ping a minecraft server.
 * This command should be executed asynchronous.
 */
public class McPing extends Command {
    /**
     * Creates a new Message Ping command.
     */
    public McPing() {
        commandName = "mcping";
        commandDesc = McPingLocale.DESCRIPTION.tag;
        commandArgs = new CommandArg[] {new CommandArg("address", true,
                new SubArg("address", MonitoringLocale.A_ADDRESS.tag))};
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        AddressType addressType = Verifier.getAddressType(args[0]);
        if (addressType == AddressType.NONE) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ADDRESS, messageContext.getTextChannel());
            return;
        }
        PingMinecraftServer.MinecraftPing minecraftPing = PingMinecraftServer.pingServer(args[0]);

        if (minecraftPing != null && minecraftPing.isOnline()) {

            LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(messageContext.getGuild())
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
            messageContext.getChannel().sendMessage(builder.build()).queue();
        } else if (minecraftPing == null || !minecraftPing.isOnline()) {
            LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(messageContext.getGuild())
                    .setTitle(M_SERVER_DOWN.tag)
                    .setColor(Color.red)
                    .setThumbnail(ShepardReactions.SHULKY.thumbnail);

            messageContext.getChannel().sendMessage(builder.build()).queue();
        }
    }
}