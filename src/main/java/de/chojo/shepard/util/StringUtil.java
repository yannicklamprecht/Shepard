package de.chojo.shepard.util;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class holds some useful methods for manipulating
 * {@link String}s and other {@link CharSequence}s.
 */
public final class StringUtil {
    private static final Pattern QUOTED_MATCH_PATTERN = Pattern.compile("[^\\s\"']+|\"(?<double>[^\"]*)\"|'(?<single>[^']*)");

    private StringUtil() { }

    /**
     * Split a {@link CharSequence} on whitespaces as long as they aren't quoted with
     * double quotes or single quotes.
     * @param toSplit the {@link CharSequence} to split.
     * @return a {@link String} array containing the split content.
     */
    public static String[] splitQuoted(CharSequence toSplit) {
        Matcher matcher = QUOTED_MATCH_PATTERN.matcher(toSplit);
        List<String> args = new LinkedList<>();
        while (matcher.find()) {
            if (matcher.group("double") != null)
                args.add(matcher.group("double"));
            else if (matcher.group("single") != null)
                args.add(matcher.group("single"));
            else
                args.add(matcher.group());
        }
        return args.toArray(new String[0]);
    }
}
