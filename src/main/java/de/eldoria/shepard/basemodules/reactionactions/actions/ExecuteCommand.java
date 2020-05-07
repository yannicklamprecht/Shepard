package de.eldoria.shepard.basemodules.reactionactions.actions;

import de.eldoria.shepard.basemodules.commanddispatching.CommandHub;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.util.reactions.Emoji;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class ExecuteCommand extends Action {

    private final CommandHub commands;
    private final Command command;
    private final String[] args;
    private final MessageEventDataWrapper messageContext;

    /**
     * Execute a command.
     *
     * @param commands       command to execute
     * @param exclusiveUser  user which is allowed to use this execution. null if everyone is allowed.
     * @param command        command to execute
     * @param args           command args
     * @param messageContext message context for execution
     */
    public ExecuteCommand(CommandHub commands, User exclusiveUser, Command command,
                          String[] args, MessageEventDataWrapper messageContext) {
        super(Emoji.CHECK_MARK_BUTTON, exclusiveUser, 60, true);
        this.commands = commands;
        this.command = command;
        this.args = args;
        this.messageContext = messageContext;
    }

    @Override
    protected void internalExecute(GuildMessageReactionAddEvent event) {
        commands.dispatchCommand(command, command.getCommandName(), args, messageContext);
        event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(message -> {
            if (message != null) {
                message.delete().queue();
            }
        });
    }
}