package de.eldoria.shepard.commandmodules.quote;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.commandmodules.quote.commands.ManageQuote;
import de.eldoria.shepard.commandmodules.quote.commands.Quote;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class QuoteModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(new ManageQuote(), resources);
        addAndInit(new Quote(), resources);
    }
}
