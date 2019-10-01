package de.eldoria.shepard.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

    /**
     * Creates a formatted table from a two dimensional array.
     *
     * @param tableAsArray the table as array.
     * @return String.
     */
    public static String getAsTable(String[][] tableAsArray) {
        return getAsTable(tableAsArray, 1);
    }

    /**
     * Creates a formatted table from a two dimensional array.
     *
     * @param tableAsArray the table as array.
     * @param padding      padding between columns.
     * @return String.
     */
    public static String getAsTable(String[][] tableAsArray, int padding) {
        int[] length = new int[tableAsArray[0].length];
        for (int i = 0; i < tableAsArray.length; i++) {
            int max = 0;
            for (int j = 0; j < tableAsArray[i].length; j++) {
                max = Math.max(max, tableAsArray[i][j].length());
            }
            length[i] = max;
        }


        for (int i = 0; i < tableAsArray.length; i++) {
            for (int j = 0; j < tableAsArray[i].length; j++) {
                tableAsArray[i][j] = fillString(tableAsArray[i][j], length[i] + padding);
            }
        }

        List<String> rows = new ArrayList<>();

        for (String[] strings : tableAsArray) {
            rows.add(String.join("", strings));
        }

        return String.join(System.lineSeparator(), rows);
    }

    /**
     * Returns a two dimensional string array. the first row are the column names.
     *
     * @param collection  collection for row size
     * @param columnNames names of the columns
     * @return two dimensional string array. Index 0 is used for column names.
     */
    public static String[][] getPreparedStringTable(Collection collection, String... columnNames) {
        String[][] result = new String[collection.size() + 1][columnNames.length];
        result[0] = columnNames;

        return result;
    }

    /**
     * Changes the boolean in to a specified String.
     *
     * @param bool    boolean value
     * @param trueTo  value if true
     * @param falseTo value if false
     * @return bool as string representative.
     */
    public static String mapBooleanTo(boolean bool, String trueTo, String falseTo) {
        return bool ? trueTo : falseTo;
    }
}
