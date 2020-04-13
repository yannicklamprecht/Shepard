package de.eldoria.shepard.contexts.commands;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.collections.KeyWordCollection;
import de.eldoria.shepard.contexts.ContextSensitive;
import de.eldoria.shepard.contexts.keywords.Keyword;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static de.eldoria.shepard.database.DbUtil.getIdRaw;
import static de.eldoria.shepard.util.Verifier.isValidId;

/**
 * Class which has multiple methods to parse a command input.
 */
public final class ArgumentParser {
    private static final Pattern DISCORD_TAG = Pattern.compile(".+?#[0-9]{4}");

    /**
     * Get a user object by id, tag or name.
     *
     * @param guild      guild for lookup
     * @param userString string for lookup
     * @return user object if user is present or null
     */
    public static User getGuildUser(Guild guild, String userString) {
        Member guildMember = getGuildMember(guild, userString);
        if (guildMember != null) {
            return guildMember.getUser();
        }
        return null;
    }

    /**
     * Get a member object from a guild member by id, tag, nickname or effective name.
     *
     * @param guild        guild for lookup
     * @param memberString string for lookup
     * @return member object of member is present or null
     */
    public static Member getGuildMember(Guild guild, String memberString) {
        if (memberString == null) {
            return null;
        }
        //Lookup by id
        Member foundUser = byId(memberString, guild::getMemberById);

        //Lookup by tag
        if (foundUser == null && DISCORD_TAG.matcher(memberString).matches()) {
            foundUser = guild.getMemberByTag(memberString);
        }

        //lookup by nickname
        if (foundUser == null) {
            foundUser = byName(memberString, s -> guild.getMembersByNickname(s, true));
        }

        //lookup by effective name
        if (foundUser == null) {
            foundUser = byName(memberString, s -> guild.getMembersByEffectiveName(s, true));
        }

        //lookup by name
        if (foundUser == null) {
            foundUser = byName(memberString, s -> guild.getMembersByName(s, true));
        }

        if (foundUser == null) {
            foundUser = byNameOnGuild(memberString, guild);
        }

        return foundUser;
    }

    private static Member byNameOnGuild(String memberString, Guild guild) {
        List<Member> collect = guild.getMembers().stream()
                .filter(m -> m.getEffectiveName().toLowerCase()
                        .startsWith(memberString.toLowerCase())).collect(Collectors.toList());
        if (!collect.isEmpty()) {
            return collect.get(0);
        }
        collect = guild.getMembers().stream().filter(m -> m.getUser().getName().toLowerCase()
                .startsWith(memberString.toLowerCase())).collect(Collectors.toList());
        return collect.isEmpty() ? null : collect.get(0);
    }

    /**
     * Searches for a user. First on a guild and after this on all users the bot currently know.
     * Equal to calling {@link #getGuildUser(Guild, String)} and {@link #getUser(String)}.
     *
     * @param userString string for lookup
     * @param guild      guild for lookup
     * @return user object or null if no user is found
     */
    public static User getUserDeepSearch(String userString, Guild guild) {
        User user = getGuildUser(guild, userString);
        if (user == null) {
            user = getUser(userString);
        }
        return user;
    }

    /**
     * Get a user object by id, name or tag.
     *
     * @param userString string for lookup
     * @return user object or null if no user is found
     */
    public static User getUser(String userString) {
        if (userString == null) {
            return null;
        }

        JDA jda = ShepardBot.getJDA();
        User user = byId(userString, jda::getUserById);
        String idRaw = getIdRaw(userString);
        if (isValidId(idRaw)) {
            user = jda.getUserById(idRaw);
        }

        if (user == null && DISCORD_TAG.matcher(userString).matches()) {
            user = jda.getUserByTag(userString);
        }

        if (user == null) {
            user = byName(userString, s -> jda.getUsersByName(s, true));
        }

        if (user == null) {
            Optional<User> first = jda.getUserCache().stream()
                    .filter(cu -> cu.getName().toLowerCase().startsWith(userString.toLowerCase())).findFirst();
            if (first.isPresent()) {
                user = first.get();
            }
        }
        return user;
    }

    /**
     * Get a text channels by a list of ids and/or names.
     *
     * @param guild          guild for lookup
     * @param channelStrings list of ids and/or names
     * @return list of text channels without null objects
     */
    public static List<TextChannel> getTextChannels(Guild guild, Collection<String> channelStrings) {
        return channelStrings.stream().map(s -> getTextChannel(guild, s))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Get a text channels by a list of id or name.
     *
     * @param guild         guild for lookup
     * @param channelString id or name
     * @return text channel or null
     */
    public static TextChannel getTextChannel(Guild guild, String channelString) {
        if (channelString == null) {
            return null;
        }

        TextChannel textChannel = byId(channelString, guild::getTextChannelById);

        if (textChannel == null) {
            textChannel = byName(channelString, s -> guild.getTextChannelsByName(s, true));
        }
        return textChannel;
    }

    /**
     * Get roles from a list of role ids and/or names.
     *
     * @param guild       guild for lookup
     * @param roleStrings list of ids and/or names
     * @return list of roles. without null objects
     */
    public static List<Role> getRoles(Guild guild, Collection<String> roleStrings) {
        return roleStrings.stream().map(roleString -> getRole(guild, roleString))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Get a role from id or name.
     *
     * @param guild      guild for lookup
     * @param roleString id or name of role
     * @return role object or null
     */
    public static Role getRole(Guild guild, String roleString) {
        if (roleString == null) {
            return null;
        }

        Role role = byId(roleString, guild::getRoleById);

        if (role == null) {
            List<Role> roles = guild.getRolesByName(roleString, true);
            if (!roles.isEmpty()) {
                role = roles.get(0);
            }
        }
        return role;
    }

    /**
     * Get user objects from a list of ids, names and/or tags.
     *
     * @param userStrings list of ids, names and/or tags
     * @return a list of user objects. without null
     */
    public static List<User> getUsers(Collection<String> userStrings) {
        return userStrings.stream().map(ArgumentParser::getUser).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Get guild member objects by a list of ids, tags, nickname or effective name.
     *
     * @param guild         guild for lookup
     * @param memberStrings list of member ids, tags, nicknames or effective names
     * @return list of member object without nulls
     */
    public static List<Member> getGuildMembers(Guild guild, Collection<String> memberStrings) {
        return memberStrings.stream().map(memberString -> getGuildMember(guild, memberString))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Get user objects which are valid on the guild by a list of ids, tags, or name.
     *
     * @param guild       guild for lookup
     * @param userStrings list of user ids, names and/or tags
     * @return list of users without null
     */
    public static List<User> getGuildUsers(Guild guild, Collection<String> userStrings) {
        return userStrings.stream().map(memberString -> getGuildUser(guild, memberString))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Get a boolean as a boolean state.
     *
     * @param bool boolean as string.
     * @return boolean state.
     */
    public static BooleanState getBoolean(String bool) {
        return Verifier.checkAndGetBoolean(bool);
    }

    /**
     * Get a boolean as boolean state.
     *
     * @param bool    boolean as string
     * @param isTrue  string value for true. case is ignored
     * @param isFalse string value for false. case is ignored
     * @return boolean state.
     */
    public static BooleanState getBoolean(String bool, String isTrue, String isFalse) {
        if (bool.equalsIgnoreCase(isTrue)) {
            return BooleanState.TRUE;
        }
        if (bool.equalsIgnoreCase(isFalse)) {
            return BooleanState.FALSE;
        }
        return BooleanState.UNDEFINED;
    }

    /**
     * Get the name of the messageContext from a string.
     *
     * @param indicator      for lookup
     * @param messageContext messageContext from command sending for error handling. Can be null.
     * @return Name of the context or null if no messageContext was found
     */
    public static String getContextName(String indicator, MessageEventDataWrapper messageContext) {
        ContextSensitive context = getContext(indicator, messageContext);
        return context == null ? null : context.getClass().getSimpleName();
    }

    /**
     * Get the name of the context from a string.
     *
     * @param indicator for lookup
     * @param context   context from command sending for error handling. Can be null.
     * @return Name of the context or null if no context was found
     */
    public static ContextSensitive getContext(String indicator, MessageEventDataWrapper context) {
        Command command = CommandCollection.getInstance().getCommand(indicator);
        Keyword keyword = KeyWordCollection.getInstance().getKeywordWithContextName(indicator, context);

        return keyword != null ? keyword : command;
    }

    /**
     * Get a array as sublist from 'from' to array.length().
     *
     * @param objects arguments
     * @param from    start index included
     * @param <T>     Type of objects
     * @return sublist
     */
    public static <T> List<T> getRangeAsList(T[] objects, int from) {
        return getRangeAsList(objects, from, 0);
    }

    /**
     * Get a sublist from array from 'from' to 'to'.
     *
     * @param objects list of objects.
     * @param from    start index (included). Use negative counts to count from the last index.
     * @param to      end index (excluded). Use negative counts to count from the last index.
     * @param <T>     Type of objects
     * @return sublist.
     */
    public static <T> List<T> getRangeAsList(T[] objects, int from, int to) {
        return getRangeAsList(Arrays.asList(objects), from, to);
    }

    /**
     * Get a sublist from 'from' to list.size()
     *
     * @param objects list of objects.
     * @param from    start index (included). Use negative counts to count from the last index.
     * @param <T>     Type of objects
     * @return sublist.
     */
    public static <T> List<T> getRangeAsList(List<T> objects, int from) {
        return getRangeAsList(objects, from, 0);
    }

    /**
     * Get a sublist of a list.
     *
     * @param objects list of objects.
     * @param from    start index (included). Use negative counts to count from the last index.
     * @param to      end index (excluded). Use negative counts to count from the last index.
     * @param <T>     Type of objects
     * @return sublist.
     */
    public static <T> List<T> getRangeAsList(List<T> objects, int from, int to) {
        int finalTo = to;
        if (to < 1) {
            finalTo = objects.size() + to;
        }
        int finalFrom = from;
        if (from < 0) {
            finalFrom = objects.size() + from;
        }

        if (finalFrom > finalTo || finalFrom < 0 || finalTo > objects.size()) {
            return Collections.emptyList();
        }

        return objects.subList(finalFrom, finalTo);
    }

    /**
     * Get guilds by ids or names.
     *
     * @param guildStrings list of ids and/or names.
     * @return list of guilds. without null
     */
    public static List<Guild> getGuilds(List<String> guildStrings) {
        return guildStrings.stream().map(ArgumentParser::getGuild)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Get guilds by id or name.
     *
     * @param guildString guild id or name
     * @return guild object or null
     */
    public static Guild getGuild(String guildString) {
        Guild guild = byId(guildString, s -> ShepardBot.getJDA().getGuildById(guildString));

        if (guild == null) {
            List<Guild> guilds = ShepardBot.getJDA().getGuildsByName(guildString, false);
            if (!guilds.isEmpty()) {
                guild = guilds.get(0);
            }
        }

        if (guild == null) {
            Optional<Guild> first = ShepardBot.getJDA().getGuildCache().stream()
                    .filter(g -> g.getName().toLowerCase().startsWith(guildString.toLowerCase())).findFirst();
            if (first.isPresent()) {
                return first.get();
            }
        }
        return guild;
    }

    /**
     * Get a message from string array from 'from' to array.length().
     *
     * @param strings array of strings.
     * @param from    start index (included). Use negative counts to count from the last index.
     * @return array sequence as string
     */
    public static String getMessage(String[] strings, int from) {
        return getMessage(strings, from, 0);
    }

    /**
     * Get a message from string array from 'from' to 'to'.
     *
     * @param strings array of strings.
     * @param from    start index (included). Use negative counts to count from the last index.
     * @param to      end index (excluded). Use negative counts to count from the last index.
     * @return array sequence as string
     */
    public static String getMessage(String[] strings, int from, int to) {
        return TextFormatting.getRangeAsString(" ", strings, from, to);
    }

    /**
     * Parse a string to integer.
     *
     * @param number number as string
     * @return number or null if parse failed
     */
    public static OptionalInt parseInt(String number) {
        try {
            return OptionalInt.of(Integer.parseInt(number));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    /**
     * Parse a string to float.
     *
     * @param number number as string
     * @return number or null if parse failed
     */
    public static OptionalDouble parseFloat(String number) {
        try {
            return OptionalDouble.of(Float.parseFloat(number));
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    /**
     * Parse a string to long.
     *
     * @param number number as string
     * @return number or null if parse failed
     */
    public static OptionalLong parseLong(String number) {
        try {
            return OptionalLong.of(Long.parseLong(number));
        } catch (NumberFormatException e) {
            return OptionalLong.empty();
        }
    }

    private static <T> T byId(String id, Function<String, T> convert) {
        if (isValidId(id)) {
            return convert.apply(getIdRaw(id));
        }
        return null;
    }

    private static <T> T byName(String name, Function<String, List<T>> convert) {
        List<T> nameMatches = convert.apply(name);
        if (nameMatches.isEmpty()) {
            return null;
        }
        return nameMatches.get(0);
    }

    /**
     * Search a user by fuzzy search.
     *
     * @param userString user string to search
     * @return a list of users. if a direct match was found only
     */
    public static List<User> fuzzyGlobalUserSearch(String userString) {
        if (userString == null) {
            return null;
        }

        JDA jda = ShepardBot.getJDA();
        User user = byId(userString, jda::getUserById);
        if (user != null) {
            return Collections.singletonList(user);
        }

        String idRaw = getIdRaw(userString);
        if (isValidId(idRaw)) {
            user = jda.getUserById(idRaw);
            if (user != null) {
                return Collections.singletonList(user);
            }
        }

        if (DISCORD_TAG.matcher(userString).matches()) {
            user = jda.getUserByTag(userString);
            if (user != null) {
                return Collections.singletonList(user);
            }
        }

        return jda.getUserCache().stream()
                .filter(cu -> cu.getName().toLowerCase().contains(userString.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Search a user by fuzzy search on a guild.
     *
     * @param userString user string to search
     * @param guildId    guild if to search
     * @return a list of users. if a direct match was found only 1 user.
     * if guild id is invalid a empty list is returned.
     */

    public static List<User> fuzzyGuildUserSearch(long guildId, String userString) {
        Guild guild = ShepardBot.getJDA().getGuildById(guildId);
        if (guild == null) {
            return Collections.emptyList();
        }
        //Lookup by id
        Member foundUser = byId(userString, guild::getMemberById);
        if (foundUser != null) {
            return Collections.singletonList(foundUser.getUser());
        }

        //Lookup by tag
        if (DISCORD_TAG.matcher(userString).matches()) {
            foundUser = guild.getMemberByTag(userString);
            if (foundUser != null) {
                return Collections.singletonList(foundUser.getUser());
            }
        }

        //lookup by nickname
        foundUser = byName(userString, s -> guild.getMembersByNickname(s, true));
        if (foundUser != null) {
            return Collections.singletonList(foundUser.getUser());
        }

        //lookup by effective name
        foundUser = byName(userString, s -> guild.getMembersByEffectiveName(s, true));
        if (foundUser != null) {
            return Collections.singletonList(foundUser.getUser());
        }

        //lookup by name
        foundUser = byName(userString, s -> guild.getMembersByName(s, true));
        if (foundUser != null) {
            return Collections.singletonList(foundUser.getUser());
        }

        return guild.getMembers().stream()
                .filter(m -> {
                    boolean effectiveNameMatch = m.getEffectiveName().toLowerCase().contains(userString.toLowerCase());
                    boolean nameMatch = m.getUser().getName().toLowerCase().contains(userString.toLowerCase());
                    return effectiveNameMatch || nameMatch;
                }).map(Member::getUser)
                .collect(Collectors.toList());
    }
}
