package de.eldoria.shepard.commandmodules.reactions;

public class Cry extends Reaction {

    public Cry() {
        super("cry",
                new String[]{"sad"});
    }

    @Override
    protected String[] getImages() {
        return getREACTIONS().getCry();
    }
}
