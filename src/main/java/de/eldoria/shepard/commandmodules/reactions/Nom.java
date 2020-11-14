package de.eldoria.shepard.commandmodules.reactions;

public class Nom extends Reaction {

    public Nom() {
        super("nom", new String[]{"eat"});
    }

    @Override
    protected String[] getImages() {
        return getREACTIONS().getEat();
    }
}
