package de.eldoria.shepard.util;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.database.DbUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Verifier {
    /**
     * Returns true if the id is a valid id.
     *
     * @param id id to test.
     * @return true if id is valid
     */
    public static boolean isValidId(String id) {
        return DbUtil.getIdRaw(id).length() == 18;
    }

    /**
     * Tries to cast a String to a boolean.
     * Returns null if not possible
     *
     * @param string String to parse
     * @return boolean or null if string can't be parsed
     */
    public static BooleanState checkAndGetBoolean(String string) {
        BooleanState state = BooleanState.UNDEFINED;
        if (string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false")) {
            state = string.equalsIgnoreCase("true") ? BooleanState.TRUE : BooleanState.FALSE;
        }
        return state;
    }

    /**
     * Returns from a list of role ids all valid roles.
     *
     * @param guild guild for role lookup
     * @param args  array of role id
     * @return list of valid roles
     */
    public static List<Role> getValidRoles(Guild guild, List<String> args) {
        return getValidRoles(guild, args.toArray(String[]::new));
    }

    /**
     * Returns from a list of role ids all valid roles.
     *
     * @param guild guild for role lookup
     * @param args  array of role ids
     * @return list of valid roles
     */
    public static List<Role> getValidRoles(Guild guild, String[] args) {
        return Arrays.stream(args).map(roleId -> guild.getRoleById(DbUtil.getIdRaw(roleId)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Returns from a list of text channels ids all valid channels.
     *
     * @param guild guild for role lookup
     * @param args  array of role id
     * @return list of valid roles
     */
    public static List<TextChannel> getValidTextChannels(Guild guild, List<String> args) {
        return getValidTextChannels(guild, args.toArray(String[]::new));
    }

    /**
     * Returns from a list of text channel ids all valid channels.
     *
     * @param guild guild for channel lookup
     * @param args  array of channel ids
     * @return list of valid channels
     */
    public static List<TextChannel> getValidTextChannels(Guild guild, String[] args) {
        return Arrays.stream(args).map(channelId -> guild.getTextChannelById(DbUtil.getIdRaw(channelId)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Returns from a list of text user ids all valid user.
     *
     * @param args array of role id
     * @return list of valid roles
     */
    public static List<User> getValidUserByString(List<String> args) {
        return getValidUserByString(args.toArray(String[]::new));
    }

    /**
     * Returns from a list of user ids all valid user.
     *
     * @param args array of channel ids
     * @return list of valid channels
     */
    public static List<User> getValidUserByString(String[] args) {
        return Arrays.stream(args)
                .map(channelId -> ShepardBot.getJDA().getUserById(DbUtil.getIdRaw(channelId)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<User> getValidUserByLong(List<Long> collect) {
        return collect.stream()
                .map(id -> ShepardBot.getJDA().getUserById(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    /**
     * Returns true if on of the arguments matches the argument. Not case sensitive.
     * @param argument argument to match against.
     * @param args one or more arguments to check.
     * @return true if one argument matches.
     */
    public static boolean isArgument(String argument, String... args) {
        for (String arg : args) {
            if (argument.equalsIgnoreCase(arg)) {
                return true;
            }
        }
        return false;
    }
}
