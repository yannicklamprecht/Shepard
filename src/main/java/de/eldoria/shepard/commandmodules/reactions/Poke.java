package de.eldoria.shepard.commandmodules.reactions;

public class Poke extends Reaction {

    public Poke() {
        super("poke", null);
    }

    @Override
    protected String[] getImages() {
        return getREACTIONS().getPoke();
    }
}
