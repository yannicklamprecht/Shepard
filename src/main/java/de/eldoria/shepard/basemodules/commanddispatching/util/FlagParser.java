package de.eldoria.shepard.basemodules.commanddispatching.util;

import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@UtilityClass
public class FlagParser {

    /**
     * Get the value of the flag as string.
     *
     * @param flag flag to parse
     * @param args args to parse
     * @return value of flag or null if flag is not present
     */
    public String getFlagValue(char flag, String[] args) {
        return getFlagValue(flag, args, null);
    }

    /**
     * Get the value of the flag as string.
     *
     * @param flag         flag to parse
     * @param args         args to parse
     * @param defaultValue value to return if the flag is empty
     * @return the value of the flag or the default value if the flag is not present
     */
    @Nonnull
    public String getFlagValue(char flag, String[] args, String defaultValue) {
        List<String> argList = new ArrayList<>();

        // set flag as closed
        boolean open = false;

        // iterate over arguments
        for (String arg : args) {
            if (open) {
                if (arg.length() == 2 && arg.startsWith("-")) {
                    return String.join(" ", argList);
                }
                argList.add(arg);
            } else {
                if (arg.length() != 2 || !arg.startsWith("-" + flag)) continue;
                open = true;
            }
        }
        // if the flag is not open, the flag wasn't present.
        if (!open) {
            return defaultValue;
        }
        String result = String.join(" ", argList);
        return result.isEmpty() ? defaultValue : result;
    }

    /**
     * Get the value of the flag parsed with the function.
     *
     * @param parsingFunction function to parse the value.
     * @param flag            the flag which should be parsed
     * @param args            all arguments which contains the flag.
     * @param <T>             type the function should return.
     * @return a value of type T or null, if the flag was not found or contains no content.
     */
    public <T> T getFlagValue(Function<String, T> parsingFunction, char flag, String[] args) {
        return getFlagValue(parsingFunction, flag, args, null);
    }

    /**
     * Get the value of the flag parsed with the function.
     *
     * @param parsingFunction function to parse the value.
     * @param flag            the flag which should be parsed
     * @param args            all arguments which contains the flag.
     * @param defaultValue    Value which will be parsed if the flag is not present.
     * @param <T>             type the function should return.
     * @return a value of type T or default value, if the flag was not found or contains no content.
     */
    public <T> T getFlagValue(Function<String, T> parsingFunction, char flag, String[] args, String defaultValue) {
        String value = getFlagValue(flag, args, defaultValue);

        return parsingFunction.apply(value);
    }
}
