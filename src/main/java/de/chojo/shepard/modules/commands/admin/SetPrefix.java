package de.chojo.shepard.modules.commands.admin;

import de.chojo.shepard.Settings;
import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.modules.commands.Command;
import de.chojo.shepard.modules.commands.CommandArg;
import de.chojo.shepard.util.ArrayUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SetPrefix extends Command {

    public SetPrefix() {
        super("setPrefix", "Changes the prefix for the Server",
                ArrayUtil.array(new CommandArg("Prefix", "The Prefix. Only one Char is allowed", true)));
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent receivedEvent) {
        if (args[1].length() != 1) {
            Messages.sendSimpleError("Only one Char is allowd as prefix.", receivedEvent.getChannel());
            return true;
        }
        Settings.setPrefix(receivedEvent.getGuild(), args[1].charAt(0));
        Messages.sendMessage("Changed prefix to '" + args[1] + "'", receivedEvent.getChannel());
        return true;
    }
}
