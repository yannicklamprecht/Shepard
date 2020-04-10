package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArgument;
import de.eldoria.shepard.contexts.commands.argument.SubArgument;
import de.eldoria.shepard.database.queries.commands.KudoData;
import de.eldoria.shepard.database.types.Rank;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Member;

import java.util.List;

import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_EMPTY;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_USER;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.A_POINTS;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.C_EMPTY;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.C_GIVE;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.C_TOP;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.C_TOP_GLOBAL;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.M_DESCRIPTION_EXTENDED;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.M_DESCRIPTION_GENERAL;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.M_GLOBAL_RANKING;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.M_RECEIVED_KUDOS;
import static de.eldoria.shepard.localization.enums.commands.fun.KudosLocale.M_SERVER_RANKING;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;
import static java.lang.System.lineSeparator;

/**
 * Command to give Kudos and see the top Kudos owner.
 */
public class Kudos extends Command {
    /**
     * Create a new kudos command object.
     */
    public Kudos() {
        commandName = "kudo";
        commandAliases = new String[] {"kudos"};
        commandDesc = DESCRIPTION.tag;
        commandArguments = new CommandArgument[] {
                new CommandArgument("action", false,
                        new SubArgument("leave empty", C_EMPTY.tag, false),
                        new SubArgument("give", C_GIVE.tag, true),
                        new SubArgument("top", C_TOP.tag, true),
                        new SubArgument("topGlobal", C_TOP_GLOBAL.tag, true)),
                new CommandArgument("values", false,
                        new SubArgument("give", A_USER + " " + A_POINTS),
                        new SubArgument("top", A_EMPTY.tag),
                        new SubArgument("topGlobal", A_EMPTY.tag))
        };
        category = ContextCategory.FUN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (args.length == 0) {
            int freePoints = KudoData.getFreePoints(
                    messageContext.getGuild(), messageContext.getAuthor(), messageContext);
            int userPoints = KudoData.getUserScore(
                    messageContext.getGuild(), messageContext.getAuthor(), messageContext);
            int globalUserPoints = KudoData.getGlobalUserScore(messageContext.getAuthor(), messageContext);

            String message = localizeAllAndReplace(M_DESCRIPTION_GENERAL.tag, messageContext.getGuild(),
                    "**" + freePoints + "**", "**100**", "1", "**" + userPoints + "**");
            message = userPoints != globalUserPoints
                    ? message + lineSeparator() + localizeAllAndReplace(M_DESCRIPTION_EXTENDED.tag,
                    messageContext.getGuild(), "**" + globalUserPoints + "**")
                    : message;

            MessageSender.sendMessage(message, messageContext.getTextChannel());
            return;
        }

        String cmd = args[0];
        CommandArgument arg = commandArguments[0];

        if (arg.isSubCommand(cmd, 1)) {
            give(args, messageContext);
            return;
        }

        if (arg.isSubCommand(cmd, 2)) {
            sendTopScores(false, messageContext);
            return;
        }

        if (arg.isSubCommand(cmd, 3)) {
            sendTopScores(true, messageContext);
            return;
        }
        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getTextChannel());
    }

    private void give(String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }

        Member member = ArgumentParser.getGuildMember(messageContext.getGuild(), args[1]);

        if (member == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, messageContext.getTextChannel());
            return;
        }

        if (Verifier.equalSnowflake(member.getUser(), messageContext.getAuthor())) {
            MessageSender.sendSimpleError(ErrorType.SELF_ASSIGNMENT, messageContext.getTextChannel());
            return;
        }

        Integer points = ArgumentParser.parseInt(args[2]);

        if (points == null) {
            MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, messageContext.getTextChannel());
            return;
        }

        if (points <= 0) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }


        if (!KudoData.tryTakeCompletePoints(
                messageContext.getGuild(), messageContext.getAuthor(), points, messageContext)) {
            MessageSender.sendSimpleError(ErrorType.NOT_ENOUGH_KUDOS, messageContext.getTextChannel());
            return;
        }
        if (!KudoData.addRubberPoints(
                messageContext.getGuild(), member.getUser(), points, messageContext)) {
            return;
        }
        MessageSender.sendMessage(localizeAllAndReplace(M_RECEIVED_KUDOS.tag, messageContext.getGuild(),
                "**" + member.getEffectiveName() + "**", "**" + points
                        + "**", "**" + messageContext.getMember().getEffectiveName() + "**"),
                messageContext.getTextChannel());
    }

    private void sendTopScores(boolean global, MessageEventDataWrapper messageContext) {
        List<Rank> ranks = global
                ? KudoData.getGlobalTopScore(25, messageContext)
                : KudoData.getTopScore(messageContext.getGuild(), 25, messageContext);

        String rankTable = TextFormatting.getRankTable(ranks, messageContext);

        MessageSender.sendMessage("**" + (global ? M_GLOBAL_RANKING.tag : M_SERVER_RANKING.tag) + "**"
                + lineSeparator() + rankTable, messageContext.getTextChannel());
    }
}
