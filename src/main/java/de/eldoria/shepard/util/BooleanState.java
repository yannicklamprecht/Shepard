package de.eldoria.shepard.util;

public enum BooleanState {
    /**
     * State for a true boolean.
     */
    TRUE(true),
    /**
     * State for a false boolean.
     */
    FALSE(false),
    /**
     * State for a undefined or not valid boolean. False if parsed.
     */
    UNDEFINED(false);

    /**
     * state of the boolean.
     */
    public final boolean stateAsBoolean;

    /**
     * Create a new boolean state.
     * @param state state of the boolean
     */
    BooleanState(boolean state) {
        this.stateAsBoolean = state;
    }

}
