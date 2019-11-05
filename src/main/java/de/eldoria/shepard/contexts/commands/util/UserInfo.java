package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.localization.enums.commands.util.UserInfoLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;
import java.time.Period;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.util.TextLocalizer.fastLocaleAndReplace;

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
        commandDesc = UserInfoLocale.DESCRIPTION.tag;
        commandArgs = new CommandArg[] {new CommandArg("user", false,
                new SubArg("user", GeneralLocale.A_USER.tag))
        };
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

        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(messageContext)
                .setThumbnail(user.getAvatarUrl())
                .addField(new LocalizedField(GeneralLocale.A_ID.tag, user.getId(), true, messageContext))
                .addField(new LocalizedField(UserInfoLocale.W_NICKNAME.tag, member != null ? member.getNickname() : "",
                        true, messageContext))
                .addField(new LocalizedField(
                        UserInfoLocale.W_STATUS.tag,
                        member != null
                                ? member.getOnlineStatus().toString().toLowerCase().replace("_", " ")
                                : UserInfoLocale.W_UNKOWN.tag,
                        true,
                        messageContext))
                .addField(new LocalizedField(UserInfoLocale.W_MINECRAFT_NAME.tag, "Not implemented yet",
                        true, messageContext))
                .addField(UserInfoLocale.W_MENTION.tag, user.getAsMention(), false)
                .setAuthor(user.getAsTag(), user.getEffectiveAvatarUrl(), user.getEffectiveAvatarUrl())
                .setColor(member != null ? member.getColor() : Color.gray);

        if (member != null) {
            builder.addField(UserInfoLocale.W_JOINED.tag, getIntervalString(member.getTimeJoined(), messageContext),
                    false);

            String roles = member.getRoles().stream().map(IMentionable::getAsMention).collect(Collectors.joining(", "));
            builder.addField(UserInfoLocale.W_ROLES.tag, roles, false);
        }
        builder.setFooter(UserInfoLocale.M_CREATED + " " + getIntervalString(user.getTimeCreated(), messageContext));

        messageContext.getChannel().sendMessage(builder.build()).queue();
    }

    private String getIntervalString(OffsetDateTime time, MessageEventDataWrapper messageContext) {
        LocalDate date = time.toLocalDate();
        Period period = date.until(LocalDate.now());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formatted = date.format(formatter);
        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();
        String year = years != 1 ? UserInfoLocale.W_YEARS.tag : UserInfoLocale.W_YEAR.tag;
        String day = days != 1 ? UserInfoLocale.W_DAYS.tag : UserInfoLocale.W_DAY.tag;
        return fastLocaleAndReplace(UserInfoLocale.M_JOINED.tag, messageContext.getGuild(),
                formatted, years + " " + year, months + "", days + day);
    }
}
