package de.eldoria.shepard.commandmodules.standalone.util;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.WordsLocale;
import de.eldoria.shepard.localization.enums.commands.util.UserInfoLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.LocalizedField;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

/**
 * A command to get important information about a user.
 */
@CommandUsage(EventContext.GUILD)
public class UserInfo extends Command implements Executable, ReqParser {

    private ArgumentParser parser;

    /**
     * Creates new User info command object.
     */
    public UserInfo() {
        super("userInfo",
                new String[] {"aboutuser"},
                "command.userInfo.description",
                SubCommand.builder("userInfo")
                        .addSubcommand(null,
                                Parameter.createInput("command.general.argument.user", "command.general.argumentDescription.user", true))
                        .build(),
                "command.userInfo.command.default",
                CommandCategory.UTIL);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        Guild guild = wrapper.getGuild().get();
        User user;
        if (args.length != 0) {
            user = parser.getGuildUser(guild, ArgumentParser.getMessage(args, 0));
            if (user == null) {
                user = parser.getUser(ArgumentParser.getMessage(args, 0));
            }
        } else {
            user = wrapper.getAuthor();
        }

        if (user == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, wrapper);
            return;
        }

        Member member = guild.getMember(user);

        LocalizedEmbedBuilder builder = new LocalizedEmbedBuilder(wrapper)
                .setThumbnail(user.getAvatarUrl())
                .addField(new LocalizedField(WordsLocale.ID.tag, user.getId(), true, wrapper));
        if (member != null) {
            builder.addField(new LocalizedField(UserInfoLocale.W_NICKNAME.tag, member.getNickname() != null
                    ? member.getNickname() : "none",
                    true, wrapper));
        }
        builder.addField(new LocalizedField(
                UserInfoLocale.W_STATUS.tag,
                member != null
                        ? member.getOnlineStatus().toString().toLowerCase().replace("_", " ")
                        : UserInfoLocale.W_UNKOWN.tag,
                true,
                wrapper))
                //.addField(new LocalizedField(UserInfoLocale.W_MINECRAFT_NAME.tag, "Not implemented yet",
                //        true, messageContext))
                .addField(UserInfoLocale.W_MENTION.tag, user.getAsMention(), false)
                .setAuthor(user.getAsTag(), user.getEffectiveAvatarUrl(), user.getEffectiveAvatarUrl())
                .setColor(member != null ? member.getColor() : Color.gray);

        if (member != null) {
            builder.addField(UserInfoLocale.W_JOINED.tag, getIntervalString(UserInfoLocale.M_JOINED.tag,
                    member.getTimeJoined(), wrapper), false);

            String roles = member.getRoles().stream().map(IMentionable::getAsMention).collect(Collectors.joining(", "));
            builder.addField(UserInfoLocale.W_ROLES.tag, roles, false);
        }
        builder.setFooter(getIntervalString(UserInfoLocale.M_CREATED.tag, user.getTimeCreated(), wrapper));

        wrapper.getMessageChannel().sendMessage(builder.build()).queue();
    }

    private String getIntervalString(String messageTag, OffsetDateTime time, EventWrapper wrapper) {
        LocalDate date = time.toLocalDate();
        Period period = date.until(LocalDate.now());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formatted = date.format(formatter);
        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();
        String year = years != 1 ? UserInfoLocale.W_YEARS.tag : UserInfoLocale.W_YEAR.tag;
        String day = days != 1 ? UserInfoLocale.W_DAYS.tag : UserInfoLocale.W_DAY.tag;
        String month = months != 1 ? UserInfoLocale.W_MONTHS.tag : UserInfoLocale.W_MONTH.tag;
        return localizeAllAndReplace(messageTag, wrapper,
                formatted, years + " " + year, months + " " + month, days + " " + day);
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }
}
