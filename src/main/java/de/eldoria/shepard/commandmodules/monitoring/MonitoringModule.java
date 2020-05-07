package de.eldoria.shepard.commandmodules.monitoring;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.commandmodules.monitoring.analyzer.MonitoringScheduler;
import de.eldoria.shepard.commandmodules.monitoring.commands.McPing;
import de.eldoria.shepard.commandmodules.monitoring.commands.Monitoring;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class MonitoringModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(new McPing(), resources);
        addAndInit(new Monitoring(), resources);
        addAndInit(new MonitoringScheduler(), resources);
    }
}
