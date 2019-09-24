package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.database.queries.PrefixData;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.time.Period;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static de.eldoria.shepard.ShepardBot.getJDA;

/**
 * A command to get important information about a user.
 */
public class UserInfo extends Command {

    /**
     * Creates new User info command object.
     */
    public UserInfo() {
        commandName = "UserInfo";
        commandAliases = new String[] {"aboutuser"};
        commandDesc = "Information about a user";
        commandArgs = new CommandArg[] {new CommandArg("user", "Tag, Name or ID of user", true)};
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {

        if (args.length == 0) {

            MessageSender.sendError(
                    new MessageEmbed.Field[] {
                            new MessageEmbed.Field("Too few arguments",
                                    "Usage: " + PrefixData.getPrefix(messageContext.getGuild(),
                                            messageContext) + "userInfo <id, name, tag>", false)},
                    messageContext.getChannel());
        }

        InternUser internUser = new InternUser(args[0], messageContext).invoke();
        User searchedUser = internUser.getSearchedUser();
        String id = internUser.getId();

        if (searchedUser == null) {
            MessageSender.sendMessage("Can't find this user (" + id + ")", messageContext.getChannel());
            return;
        }

        EmbedBuilder builder = new EmbedBuilder()
                .setThumbnail(searchedUser.getAvatarUrl())
                .addField("ID", searchedUser.getId(), true)
                .addField("Nickname", Objects.requireNonNull(messageContext.getGuild()
                        .getMemberById(searchedUser.getId())).getNickname() + "", true)
                .addField("Status", Objects.requireNonNull(messageContext.getGuild()
                        .getMemberById(searchedUser.getId())).getOnlineStatus().toString() + "", true)
                .addField("Minecraft Name", "Not implemented yet", true)
                .addField("Mention", "<@" + searchedUser.getId() + ">", false)
                .setAuthor(searchedUser.getAsTag(), searchedUser.getAvatarUrl(), searchedUser.getAvatarUrl());
        OffsetDateTime time = Objects.requireNonNull(messageContext.getGuild()
                .getMemberById(searchedUser.getId())).getTimeJoined();
        LocalDate date = time.toLocalDate();
        Period period = date.until(LocalDate.now());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("DD.MM.yyyy");
        String formatted = date.format(formatter);
        int years = period.getYears();
        period.minusYears(years);
        int months = period.getMonths();
        period.minusMonths(months);
        int days = period.getDays();
        String year = years != 1 ? " Years, " : " Year, ";
        String day = days != 1 ? " Days" : " Day";
        builder.addField("Joined", years + year + months + " Month " + days + day + "\n(" + formatted + ")", false)
                .setColor(Objects.requireNonNull(messageContext.getGuild()
                        .getMemberById(searchedUser.getId())).getColor());
        List<Role> roles = Objects.requireNonNull(messageContext.getGuild()
                .getMemberById(searchedUser.getId())).getRoles();
        String userRoles = "";
        for (Role role : roles) {
            userRoles = userRoles.concat(role.getName() + ", ");
        }
        userRoles = userRoles.substring(0, userRoles.length() - 2);

        builder.addField("Roles", userRoles, false);

        //builder.addField("Joined", time.)
        messageContext.getChannel().sendMessage(builder.build()).queue();

    }

    private static class InternUser {
        private final String arg;
        private final MessageEventDataWrapper receivedEvent;
        private User searchedUser;
        private String id;

        InternUser(String arg, MessageEventDataWrapper receivedEvent) {
            this.arg = arg;
            this.receivedEvent = receivedEvent;
            this.searchedUser = null;
        }

        User getSearchedUser() {
            return searchedUser;
        }

        String getId() {
            return id;
        }

        InternUser invoke() {
            id = arg.replace("<", "").replace(">", "").replace("@", "").replace("!", "");
            try {
                searchedUser = getJDA().getUserById(id);
            } catch (NumberFormatException e) /*is not a id*/ {
                if (arg.contains("#")) {
                    //Name is Tag
                    List<User> users = getJDA().getUsersByName(id.substring(0, (arg.length() - 5)), true);
                    for (User user : users) {
                        if (user.getAsTag().equalsIgnoreCase(id)) {
                            searchedUser = user;
                            break;
                        }
                    }
                } else {
                    List<User> users = getJDA().getUsersByName(id, true);
                    for (User user : users) {
                        if (user.getName().equalsIgnoreCase(id)) {
                            searchedUser = user;
                            break;
                        }
                    }
                    if (searchedUser == null) {
                        List<Member> members = receivedEvent.getGuild().getMembers();
                        for (Member member : members) {
                            if (Objects.requireNonNull(member.getNickname()).equalsIgnoreCase(id)) {
                                searchedUser = member.getUser();
                                break;
                            }
                        }
                    }
                }

            }
            return this;
        }
    }
}
