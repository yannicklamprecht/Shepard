package de.eldoria.shepard.commandmodules.reactions;

public class Kiss extends Reaction {

    public Kiss() {
        super("kiss",
                null);
    }

    @Override
    protected String[] getImages() {
        return getREACTIONS().getKiss();
    }

}
