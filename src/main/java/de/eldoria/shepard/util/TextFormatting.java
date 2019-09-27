package de.eldoria.shepard.util;

public class TextFormatting {
    /**
     * Appends white spaces to a string to match the given length.
     *
     * @param string String to fill
     * @param fill   Desired String length
     * @return filled string.
     */
    public static String fillString(String string, int fill) {
        int charsToFill = fill - string.length();
        return string + " ".repeat(charsToFill);
    }

}
