package de.eldoria.shepard.commandmodules.reminder;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.reminder.data.ReminderData;
import de.eldoria.shepard.commandmodules.reminder.types.ReminderSimple;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;

import javax.sql.DataSource;
import java.util.List;
import java.util.OptionalInt;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.WordsLocale.ID;
import static de.eldoria.shepard.localization.enums.WordsLocale.MESSAGE;
import static de.eldoria.shepard.localization.enums.WordsLocale.TIME;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.AD_ID;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_ID;
import static de.eldoria.shepard.localization.enums.commands.GeneralLocale.A_MESSAGE;
import static de.eldoria.shepard.localization.enums.commands.util.ReminderLocal.AD_TIMESTAMP;
import static de.eldoria.shepard.localization.enums.commands.util.ReminderLocal.A_TIMESTAMP;
import static de.eldoria.shepard.localization.enums.commands.util.ReminderLocal.C_ADD;
import static de.eldoria.shepard.localization.enums.commands.util.ReminderLocal.C_LIST;
import static de.eldoria.shepard.localization.enums.commands.util.ReminderLocal.C_DELETE;
import static de.eldoria.shepard.localization.enums.commands.util.ReminderLocal.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.util.ReminderLocal.M_CURRENT_REMINDERS;
import static de.eldoria.shepard.localization.enums.commands.util.ReminderLocal.M_REMIND_DATE;
import static de.eldoria.shepard.localization.enums.commands.util.ReminderLocal.M_REMIND_TIME;
import static de.eldoria.shepard.localization.enums.commands.util.ReminderLocal.M_REMOVED;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;

/**
 * Reminder command which reminds user on a specific time and date or at a interval.
 */
public class Reminder extends Command implements Executable, ReqDataSource {
    private static final Pattern INTERVAL = Pattern.compile("([0-9])+\\s(((min|hour|day|week)s?)|month)",
            Pattern.MULTILINE);
    private static final Pattern DATE = Pattern.compile("[0-9]{1,2}\\.[0-9]{1,2}\\.\\s[0-9]{1,2}:[0-9]{1,2}",
            Pattern.MULTILINE);

    private ReminderData reminderData;

    /**
     * Creates a new reminder command object.
     */
    public Reminder() {
        super("remind",
                new String[] {"reminder", "timer"},
                DESCRIPTION.tag,
                SubCommand.builder("remind")
                        .addSubcommand(C_ADD.tag,
                                Parameter.createInput(A_TIMESTAMP.tag, AD_TIMESTAMP.tag, true),
                                Parameter.createInput(A_MESSAGE.tag, null, true))
                        .addSubcommand(C_DELETE.tag,
                                Parameter.createCommand("delete"),
                                Parameter.createInput(A_ID.tag, AD_ID.tag, true))
                        .addSubcommand(C_LIST.tag,
                                Parameter.createCommand("list"))
                        .build(),
                CommandCategory.UTIL);
    }

    @Override
    public void execute(String label, String[] args, MessageEventDataWrapper messageContext) {
        String cmd = args[0];

        if (isSubCommand(cmd, 2)) {
            list(messageContext);
            return;
        }

        if (args.length < 2) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getTextChannel());
            return;
        }

        if (isSubCommand(cmd, 1)) {
            remove(args, messageContext);
            return;
        }


        if (args.length < 4) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, messageContext.getTextChannel());
            return;
        }

        add(args, messageContext);
    }

    private void remove(String[] args, MessageEventDataWrapper messageContext) {
        OptionalInt number = ArgumentParser.parseInt(args[1]);
        if (number.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, messageContext.getTextChannel());
            return;
        }

        List<ReminderSimple> userReminder = reminderData.getUserReminder(messageContext.getGuild(),
                messageContext.getAuthor(), messageContext);
        if (number.getAsInt() > userReminder.size()) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ID, messageContext.getTextChannel());
            return;
        }

        ReminderSimple reminder = userReminder.stream().filter(reminderSimple ->
                reminderSimple.getReminderId() == number.getAsInt()).collect(Collectors.toList()).get(0);

        if (reminderData.removeUserReminder(messageContext.getGuild(), messageContext.getAuthor(),
                number.getAsInt(), messageContext)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_REMOVED.tag,
                    messageContext.getGuild(), reminder.getReminderId() + "",
                    TextFormatting.cropText(reminder.getText(), "...", 20, true),
                    reminder.getTime()), messageContext.getTextChannel());
        }
    }

    private void list(MessageEventDataWrapper messageContext) {
        List<ReminderSimple> reminders = reminderData.getUserReminder(messageContext.getGuild(),
                messageContext.getAuthor(), messageContext);
        TextFormatting.TableBuilder tableBuilder = TextFormatting.getTableBuilder(
                reminders,
                TextLocalizer.localizeAll(ID.tag, messageContext.getGuild()),
                TextLocalizer.localizeAll(MESSAGE.tag, messageContext.getGuild()),
                TextLocalizer.localizeAll(TIME.tag, messageContext.getGuild()));
        for (ReminderSimple reminder : reminders) {
            tableBuilder.next();
            tableBuilder.setRow(
                    reminder.getReminderId() + "",
                    TextFormatting.cropText(reminder.getText(), "...", 30, true),
                    reminder.getTime());
        }

        MessageSender.sendMessage(M_CURRENT_REMINDERS + System.lineSeparator() + tableBuilder,
                messageContext.getTextChannel());
    }

    private void add(String[] args, MessageEventDataWrapper messageContext) {
        String timeString = ArgumentParser.getMessage(args, 0, 2);
        if (!DATE.matcher(timeString).find() && !INTERVAL.matcher(timeString).find()) {
            MessageSender.sendSimpleError(ErrorType.INVALID_TIME, messageContext.getTextChannel());
            return;
        }

        String message = ArgumentParser.getMessage(args, 2, 0);
        // Date reminder
        if (DATE.matcher(timeString).find()) {
            String date = args[1];
            String time = args[2];

            if (reminderData.addReminderDate(messageContext.getGuild(), messageContext.getAuthor(),
                    messageContext.getTextChannel(), message, date, time, messageContext)) {
                MessageSender.sendMessage(localizeAllAndReplace(M_REMIND_DATE.tag,
                        messageContext.getGuild(), date, time) + System.lineSeparator() + message,
                        messageContext.getTextChannel());
            }
            return;
        }

        // Interval reminder
        if (reminderData.addReminderInterval(messageContext.getGuild(), messageContext.getAuthor(),
                messageContext.getTextChannel(), message, timeString, messageContext)) {
            MessageSender.sendMessage(localizeAllAndReplace(M_REMIND_TIME.tag,
                    messageContext.getGuild(), timeString) + System.lineSeparator() + message,
                    messageContext.getTextChannel());
        }
    }

    @Override
    public void addDataSource(DataSource source) {
        reminderData = new ReminderData(source);
    }
}
