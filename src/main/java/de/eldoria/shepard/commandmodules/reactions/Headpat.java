package de.eldoria.shepard.commandmodules.reactions;

public class Headpat extends Reaction {

    public Headpat() {
        super("headpat",
                new String[] {"pat"});
    }

    @Override
    protected String[] getImages() {
        return getREACTIONS().getPat();
    }

}
