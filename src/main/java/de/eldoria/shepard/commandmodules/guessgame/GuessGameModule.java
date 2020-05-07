package de.eldoria.shepard.commandmodules.guessgame;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.commandmodules.guessgame.commands.GuessGame;
import de.eldoria.shepard.commandmodules.guessgame.commands.GuessGameConfig;
import de.eldoria.shepard.commandmodules.guessgame.listener.GuessGameImageRegisterListener;
import de.eldoria.shepard.commandmodules.guessgame.util.ImageRegister;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class GuessGameModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        addAndInit(new GuessGame(), resources);

        // Guess Game image register
        ImageRegister register = new ImageRegister();
        addAndInit(register, resources);
        addAndInit(new GuessGameConfig(register), resources);
        addAndInit(new GuessGameImageRegisterListener(register), resources);
    }
}