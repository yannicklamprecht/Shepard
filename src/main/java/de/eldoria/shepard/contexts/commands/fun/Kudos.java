package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.CommandArg;
import de.eldoria.shepard.database.DbUtil;
import de.eldoria.shepard.database.queries.KudoData;
import de.eldoria.shepard.database.types.Rank;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Member;

import java.util.List;

import static de.eldoria.shepard.util.Verifier.isArgument;
import static java.lang.System.lineSeparator;

public class Kudos extends Command {
    public Kudos() {
        commandName = "kudos";
        commandAliases = new String[] {"gummipunkte", "rubberpoints"};
        commandDesc = "Give kudos to others, when they do good things. You earn one point every hour.";
        commandArgs = new CommandArg[] {
                new CommandArg("action",
                        "leave empty -> Show your free rubber points and how much you earned!" + lineSeparator()
                                + "**__g__ive** -> Give a user rubber points." + lineSeparator()
                                + "**__t__op** -> Show you the top 25 user on this server." + lineSeparator()
                                + "**__t__op__G__lobal** -> Show you the top 25 user!",
                        false),
                new CommandArg("values",
                        "**__g__ive** -> [user] [points]." + lineSeparator()
                                + "**__t__op** -> leave empty." + lineSeparator()
                                + "**__t__op__G__lobal** -> leave empty.",
                        false)
        };
        category = ContextCategory.FUN;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String pointType = "";
        if (label.equalsIgnoreCase("gummipunkte")) {
            pointType = "Gummipunkte";
        } else if (label.equalsIgnoreCase("kudos")) {
            pointType = "Kudos";
        } else if (label.equalsIgnoreCase("rubberpoints")) {
            pointType = "Rubber Points";
        }

        if (args.length == 0) {
            int freePoints = KudoData.getFreePoints(
                    messageContext.getGuild(), messageContext.getAuthor(), messageContext);
            int userPoints = KudoData.getUserScore(
                    messageContext.getGuild(), messageContext.getAuthor(), messageContext);
            int globalUserPoints = KudoData.getGlobalUserScore(messageContext.getAuthor(), messageContext);

            if (isArgument(label, "rubberpoints", "kudos")) {
                MessageSender.sendMessage(
                        "You have **" + freePoints + "/100** free " + pointType + " to give! (You get 1 "
                                + pointType.substring(0, pointType.length() - 1) + " every hour)" + lineSeparator()
                                + "You have earned **" + userPoints + " " + pointType + "** on this Server!"
                                + lineSeparator()
                                + (userPoints != globalUserPoints
                                ? "You have earned **" + globalUserPoints + " "
                                + pointType + "** on all Servers!" : ""),
                        messageContext.getChannel());
            } else {
                MessageSender.sendMessage(
                        "Du hast **" + freePoints + "/100** " + pointType
                                + " zu vergeben! (Du erhältst jede stunde 1 "
                                + pointType.substring(0, pointType.length() - 1) + ")" + lineSeparator()
                                + "Du hast **" + userPoints + " " + pointType + "** auf diesem Server erhalten!"
                                + lineSeparator()
                                + (userPoints != globalUserPoints
                                ? "Du hast **" + globalUserPoints + " " + pointType
                                + "** insgesamt erhalten!" : ""),
                        messageContext.getChannel());
            }
            return;
        }

        String cmd = args[0];

        if (isArgument(cmd, "top", "t")) {
            sendTopScores(pointType, false, messageContext);

            return;
        }

        if (isArgument(cmd, "topGlobal", "tg")) {
            sendTopScores(pointType, true, messageContext);
            return;
        }

        if (isArgument(cmd, "give", "g")) {
            give(label, args, messageContext);
            return;
        }
        MessageSender.sendSimpleError(ErrorType.INVALID_ACTION, messageContext.getChannel());
    }

    private void give(String label, String[] args, MessageEventDataWrapper messageContext) {
        if (args.length != 3) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getChannel());
        }
        String idRaw = DbUtil.getIdRaw(args[1]);
        if (!Verifier.isValidId(idRaw)) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, messageContext.getChannel());
            return;
        }

        Member memberById = messageContext.getGuild().getMemberById(idRaw);
        if (memberById == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, messageContext.getChannel());
            return;
        }

        if (memberById.getUser().getIdLong() == messageContext.getAuthor().getIdLong()) {
            MessageSender.sendSimpleError(ErrorType.SELF_ASSIGNMENT, messageContext.getChannel());
            return;
        }

        int points;

        try {
            points = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, messageContext.getChannel());
            return;
        }

        if (points <= 0) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext.getTextChannel());
            return;
        }


        if (!KudoData.tryTakePoints(
                messageContext.getGuild(), messageContext.getAuthor(), points, messageContext)) {
            MessageSender.sendSimpleError(ErrorType.NOT_ENOUGH_KUDOS, messageContext.getChannel());
            return;
        }
        if (!KudoData.addRubberPoints(
                messageContext.getGuild(), memberById.getUser(), points, messageContext)) {
            return;
        }
        if (label.equalsIgnoreCase("rubberpoints")) {
            MessageSender.sendMessage(memberById.getAsMention() + " recieved **" + points
                            + "** rubber points from " + messageContext.getAuthor().getAsMention() + "!",
                    messageContext.getChannel());
        }
        if (label.equalsIgnoreCase("kudos")) {
            MessageSender.sendMessage(memberById.getAsMention() + " recieved **" + points
                            + "** Kudos from " + messageContext.getAuthor().getAsMention() + "!",
                    messageContext.getChannel());
        }
        if (label.equalsIgnoreCase("gummipunkte")) {
            MessageSender.sendMessage(memberById.getAsMention() + " erhält **" + points
                            + "** Gummipunkte von " + messageContext.getAuthor().getAsMention() + "!",
                    messageContext.getChannel());
        }
    }

    private void sendTopScores(String pointType, boolean global, MessageEventDataWrapper messageContext) {
        List<Rank> ranks = global
                ? KudoData.getGlobalTopScore(25, messageContext)
                : KudoData.getTopScore(messageContext.getGuild(), 25, messageContext);

        String rankTable = TextFormatting.getRankTable(ranks);

        MessageSender.sendMessage((global ? "**GLOBAL " + pointType.toUpperCase() + " RANKING**"
                        : "**SERVER " + pointType.toUpperCase() + " RANKING**")
                        + lineSeparator() + rankTable,
                messageContext.getChannel());
    }
}
