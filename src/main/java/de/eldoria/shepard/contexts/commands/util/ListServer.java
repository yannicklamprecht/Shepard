package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.commands.Command;
import net.dv8tion.jda.api.entities.Guild;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * A command to list all servers the bot is a member of.
 */
public class ListServer extends Command {

    /**
     * Creates a new list server command object.
     */
    public ListServer() {
        commandName = "listServer";
        commandAliases = new String[] {"serverList", "servers", "server"};
        commandDesc = "Lists all Server where Shepard is online";
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        List<Guild> guilds = ShepardBot.getJDA().getGuilds();
        String[][] text = new String[guilds.size()][3];
        int sizeName = 0;
        int sizeOwner = 0;
        int sizeSince = 0;

        for (int i = 0; i < guilds.size(); i++) {
            text[i][0] = guilds.get(i).getName();
            if (text[i][0].length() > sizeName) sizeName = text[i][0].length();

            text[i][1] = Objects.requireNonNull(guilds.get(i).getOwner()).getUser().getAsTag();
            if (text[i][1].length() > sizeOwner) sizeOwner = text[i][1].length();

            OffsetDateTime time = Objects.requireNonNull(guilds.get(i)
                    .getMemberById(ShepardBot.getJDA().getSelfUser().getId())).getTimeJoined();

            LocalDate date = time.toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            String formatted = date.format(formatter);

            text[i][2] = formatted;
            if (text[i][2].length() > sizeSince) sizeSince = text[i][2].length();
        }

        //Build Message
        String messagepart = "```json\n";
        for (int i = 0; i < guilds.size(); i++) {
            messagepart = messagepart.concat("\"" + fillString(text[i][0] + "\"", sizeName + 1)
                    + " by " + fillString(text[i][1], sizeOwner) + " since: "
                    + fillString(text[i][2], sizeSince) + "\n");
        }
        messagepart = messagepart.concat("```");

        String message = "I am currently serving " + guilds.size() + " server:\n";
        MessageSender.sendMessage(message.concat(messagepart), messageContext.getChannel());
    }

    private String fillString(String string, int fill) {
        int charsToFill = fill - string.length();
        return  string + " ".repeat(charsToFill);
    }
}
