package de.eldoria.shepard.webapi;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;
import de.eldoria.shepard.webapi.botlistreporter.BotListReporter;
import de.eldoria.shepard.webapi.endpoints.BotListEndpoint;
import de.eldoria.shepard.webapi.endpoints.CommandEndpoint;
import de.eldoria.shepard.webapi.endpoints.InfoEndpoint;
import de.eldoria.shepard.webapi.endpoints.KudosEndpoint;
import de.eldoria.shepard.webapi.endpoints.MinecraftLinkEndpoint;
import de.eldoria.shepard.webapi.endpoints.SystemEndpoint;

public class ApiModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(new ApiHandler(), resources);

        BotListReporter botListReporter = new BotListReporter();
        addAndInit(botListReporter, resources);

        addAndInit(resources, new CommandEndpoint(), new BotListEndpoint(botListReporter),
                new MinecraftLinkEndpoint(), new KudosEndpoint(), new InfoEndpoint(), new SystemEndpoint());
    }
}
