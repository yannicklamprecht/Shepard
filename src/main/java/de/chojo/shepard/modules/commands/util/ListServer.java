package de.chojo.shepard.modules.commands.util;

import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import de.chojo.shepard.Messages;
import de.chojo.shepard.ShepardBot;
import de.chojo.shepard.modules.commands.Command;
import de.chojo.shepard.util.ListType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.OffsetDateTime;
import java.util.List;

public class ListServer extends Command {
    public ListServer() {
        commandName = "listServer";
        commandDesc = "Lists all Server Shepard is online";
        serverListType = ListType.Whitelist;
        serverCheckEnabled = true;
        listedServer = new String[]{"538084337984208906"};
    }

    @Override
    public boolean execute(String[] args, MessageChannel channel, MessageReceivedEvent receivedEvent) {
        List<Guild> guilds = ShepardBot.getJDA().getGuilds();
        String message = "Auf folgenden Servern stehe ich zur Verfügung (" + guilds.size() + "):\n";
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
            text[i][2] = time.getDayOfMonth() + "." + time.getMonthValue() + "." + time.getYear();
            if (text[i][2].length() > sizeSince) sizeSince = text[i][2].length();
        }

        //Build Message
        String messagepart = "```java\n";
        for (int i = 0; i < guilds.size(); i++) {
            messagepart = messagepart.concat(fillString(text[i][0], sizeName) + " by " + fillString(text[i][1], sizeOwner) + " since " + fillString(text[i][2], sizeSince) + "\n");
        }
        messagepart = messagepart.concat("```");

        Messages.sendMessage(message.concat(messagepart), channel);
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
