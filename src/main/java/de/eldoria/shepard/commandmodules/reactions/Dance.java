package de.eldoria.shepard.commandmodules.reactions;

public class Dance extends Reaction {

    public Dance() {
        super("dance", null);
    }

    @Override
    protected String[] getImages() {
        return getREACTIONS().getDance();
    }
}
