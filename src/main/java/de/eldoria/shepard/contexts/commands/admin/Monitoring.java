package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.database.queries.MonitoringData;
import de.eldoria.shepard.database.types.Address;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.localization.enums.WordsLocale;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.AddressType;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_BOOLEAN_YES_NO;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_EMPTY;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_NAME;
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
import static de.eldoria.shepard.util.Verifier.isArgument;
import static java.lang.System.lineSeparator;

public class Monitoring extends Command {
    public Monitoring() {
        commandName = "monitoring";
        commandAliases = new String[] {"monitor"};
        commandDesc = DESCRIPTION.replacement;
        commandArgs = new CommandArg[] {
                new CommandArg("action", true,
                        new SubArg("add", C_ADD.replacement, true),
                        new SubArg("remove", C_REMOVE.replacement, true),
                        new SubArg("list", C_LIST.replacement, true),
                        new SubArg("enable", C_ENABLE.replacement, true),
                        new SubArg("disable", C_DISABLE.replacement, true)),
                new CommandArg("value", false,
                        new SubArg("add", A_ADDRESS + " " + A_NAME + " "
                                + A_BOOLEAN_YES_NO + lineSeparator() + A_ADD_TEXT),
                        new SubArg("remove", GeneralLocale.A_ID.replacement),
                        new SubArg("list", GeneralLocale.A_EMPTY.replacement),
                        new SubArg("enable", GeneralLocale.A_CHANNEL_MENTION_OR_EXECUTE.replacement),
                        new SubArg("disable", A_EMPTY.replacement))
        };
        category = ContextCategory.ADMIN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 0) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext);
            return;
        }

        String cmd = args[0];

        if (isArgument(cmd, "disable", "d")) {
            disable(messageContext);
            return;
        }

        if (isArgument(cmd, "show", "s")) {
            show(messageContext);
            return;
        }

        if (isArgument(cmd, "enable", "e")) {
            if (easyEnable(messageContext)) return;
        }

        if (args.length < 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext);
            return;
        }

        if (isArgument(cmd, "remove", "r")) {
            remove(args[1], messageContext);
            return;
        }

        if (isArgument(cmd, "enable", "e")) {
            enable(args[1], messageContext);
            return;
        }

        if (isArgument(cmd, "add", "a")) {
            add(args, messageContext);
            return;
        }
        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext);
    }

    private void add(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext);
            return;
        }
        if (Verifier.getAddressType(args[1]) == AddressType.NONE) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ADDRESS, messageContext);
            return;
        }

        BooleanState booleanState = Verifier.checkAndGetBoolean(args[args.length - 1]);
        if (booleanState == BooleanState.UNDEFINED) {
            MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext);
            return;
        }

        String name = ArgumentParser.getMessage(args, 2, -1);
        if (MonitoringData.addMonitoringAddress(
                messageContext.getGuild(), args[1], name, booleanState.stateAsBoolean, messageContext)) {
            MessageSender.sendMessage(locale.getReplacedString(M_REGISTERED_ADDRESS.localeCode,
                    messageContext.getGuild(), "**" + args[1] + "**", "**" + name + "**"),
                    messageContext);
        }
    }

    private void enable(String channelString, MessageEventDataWrapper messageContext) {
        TextChannel channel = ArgumentParser.getTextChannel(messageContext.getGuild(), channelString);
        if (channel != null) {
            if (MonitoringData.setMonitoringChannel(messageContext.getGuild(), channel, messageContext)) {
                MessageSender.sendMessage(locale.getReplacedString(M_REGISTERED_CHANNEL.localeCode,
                        messageContext.getGuild(), channel.getAsMention()), messageContext);
            }
        } else {
            MessageSender.sendSimpleError(ErrorType.INVALID_CHANNEL, messageContext);
        }
    }

    private void remove(String arg, MessageEventDataWrapper messageContext) {
        Integer integer = ArgumentParser.parseInt(arg);
        if (integer == null) {
            MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, messageContext);
            return;
        }
        if (MonitoringData.removeMonitoringAddressByIndex(messageContext.getGuild(), integer, messageContext)) {
            MessageSender.sendMessage(locale.getReplacedString(M_REMOVED_ADDRESS.localeCode, messageContext.getGuild(),
                    "**" + integer + "**"), messageContext);
        }
    }

    private boolean easyEnable(MessageEventDataWrapper messageContext) {
        if (MonitoringData.setMonitoringChannel(messageContext.getGuild(),
                messageContext.getTextChannel(), messageContext)) {
            MessageSender.sendMessage(locale.getReplacedString(M_REGISTERED_CHANNEL.localeCode,
                    messageContext.getGuild(), messageContext.getTextChannel().getAsMention()),
                    messageContext);
            return true;
        }
        return false;
    }

    private void disable(MessageEventDataWrapper messageContext) {
        MonitoringData.removeMonitoringChannel(messageContext.getGuild(), messageContext);
        MessageSender.sendMessage(M_REMOVED_CHANNEL.replacement, messageContext);
    }

    private void show(MessageEventDataWrapper messageContext) {
        List<Address> monitoringAddresses = MonitoringData.getMonitoringAddressesForGuild(
                messageContext.getGuild(), messageContext);

        TextFormatting.TableBuilder tableBuilder = TextFormatting.getTableBuilder(monitoringAddresses,
                WordsLocale.ID.replacement, WordsLocale.NAME.replacement,
                WordsLocale.ADDRESS.replacement, WordsLocale.MINECRAFT.replacement);

        for (Address address : monitoringAddresses) {
            tableBuilder.next();
            tableBuilder.setRow(address.getId() + "",
                    address.getName(),
                    address.getAddress(),
                    TextFormatting.mapBooleanTo(address.isMinecraftIp(), "yes", "no"));
        }

        MessageSender.sendMessage(M_REGISTERED_ADDRESSES + lineSeparator() + tableBuilder,
                messageContext);
    }
}
