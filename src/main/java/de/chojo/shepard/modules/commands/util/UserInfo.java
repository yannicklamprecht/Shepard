package de.chojo.shepard.modules.commands.util;

import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.Settings;
import de.chojo.shepard.ShepardBot;
import de.chojo.shepard.modules.commands.Command;
import de.chojo.shepard.modules.commands.CommandArg;
import de.chojo.shepard.util.ArrayUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.Period;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static de.chojo.shepard.ShepardBot.getJDA;

/**
 * A command to get important information about a user.
 */
public class UserInfo extends Command {

    public UserInfo() {
        super("UserInfo", ArrayUtil.array("aboutuser"), "Information about a user",
                ArrayUtil.array(new CommandArg("user", "Tag, Name or ID of user", true)));
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent receivedEvent) {

        if (args.length == 1) {
            Messages.sendError(new MessageEmbed.Field[]{new MessageEmbed.Field("Too few arguments", "Usage: " + Settings.getPrefix(receivedEvent.getGuild()) + "userInfo <id, name, tag>", false)}, receivedEvent.getChannel());
        }

        User searchedUser = findUser(args, receivedEvent);

        if (searchedUser == null) {
            Messages.sendMessage("Can't find this user ._.", receivedEvent.getChannel());
            return true;
        }
        String id = searchedUser.getId();

        boolean userIsOnServer = isUserOnServer(receivedEvent, searchedUser);
        String nickname;
        String status;
        if (!userIsOnServer) {
            nickname = "";
            status = "UNKNOWN";
        } else {
            nickname = receivedEvent.getGuild().getMemberById(searchedUser.getId()).getNickname();
            status = receivedEvent.getGuild().getMemberById(searchedUser.getId()).getOnlineStatus().toString();
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setThumbnail(searchedUser.getAvatarUrl())
                .addField("ID", searchedUser.getId(), true)
                .addField("Nickname", nickname + "", true)
                .addField("Status", status + "", true)
                .addField("Minecraft Name", "Not implemented yet", true)
                .addField("Mention", "<@" + searchedUser.getId() + ">", false)
                .setAuthor(searchedUser.getAsTag(), searchedUser.getAvatarUrl(), searchedUser.getAvatarUrl());
        if (userIsOnServer) {
            OffsetDateTime time = receivedEvent.getGuild().getMemberById(searchedUser.getId()).getTimeJoined();
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
                    .setColor(receivedEvent.getGuild().getMemberById(searchedUser.getId()).getColor());
            List<Role> roles = receivedEvent.getGuild().getMemberById(searchedUser.getId()).getRoles();
            String userRoles = "";
            for (Role role : roles) {
                userRoles = userRoles.concat(role.getName() + ", ");
            }
            userRoles = userRoles.substring(0, userRoles.length() - 2);
            builder.addField("Roles", userRoles, false);
        } else {
            builder.addField("Joined", "User is not on this Server", false)
                    .addField("Roles", "User is not on this Server", false)
                    .setColor(Color.gray);


        }


        //builder.addField("Joined", time.)
        receivedEvent.getChannel().sendMessage(builder.build()).queue();

        return true;
    }

    private User findUser(String[] args, MessageReceivedEvent event) {
        User searchedUser = null;

        args[0] = "";

        String arg = String.join(" ", args);
        String id = arg.replace("<", "").replace(">", "").replace("@", "").replace("!", "").replaceAll(" ", "");
        System.out.println("\"" + id + "\"" + " " + arg.length());

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
                List<User> users = ShepardBot.getJDA().getUsersByName(id, true);
                for (User user : users) {
                    if (user.getName().equalsIgnoreCase(id)) {
                        searchedUser = user;
                        break;
                    }
                }
                if (searchedUser == null) {
                    List<Member> members = event.getGuild().getMembers();
                    for (Member member : members) {
                        if (member.getNickname().equalsIgnoreCase(id)) {
                            searchedUser = member.getUser();
                            break;
                        }
                    }
                }
            }

        }
        return searchedUser;
    }

    private boolean isUserOnServer(MessageReceivedEvent event, User user) {
        return event.getGuild().getMember(user) != null;
    }
}
