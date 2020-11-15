package de.eldoria.shepard.commandmodules.greeting.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.greeting.data.InviteData;
import de.eldoria.shepard.commandmodules.greeting.types.DatabaseInvite;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.localization.util.Replacement;
import de.eldoria.shepard.localization.util.TextLocalizer;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqParser;
import de.eldoria.shepard.util.TextFormatting;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static de.eldoria.shepard.localization.enums.commands.admin.InviteLocale.*;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeAllAndReplace;
import static de.eldoria.shepard.localization.util.TextLocalizer.localizeByWrapper;
import static java.lang.System.lineSeparator;

@CommandUsage(EventContext.GUILD)
public class InviteDetection extends Command implements Executable, ReqDataSource, ReqParser {

    private static final Pattern INVITE = Pattern.compile("([a-zA-Z0-9]{4,10})$");

    private InviteData inviteData;
    private ArgumentParser parser;

    /**
     * Creates a new Invite command object.
     */
    public InviteDetection() {
        super("inviteDetection",
                new String[]{"registerInvites", "id"},
                "command.invite.description",
                SubCommand.builder("inviteDetection")
                        .addSubcommand("command.invite.subcommand.addInvite",
                                Parameter.createCommand("add"),
                                Parameter.createInput("command.invite.argument.codeOfInvite", "command.invite.argumentDescription.codeOfInvite", true),
                                Parameter.createInput("command.general.argument.name", "command.invite.argument.inviteName", true))
                        .addSubcommand("command.invite.subcommand.removeInvite",
                                Parameter.createCommand("remove"),
                                Parameter.createInput("command.invite.argument.codeOfInvite", "command.invite.argumentDescription.codeOfInvite", true))
                        .addSubcommand("command.invite.subcommand.refreshInvites",
                                Parameter.createCommand("refresh"))
                        .addSubcommand("command.invite.subcommand.showInvites",
                                Parameter.createCommand("list"))
                        .addSubcommand("command.invite.subcommand.setInviteRole",
                                Parameter.createCommand("setInviteRole"),
                                Parameter.createInput("command.invite.argument.codeOfInvite", "command.invite.argumentDescription.codeOfInvite", true),
                                Parameter.createInput("command.general.argument.role", "command.general.argumentDescription.role", true))
                        .build(),
                CommandCategory.ADMIN);
    }


    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        String cmd = args[0];
        if (isSubCommand(cmd, "add")) {
            addInvite(args, wrapper);
            return;
        }
        if (isSubCommand(cmd, "remove")) {
            removeInvite(args, wrapper);
            return;
        }
        if (isSubCommand(cmd, "refresh")) {
            refreshInvites(wrapper);
            return;
        }
        if (isSubCommand(cmd, "list")) {
            listInvites(wrapper);
            return;
        }
        if (isSubCommand(cmd, "setInviteRole")) {
            setInviteRole(args, wrapper);
            return;
        }
    }

    private void setInviteRole(String[] args, EventWrapper wrapper) {
        String code = getCode(args[1]);
        if (code == null) {
            MessageSender.sendSimpleError(ErrorType.NO_INVITE_FOUND, wrapper);
            return;
        }

        if ("none".equalsIgnoreCase(args[2])) {
            if (inviteData.setInviteRole(wrapper.getGuild().get(), code, null, wrapper)) {
                MessageSender.sendLocalized("command.invite.messages.removedInviteRole",
                        wrapper, Replacement.create("CODE", code));
            }
            return;
        }

        Optional<Role> role = parser.getRole(wrapper.getGuild().get(), args[2]);

        if (role.isPresent()) {
            if (!wrapper.getSelfMember().get().canInteract(role.get())) {
                MessageSender.sendSimpleError(ErrorType.HIERARCHY_EXCEPTION, wrapper, Replacement.createMention(role.get()));
                return;
            }

            if (inviteData.setInviteRole(wrapper.getGuild().get(), code, role.get(), wrapper)) {
                MessageSender.sendLocalized("command.invite.messages.setInviteRole",
                        wrapper, Replacement.createMention(role.get()), Replacement.create("CODE", code));
            }
        } else {
            MessageSender.sendSimpleError(ErrorType.INVALID_ROLE, wrapper);
        }

    }

    private void listInvites(EventWrapper wrapper) {
        Guild guild = wrapper.getGuild().get();
        List<DatabaseInvite> invites = inviteData.getInvites(guild, wrapper).stream().filter(i -> i.getSource() != null).collect(Collectors.toList());

        StringBuilder message = new StringBuilder();
        message.append(M_REGISTERED_INVITES.tag).append(lineSeparator());

        TextFormatting.TableBuilder tableBuilder = TextFormatting.getTableBuilder(
                invites, TextLocalizer.localizeAll(M_CODE.tag, wrapper),
                TextLocalizer.localizeAll(M_USAGE_COUNT.tag, wrapper),
                TextLocalizer.localizeAll(M_INVITE_NAME.tag, wrapper),
                TextLocalizer.localizeByWrapper("command.general.argument.role", wrapper));
        for (DatabaseInvite invite : invites) {
            tableBuilder.next();
            tableBuilder.setRow(
                    invite.getCode(),
                    String.valueOf(invite.getUsedCount()),
                    invite.getSource(),
                    invite.getRole() != null ? invite.getRole().getName() : localizeByWrapper("words.disabled", wrapper));
        }
        message.append(tableBuilder);
        MessageSender.sendMessage(message.toString(), wrapper.getMessageChannel());
    }

    private void refreshInvites(EventWrapper wrapper) {
        wrapper.getGuild().get().retrieveInvites().queue(invites -> {
            if (inviteData.updateInvite(wrapper.getGuild().get(), invites)) {
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

        String code = getCode(args[1]);
        if (code == null) {
            MessageSender.sendSimpleError(ErrorType.NO_INVITE_FOUND, wrapper);
            return;
        }


        wrapper.getGuild().get().retrieveInvites().queue(invites -> {
            for (var invite : invites) {
                if (invite.getCode().equals(code)) {
                    String name = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                    if (inviteData.addInvite(wrapper.getGuild().get(), invite.getInviter(), invite.getCode(), name,
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

    private String getCode(String code) {
        Matcher matcher = INVITE.matcher(code);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1);

    }

    @Override
    public void addDataSource(DataSource source) {
        inviteData = new InviteData(source);
    }

    @Override
    public void addParser(ArgumentParser parser) {
        this.parser = parser;
    }
}
