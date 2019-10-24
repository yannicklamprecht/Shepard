package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;
import java.time.Period;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * A command to get important information about a user.
 */
public class UserInfo extends Command {

    /**
     * Creates new User info command object.
     */
    public UserInfo() {
        commandName = "userInfo";
        commandAliases = new String[] {"aboutuser"};
        commandDesc = "Information about a user";
        commandArgs = new CommandArg[] {new CommandArg("user", "Tag, Name or ID of user", false)};
        category = ContextCategory.UTIL;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        User user;
        if (args.length != 0) {
            user = ArgumentParser.getUser(args[0]);
        } else {
            user = messageContext.getAuthor();
        }

        if (user == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, messageContext.getTextChannel());
            return;
        }

        Member member = messageContext.getGuild().getMember(user);

        EmbedBuilder builder = new EmbedBuilder()
                .setThumbnail(user.getAvatarUrl())
                .addField("ID", user.getId(), true)
                .addField("Nickname", member != null ? member.getNickname() : "", true)
                .addField("Status", member != null ? member.getOnlineStatus().toString()
                        .toLowerCase().replace("_", " ") : "unknown", true)
                .addField("Minecraft Name", "Not implemented yet", true)
                .addField("Mention", user.getAsMention(), false)
                .setAuthor(user.getAsTag(), user.getEffectiveAvatarUrl(), user.getEffectiveAvatarUrl())
                .setColor(member != null ? member.getColor() : Color.gray);

        if (member != null) {
            builder.addField("Joined", getIntervalString(member.getTimeJoined()), false);

            String roles = member.getRoles().stream().map(IMentionable::getAsMention).collect(Collectors.joining(", "));
            builder.addField("Roles", roles, false);
        }
        builder.setFooter("Created at " + getIntervalString(user.getTimeCreated()));

        messageContext.getChannel().sendMessage(builder.build()).queue();
    }

    private String getIntervalString(OffsetDateTime time) {
        LocalDate date = time.toLocalDate();
        Period period = date.until(LocalDate.now());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formatted = date.format(formatter);
        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();
        String year = years != 1 ? " Years, " : " Year, ";
        String day = days != 1 ? " Days" : " Day";
        return formatted + " | " + years + year + months + " Month " + days + day + " ago";
    }
}
