package de.eldoria.shepard.commandmodules.monitoring.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.monitoring.data.MonitoringData;
import de.eldoria.shepard.commandmodules.monitoring.util.Address;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.WordsLocale;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.util.AddressType;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static de.eldoria.shepard.localization.enums.commands.admin.MonitoringLocale.*;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;
import static java.lang.System.lineSeparator;

/**
 * Command to modify the logging channel.
 * The monitoring is done in {@link de.eldoria.shepard.commandmodules.monitoring.analyzer.MonitoringCoordinator}
 */
@CommandUsage(EventContext.GUILD)
public class Monitoring extends Command implements Executable, ReqDataSource {
    private MonitoringData monitoringData;

    /**
     * Creates a new monitoring command object.
     */
    public Monitoring() {
        super("monitoring",
                new String[]{"monitor"},
                "command.monitoring.description",
                SubCommand.builder("monitoring")
                        .addSubcommand("command.monitoring.subcommand.add",
                                Parameter.createCommand("add"),
                                Parameter.createInput("command.monitoring.argument.address", "command.monitoring.argumentDescription.address", true),
                                Parameter.createInput("command.general.argument.name", null, true),
                                Parameter.createInput("command.general.argument.boolean", "command.monitoring.argument.addText", true))
                        .addSubcommand("command.monitoring.subcommand.remove",
                                Parameter.createCommand("remove"),
                                Parameter.createInput("command.general.argument.id", "command.general.argumentDescription.id", true))
                        .addSubcommand("command.monitoring.subcommand.list",
                                Parameter.createCommand("list"))
                        .addSubcommand("command.monitoring.subcommand.enable",
                                Parameter.createCommand("enable"),
                                Parameter.createInput("command.general.argument.channel", "command.general.argumentDescription.channelMentionOrExecution", false))
                        .addSubcommand("command.monitoring.subcommand.disable",
                                Parameter.createCommand("disable"))
                        .build(),
                CommandCategory.ADMIN);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        if (args.length == 0) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, wrapper);
            return;
        }

        String cmd = args[0];
        if (isSubCommand(cmd, 2)) {
            list(wrapper);
            return;
        }

        if (isSubCommand(cmd, 3)) {
            if (easyEnable(wrapper)) return;
        }

        if (isSubCommand(cmd, 4)) {
            disable(wrapper);
            return;
        }

        if (args.length < 2) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, wrapper);
            return;
        }

        if (isSubCommand(cmd, 0)) {
            add(args, wrapper);
            return;
        }

        if (isSubCommand(cmd, 1)) {
            remove(args[1], wrapper);
            return;
        }

        if (isSubCommand(cmd, 3)) {
            enable(args[1], wrapper);
            return;
        }
    }

    private void add(String[] args, EventWrapper wrapper) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, wrapper);
            return;
        }
        if (Verifier.getAddressType(args[1]) == AddressType.NONE) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ADDRESS, wrapper);
            return;
        }

        BooleanState booleanState = Verifier.checkAndGetBoolean(args[args.length - 1]);
        if (booleanState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, wrapper);
            return;
        }

        String name = ArgumentParser.getMessage(args, 2, -1);
        if (monitoringData.addMonitoringAddress(
                wrapper.getGuild().get(), args[1], name, booleanState.stateAsBoolean, wrapper)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_REGISTERED_ADDRESS.tag,
                    wrapper, "**" + args[1] + "**", "**" + name + "**"),
                    wrapper.getMessageChannel());
        }
    }

    private void enable(String channelString, EventWrapper wrapper) {
        Optional<TextChannel> channel = ArgumentParser.getTextChannel(wrapper.getGuild().get(), channelString);
        if (channel.isPresent()) {
            if (monitoringData.setMonitoringChannel(wrapper.getGuild().get(), channel.get(), wrapper)) {
                MessageSender.sendMessage(localizeAllAndReplace(M_REGISTERED_CHANNEL.tag,
                        wrapper, channel.get().getAsMention()), wrapper.getMessageChannel());
            }
        } else {
            MessageSender.sendSimpleError(ErrorType.INVALID_CHANNEL, wrapper);
        }
    }

    private void remove(String arg, EventWrapper wrapper) {
        OptionalInt integer = ArgumentParser.parseInt(arg);
        if (integer.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, wrapper);
            return;
        }
        if (monitoringData.removeMonitoringAddressByIndex(wrapper.getGuild().get(), integer.getAsInt(),
                wrapper)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_REMOVED_ADDRESS.tag, wrapper,
                    "**" + integer.getAsInt() + "**"), wrapper.getMessageChannel());
        }
    }

    private boolean easyEnable(EventWrapper wrapper) {
        if (monitoringData.setMonitoringChannel(wrapper.getGuild().get(),
                wrapper.getTextChannel().get(), wrapper)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_REGISTERED_CHANNEL.tag,
                    wrapper, wrapper.getTextChannel().get().getAsMention()),
                    wrapper.getMessageChannel());
            return true;
        }
        return false;
    }

    private void disable(EventWrapper wrapper) {
        if (monitoringData.removeMonitoringChannel(wrapper.getGuild().get(), wrapper)) {
            MessageSender.sendMessage(M_REMOVED_CHANNEL.tag, wrapper.getMessageChannel());
        }
    }

    private void list(EventWrapper wrapper) {
        List<Address> monitoringAddresses = monitoringData.getMonitoringAddressesForGuild(
                wrapper.getGuild().get(), wrapper);

        TextFormatting.TableBuilder tableBuilder = TextFormatting.getTableBuilder(
                monitoringAddresses,
                TextLocalizer.localizeAllAndReplace(WordsLocale.ID.tag, wrapper),
                TextLocalizer.localizeAllAndReplace(WordsLocale.NAME.tag, wrapper),
                TextLocalizer.localizeAllAndReplace(WordsLocale.ADDRESS.tag, wrapper),
                TextLocalizer.localizeAllAndReplace(WordsLocale.MINECRAFT.tag, wrapper));

        for (Address address : monitoringAddresses) {
            tableBuilder.next();
            tableBuilder.setRow(address.getId() + "",
                    address.getName(),
                    address.getAddress(),
                    TextFormatting.mapBooleanTo(address.isMinecraftIp(), "yes", "no"));
        }

        MessageSender.sendMessage(M_REGISTERED_ADDRESSES + lineSeparator() + tableBuilder,
                wrapper.getMessageChannel());
    }

    @Override
    public void addDataSource(DataSource source) {
        monitoringData = new MonitoringData(source);
    }
}
