package de.eldoria.shepard.commandmodules.quote.commands;

import de.eldoria.shepard.basemodules.commanddispatching.dialogue.Dialog;
import de.eldoria.shepard.basemodules.commanddispatching.dialogue.DialogHandler;
import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.quote.data.QuoteData;
import de.eldoria.shepard.commandmodules.quote.types.QuoteElement;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.admin.ManageQuoteLocale;
import de.eldoria.shepard.localization.util.Format;
import de.eldoria.shepard.localization.util.Replacement;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqDialogHandler;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.util.Verifier;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction;

import javax.sql.DataSource;
import java.util.*;

import static de.eldoria.shepard.localization.enums.commands.admin.ManageQuoteLocale.M_NO_QUOTES;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeByWrapper;
import static java.lang.System.lineSeparator;


/**
 * Command to add, remove, alter and list quotes.
 */
@CommandUsage(EventContext.GUILD)
public class ManageQuote extends QuoteCommand implements ExecutableAsync, ReqDataSource, ReqParser, ReqDialogHandler {

    private QuoteData quoteData;
    private ArgumentParser parser;
    private DialogHandler dialogHandler;

    /**
     * Create a new manage quote command object.
     */
    public ManageQuote() {
        super("manageQuote",
                new String[]{"mq", "manageQuotes"},
                "command.manageQuote.description",
                SubCommand.builder("manageQuotes")
                        .addSubcommand("command.manageQuote.subcommand.add",
                                Parameter.createCommand("create"))
                        .addSubcommand("command.manageQuote.subcommand.add",
                                Parameter.createCommand("add"))
                        .addSubcommand("command.manageQuote.subcommand.alter",
                                Parameter.createCommand("edit"),
                                Parameter.createInput("command.general.argument.id", "command.general.argumentDescription.id", true))
                        .addSubcommand("command.manageQuote.subcommand.remove",
                                Parameter.createCommand("remove"),
                                Parameter.createInput("command.general.argument.id", "command.general.argumentDescription.id", true))
                        .addSubcommand("command.manageQuote.subcommand.list",
                                Parameter.createCommand("list"),
                                Parameter.createInput("command.general.argument.keyword", "command.general.argumentDescription.keyword", false))
                        .addSubcommand("command.manageQuote.subcommand.import",
                                Parameter.createCommand("import"),
                                Parameter.createInput("command.general.argument.user", "command.general.argumentDescription.user", true))
                        .addSubcommand("command.manageQuote.subcommand.setChannel",
                                Parameter.createCommand("setChannel"),
                                Parameter.createInput("command.general.argument.channel", "command.general.argumentDescription.channelMentionOrExecution", true))
                        .build(),
                CommandCategory.ADMIN);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        String cmd = args[0];
        if (isSubCommand(cmd, 0) || isSubCommand(cmd, 1)) {
            create(args, wrapper);
            return;
        }

        if (isSubCommand(cmd, 2)) {
            alter(args, wrapper);
            return;
        }

        if (isSubCommand(cmd, 3)) {
            remove(args, wrapper);
            return;
        }

        if (isSubCommand(cmd, 4)) {
            list(args, wrapper);
            return;
        }
        if (isSubCommand(cmd, 5)) {
            importQuotes(args, wrapper);
            return;
        }
        if (isSubCommand(cmd, 6)) {
            setChannel(args, wrapper);
            return;
        }
    }

    private void setChannel(String[] args, EventWrapper wrapper) {
        if (args.length == 1) {
            quoteData.setQuoteChannel(wrapper.getGuild().get(), wrapper.getTextChannel().get(), wrapper);
            wrapper.getMessageChannel().sendMessage(localizeByWrapper("command.manageQuote.message.setChannel", wrapper,
                    Replacement.create("channel", wrapper.getTextChannel().get().getAsMention()))).queue();
            return;
        }
        if ("none".equalsIgnoreCase(args[1])) {
            if (quoteData.setQuoteChannel(wrapper.getGuild().get(), null, wrapper)) {
                MessageSender.sendLocalized("command.manageQuote.message.removedChannel", wrapper);
            }
            return;
        }
        Optional<TextChannel> textChannel = ArgumentParser.getTextChannel(wrapper.getGuild().get(), args[1]);
        if (textChannel.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.INVALID_CHANNEL, wrapper);
            return;
        }
        if (quoteData.setQuoteChannel(wrapper.getGuild().get(), textChannel.get(), wrapper)) {
            wrapper.getMessageChannel().sendMessage(localizeByWrapper("command.manageQuote.message.setChannel", wrapper,
                    Replacement.create("channel", textChannel.get().getAsMention()))).queue();
        }
    }

    private void importQuotes(String[] args, EventWrapper wrapper) {
        User user = parser.getUser(args[1]);
        if (user == null) {
            MessageSender.sendSimpleError(ErrorType.INVALID_USER, wrapper);
            return;
        }

        PaginationAction.PaginationIterator<Message> iterator = wrapper.getMessageChannel().getIterableHistory().iterator();
        List<String> messages = new ArrayList<>();
        int total = 0;
        int i = 0;
        while (iterator.hasNext() && i < 1000 && total < 5000) {
            total++;
            Message next = iterator.next();
            if (!Verifier.equalSnowflake(user, next.getAuthor())) continue;
            if (Verifier.equalSnowflake(next, wrapper.getMessage().get())) continue;
            messages.add(next.getContentRaw());
            i++;
        }
        for (String message : messages) {
            quoteData.addQuote(wrapper.getGuild().get(), message, null, wrapper);
        }

        MessageSender.sendMessage(localizeAllAndReplace(ManageQuoteLocale.M_IMPORTED.tag, wrapper,
                "**" + messages.size() + "**"), wrapper.getMessageChannel());
    }

    private void alter(String[] args, EventWrapper messageContext) {
        if (args.length < 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext);
        }

        int quoteId = verifyId(args[1], messageContext);

        if (quoteId == -1) {
            return;
        }

        dialogHandler.startDialog(messageContext, "command.manageQuote.dialog.edit.start",
                new Dialog() {
                    private String quote = null;
                    private String source = null;

                    @Override
                    public boolean invoke(EventWrapper wrapper, Message message) {
                        String content = message.getContentRaw();
                        if (quote == null) {
                            if ("skip".equalsIgnoreCase(content)) {
                                quote = "";
                            }
                            MessageSender.sendLocalized("command.manageQuote.dialog.edit.source", messageContext);
                            return false;
                        }

                        if (!"skip".equalsIgnoreCase(content)) {
                            source = content;
                        }
                        quoteData.alterQuote(wrapper.getGuild().get(), quoteId, quote.isBlank() ? null : quote, source, messageContext);
                        QuoteElement quote = quoteData.getQuote(wrapper.getGuild().get(), quoteId, wrapper);
                        sendQuote(messageContext.getMessageChannel(), quote);
                        return true;
                    }
                });
    }

    @SneakyThrows
    private void list(String[] args, EventWrapper messageContext) {
        List<QuoteElement> quotes;
        if (args.length > 1) {
            quotes = quoteData.getQuotesByKeyword(messageContext.getGuild().get(),
                    String.join(" ", Arrays.copyOfRange(args, 1, args.length)), messageContext);

        } else {
            quotes = quoteData.getQuotes(messageContext.getGuild().get(), messageContext);
        }

        if (quotes.isEmpty()) {
            MessageSender.sendMessage(M_NO_QUOTES.tag, messageContext.getMessageChannel());
        }
        for (QuoteElement quote : quotes) {
            sendQuote(messageContext.getMessageChannel(), quote);
            // wait a bit
            Thread.sleep(1000);
        }
    }

    private void remove(String[] args, EventWrapper messageContext) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, messageContext);
        }

        int quoteId = verifyId(args[1], messageContext);

        if (quoteId == -1) {
            return;
        }

        if (quoteData.removeQuote(messageContext.getGuild().get(), quoteId, messageContext)) {
            MessageSender.sendMessage(localizeByWrapper("command.manageQuote.message.removedQuote",
                    messageContext, Replacement.create("ID", quoteId, Format.BOLD)),
                    messageContext.getMessageChannel());
        }
    }

    private void create(String[] args, EventWrapper event) {

        dialogHandler.startDialog(event, "command.manageQuote.dialog.create.start",
                new Dialog() {
                    private String quote = null;

                    @Override
                    public boolean invoke(EventWrapper wrapper, Message message) {
                        String content = message.getContentRaw();
                        if (quote == null) {
                            quote = content;
                            MessageSender.sendLocalized("command.manageQuote.dialog.create.author", wrapper);
                            return false;
                        }
                        int i = quoteData.addQuote(wrapper.getGuild().get(), quote, message.getContentRaw(), wrapper);
                        if (i == -1) return true;

                        QuoteElement quote = quoteData.getQuote(i, wrapper);
                        if (quote == null) return true;
                        sendQuote(wrapper.getMessageChannel(), quote);

                        long quoteChannel = quoteData.getQuoteChannel(wrapper.getGuild().get(), null);
                        TextChannel textChannelById = wrapper.getGuild().get().getTextChannelById(quoteChannel);
                        if (textChannelById != null) {
                            sendQuote(textChannelById, quote);
                        }
                        return true;
                    }
                });
    }

    /**
     * Returns the id from a string.
     *
     * @param number         string to parse
     * @param messageContext message context for error logging
     * @return -1 when the string is not a number or the number is <0 or larger than the amount of quotes.
     */
    private int verifyId(String number, EventWrapper messageContext) {
        int quotesCount = quoteData.getQuotesCount(messageContext.getGuild().get(), messageContext);
        OptionalInt quoteId = ArgumentParser.parseInt(number);
        if (quoteId.isEmpty()) {
            MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, messageContext);
            return -1;
        }

        if (quoteId.getAsInt() > quotesCount || quoteId.getAsInt() < 0) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ID, messageContext);
            return -1;
        }
        return quoteId.getAsInt();
    }

    @Override
    public void addDataSource(DataSource source) {
        quoteData = new QuoteData(source);
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }

    @Override
    public void addDialogHandler(DialogHandler dialogHandler) {
        this.dialogHandler = dialogHandler;
    }
}
