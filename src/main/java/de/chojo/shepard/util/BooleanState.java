package de.chojo.shepard.util;

public enum BooleanState {
    TRUE {
        public boolean getAsBoolean() {
            return true;
        }
    },
    FALSE {
        public boolean getAsBoolean() {
            return false;
        }
    },
    UNDEFINED

}
