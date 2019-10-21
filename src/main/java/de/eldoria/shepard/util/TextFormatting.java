package de.eldoria.shepard.util;

import de.eldoria.shepard.database.types.Rank;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.lang.System.lineSeparator;

public final class TextFormatting {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");


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
     * @param from      start index (included). Use negative counts to count from the last index.
     * @param to        end index (excluded). Use negative counts to count from the last index.
     * @return range as string
     */
    public static String getRangeAsString(String delimiter, String[] source, int from, int to) {
        int finalTo = to;
        if (to < 1) {
            finalTo = source.length + to;
        }
        int finalFrom = from;
        if (from < 0) {
            finalFrom = source.length + from;
        }
        return String.join(delimiter, Arrays.copyOfRange(source, finalFrom, finalTo)).trim();
    }

    /**
     * Trims a text to the desired length.
     *
     * @param string      String to trim
     * @param endSequence end sequence which should be append at the end of the string. included in max chars.
     * @param maxChars    max char length.
     * @param keepWords   true if no word should be cut.
     * @return String with length of maxChars of shorter.
     */
    public static String trimText(String string, String endSequence, int maxChars, boolean keepWords) {
        if (!keepWords) {
            String substring = string.substring(0, Math.max(0, maxChars - endSequence.length()));
            return substring + endSequence;
        }
        String[] split = string.split("\\s");
        StringBuilder builder = new StringBuilder();
        for (String s : split) {
            if (builder.length() + s.length() > maxChars + endSequence.length()) {
                builder.append(endSequence);
                return builder.toString();
            }
            builder.append(" ").append(s);
        }
        return builder.toString();
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

    /**
     * Create a new TableBuilder.
     *
     * @param collection  Collection to determine the row size.
     * @param columnNames Determines the name and amount of the columns. Empty column names are possible
     * @return new Table builder object.
     */
    public static TableBuilder getTableBuilder(Collection collection, @NotNull String... columnNames) {
        return new TableBuilder(collection, columnNames);
    }

    public static String getRankTable(List<Rank> ranks) {
        TextFormatting.TableBuilder tableBuilder = TextFormatting.getTableBuilder(ranks, "Rank", "User", "Score");

        int ranking = 1;
        for (Rank rank : ranks) {
            tableBuilder.next();
            tableBuilder.setRow(ranking + "", rank.getUser().getAsTag(), rank.getScore() + "");
            ranking++;
        }
        return tableBuilder.toString();
    }

    /**
     * Get the current time as string.
     *
     * @return time in format:  HH:mm dd.MM.yyyy
     */
    public static String getTimeAsString() {
        LocalDateTime now = LocalDateTime.now();
        return DATE_TIME_FORMATTER.format(now);
    }

    public static class TableBuilder {
        private final String[][] table;
        private String markdown = "";
        private int padding = 1;
        private int rowPointer = 0;

        TableBuilder(Collection collection, String... columnNames) {
            table = new String[collection.size() + 1][columnNames.length];
            table[0] = columnNames;
        }


        /**
         * Set the current row. To go a row forward user next().
         *
         * @param columnEntries Entries for the columns in the current row
         */
        public void setRow(String... columnEntries) {
            if (rowPointer == 0) {
                return;
            }
            if (columnEntries.length <= table[0].length) {
                table[rowPointer] = columnEntries;
            } else {
                table[rowPointer] = Arrays.copyOfRange(columnEntries, 0, table[0].length);
            }
        }

        /**
         * The pointer starts at 0. Row zero can only be set on object creation.
         * use next() before you set the first row.
         *
         * @return true when there is one more row and the pointer moved forward.
         */
        public boolean next() {
            rowPointer++;
            return table.length > rowPointer;
        }

        /**
         * Set the markdown for the table code block.
         *
         * @param markdown Markdown code (i.e. java, md, csharp)
         */
        public void setHighlighting(@NotNull String markdown) {
            this.markdown = markdown;
        }

        /**
         * Set the space between the columns.
         *
         * @param padding number between 1 and 10
         */
        public void setPadding(int padding) {
            this.padding = Math.min(10, Math.max(1, padding));
        }

        /**
         * Returns the formatted table in block code style.
         *
         * @return Table as string.
         */
        @Override
        public String toString() {
            int[] length = new int[table[0].length];
            for (int column = 0; column < length.length; column++) {
                int max = 0;
                for (int row = 0; row < table.length; row++) {
                    max = Math.max(max, table[row][column].length());
                }
                length[column] = max;
            }


            for (int col = 0; col < length.length; col++) {
                for (int row = 0; row < table.length; row++) {
                    table[row][col] = fillString(table[row][col], length[col] + padding);
                }
            }

            List<String> rows = new ArrayList<>();

            for (String[] strings : table) {
                rows.add(String.join("", strings));
            }

            StringBuilder builder = new StringBuilder("```");
            if (!markdown.isBlank()) {
                builder.append(markdown.trim());
            }
            builder.append(lineSeparator())
                    .append(String.join(lineSeparator(), rows))
                    .append(lineSeparator())
                    .append("```");
            return builder.toString();
        }
    }
}
