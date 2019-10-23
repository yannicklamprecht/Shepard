package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.commands.Command;
import net.dv8tion.jda.api.entities.Guild;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static de.eldoria.shepard.util.TextFormatting.cropText;

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
        category = ContextCategory.UTIL;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        List<Guild> guilds = ShepardBot.getJDA().getGuilds();

        TextFormatting.TableBuilder tableBuilder
                = TextFormatting.getTableBuilder(guilds, "Servername", "Members", "Region");
        tableBuilder.setHighlighting("json");
        for (Guild guild : guilds) {
            OffsetDateTime time = guild.getMemberById(ShepardBot.getJDA().getSelfUser().getId()).getTimeJoined();

            LocalDate date = time.toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            String formatted = date.format(formatter);


            tableBuilder.next();
            tableBuilder.setRow("\"" + cropText(guild.getName(), "...", 15, true) + "\"",
                    guild.getMembers().size() + "",
                    guild.getRegion().getName(),
                    formatted);
        }

        String message = "I am currently serving " + guilds.size() + " server:" + System.lineSeparator();
        messageContext.getChannel().sendMessage(message + tableBuilder).queue();
    }

}
