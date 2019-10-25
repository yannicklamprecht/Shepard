package de.eldoria.shepard.contexts.commands.util;

import de.eldoria.shepard.contexts.ContextCategory;
import de.eldoria.shepard.contexts.commands.ArgumentParser;
import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.contexts.commands.argument.CommandArg;
import de.eldoria.shepard.database.queries.ReminderData;
import de.eldoria.shepard.database.types.ReminderSimple;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static de.eldoria.shepard.util.Verifier.isArgument;
import static java.lang.System.lineSeparator;

public class Reminder extends Command {
    private static final Pattern INTERVAL = Pattern.compile("in\\s([0-9])+\\s(((min|hour|day|week)s?)|month)",
            Pattern.MULTILINE);
    private static final Pattern DATE = Pattern.compile("on\\s[0-9]{1,2}\\.[0-9]{1,2}\\.\\s[0-9]{1,2}:[0-9]{1,2}",
            Pattern.MULTILINE);

    public Reminder() {
        commandName = "remind";
        commandAliases = new String[] {"reminder"};
        commandDesc = "Set a reminder in a specified time.";
        commandArgs = new CommandArg[] {
                new CommandArg("action",
                        "**__a__dd** -> Create a new reminder." + lineSeparator()
                                + "**__r__emove** -> Remove a reminder" + lineSeparator()
                                + "**__s__how** -> Show your reminder.",
                        true),
                new CommandArg("value",
                        "**__a__dd** -> [message] [number] [min|hour|day|week|month]" + lineSeparator()
                                + "**__r__emove** -> [id]" + lineSeparator()
                                + "**__s__how** -> leave empty",
                        false
                )
        };
        category = ContextCategory.UTIL;
    }

    @Override
    protected void internalExecute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];
        if (isArgument(cmd, "show", "s")) {
            show(messageContext);
            return;
        }

        if (args.length < 2) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getTextChannel());
            return;
        }

        if (isArgument(cmd, "remove", "r")) {
            remove(args, messageContext);
            return;
        }


        if (args.length < 4) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getChannel());
            return;
        }

        if (isArgument(cmd, "add", "a")) {
            add(args, messageContext);
        }


    }

    private void remove(String[] args, MessageEventDataWrapper messageContext) {
        Integer number = ArgumentParser.parseInt(args[2]);
        if (number == null) {
            MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, messageContext.getTextChannel());
            return;
        }

        List<ReminderSimple> userReminder = ReminderData.getUserReminder(messageContext.getGuild(),
                messageContext.getAuthor(), messageContext);
        if (number > userReminder.size()) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ID, messageContext.getTextChannel());
            return;
        }

        ReminderSimple reminder = userReminder.stream().filter(reminderSimple ->
                reminderSimple.getReminderId() == number).collect(Collectors.toList()).get(0);

        ReminderData.removeUserReminder(messageContext.getGuild(), messageContext.getAuthor(),
                number, messageContext);

        MessageSender.sendMessage("Removed reminder " + reminder.getReminderId() + ": \""
                        + TextFormatting.cropText(reminder.getText(), "...", 20, true)
                        + System.lineSeparator()
                        + "Which would be send at " + reminder.getTime(),
                messageContext.getTextChannel());
    }

    private void show(MessageEventDataWrapper messageContext) {
        List<ReminderSimple> reminders = ReminderData.getUserReminder(messageContext.getGuild(),
                messageContext.getAuthor(), messageContext);
        TextFormatting.TableBuilder tableBuilder
                = TextFormatting.getTableBuilder(reminders, "ID", "Message", "Time");
        for (ReminderSimple reminder : reminders) {
            tableBuilder.next();
            tableBuilder.setRow(
                    reminder.getReminderId() + "",
                    TextFormatting.cropText(reminder.getText(), "...", 30, true),
                    reminder.getTime());
        }

        MessageSender.sendMessage("Your current reminders: " + System.lineSeparator() + tableBuilder,
                messageContext.getTextChannel());
    }

    private void add(String[] args, MessageEventDataWrapper messageContext) {
        String command = ArgumentParser.getMessage(args, 0, 0);
        if (!DATE.matcher(command).find() && !INTERVAL.matcher(command).find()) {
            MessageSender.sendSimpleError(ErrorType.INVALID_TIME, messageContext.getChannel());
            return;
        }

        if (DATE.matcher(command).find()) {
            String date = args[args.length - 2];
            String time = args[args.length - 1];
            String message = ArgumentParser.getMessage(args, 1, -4);

            if (ReminderData.addReminderDate(messageContext.getGuild(), messageContext.getAuthor(),
                    messageContext.getTextChannel(), message, date, time, messageContext)) {
                MessageSender.sendMessage("I will remind you of " + date + " at " + time
                        + " with the following text:" + System.lineSeparator()
                        + message, messageContext.getTextChannel());
            }
            return;
        }

        String interval = ArgumentParser.getMessage(args, -2);
        String message = ArgumentParser.getMessage(args, 1, -3);

        if (ReminderData.addReminderInterval(messageContext.getGuild(), messageContext.getAuthor(),
                messageContext.getTextChannel(), message, interval, messageContext)) {
            MessageSender.sendMessage("I will remind you in " + interval
                    + " with the following text:" + System.lineSeparator()
                    + message, messageContext.getTextChannel());
        }
    }
}
