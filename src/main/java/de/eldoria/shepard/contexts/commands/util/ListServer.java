package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.util.TextFormatting;
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

import static de.eldoria.shepard.util.TextFormatting.fillString;

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

        TextFormatting.TableBuilder tableBuilder = TextFormatting.getTableBuilder(guilds, "Servername", "Serverowner", "Join Date");
        tableBuilder.setHighlighting("json");
        for (Guild guild : guilds) {
            OffsetDateTime time = guild.getMemberById(ShepardBot.getJDA().getSelfUser().getId()).getTimeJoined();

            LocalDate date = time.toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            String formatted = date.format(formatter);

            tableBuilder.next();
            tableBuilder.setRow("\"" + guild.getName() + "\"",
                    guild.getOwner().getUser().getAsTag(),
                    formatted);
        }

        String message = "I am currently serving " + guilds.size() + " server:\n";
        MessageSender.sendMessage(message.concat(tableBuilder.toString()), messageContext.getChannel());
    }

}
