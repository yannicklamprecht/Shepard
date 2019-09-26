package de.eldoria.shepard.reactionactions;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class ExecuteCommand extends Action {

    private final Command command;
    private final String[] args;
    private final MessageEventDataWrapper messageContext;

    public ExecuteCommand(User exclusiveUser, Command command,
                          String[] args, MessageEventDataWrapper messageContext) {
        super("U+2705", exclusiveUser, 60,true);
        this.command = command;
        this.args = args;
        this.messageContext = messageContext;
    }

    @Override
    protected void internalExecute(GuildMessageReactionAddEvent event) {
        command.execute(command.getCommandName(), args, messageContext);
        event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(message -> {
            if (message != null) {
                message.delete().queue();
            }
        });
    }
}