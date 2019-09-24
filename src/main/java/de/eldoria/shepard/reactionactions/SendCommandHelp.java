package de.eldoria.shepard.reactionactions;

import de.eldoria.shepard.contexts.commands.Command;
import de.eldoria.shepard.wrapper.MessageEventDataWrapper;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class SendCommandHelp extends Action {

    private final Command command;
    private final MessageEventDataWrapper wrapper;

    public SendCommandHelp(Command command, MessageEventDataWrapper wrapper) {
        super("U+2753", null, false);
        this.command = command;
        this.wrapper = wrapper;
    }

    @Override
    protected void internalExecute(GuildMessageReactionAddEvent event) {
        wrapper.getAuthor().openPrivateChannel().queue(privateChannel -> command.sendCommandUsage(privateChannel));
        event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(message -> {
            if (message != null) {
                message.delete().queue();
            }
        });
    }
}
