package de.eldoria.shepard.commandmodules.ticketsystem;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.commandmodules.ticketsystem.commands.Ticket;
import de.eldoria.shepard.commandmodules.ticketsystem.commands.TicketSettings;
import de.eldoria.shepard.commandmodules.ticketsystem.listener.TicketCleanupListener;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class TicketSystemModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(resources, new Ticket(), new TicketSettings(), new TicketCleanupListener());
    }
}
