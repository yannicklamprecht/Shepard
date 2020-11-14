package de.eldoria.shepard.commandmodules.reactions;

public class Shrug extends Reaction {

    public Shrug() {
        super("shrug", null);
    }

    @Override
    protected String[] getImages() {
        return getREACTIONS().getShrug();
    }
}
