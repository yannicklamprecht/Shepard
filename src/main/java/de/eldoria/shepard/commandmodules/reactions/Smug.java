package de.eldoria.shepard.commandmodules.reactions;

public class Smug extends Reaction {

    public Smug() {
        super("smug", null);
    }

    @Override
    protected String[] getImages() {
        return getREACTIONS().getSmug();
    }
}
