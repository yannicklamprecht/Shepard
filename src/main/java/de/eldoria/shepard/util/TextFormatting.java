package de.eldoria.shepard.util;

import java.util.Arrays;

public final class TextFormatting {
    private TextFormatting() {
    }

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

    /**
     * Returns a range of a string array as string.
     *
     * @param delimiter delimiter for string join
     * @param source    source array
     * @param from      start index (included)
     * @param to        end index (excluded)
     * @return range as string
     */
    public static String getRangeAsString(String delimiter, String[] source, int from, int to) {
        return String.join(delimiter, Arrays.copyOfRange(source, from, to));
    }

}
