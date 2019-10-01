package de.eldoria.shepard.contexts.commands.admin;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.database.queries.MonitoringData;
import de.eldoria.shepard.database.types.Address;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

import static java.lang.System.lineSeparator;

public class Monitoring extends Command {
    public Monitoring() {
        commandName = "monitoring";
        commandAliases = new String[] {"monitor"};
        commandDesc = "Monitoring of domains, ipv4/6 and minecraft server";
        commandArgs = new CommandArg[] {
                new CommandArg("action",
                        "**__a__dd** -> Add a address to monitoring" + lineSeparator()
                                + "**__r__emove** -> Removes an address from monitoring" + lineSeparator()
                                + "**__s__how** -> Lists all currently monitored addresses" + lineSeparator()
                                + "**__e__nable** -> enable monitoring in a channel" + lineSeparator()
                                + "**__d__isable** -> disable monitoring" + lineSeparator(), true),
                new CommandArg("value",
                        "**add** -> [domain|ipv4|ipv6|minecraft server] [name] [true|false]" + lineSeparator()
                                + "enter any address with port if needed." + lineSeparator()
                                + "true if the address is a minecraft Server. Provides additional information."
                                + lineSeparator()
                                + "**remove** -> [index] Enter index from show command" + lineSeparator()
                                + "**show** -> leave empty." + lineSeparator()
                                + "**activate** -> [channel] leave empty to set the current channel"
                                + " or enter channel for monitoring" + lineSeparator()
                                + "**deactivate** -> leave empty" + lineSeparator(), true)

        };
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 0) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getChannel());
            return;
        }

        String cmd = args[0];

        if (Verifier.isArgument(cmd, "disable", "d")) {
            MonitoringData.removeMonitoringChannel(messageContext.getGuild(), messageContext);
            MessageSender.sendMessage("Monitoring Channel removed!", messageContext.getChannel());
            return;
        }

        if (Verifier.isArgument(cmd, "show", "s")) {
            List<Address> monitoringAddresses = MonitoringData.getMonitoringAddresses(
                    messageContext.getGuild(), messageContext);

            String[][] preparedStringTable = TextFormatting.getPreparedStringTable(monitoringAddresses,
                    "Index", "Name", "Address", "Minecraft");

            for (int i = 1; i < preparedStringTable.length; i++) {
                Address address = monitoringAddresses.get(i);
                preparedStringTable[i] = new String[] {
                        address.getId() + "",
                        address.getName(),
                        address.getAddress(),
                        TextFormatting.mapBooleanTo(address.isMinecraftIp(), "yes", "no")};
            }

            String asTable = TextFormatting.getAsTable(preparedStringTable);

            MessageSender.sendMessage("Registered for Monitoring: " + lineSeparator()
                            + "```" + lineSeparator()
                            + asTable + lineSeparator()
                            + "```",
                    messageContext.getChannel());
        }

        if (Verifier.isArgument(cmd, "enable", "e")) {
            if (MonitoringData.setMonitoringChannel(messageContext.getGuild(),
                    messageContext.getTextChannel(), messageContext)) {
                MessageSender.sendMessage(
                        "Registered " + messageContext.getTextChannel().getAsMention() + " as monitoring Channel",
                        messageContext.getChannel());
            }
        }

        if (args.length < 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getChannel());
            return;
        }

        if (Verifier.isArgument(cmd, "remove", "r")) {
            try {
                int i = Integer.parseInt(args[1]);
                if (MonitoringData.removeMonitoringAddressByIndex(messageContext.getGuild(), i, messageContext)) {
                    MessageSender.sendMessage("Removed address with index: " + i, messageContext.getChannel());
                }
            } catch (NumberFormatException e) {
                MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, messageContext.getChannel());
            }
            return;
        }

        if (Verifier.isArgument(cmd, "enable", "e")) {
            if (Verifier.isValidId(args[1])) {
                TextChannel channel = messageContext.getGuild().getTextChannelById(DbUtil.getIdRaw(args[1]));
                if (channel != null) {
                    if (MonitoringData.setMonitoringChannel(messageContext.getGuild(), channel, messageContext)) {
                        MessageSender.sendMessage(
                                "Registered " + channel.getAsMention() + " as monitoring Channel",
                                messageContext.getChannel());
                    }
                } else {
                    MessageSender.sendSimpleError(ErrorType.INVALID_CHANNEL, messageContext.getChannel());
                    return;
                }
            } else {
                MessageSender.sendSimpleError(ErrorType.INVALID_ID, messageContext.getChannel());
            }
            return;
        }

        if (Verifier.isArgument(cmd, "add", "a")) {
            if (args.length != 3) {
                MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getChannel());
                return;
            }
            if (!Verifier.isAddress(args[1])) {
                MessageSender.sendSimpleError(ErrorType.INVALID_ADDRESS, messageContext.getChannel());
                return;
            }

            BooleanState booleanState = Verifier.checkAndGetBoolean(args[args.length - 1]);
            if (booleanState == BooleanState.UNDEFINED) {
                MessageSender.sendSimpleError(ErrorType.INVALID_BOOLEAN, messageContext.getChannel());
                return;
            }

            String name = TextFormatting.getRangeAsString(" ", args, 2, args.length - 2);
            if (MonitoringData.addMonitoringAddress(
                    messageContext.getGuild(), args[1], name, booleanState.stateAsBoolean, messageContext)) {
                MessageSender.sendMessage("Added Address **" + args[1] + "** as **" + name + "**!",
                        messageContext.getTextChannel());
            }
        }

        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getChannel());


    }
}
