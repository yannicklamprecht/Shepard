package de.eldoria.shepard.commandmodules.reactions;

public class Lick extends Reaction {

    public Lick() {
        super("lick",
                null);
    }

    @Override
    protected String[] getImages() {
        return getREACTIONS().getLick();
    }

}
