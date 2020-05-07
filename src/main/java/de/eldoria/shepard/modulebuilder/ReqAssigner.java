package de.eldoria.shepard.modulebuilder;

import de.eldoria.shepard.basemodules.commanddispatching.CommandHub;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.modulebuilder.requirements.ReqCommands;
import de.eldoria.shepard.modulebuilder.requirements.ReqConfig;
import de.eldoria.shepard.modulebuilder.requirements.ReqCooldownManager;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqExecutionValidator;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqJDA;
import de.eldoria.shepard.modulebuilder.requirements.ReqLatestCommands;
import de.eldoria.shepard.modulebuilder.requirements.ReqNormandy;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.modulebuilder.requirements.ReqPrivateMessages;
import de.eldoria.shepard.modulebuilder.requirements.ReqReactionAction;
import de.eldoria.shepard.modulebuilder.requirements.ReqShepard;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public interface ReqAssigner {
    /**
     * Uses the {@link SharedResources} object to retrieve dependency objects of the provided object.
     * Adds the dependencies based on the requirement interfaces:
     * {@link ReqJDA},{@link ReqCommands},{@link ReqConfig},{@link ReqCooldownManager},{@link ReqExecutionValidator},
     * {@link ReqLatestCommands}, {@link ReqNormandy},{@link ReqParser},{@link ReqPrivateMessages},
     * {@link ReqReactionAction}.
     * After adding the dependencies the {@link ReqInit#init()} is called if the objects implement {@link ReqInit}.
     * If the object is a {@link Command}, the {@link CommandHub} in the
     * resources object is used to register it.
     *
     * @param objects   objects to add dependencies
     * @param resources resources object which holds all required dependencies
     */
    default void addAndInit(SharedResources resources, Object... objects) {
        for (var o : objects) {
            addAndInit(o, resources);
        }
    }

    /**
     * Uses the {@link SharedResources} object to retrieve dependency objects of the provided object.
     * Adds the dependencies based on the requirement interfaces:
     * {@link ReqJDA},{@link ReqCommands},{@link ReqConfig},{@link ReqCooldownManager},{@link ReqExecutionValidator},
     * {@link ReqLatestCommands}, {@link ReqNormandy},{@link ReqParser},{@link ReqPrivateMessages},
     * {@link ReqReactionAction}.
     * After adding the dependencies the {@link ReqInit#init()} is called if the objects implement {@link ReqInit}.
     * If the object is a {@link Command}, the {@link CommandHub} in the
     * resources object is used to register it.
     * If the object is a {@link ListenerAdapter} the {@link net.dv8tion.jda.api.JDA} int the resources object
     * is used to register it.
     *
     * @param object    object to add dependencies
     * @param resources resources object which holds all required dependencies
     */
    default void addAndInit(Object object, SharedResources resources) {
        if (object instanceof ReqJDA) {
            ((ReqJDA) object).addJDA(resources.getJda());
        }
        if (object instanceof ReqConfig) {
            ((ReqConfig) object).addConfig(resources.getConfig());
        }
        if (object instanceof ReqCommands) {
            ((ReqCommands) object).addCommands(resources.getCommandHub());
        }
        if (object instanceof ReqDataSource) {
            ((ReqDataSource) object).addDataSource(resources.getDataSource());
        }
        if (object instanceof ReqCooldownManager) {
            ((ReqCooldownManager) object).addCooldownManager(resources.getCooldownManager());
        }
        if (object instanceof ReqExecutionValidator) {
            ((ReqExecutionValidator) object).addExecutionValidator(resources.getValidator());
        }
        if (object instanceof ReqLatestCommands) {
            ((ReqLatestCommands) object).addLatestCommand(resources.getCollections().getLatestCommands());
        }
        if (object instanceof ReqNormandy) {
            ((ReqNormandy) object).addNormandy(resources.getCollections().getNormandy());
        }
        if (object instanceof ReqParser) {
            ((ReqParser) object).addParser(resources.getParser());
        }
        if (object instanceof ReqPrivateMessages) {
            ((ReqPrivateMessages) object).addPrivateMessages(resources.getCollections().getPrivateMessages());
        }
        if (object instanceof ReqReactionAction) {
            ((ReqReactionAction) object).addReactionAction(resources.getCollections().getReactionActions());
        }
        if (object instanceof ReqShepard) {
            ((ReqShepard) object).addShepard(resources.getShepardBot());
        }
        if (object instanceof ReqInit) {
            ((ReqInit) object).init();
        }
        if (object instanceof Command) {
            resources.getCommandHub().addCommand((Command) object);
        }
        if (object instanceof ListenerAdapter) {
            ListenerAdapter listener = (ListenerAdapter) object;
            resources.addListener(listener);
            resources.getJda().addEventListener(listener);
        }
    }
}
