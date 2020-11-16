package de.eldoria.shepard.commandmodules.reactions;

public class Punish extends Reaction {

    public Punish() {
        super("punish",
                null);
    }

    @Override
    protected String[] getImages() {
        return getREACTIONS().getPunish();
    }
}
