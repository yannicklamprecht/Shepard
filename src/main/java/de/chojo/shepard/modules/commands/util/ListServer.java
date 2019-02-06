package de.chojo.shepard.modules.commands.util;

import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.ShepardBot;
import de.chojo.shepard.modules.commands.Command;
import de.chojo.shepard.util.ListType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * A command to list all servers the bot is a member of.
 */
public class ListServer extends Command {

    public ListServer() {
        commandName = "listServer";
        commandAliases = new String[]{"serverList", "servers", "server"};
        commandDesc = "Lists all Server where Shepard is online";
        serverListType = ListType.WHITELIST;
        listedServer = new String[]{"538084337984208906"};
        serverCheckEnabled = true;
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent receivedEvent) {
        List<Guild> guilds = ShepardBot.getJDA().getGuilds();
        String message = "I am currently serving " + guilds.size() + " server:\n";
        String[][] text = new String[guilds.size()][3];
        int sizeName = 0;
        int sizeOwner = 0;
        int sizeSince = 0;

        for (int i = 0; i < guilds.size(); i++) {
            text[i][0] = guilds.get(i).getName();
            if (text[i][0].length() > sizeName) sizeName = text[i][0].length();

            text[i][1] = guilds.get(i).getOwner().getUser().getAsTag();
            if (text[i][1].length() > sizeOwner) sizeOwner = text[i][1].length();

            OffsetDateTime time = guilds.get(i).getMemberById("512413049894731780").getTimeJoined();

            LocalDate date = time.toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            String formatted = date.format(formatter);

            text[i][2] = formatted;
            if (text[i][2].length() > sizeSince) sizeSince = text[i][2].length();
        }

        //Build Message
        String messagepart = "```json\n";
        for (int i = 0; i < guilds.size(); i++) {
            messagepart = messagepart.concat("\"" + fillString(text[i][0] + "\"", sizeName + 1) + " by " + fillString(text[i][1], sizeOwner) + " since: " + fillString(text[i][2], sizeSince) + "\n");
        }
        messagepart = messagepart.concat("```");

        Messages.sendMessage(message.concat(messagepart), receivedEvent.getChannel());
        return true;
    }

    private String fillString(String string, int fill) {
        int charsToFill = fill - string.length();
        for (int i = 0; i < charsToFill; i++) {
            string = string.concat(" ");
        }
        return string;
    }
}
