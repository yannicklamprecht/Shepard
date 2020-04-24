package de.eldoria.shepard.commandmodules.commandsettings;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.commandmodules.commandsettings.commands.CommandInfo;
import de.eldoria.shepard.commandmodules.commandsettings.commands.ManageCommand;
import de.eldoria.shepard.commandmodules.commandsettings.commands.ManageCommandGuild;
import de.eldoria.shepard.commandmodules.commandsettings.commands.ManageCommandUsers;
import de.eldoria.shepard.commandmodules.commandsettings.commands.Permission;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class CommandSettingsModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(new CommandInfo(), resources);
        addAndInit(new ManageCommand(), resources);
        addAndInit(new ManageCommandGuild(), resources);
        addAndInit(new ManageCommandUsers(), resources);
        addAndInit(new Permission(), resources);
    }
}
