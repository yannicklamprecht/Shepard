package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import static de.eldoria.shepard.localization.enums.commands.util.SystemInfoLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.util.SystemInfoLocale.M_AVAILABLE_CORES;
import static de.eldoria.shepard.localization.enums.commands.util.SystemInfoLocale.M_MEMORY;
import static de.eldoria.shepard.localization.enums.commands.util.SystemInfoLocale.M_SERVICE_INFO;
import static de.eldoria.shepard.localization.enums.commands.util.SystemInfoLocale.M_SERVICE_INFO_MESSAGE;
import static de.eldoria.shepard.localization.enums.commands.util.SystemInfoLocale.M_TITLE;
import static de.eldoria.shepard.localization.enums.commands.util.SystemInfoLocale.M_USED_MEMORY;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

/**
 * Command which provides information about the system the bot is running on.
 */
public class SystemInfo extends Command {
    /**
     * Creates a new system info command object.
     */
    public SystemInfo() {
        super("systemInfo",
                new String[] {"system"},
                DESCRIPTION.tag,
                ContextCategory.FUN);
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        Runtime runtime = Runtime.getRuntime();

        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(messageContext)
                .setTitle(M_TITLE.tag)
                .addField("CPU",
                        M_AVAILABLE_CORES.tag + " " + runtime.availableProcessors(),
                        true)
                .addField(M_MEMORY.tag, M_USED_MEMORY.tag
                        + runtime.totalMemory() / 1000000 + "MB/"
                        + runtime.maxMemory() / 1000000 + "MB", false);
        long guildSize = ShepardBot.getJDA().getGuildCache().size();
        long userSize = ShepardBot.getJDA().getUserCache().size();
        builder.addField(M_SERVICE_INFO.tag,
                localizeAllAndReplace(M_SERVICE_INFO_MESSAGE.tag, messageContext.getGuild(),
                        guildSize + "", userSize + ""), false);
        messageContext.getChannel().sendMessage(builder.build()).queue();
        category = ContextCategory.UTIL;
    }
}
