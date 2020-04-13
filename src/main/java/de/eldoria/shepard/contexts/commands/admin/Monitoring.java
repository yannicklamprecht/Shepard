package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.Parameter;
import de.eldoria.shepard.contexts.commands.argument.SubCommand;
import de.eldoria.shepard.database.queries.commands.MonitoringData;
import de.eldoria.shepard.database.types.Address;
import de.eldoria.shepard.localization.enums.WordsLocale;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.AddressType;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_CHANNEL_MENTION_OR_EXECUTE;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_BOOLEAN;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_CHANNEL;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_NAME;
import static de.eldoria.shepard.localization.enums.commands.admin.MonitoringLocale.AD_ADDRESS;
import static de.eldoria.shepard.localization.enums.commands.admin.MonitoringLocale.A_ADDRESS;
import static de.eldoria.shepard.localization.enums.commands.admin.MonitoringLocale.A_ADD_TEXT;
import static de.eldoria.shepard.localization.enums.commands.admin.MonitoringLocale.C_ADD;
import static de.eldoria.shepard.localization.enums.commands.admin.MonitoringLocale.C_DISABLE;
import static de.eldoria.shepard.localization.enums.commands.admin.MonitoringLocale.C_ENABLE;
import static de.eldoria.shepard.localization.enums.commands.admin.MonitoringLocale.C_LIST;
import static de.eldoria.shepard.localization.enums.commands.admin.MonitoringLocale.C_REMOVE;
import static de.eldoria.shepard.localization.enums.commands.admin.MonitoringLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.admin.MonitoringLocale.M_REGISTERED_ADDRESS;
import static de.eldoria.shepard.localization.enums.commands.admin.MonitoringLocale.M_REGISTERED_ADDRESSES;
import static de.eldoria.shepard.localization.enums.commands.admin.MonitoringLocale.M_REGISTERED_CHANNEL;
import static de.eldoria.shepard.localization.enums.commands.admin.MonitoringLocale.M_REMOVED_ADDRESS;
import static de.eldoria.shepard.localization.enums.commands.admin.MonitoringLocale.M_REMOVED_CHANNEL;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;
import static java.lang.System.lineSeparator;

/**
 * Command to modify the logging channel.
 * The monitoring is done in {@link de.eldoria.shepard.scheduler.monitoring.MonitoringCoordinator}
 */
public class Monitoring extends Command {
    /**
     * Creates a new monitoring command object.
     */
    public Monitoring() {
        super("monitoring",
                new String[] {"monitor"},
                DESCRIPTION.tag,
                SubCommand.builder("monitoring")
                        .addSubcommand(C_ADD.tag,
                                Parameter.createCommand("add"),
                                Parameter.createInput(A_ADDRESS.tag, AD_ADDRESS.tag, true),
                                Parameter.createInput(A_NAME.tag, null, true),
                                Parameter.createInput(A_BOOLEAN.tag, A_ADD_TEXT.tag, true))
                        .addSubcommand(C_REMOVE.tag,
                                Parameter.createCommand("remove"),
                                Parameter.createInput(GeneralLocale.A_ID.tag, GeneralLocale.AD_ID.tag, true))
                        .addSubcommand(C_LIST.tag,
                                Parameter.createCommand("list"))
                        .addSubcommand(C_ENABLE.tag,
                                Parameter.createCommand("enable"),
                                Parameter.createInput(A_CHANNEL.tag, AD_CHANNEL_MENTION_OR_EXECUTE.tag, true))
                        .addSubcommand(C_DISABLE.tag,
                                Parameter.createCommand("disable"))
                        .build(),
                ContextCategory.ADMIN);
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 0) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getTextChannel());
            return;
        }

        String cmd = args[0];
        SubCommand arg = subCommands[0];
        if (isSubCommand(cmd, 2)) {
            list(messageContext);
            return;
        }

        if (isSubCommand(cmd, 3)) {
            if (easyEnable(messageContext)) return;
        }

        if (isSubCommand(cmd, 4)) {
            disable(messageContext);
            return;
        }

        if (args.length < 2) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getTextChannel());
            return;
        }

        if (isSubCommand(cmd, 0)) {
            add(args, messageContext);
            return;
        }

        if (isSubCommand(cmd, 1)) {
            remove(args[1], messageContext);
            return;
        }

        if (isSubCommand(cmd, 3)) {
            enable(args[1], messageContext);
            return;
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());
    }

    private void add(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }
        if (Verifier.getAddressType(args[1]) == AddressType.NONE) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ADDRESS, messageContext.getTextChannel());
            return;
        }

        BooleanState booleanState = Verifier.checkAndGetBoolean(args[args.length - 1]);
        if (booleanState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getTextChannel());
            return;
        }

        String name = ArgumentParser.getMessage(args, 2, -1);
        if (MonitoringData.addMonitoringAddress(
                messageContext.getGuild(), args[1], name, booleanState.stateAsBoolean, messageContext)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_REGISTERED_ADDRESS.tag,
                    messageContext.getGuild(), "**" + args[1] + "**", "**" + name + "**"),
                    messageContext.getTextChannel());
        }
    }

    private void enable(String channelString, MessageEventDataWrapper messageContext) {
        TextChannel channel = ArgumentParser.getTextChannel(messageContext.getGuild(), channelString);
        if (channel != null) {
            if (MonitoringData.setMonitoringChannel(messageContext.getGuild(), channel, messageContext)) {
                MessageSender.sendMessage(localizeAllAndReplace(M_REGISTERED_CHANNEL.tag,
                        messageContext.getGuild(), channel.getAsMention()), messageContext.getTextChannel());
            }
        } else {
            MessageSender.sendSimpleError(ErrorType.INVALID_CHANNEL, messageContext.getTextChannel());
        }
    }

    private void remove(String arg, MessageEventDataWrapper messageContext) {
        Integer integer = ArgumentParser.parseInt(arg);
        if (integer == null) {
            MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, messageContext.getTextChannel());
            return;
        }
        if (MonitoringData.removeMonitoringAddressByIndex(messageContext.getGuild(), integer, messageContext)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_REMOVED_ADDRESS.tag, messageContext.getGuild(),
                    "**" + integer + "**"), messageContext.getTextChannel());
        }
    }

    private boolean easyEnable(MessageEventDataWrapper messageContext) {
        if (MonitoringData.setMonitoringChannel(messageContext.getGuild(),
                messageContext.getTextChannel(), messageContext)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_REGISTERED_CHANNEL.tag,
                    messageContext.getGuild(), messageContext.getTextChannel().getAsMention()),
                    messageContext.getTextChannel());
            return true;
        }
        return false;
    }

    private void disable(MessageEventDataWrapper messageContext) {
        if (MonitoringData.removeMonitoringChannel(messageContext.getGuild(), messageContext)) {
            MessageSender.sendMessage(M_REMOVED_CHANNEL.tag, messageContext.getTextChannel());
        }
    }

    private void list(MessageEventDataWrapper messageContext) {
        List<Address> monitoringAddresses = MonitoringData.getMonitoringAddressesForGuild(
                messageContext.getGuild(), messageContext);

        TextFormatting.TableBuilder tableBuilder = TextFormatting.getTableBuilder(
                monitoringAddresses,
                TextLocalizer.localizeAllAndReplace(WordsLocale.ID.tag, messageContext.getGuild()),
                TextLocalizer.localizeAllAndReplace(WordsLocale.NAME.tag, messageContext.getGuild()),
                TextLocalizer.localizeAllAndReplace(WordsLocale.ADDRESS.tag, messageContext.getGuild()),
                TextLocalizer.localizeAllAndReplace(WordsLocale.MINECRAFT.tag, messageContext.getGuild()));

        for (Address address : monitoringAddresses) {
            tableBuilder.next();
            tableBuilder.setRow(address.getId() + "",
                    address.getName(),
                    address.getAddress(),
                    TextFormatting.mapBooleanTo(address.isMinecraftIp(), "yes", "no"));
        }

        MessageSender.sendMessage(M_REGISTERED_ADDRESSES + lineSeparator() + tableBuilder,
                messageContext.getTextChannel());
    }
}
