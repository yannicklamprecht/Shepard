package de.eldoria.shepard.commandmodules.modlog.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.modlog.data.ModLogData;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.localization.enums.commands.moderation.ModLogLocale;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.util.Optional;

@CommandUsage(EventContext.GUILD)
public class ModLog extends Command implements ExecutableAsync, ReqDataSource, ReqInit {
    private ModLogData commandData;
    private DataSource source;

    public ModLog(){
        super("modlog",
                new String[] {"ml"},
                ModLogLocale.DESCRIPTION.tag,
                SubCommand.builder("modlog")
        .addSubcommand(ModLogLocale.ENABLE.tag, Parameter.createCommand("enabled"),
                Parameter.createInput(GeneralLocale.A_CHANNEL.tag, GeneralLocale.AD_CHANNEL_MENTION_OR_EXECUTE.tag, false))
        .addSubcommand(ModLogLocale.DISABLE.tag, Parameter.createCommand("disable"))
        .build(),
        CommandCategory.MODERATION);
    }


    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        String cmd = args[0];

        if(isSubCommand(cmd, 0)){
            enable(wrapper, args);
        }
        if(isSubCommand(cmd, 1)){
            disable(wrapper);
        }
    }

    private void disable(@NotNull EventWrapper wrapper) {
        if(commandData.deleteModChannel(wrapper.getGuild().get().getIdLong(), wrapper)){
            MessageSender.sendMessage(ModLogLocale.SUCCESS_DISABLED.tag, wrapper.getMessageChannel());
        }
    }

    private void enable(EventWrapper wrapper, String @NotNull [] args) {
        TextChannel channel;
        if(args.length > 1) {
            Optional<TextChannel> optChannel = ArgumentParser.getTextChannel(wrapper.getGuild().get(), args[1]);
            if(optChannel.isPresent()){
                channel = optChannel.get();
            }
            else {
                MessageSender.sendSimpleError(ErrorType.INVALID_CHANNEL, wrapper);
                return;
            }
        }
        else {
            channel = wrapper.getTextChannel().get();
        }
        if(commandData.updateOrSetModChannel(wrapper.getGuild().get().getIdLong(), channel.getIdLong(), wrapper)){
            MessageSender.sendMessage(
                    TextLocalizer.localizeAllAndReplace(
                            ModLogLocale.SUCCESS_ENABLED.tag,
                            wrapper,
                            channel.getAsMention()
                    ),
                    wrapper.getMessageChannel());
        }
    }

    @Override
    public void addDataSource(DataSource source) {
        this.source = source;
    }

    @Override
    public void init() {
        this.commandData = new ModLogData(source);
    }
}
