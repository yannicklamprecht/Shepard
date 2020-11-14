package de.eldoria.shepard.commandmodules.reactions;

public class Spank extends Reaction {

    public Spank() {
        super("spank",
                null);
    }

    @Override
    protected String[] getImages() {
        return getREACTIONS().getSpank();
    }

}
