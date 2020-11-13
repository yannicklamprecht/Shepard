package de.eldoria.shepard.commandmodules.registerPrefix.command;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.registerPrefix.data.RegisterPrefixData;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.moderation.RegisterPrefixLocale;
import de.eldoria.shepard.localization.util.LocalizedEmbedBuilder;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.util.Colors;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.stream.Collectors;

@CommandUsage(EventContext.GUILD)
public class RegisterPrefix extends Command implements ExecutableAsync, ReqInit, ReqDataSource {

    private DataSource source;
    private RegisterPrefixData commandData;

    public RegisterPrefix(){
        super("externalPrefix",
                new String[] {"ep", "prefix"},
                "command.prefix.subcommand.description",
                SubCommand.builder("externalPrefix")
                        .addSubcommand("command.prefix.subcommand.add", Parameter.createCommand("add"),
                                Parameter.createInput("command.general.argument.message", null, true))
                        .addSubcommand("command.prefix.subcommand.remove", Parameter.createCommand("remove"),
                                Parameter.createInput("command.general.argument.message", null, true))
                        .addSubcommand("command.prefix.subcommand.list", Parameter.createCommand("list"))
                    .build(),
                CommandCategory.MODERATION
        );
    }

    @Override
    public void addDataSource(DataSource source) {
        this.source = source;
    }

    @Override
    public void init() {
        this.commandData = new RegisterPrefixData(source);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        String cmd = args[0];
        if(isSubCommand(cmd, 0)){
            add(args, wrapper);
        }

        if(isSubCommand(cmd, 1)){
            remove(args, wrapper);
        }

        if(isSubCommand(cmd, 2)){
            list(wrapper);
        }
    }

    private void list(EventWrapper wrapper) {
        String[] list = commandData.getList(wrapper.getGuild().get(), wrapper);
        if(list != null){
            LocalizedEmbedBuilder leb = new LocalizedEmbedBuilder();
            leb.setTitle(RegisterPrefixLocale.LIST_TITLE.tag);
            String prefixes = Arrays.stream(list).map(s -> "`" + s + "`").collect(Collectors.joining(","));
            leb.setDescription(prefixes);
            leb.setColor(Colors.Pastel.RED);
            wrapper.getMessageChannel().sendMessage(leb.build()).queue();

        }
    }

    private void remove(String[] args, EventWrapper wrapper) {
        String message = ArgumentParser.getMessage(args, 1);
        if(commandData.removePrefix(wrapper.getGuild().get().getIdLong(), message, wrapper)){
            MessageSender.sendMessage(
                    TextLocalizer.localizeAllAndReplace(
                            RegisterPrefixLocale.SUCCESS_REMOVE.tag,
                            wrapper.getGuild().get(),
                            message),
                    wrapper.getMessageChannel()
            );
        }
    }

    private void add(String[] args, EventWrapper wrapper) {
        String message = ArgumentParser.getMessage(args, 1);
        if(commandData.addPrefix(wrapper.getGuild().get().getIdLong(), message, wrapper)){
            MessageSender.sendMessage(
                    TextLocalizer.localizeAllAndReplace(
                            RegisterPrefixLocale.SUCCESS_ADD.tag,
                            wrapper.getGuild().get(),
                            message),
                    wrapper.getMessageChannel()
            );
        }
    }
}
