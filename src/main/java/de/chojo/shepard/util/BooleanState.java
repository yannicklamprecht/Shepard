package de.chojo.shepard.util;

public enum BooleanState {
    /**
     * State for a true boolean.
     */
    TRUE {
        /**
         * returns the value as boolean.
         * @return true
         */
        public boolean getAsBoolean() {
            return true;
        }
    },
    /**
     * State for a false boolean.
     */
    FALSE {
        /**
         * returns the value as boolean.
         * @return false
         */
        public boolean getAsBoolean() {
            return false;
        }
    },
    /**
     * State for a undefined or not valid boolean.
     */
    UNDEFINED

}
