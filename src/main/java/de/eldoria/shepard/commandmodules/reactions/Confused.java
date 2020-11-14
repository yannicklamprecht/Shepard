package de.eldoria.shepard.commandmodules.reactions;

public class Confused extends Reaction {

    public Confused() {
        super("confused", null);
    }

    @Override
    protected String[] getImages() {
        return getREACTIONS().getConfused();
    }
}
