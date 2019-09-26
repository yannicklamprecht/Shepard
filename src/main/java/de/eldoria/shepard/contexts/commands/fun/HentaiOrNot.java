package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

public class HentaiOrNot extends Command {

    public HentaiOrNot() {
        commandName = "hentaiOrNot";
        commandAliases = new String[] {"hentaigame"};
        commandDesc = "Game where you have to guess if a cropped image is part of a hentai image or not.";
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {

    }
}
