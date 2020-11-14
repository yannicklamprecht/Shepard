package de.eldoria.shepard.commandmodules.reactions;

public class Wave extends Reaction {

    public Wave() {
        super("wave",
                new String[]{"greet"});
    }

    @Override
    protected String[] getImages() {
        return getREACTIONS().getWave();
    }
}
