package de.eldoria.shepard.commandmodules.greeting.commands;

import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.command.GuildChannelOnly;
import de.eldoria.shepard.commandmodules.greeting.data.InviteData;
import de.eldoria.shepard.commandmodules.greeting.types.DatabaseInvite;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.enums.commands.GeneralLocale;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Guild;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.eldoria.shepard.localization.enums.commands.admin.InviteLocale.AD_CODE;
import static de.eldoria.shepard.localization.enums.commands.admin.InviteLocale.A_CODE;
import static de.eldoria.shepard.localization.enums.commands.admin.InviteLocale.A_INVITE_NAME;
import static de.eldoria.shepard.localization.enums.commands.admin.InviteLocale.C_ADD_INVITE;
import static de.eldoria.shepard.localization.enums.commands.admin.InviteLocale.C_REFRESH_INVITES;
import static de.eldoria.shepard.localization.enums.commands.admin.InviteLocale.C_REMOVE_INVITE;
import static de.eldoria.shepard.localization.enums.commands.admin.InviteLocale.C_SHOW_INVITES;
import static de.eldoria.shepard.localization.enums.commands.admin.InviteLocale.DESCRIPTION;
import static de.eldoria.shepard.localization.enums.commands.admin.InviteLocale.M_ADDED_INVITE;
import static de.eldoria.shepard.localization.enums.commands.admin.InviteLocale.M_CODE;
import static de.eldoria.shepard.localization.enums.commands.admin.InviteLocale.M_INVITE_NAME;
import static de.eldoria.shepard.localization.enums.commands.admin.InviteLocale.M_REGISTERED_INVITES;
import static de.eldoria.shepard.localization.enums.commands.admin.InviteLocale.M_REMOVED_INVITE;
import static de.eldoria.shepard.localization.enums.commands.admin.InviteLocale.M_REMOVED_NON_EXISTENT_INVITES;
import static de.eldoria.shepard.localization.enums.commands.admin.InviteLocale.M_USAGE_COUNT;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;
import static java.lang.System.lineSeparator;

public class Invite extends Command implements GuildChannelOnly, Executable, ReqDataSource {

    private static final Pattern INVITE = Pattern.compile("([a-zA-Z0-9]{6,7})$");

    private InviteData inviteData;

    /**
     * Creates a new Invite command object.
     */
    public Invite() {
        super("inviteDetection",
                new String[]{"registerInvites"},
                DESCRIPTION.tag,
                SubCommand.builder("invite")
                        .addSubcommand(C_ADD_INVITE.tag,
                                Parameter.createCommand("add"),
                                Parameter.createInput(A_CODE.tag, AD_CODE.tag, true),
                                Parameter.createInput(GeneralLocale.A_NAME.tag, A_INVITE_NAME.tag, true))
                        .addSubcommand(C_REMOVE_INVITE.tag,
                                Parameter.createCommand("remove"),
                                Parameter.createInput(A_CODE.tag, AD_CODE.tag, true))
                        .addSubcommand(C_REFRESH_INVITES.tag,
                                Parameter.createCommand("refresh"))
                        .addSubcommand(C_SHOW_INVITES.tag,
                                Parameter.createCommand("list"))
                        .build(),
                CommandCategory.ADMIN);
    }


    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        String cmd = args[0];
        if (isSubCommand(cmd, 0)) {
            addInvite(args, wrapper);
            return;
        }
        if (isSubCommand(cmd, 1)) {
            removeInvite(args, wrapper);
            return;
        }
        if (isSubCommand(cmd, 2)) {
            refreshInvites(wrapper);
            return;
        }
        if (isSubCommand(cmd, 3)) {
            listInvites(wrapper);
            return;
        }
    }

    private void listInvites(EventWrapper wrapper) {
        Guild guild = wrapper.getGuild().get();
        List<DatabaseInvite> invites = inviteData.getInvites(guild, wrapper);

        StringBuilder message = new StringBuilder();
        message.append(M_REGISTERED_INVITES.tag).append(lineSeparator());

        TextFormatting.TableBuilder tableBuilder = TextFormatting.getTableBuilder(
                invites, TextLocalizer.localizeAll(M_CODE.tag, wrapper),
                TextLocalizer.localizeAll(M_USAGE_COUNT.tag, wrapper),
                TextLocalizer.localizeAll(M_INVITE_NAME.tag, wrapper));
        for (DatabaseInvite invite : invites) {
            tableBuilder.next();
            tableBuilder.setRow(invite.getCode(), invite.getUses() + "", invite.getSource());
        }
        message.append(tableBuilder);
        MessageSender.sendMessage(message.toString(), wrapper.getMessageChannel());
    }

    private void refreshInvites(EventWrapper wrapper) {
        wrapper.getGuild().get().retrieveInvites().queue(invites -> {
            if (inviteData.updateInvite(wrapper.getGuild().get(), invites, wrapper)) {
                MessageSender.sendMessage(M_REMOVED_NON_EXISTENT_INVITES.tag, wrapper.getMessageChannel());
            }
        });
    }

    private void removeInvite(String[] args, EventWrapper wrapper) {
        if (args.length != 2) {
            MessageSender.sendSimpleError(ErrorType.INVALID_ARGUMENT, wrapper);
            return;
        }
        List<DatabaseInvite> databaseInvites = inviteData.getInvites(wrapper.getGuild().get(), wrapper);

        for (DatabaseInvite invite : databaseInvites) {
            if (invite.getCode().equals(args[1])) {
                if (inviteData.removeInvite(wrapper.getGuild().get(), args[1], wrapper)) {
                    MessageSender.sendMessage(M_REMOVED_INVITE.tag + " **" + invite.getSource()
                            + "**", wrapper.getMessageChannel());
                    return;
                }
            }
        }
        MessageSender.sendSimpleError(ErrorType.NO_INVITE_FOUND, wrapper);
    }

    private void addInvite(String[] args, EventWrapper wrapper) {
        if (args.length < 3) {
            MessageSender.sendSimpleError(ErrorType.TOO_FEW_ARGUMENTS, wrapper);
            return;
        }

        Matcher matcher = INVITE.matcher(args[1]);
        if (!matcher.find()) {
            MessageSender.sendSimpleError(ErrorType.NO_INVITE_FOUND, wrapper);
        }
        String code = matcher.group(1);


        wrapper.getGuild().get().retrieveInvites().queue(invites -> {
            for (var invite : invites) {
                if (invite.getCode().equals(code)) {
                    String name = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                    if (inviteData.addInvite(wrapper.getGuild().get(), invite.getCode(), name,
                            invite.getUses(), wrapper)) {
                        MessageSender.sendMessage(localizeAllAndReplace(M_ADDED_INVITE.tag,
                                wrapper,
                                "**" + name + "**",
                                "**" + invite.getCode() + "**",
                                "**" + invite.getUses() + "**"), wrapper.getMessageChannel());
                    }
                    return;
                }
            }
            MessageSender.sendSimpleError(ErrorType.NO_INVITE_FOUND, wrapper);
        });
    }

    @Override
    public void addDataSource(DataSource source) {
        inviteData = new InviteData(source);
    }
}
