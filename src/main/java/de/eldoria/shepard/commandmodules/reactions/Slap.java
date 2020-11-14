package de.eldoria.shepard.commandmodules.reactions;

public class Slap extends Reaction {

    public Slap() {
        super("slap",
                null);
    }

    @Override
    protected String[] getImages() {
        return getREACTIONS().getSlap();
    }

}
