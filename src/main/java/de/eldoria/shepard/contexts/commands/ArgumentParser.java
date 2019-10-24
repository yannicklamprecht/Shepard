package de.eldoria.shepard.contexts.commands;

import de.eldoria.shepard.ShepardBot;
import de.eldoria.shepard.collections.CommandCollection;
import de.eldoria.shepard.collections.KeyWordCollection;
import de.eldoria.shepard.contexts.keywords.Keyword;
import de.eldoria.shepard.util.BooleanState;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static de.eldoria.shepard.database.DbUtil.getIdRaw;
import static de.eldoria.shepard.util.Verifier.isValidId;

public class ArgumentParser {
    private static final Pattern DISCORD_TAG = Pattern.compile(".+?#[0-9]{4}");

    public static User getGuildUser(Guild guild, String userString) {
        Member guildMember = getGuildMember(guild, userString);
        if (guildMember != null) {
            return guildMember.getUser();
        }
        return null;
    }

    public static Member getGuildMember(Guild guild, String userString) {
        if (userString == null) {
            return null;
        }
        Member foundUser = null;
        if (isValidId(userString)) {
            foundUser = guild.getMemberById(getIdRaw(userString));
        }

        if (foundUser == null && DISCORD_TAG.matcher(userString).matches()) {
            foundUser = guild.getMemberByTag(userString);
        }

        if (foundUser == null) {
            List<Member> membersByName = guild.getMembersByName(userString, true);
            if (!membersByName.isEmpty()) {
                foundUser = membersByName.get(0);
            }
        }

        if (foundUser == null) {
            List<Member> membersByName = guild.getMembersByEffectiveName(userString, true);
            if (!membersByName.isEmpty()) {
                foundUser = membersByName.get(0);
            }
        }
        if (foundUser == null) {
            List<Member> membersByName = guild.getMembersByNickname(userString, true);
            if (!membersByName.isEmpty()) {
                foundUser = membersByName.get(0);
            }
        }
        return foundUser;
    }

    public static User getUser(String userString) {
        if (userString == null) {
            return null;
        }

        User user = null;
        String idRaw = getIdRaw(userString);
        if (isValidId(idRaw)) {
            user = ShepardBot.getJDA().getUserById(idRaw);
        }

        if (user == null && DISCORD_TAG.matcher(userString).matches()) {
            user = ShepardBot.getJDA().getUserByTag(userString);
        }

        if (user == null) {
            List<User> usersByName = ShepardBot.getJDA().getUsersByName(userString, true);
            if (!usersByName.isEmpty()) {
                user = usersByName.get(0);
            }
        }

        if (user == null) {
            Optional<User> first = ShepardBot.getJDA().getUserCache().stream()
                    .filter(cu -> cu.getName().toLowerCase().startsWith(userString.toLowerCase())).findFirst();
            if (first.isPresent()) {
                return first.get();
            }
        }
        return user;
    }

    public static String getMessage(String[] args, int from) {
        return getMessage(args, from, 0);
    }

    public static String getMessage(String[] args, int from, int to) {
        return TextFormatting.getRangeAsString(" ", args, from, to);
    }

    public static Integer parseInt(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static List<TextChannel> getTextChannels(Guild guild, Collection<String> channelString) {
        return channelString.stream().map(s -> getTextChannel(guild, s))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static TextChannel getTextChannel(Guild guild, String channelString) {
        if (channelString == null) {
            return null;
        }

        TextChannel textChannel = null;
        if (isValidId(channelString)) {
            textChannel = guild.getTextChannelById(getIdRaw(channelString));
        }

        if (textChannel == null) {
            List<TextChannel> channels = guild.getTextChannelsByName(channelString, true);
            if (!channels.isEmpty()) {
                textChannel = channels.get(0);
            }
        }
        return textChannel;
    }

    public static Role getRole(Guild guild, String roleString) {
        if (roleString == null) {
            return null;
        }

        Role role = null;
        if (isValidId(roleString)) {
            role = guild.getRoleById(getIdRaw(roleString));
        }

        if (role == null) {
            List<Role> roles = guild.getRolesByName(roleString, true);
            if (!roles.isEmpty()) {
                role = roles.get(0);
            }
        }
        return role;
    }

    public static List<Role> getRoles(Guild guild, Collection<String> roleStrings) {
        return roleStrings.stream().map(roleString -> getRole(guild, roleString))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static List<User> getUsers(Collection<String> userStrings) {
        return userStrings.stream().map(ArgumentParser::getUser).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static List<Member> getGuildMembers(Guild guild, Collection<String> memberStrings) {
        return memberStrings.stream().map(memberString -> getGuildMember(guild, memberString))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static List<User> getGuildUsers(Guild guild, Collection<String> userStrings) {
        return userStrings.stream().map(memberString -> getGuildUser(guild, memberString))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static BooleanState getBoolean(String bool) {
        return Verifier.checkAndGetBoolean(bool);
    }

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
     * Get the name of the context from a string.
     *
     * @param indicator for lookup
     * @param context   context from command sending for error handling. Can be null.
     * @return Name of the context or null if no context was found
     */
    public static String getContextName(String indicator, MessageEventDataWrapper context) {
        Command command = CommandCollection.getInstance().getCommand(indicator);
        Keyword keyword = KeyWordCollection.getInstance().getKeywordWithContextName(indicator, context);

        String contextName = null;
        if (keyword != null) {
            contextName = keyword.getClass().getSimpleName();
        } else if (command != null) {
            contextName = command.getClass().getSimpleName();
        }

        return contextName;
    }

    public static List<String> getRangeAsList(String[] args, int from) {
        return getRangeAsList(args, from, 0);
    }

    public static List<String> getRangeAsList(String[] args, int from, int to) {
        return getRangeAsList(Arrays.asList(args), from, to);
    }

    public static List<String> getRangeAsList(List<String> args, int from) {
        return getRangeAsList(args, from, 0);
    }

    public static List<String> getRangeAsList(List<String> args, int from, int to) {
        int finalTo = to;
        if (to < 1) {
            finalTo = args.size() + to;
        }
        int finalFrom = from;
        if (from < 0) {
            finalFrom = args.size() + from;
        }

        if (finalFrom > finalTo || finalFrom < 0 || finalTo > args.size()) {
            return Collections.emptyList();
        }

        return args.subList(finalFrom, finalTo);
    }

    public static List<Guild> getGuilds(List<String> guildStrings) {
        return guildStrings.stream().map(ArgumentParser::getGuild)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static Guild getGuild(String guildString) {
        Guild guild = null;
        if (Verifier.isValidId(guildString)) {
            guild = ShepardBot.getJDA().getGuildById(guildString);
        }

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
}
