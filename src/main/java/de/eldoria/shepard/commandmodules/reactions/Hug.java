package de.eldoria.shepard.commandmodules.reactions;

public class Hug extends Reaction {

    public Hug() {
        super("hug",
                null);
    }

    @Override
    protected String[] getImages() {
        return getREACTIONS().getHug();
    }

}
