package de.eldoria.shepard.commandmodules;

import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

@CommandUsage(EventContext.GUILD)
public class MuteSettings extends Command implements ExecutableAsync {

    public MuteSettings() {
        super("muteConfig",
                new String[] {"mConfig"},
                "manage mute settings",
                SubCommand.builder("muteConfig")
                        .addSubcommand("Enable mute module", Parameter.createCommand("enable"))
                        .addSubcommand("Disable mute module", Parameter.createCommand("disable"))
                        .addSubcommand("Set the max duration for a mute", Parameter.createCommand("setMaxDuration"))
                        .addSubcommand("Exclude a channel from muting", Parameter.createCommand("excludeChannel"))
                        .addSubcommand("Include a execluded channel again", Parameter.createCommand("includeChannel"))
                        .addSubcommand("Get a list of excluded channels", Parameter.createCommand("excludedChannelList"))
                        .build(),
                CommandCategory.MODERATION);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        Guild guild = wrapper.getGuild().get();
        String cmd = args[0];
        if (isSubCommand(cmd, 0)) {
            //check if mute is already enabled
            if (false) {
                return;
            }

            Role muted = guild.createRole().setName("muted").setPermissions(0L).complete();


            // save muted role id

            // add muted role to all channels and categories
            for (var channel : guild.getTextChannels()) {
                channel.upsertPermissionOverride(muted).setDeny(Permission.MESSAGE_WRITE).queue();
            }

            for (var category : guild.getCategories()) {
                category.upsertPermissionOverride(muted).setDeny(Permission.MESSAGE_WRITE).queue();
            }
        }
        if (isSubCommand(cmd, 1)) {
            // delete role
            long roleId = 0L;
            Role role = guild.getRoleById(roleId);
            if (role != null) {
                role.delete().queue();
            }

            // mark mute module as inactive
        }
    }
}
