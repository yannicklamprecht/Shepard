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
        for (int column = 0; column < length.length; column++) {
            int max = 0;
            for (int row = 0; row < tableAsArray.length; row++) {
                max = Math.max(max, tableAsArray[row][column].length());
            }
            length[column] = max;
        }


        for (int col = 0; col < length.length; col++) {
            for (int row = 0; row < tableAsArray.length; row++) {
                tableAsArray[row][col] = fillString(tableAsArray[row][col], length[col] + padding);
            }
        }

        List<String> rows = new ArrayList<>();

        for (String[] strings : tableAsArray) {
            rows.add(String.join("", strings));
        }

        return "```" + System.lineSeparator()
                + String.join(System.lineSeparator(), rows)
                + System.lineSeparator() + "```";
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
