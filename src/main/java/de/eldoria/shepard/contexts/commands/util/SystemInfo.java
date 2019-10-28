package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.EmbedBuilder;

public class SystemInfo extends Command {
    public SystemInfo() {
        commandName = "systemInfo";
        commandAliases = new String[] {"system"};
        commandDesc = "Get the current state of the system of Shepard.";
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        Runtime runtime = Runtime.getRuntime();

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Shepard System Information")
                .addField("CPU",
                        "Available Cores: " + runtime.availableProcessors(),
                        true)
                .addField("Memory", "Used Memory: "
                        + runtime.totalMemory() / 1000000 + "MB/"
                        + runtime.maxMemory() / 1000000 + "MB", false);
        long guildSize = ShepardBot.getJDA().getGuildCache().size();
        long userSize = ShepardBot.getJDA().getUserCache().size();
        builder.addField("Service Info:",
                "Guilds: " + guildSize + System.lineSeparator()
                        + "Users: " + userSize, false);


        messageContext.getChannel().sendMessage(builder.build()).queue();
    }
}
