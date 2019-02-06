package de.chojo.shepard.util;

/**
 * A class for utility methods for arrays.
 */
public final class ArrayUtil {

    private ArrayUtil() { }

    /**
     * Creates a new array from a varargs array.
     * @param values the values contained in the array.
     * @param <T> the type of the array elements.
     * @return the varargs array as normal array.
     */
    @SafeVarargs
    public static <T> T[] array(T... values) {
        return values;
    }
}
