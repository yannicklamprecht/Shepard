package de.chojo.shepard.modules.commands.util;

import de.chojo.shepard.messagehandler.Messages;
import de.chojo.shepard.Settings;
import de.chojo.shepard.ShepardBot;
import de.chojo.shepard.modules.commands.Command;
import de.chojo.shepard.modules.commands.CommandArg;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Period;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static de.chojo.shepard.ShepardBot.getJDA;

public class UserInfo extends Command {

    public UserInfo() {
        commandName = "UserInfo";
        commandAliases = new String[]{"aboutuser"};
        commandDesc = "Information about a user";
        args = new CommandArg[]{new CommandArg("user", "Tag, Name or ID of user", true)};
    }

    @Override
    public boolean execute(String[] args, MessageReceivedEvent receivedEvent) {

        if (args.length == 1) {
            Messages.sendError(new MessageEmbed.Field[]{new MessageEmbed.Field("Too few arguments", "Usage: " + Settings.getPrefix(receivedEvent.getGuild()) + "userInfo <id, name, tag>", false)}, receivedEvent.getChannel());
        }

        InternUser internUser = new InternUser(args[1], receivedEvent).invoke();
        User searchedUser = internUser.getSearchedUser();
        String id = internUser.getId();

        if (searchedUser == null) {
            Messages.sendMessage("Can't find this user (" + id + ")", receivedEvent.getChannel());
            return true;
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setThumbnail(searchedUser.getAvatarUrl());
        builder.addField("ID", searchedUser.getId(), true);
        builder.addField("Nickname", receivedEvent.getGuild().getMemberById(searchedUser.getId()).getNickname() + "", true);
        builder.addField("Status", receivedEvent.getGuild().getMemberById(searchedUser.getId()).getOnlineStatus().toString() + "", true);
        builder.addField("Minecraft Name", "Not implemented yet", true);
        builder.addField("Mention", "<@" + searchedUser.getId() + ">", false);
        builder.setAuthor(searchedUser.getAsTag(), searchedUser.getAvatarUrl(), searchedUser.getAvatarUrl());
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
        builder.addField("Joined", years + year + months + " Month " + days + day + "\n(" + formatted + ")", false);
        builder.setColor(receivedEvent.getGuild().getMemberById(searchedUser.getId()).getColor());
        List<Role> roles = receivedEvent.getGuild().getMemberById(searchedUser.getId()).getRoles();
        String userRoles = "";
        for (Role role : roles) {
            userRoles = userRoles.concat(role.getName() + ", ");
        }
        userRoles = userRoles.substring(0, userRoles.length() - 2);

        builder.addField("Roles", userRoles, false);

        //builder.addField("Joined", time.)
        receivedEvent.getChannel().sendMessage(builder.build()).queue();

        return true;
    }

    private class InternUser {
        private String arg;
        private MessageReceivedEvent receivedEvent;
        private User searchedUser;
        private String id;

        public InternUser(String arg, MessageReceivedEvent receivedEvent) {
            this.arg = arg;
            this.receivedEvent = receivedEvent;
            this.searchedUser = null;
        }

        public User getSearchedUser() {
            return searchedUser;
        }

        public String getId() {
            return id;
        }

        public InternUser invoke() {
            id = arg.replace("<", "").replace(">", "").replace("@", "").replace("!", "");
            try {
                searchedUser = getJDA().getUserById(id);
            } catch (NumberFormatException e) /*is not a id*/ {
                if (arg.contains("#")) {
                    //Name is Tag
                    List<User> users = ShepardBot.getJDA().getUsersByName(id.substring(0, (arg.length() - 5)), true);
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
                    if(searchedUser == null){
                        List<Member> members = receivedEvent.getGuild().getMembers();
                        for (Member member : members) {
                            if (member.getNickname().equalsIgnoreCase(id)) {
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
