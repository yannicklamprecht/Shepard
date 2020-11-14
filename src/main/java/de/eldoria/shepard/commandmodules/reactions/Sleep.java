package de.eldoria.shepard.commandmodules.reactions;

public class Sleep extends Reaction {

    public Sleep() {
        super("sleep",
                new String[]{"tired"});
    }

    @Override
    protected String[] getImages() {
        return getREACTIONS().getSleep();
    }
}
