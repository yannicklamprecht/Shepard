package de.eldoria.shepard.commandmodules.standalone.util;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.core.Statistics;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.modulebuilder.requirements.ReqShardManager;
import de.eldoria.shepard.modulebuilder.requirements.ReqStatistics;
import de.eldoria.shepard.webapi.apiobjects.SystemStatistic;
import de.eldoria.shepard.wrapper.EventWrapper;
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
public class SystemInfo extends Command implements ExecutableAsync, ReqShardManager, ReqStatistics {
    private ShardManager shardManager;
    private Statistics statistics;

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
    public void execute(String label, String[] args, EventWrapper wrapper) {
        SystemStatistic statistic = statistics.getSystemStatistic();

        Runtime runtime = Runtime.getRuntime();


        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(wrapper)
                .setTitle(M_TITLE.tag)
                .addField("CPU",
                        M_AVAILABLE_CORES.tag + " " + runtime.availableProcessors(),
                        true)
                .addField(M_MEMORY.tag,
                        M_USED_MEMORY.tag + " " + statistic.getRamUsed() + "MB", false);

        builder.addField(M_SERVICE_INFO.tag,
                localizeAllAndReplace(M_SERVICE_INFO_MESSAGE.tag, wrapper,
                        statistic.getAggregatedShards().getGuildCount() + "",
                        statistic.getAggregatedShards().getUsercount() + ""), false);
        wrapper.getMessageChannel().sendMessage(builder.build()).queue();
        category = CommandCategory.UTIL;
    }

    @Override
    public void addShardManager(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    @Override
    public void addStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}
