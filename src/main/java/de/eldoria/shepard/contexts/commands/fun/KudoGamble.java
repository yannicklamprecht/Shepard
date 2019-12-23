package de.eldoria.shepard.contexts.commands.fun;

import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.contexts.commands.argument.SubArg;
import de.eldoria.shepard.database.queries.KudoData;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.reactions.Emoji;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.Random;

public class KudoGamble extends Command {
    private Random random = new Random();

    public KudoGamble() {
        commandName = "kudoGamble";
        commandAliases = new String[] {"gamble"};
        commandDesc = "Gamble your Kudos. Challenge your Luck!";
        commandArgs = new CommandArg[] {new CommandArg("amount", true,
                new SubArg("amount", "Amount you want to set."))};
    }

    private final int bonus = 64;
    private final int tier1 = 16;
    private final int tier2 = 4;
    private final int tier3 = 1;
    private final int tier4 = 0;

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        Message message;
        Integer amount = ArgumentParser.parseInt(args[0]);
        if (amount == null) {
            MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, messageContext.getTextChannel());
            return;
        }
        if (!KudoData.tryTakePoints(messageContext.getGuild(), messageContext.getAuthor(), amount, messageContext)) {
            MessageSender.sendSimpleError(ErrorType.NOT_ENOUGH_KUDOS, messageContext.getTextChannel());
            return;
        }
        MessageChannel channel = messageContext.getChannel();
        StringBuilder messageText = new StringBuilder("Starting a gamble for **" + messageContext.getMember().getEffectiveName() +
                "**. May the luck be with you!");
        message = channel.sendMessage(messageText.toString()).complete();

        messageText.append(System.lineSeparator()).append("**GAMBLE START**").append(System.lineSeparator());

        String square = Emoji.BLACK_LARGE_SQUARE.unicode;

        message.editMessage(messageText.toString() + square + " " + square + " " + square).complete();

        int win1 = getValue();
        int win2 = getValue();
        int win3 = getValue();

        String finalMessageText;
        try {
            Thread.sleep(2000);
            message.editMessage(messageText.toString() + getEmoji(win1)
                    + " " + square + " " + square).complete();
            Thread.sleep(2000);
            message.editMessage(messageText.toString() + getEmoji(win1) + " "
                    + getEmoji(win2) + " " + square).complete();
            Thread.sleep(2000);
            finalMessageText = messageText.toString() + getEmoji(win1) + " "
                    + getEmoji(win2) + " " + getEmoji(win3);
            message.editMessage(finalMessageText).complete();
        } catch (InterruptedException e) {
            KudoData.addRubberPoints(messageContext.getGuild(), messageContext.getAuthor(), amount, messageContext);
            return;
        }

        int winAmount = amount;
        int winId = (win1) + (win2) + (win3);
        boolean jackpot = false;


        if (winId == tier4 * 3) {
            winAmount *= 50;
        } else if (winId == tier3 * 3) {
            winAmount *= 10;
        } else if (winId == tier2 * 3) {
            winAmount *= 5;
        } else if (winId == tier1 * 3) {
            winAmount = (int) Math.round(amount * 2.5);
        } else if (winId == bonus * 3) {
            winAmount = amount + KudoData.getAndClearJackpot(messageContext.getGuild(), messageContext);
            jackpot = true;
        }

        //Bonus
        else if (win1 == bonus || win2 == bonus || win3 == bonus) {
            winAmount = (int) Math.round(amount * 1.5);
        }

        //Two are equal
        else if (win1 == win2 || win2 == win3) {
            winAmount = (int) Math.round(amount * evaluatePairs(win2));
        } else if (win1 == win3) {
            winAmount = (int) Math.round(amount * evaluatePairs(win1));
        } else {
            winAmount = 0;
        }

        if (winAmount == 0) {
            int i = KudoData.addAndGetJackpot(messageContext.getGuild(), amount, messageContext);
            message.editMessage(finalMessageText + System.lineSeparator() + "Sad. You don't win anything this time."
                    + System.lineSeparator() + "Jackpot is now on " + i + " Kudos").complete();
            return;
        }

        if (!jackpot) {
            message.editMessage(finalMessageText + System.lineSeparator() + "You win " + winAmount + " Kudos!").complete();
            KudoData.addRubberPoints(messageContext.getGuild(), messageContext.getAuthor(), winAmount, messageContext);
            return;
        }

        message.editMessage(finalMessageText + System.lineSeparator() + "**JACKPOT! YOU WIN " + winAmount + " KUDOS!**").complete();
        KudoData.addRubberPoints(messageContext.getGuild(), messageContext.getAuthor(), winAmount, messageContext);


    }

    private String getEmoji(int i) {
        switch (i) {
            case tier4:
                return Emoji.GEM.unicode;
            case tier3:
                return Emoji.DIAMAOND_SHAPE_WITH_DOT.unicode;
            case tier2:
                return Emoji.MONEY_BAG.unicode;
            case tier1:
                return Emoji.DOLLAR.unicode;
            case bonus:
                return Emoji.TADA.unicode;
        }
        return Emoji.CROSS_MARK.unicode;
    }

    private int getValue() {
        int value = random.nextInt(101);
        //Bonus
        // 2 of 100
        if (value >= 98) {
            return bonus;
        }
        //Tier 1
        //48 of 100
        if (value >= 50) {
            return tier1;
        }

        //Tier 2
        //25 of 100
        if (value >= 25) {
            return tier2;
        }

        //Tier 3
        //15 of 100
        if (value >= 10) {
            return tier3;
        }

        //Tier 4
        //10 of 100
        return tier4;
    }

    private double evaluatePairs(int tier) {
        switch (tier) {
            case bonus:
                return 3;
            case tier1:
                return 1.4;
            case tier2:
                return 1.6;
            case tier3:
                return 1.8;
            case tier4:
                return 2;

        }
        return 0;
    }
}
