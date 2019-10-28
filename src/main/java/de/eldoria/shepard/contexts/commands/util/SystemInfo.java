package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.localization.enums.commands.util.SystemInfoLocale;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.EmbedBuilder;

public class SystemInfo extends Command {
    public SystemInfo() {
        commandName = "systemInfo";
        commandAliases = new String[] {"system"};
        commandDesc = SystemInfoLocale.DESCRIPTION.tag;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        Runtime runtime = Runtime.getRuntime();

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(SystemInfoLocale.M_TITLE.tag)
                .addField("CPU",
                        SystemInfoLocale.M_AVAILABLE_CORES.tag + " " + runtime.availableProcessors(),
                        true)
                .addField(SystemInfoLocale.M_MEMORY.tag, SystemInfoLocale.M_USED_MEMORY.tag
                        + runtime.totalMemory() / 1000000 + "MB/"
                        + runtime.maxMemory() / 1000000 + "MB", false);
        long guildSize = ShepardBot.getJDA().getGuildCache().size();
        long userSize = ShepardBot.getJDA().getUserCache().size();
        builder.addField(SystemInfoLocale.M_SERVICE_INFO.tag,
                SystemInfoLocale.M_SERVERS + " " + guildSize + System.lineSeparator()
                        + SystemInfoLocale.M_USERS + " " + userSize, false);


        messageContext.getChannel().sendMessage(builder.build()).queue();
    }
}
