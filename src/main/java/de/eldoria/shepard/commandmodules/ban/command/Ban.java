package de.eldoria.shepard.commandmodules.ban.command;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.basemodules.commanddispatching.util.FlagParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.ban.data.BanData;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.ExecutableAsync;
import de.eldoria.shepard.commandmodules.modlog.data.MoodLogData;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.localization.enums.commands.moderation.BanLocale;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;

import javax.sql.DataSource;
import java.sql.Timestamp;


@CommandUsage(EventContext.GUILD)
public class Ban extends Command implements ExecutableAsync, ReqInit, ReqDataSource, ReqParser {

    private DataSource source;
    private BanData commandData;
    private ArgumentParser paser;
    private MoodLogData modLogData;

    public Ban(){
        super("ban",
                new String []{"b"},
                BanLocale.DESCRIPTION.tag,
                SubCommand.builder("ban")
                    .addSubcommand(BanLocale.PERMA.tag,
                            Parameter.createCommand("perma"),
                            Parameter.createInput(GeneralLocale.A_USER.tag, GeneralLocale.AD_USER.tag, true),
                            Parameter.createInput(BanLocale.A_PARAMETER_REASON.tag, BanLocale.AD_PARAMETER_REASON.tag, false),
                            Parameter.createInput(BanLocale.A_PARAMETER_PURGE.tag, BanLocale.AD_PARAMETER_PURGE.tag, false)
                            )
                    .addSubcommand(BanLocale.TEMP.tag,
                            Parameter.createCommand("temp"),
                            Parameter.createInput(GeneralLocale.A_USER.tag, GeneralLocale.AD_USER.tag, true),
                            Parameter.createInput(BanLocale.A_PARAMETER_REASON.tag, BanLocale.AD_PARAMETER_REASON.tag, false),
                            Parameter.createInput(BanLocale.A_PARAMETER_PURGE.tag, BanLocale.AD_PARAMETER_PURGE.tag, false),
                            Parameter.createInput(BanLocale.A_PARAMETER_TIME.tag, GeneralLocale.AD_INTERVAL.tag, false)
                            )
                    .addSubcommand(BanLocale.SOFT.tag,
                            Parameter.createCommand("soft"),
                            Parameter.createInput(GeneralLocale.A_USER.tag, GeneralLocale.AD_USER.tag, true),
                            Parameter.createInput(BanLocale.A_PARAMETER_REASON.tag, BanLocale.AD_PARAMETER_REASON.tag, false),
                            Parameter.createInput(BanLocale.A_PARAMETER_PURGE.tag, BanLocale.AD_PARAMETER_PURGE.tag, false)
                            )
                        .build(),
                CommandCategory.MODERATION);
    }

    @Override
    public void addDataSource(DataSource source) {
        this.source = source;
    }

    @Override
    public void init() {
        this.commandData = new BanData(source);
        this.modLogData = new MoodLogData(source);
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        String cmd = args[0];
        String reason = FlagParser.getFlagValue('r', args);
        long channel_id = modLogData.getChannel(wrapper.getGuild().get(), wrapper);
        if(channel_id > 0 ){
            //TODO: Add ModLog
        }
        int purge;
        try {
             purge = FlagParser.getFlagValue(Integer::parseInt, 'p', args, 0);
        }catch (NumberFormatException e){
            MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, wrapper);
            return;
        }

        Member user = paser.getGuildMember(wrapper.getGuild().get(), args[1]);

        if(isSubCommand(cmd, 0)){
            perma(user, purge, reason, wrapper);
        }
        
        if(isSubCommand(cmd, 1)){
            temp(user, purge, reason, wrapper);
        }
        
        if(isSubCommand(cmd, 2)){
            soft(user, purge, reason, wrapper);
        }
    }

    private void soft(Member user, int purge, String reason, EventWrapper wrapper) {
        String locReason = TextLocalizer.localizeAllAndReplace(
                BanLocale.SOFT_BANNED.tag,
                wrapper,
                wrapper.getGuild().get().getName(),
                reason
        );
        sendBannedUserInfo(user, locReason);
        user.ban(purge, reason).complete();
        wrapper.getGuild().get().unban(user.getUser()).queue(s->{
            MessageSender.sendMessage(
                    TextLocalizer.localizeAllAndReplace(
                           BanLocale.SUCCESS_SOFT.tag,
                           wrapper,
                           wrapper.getGuild().get().getName()
                    ),
                    wrapper.getMessageChannel()
            );
        });
    }

    private void temp(Member user, int purge, String reason, EventWrapper wrapper) {

    }

    private void perma(Member user, int purge, String reason, EventWrapper wrapper) {
        String locReason = TextLocalizer.localizeAllAndReplace(
                BanLocale.PERM_BANNED.tag,
                wrapper,
                wrapper.getGuild().get().getName(),
                reason
        );
        sendBannedUserInfo(user, locReason);
        user.ban(purge, reason).queue(s -> {
            MessageSender.sendMessage(
                    TextLocalizer.localizeAllAndReplace(BanLocale.SUCCESS_PERM.tag,
                    wrapper,
                    user.getEffectiveName()),
                    wrapper.getMessageChannel()
            );
        });
    }

    private void sendBannedUserInfo(Member user, String reason) {
        PrivateChannel userChannel = user.getUser().openPrivateChannel().complete();
        userChannel.sendMessage(reason).complete();
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.paser = parser;
    }
}
