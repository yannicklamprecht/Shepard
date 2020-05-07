package de.eldoria.shepard.commandmodules.privatemessages;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.commandmodules.privatemessages.commands.PrivateAnswer;
import de.eldoria.shepard.commandmodules.privatemessages.commands.SendPrivateMessage;
import de.eldoria.shepard.commandmodules.privatemessages.listener.PrivateMessageListener;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class PrivateMessagesModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(new PrivateAnswer(), resources);
        addAndInit(new SendPrivateMessage(), resources);
        addAndInit(new PrivateMessageListener(), resources);
    }
}
