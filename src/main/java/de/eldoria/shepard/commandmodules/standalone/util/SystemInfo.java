package de.eldoria.shepard.commandmodules.standalone.util;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.modulebuilder.requirements.ReqShardManager;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.sharding.ShardManager;

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
public class SystemInfo extends Command implements ExecutableAsync, ReqShardManager {
    private ShardManager shardManager;

    /**
     * Creates a new system info command object.
     */
    public SystemInfo() {
        super("systemInfo",
                new String[] {"system"},
                DESCRIPTION.tag,
                CommandCategory.FUN);
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        Runtime runtime = Runtime.getRuntime();

        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(messageContext)
                .setTitle(M_TITLE.tag)
                .addField("CPU",
                        M_AVAILABLE_CORES.tag + " " + runtime.availableProcessors(),
                        true)
                .addField(M_MEMORY.tag, M_USED_MEMORY.tag
                        + runtime.totalMemory() / 1000000 + "MB/"
                        + runtime.maxMemory() / 1000000 + "MB", false);
        long guildSize = shardManager.getGuildCache().size();
        long userSize = shardManager.getUserCache().size();
        builder.addField(M_SERVICE_INFO.tag,
                localizeAllAndReplace(M_SERVICE_INFO_MESSAGE.tag, messageContext.getGuild(),
                        guildSize + "", userSize + ""), false);
        messageContext.getChannel().sendMessage(builder.build()).queue();
        category = CommandCategory.UTIL;
    }

    @Override
    public void addShardManager(ShardManager shardManager) {
        this.shardManager = shardManager;
    }
}
